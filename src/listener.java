import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServlet;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by htxx on 2017/8/2.
 */
public class listener
        extends HttpServlet
        implements ServletContextListener {
    public listener() {
    }

    private java.util.Timer timer = null;
    public void contextInitialized(ServletContextEvent event) {
        timer = new java.util.Timer(true);
        event.getServletContext().log("start");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                TomCatServer.sendToAll("", " ");
                System.out.println("Asd");
            }
        }, 0, 60 * 2 * 1000);

    }

    public void contextDestroyed(ServletContextEvent event) {
        timer.cancel();
        event.getServletContext().log("destory");
    }

}