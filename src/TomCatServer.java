
import com.sun.org.apache.regexp.internal.RE;
import net.sf.json.JSONObject;

import java.util.*;

import javax.servlet.ServletContextEvent;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.OnError;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import static jdk.nashorn.internal.objects.NativeNumber.valueOf;

@ServerEndpoint(value = "/websocket/{user}")
public class TomCatServer {

    private static Set<Session> sessions = new HashSet<Session>();
    private Session session;



    @OnOpen
    public void open(Session session, @PathParam(value = "user") String user) {

        this.session = session;
        sessions.add(this.session);
        // TODO:search database
        System.out.println(session.getRequestURI() + " come in ！");
    }

    @OnClose
    public void close() {

        sessions.remove(session);
        // TODO:close and commit database

        System.out.println(session.getRequestURI() + " leave ！");
    }

    @OnMessage
    public void message(String message, @PathParam(value = "user") String user) {
        JSONObject data = JSONObject.fromObject(message);
        if (valueOf(data.get("mode")) == 1) {
            if (data.get("message") != "MSGFORTEST") {
                sendToAll((String) data.get("message"), user);
            }
        } else {
            if (data.get("message") != "MSGFORTEST") {
                sendTo(data.get("message"), data.get("receiver"));
            }
        }
        System.out.println("messsageS:  " + data.get("message"));
        System.out.println("messsageS:  " + data.get("receiver"));
    }


    @OnError
    public void onError(Session session , Throwable throwable) {
        System.out.println("error"+session.getId() + "---" + throwable.getMessage());
    }


        public static void sendToAll(String text, @PathParam(value = "user") String sender) {
        for (Session client : sessions) {
            if (!Objects.equals(client.getRequestURI().toString().split("/")[2], sender)) {
                synchronized (client) {
                    client.getAsyncRemote().sendText(text);
                }
            }
        }
    }

    private void sendTo(Object text, Object user) {
        int count = 0;
        for (Session client : sessions) {
            if (Objects.equals(client.getRequestURI().toString().split("/")[2], user)) {
                synchronized (client) {
                    client.getAsyncRemote().sendText(text.toString());
                    count += 1;
                }
                break;
            }
        }
        if (count == 0) {
            //TODO: connect to db for sending message to users who are offline
        }
    }


}


