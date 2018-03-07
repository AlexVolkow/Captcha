package main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import services.captcha.CaptchaGenerator;
import services.captcha.CaptchaService;
import services.captcha.VerifyService;
import services.register.RegisterService;
import servlets.*;
import store.RuntimeStore;
import store.Store;

public class Main {
    private final static Logger logger = LogManager.getLogger(Main.class.getName());

    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            logger.error("Expected port number");
            return;
        }

        int port = Integer.parseInt(args[0]);
        long ttl = Integer.parseInt(System.getProperty("ttl")) * 1000;
        boolean production = System.getProperty("production") != null;

        Store store = new RuntimeStore();
        RegisterService regService = new RegisterService(store);
        CaptchaGenerator generator = new CaptchaGenerator();
        CaptchaService captchaService = new CaptchaService(generator, ttl);
        VerifyService verifyService = new VerifyService(captchaService);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.addServlet(new ServletHolder(new RegisterServlet(regService)), RegisterServlet.PAGE_URL);
        context.addServlet(new ServletHolder(new NewCaptchaServlet(regService, captchaService, production)),
                NewCaptchaServlet.PAGE_URL);
        context.addServlet(new ServletHolder(new CaptchaImageServlet(regService, captchaService, generator)),
                CaptchaImageServlet.PAGE_URL);
        context.addServlet(new ServletHolder(new CaptchaSolveServlet(regService, verifyService, captchaService)),
                CaptchaSolveServlet.PAGE_URL);
        context.addServlet(new ServletHolder(new CaptchaVerifyServlet(verifyService)),
                CaptchaVerifyServlet.PAGE_URL);

        Server server = new Server(port);
        server.setHandler(context);
        try {
            server.start();
            logger.info("Server started on port " + port + " with ttl = "
                    + (ttl / 1000) + " production = " + production);
            server.join();
        } catch (Exception e) {
            logger.error("Error when server started " + e.getMessage());
        }
    }
}
