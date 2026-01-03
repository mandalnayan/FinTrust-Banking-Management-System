package com.fintrust.listener;


import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;

/**
 * MyAppContextListener is a servlet context listener that performs cleanup tasks
 * when the web application is stopped or redeployed.
 * 
 * <p>
 * Specifically, it stops the MySQL JDBC driver's background
 * {@link AbandonedConnectionCleanupThread} to prevent "Illegal access" warnings
 * during Tomcat redeploys. This ensures that no threads remain active after
 * the web application has been undeployed.
 * </p>
 * 
 * <p>
 * To use this listener, register it in your {@code web.xml}:
 * </p>
 * <pre>
 * &lt;listener&gt;
 *     &lt;listener-class&gt;com.yourcompany.app.listener.MyAppContextListener&lt;/listener-class&gt;
 * &lt;/listener&gt;
 * </pre>
 * 
 * <p>
 * Note: This listener does not interact with Spring beans or application logic.
 * Its sole purpose is resource/thread cleanup.
 * </p>
 */
public class MyAppContextListener implements ServletContextListener {

    /**
     * Called when the web application is starting.
     * 
     * @param sce the ServletContextEvent containing the ServletContext that is being initialized
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // No initialization logic needed for this listener
    }

    /**
     * Called when the web application is stopping or being redeployed.
     * 
     * <p>
     * This method shuts down the MySQL JDBC driver's
     * {@link AbandonedConnectionCleanupThread} to prevent warnings about illegal
     * access to resources after the web application has been stopped.
     * </p>
     * 
     * @param sce the ServletContextEvent containing the ServletContext that is being destroyed
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        
            AbandonedConnectionCleanupThread.checkedShutdown();
        
    }
}

