package org.visallo.webster.testApp;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.visallo.webster.*;
import org.visallo.webster.handlers.RouteRunner;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TestApp {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new WebsterException("Usage <port>");
        }
        int port = Integer.parseInt(args[0]);

        Server server = new Server(port);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        context.addServlet(new ServletHolder(new TestServlet()), "/*");
        server.start();
        System.out.println("Listening http://localhost:" + port);
        server.join();
    }

    private static class TestServlet extends HttpServlet {
        private App app;

        @Override
        public void init(ServletConfig config) throws ServletException {
            super.init(config);

            App.registeredParameterProviderFactory(new TestUserParameterProviderFactory());
            App.registerParameterValueConverter(TestParameterObject.class, new DefaultParameterValueConverter.SingleValueConverter<TestParameterObject>() {
                @Override
                public TestParameterObject convert(Class parameterType, String parameterName, String value) {
                    return TestParameterObjectExtended.parse(value);
                }
            });

            app = new App(this.getServletContext());

            app.get("/", RouteRunner.class);
            app.get("/favicon.ico", FavIconHandler.class);
            app.get("/noParameters", NoParametersHandler.class);
            app.get("/withParameters", WithParametersHandler.class);
            app.post("/withParameters", WithParametersHandler.class);
            app.delete("/withParameters", WithParametersHandler.class);
            app.get("/long/path/name/to/test/wrapping/of/really/long/path/names/test/wrapping/of/really/long/path/names", WithParametersHandler.class);
            app.get("/json", ReturnsJsonHandler.class);
            app.get("/webster.jpg", ImageHandler.class);
        }

        @Override
        protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            try {
                app.handle(req, resp);
            } catch (WebsterException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new WebsterException("Could not service request", ex);
            }
        }
    }
}
