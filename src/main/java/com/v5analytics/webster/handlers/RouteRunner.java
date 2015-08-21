package com.v5analytics.webster.handlers;

import com.v5analytics.webster.*;
import com.v5analytics.webster.annotations.Handle;
import com.v5analytics.webster.parameterProviders.OptionalParameterProvider;
import com.v5analytics.webster.parameterProviders.ParameterProvider;
import com.v5analytics.webster.parameterProviders.RequiredParameterProvider;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class RouteRunner implements ParameterizedHandler {
    public static final String ROUTE_RUNNER_HTML = "routeRunner.html";
    private final String routeRunnerHtml;

    public RouteRunner() {
        routeRunnerHtml = loadRouteRunnerHtml();
    }

    protected String loadRouteRunnerHtml() {
        try {
            InputStream routeRunnerHtmlStream = RouteRunner.class.getResourceAsStream(ROUTE_RUNNER_HTML);
            if (routeRunnerHtmlStream == null) {
                throw new WebsterException("Could not find " + RouteRunner.class.getResource(ROUTE_RUNNER_HTML));
            }
            ByteArrayOutputStream temp = new ByteArrayOutputStream();
            int read;
            byte[] data = new byte[1024];
            while ((read = routeRunnerHtmlStream.read(data)) > 0) {
                temp.write(data, 0, read);
            }
            String routeRunnerHtml = new String(temp.toByteArray());
            routeRunnerHtml = routeRunnerHtml.replaceAll("\\$\\{pageTitle\\}", getPageTitle());
            routeRunnerHtml = routeRunnerHtml.replaceAll("\\$\\{additionalStyles\\}", getAdditionalStyles());
            routeRunnerHtml = routeRunnerHtml.replaceAll("\\$\\{additionalJavascript\\}", getAdditionalJavascript());
            return routeRunnerHtml;
        } catch (IOException ex) {
            throw new WebsterException("Could not read " + ROUTE_RUNNER_HTML, ex);
        }
    }

    private String getAdditionalJavascript() {
        return "";
    }

    protected String getAdditionalStyles() {
        return "";
    }

    protected String getPageTitle() {
        return "Webster: Route Runner";
    }

    @Handle
    public void handle(Router router, HttpServletResponse response) throws Exception {
        response.setContentType("text/html");
        response.getOutputStream().print(getHtml(router));
    }

    protected String getHtml(Router router) {
        String result = routeRunnerHtml;
        result = result.replaceAll("\\$\\{routesJson\\}", getRoutesJson(router));
        result = result.replaceAll("\\$\\{routes\\}", getRoutesHtml(router));
        return result;
    }

    protected String getRoutesJson(Router router) {
        StringBuilder result = new StringBuilder();
        result.append("{\n");
        Map<Route.Method, List<Route>> routesByMethod = getRoutesByMethod(router);
        boolean firstMethod = true;
        for (Map.Entry<Route.Method, List<Route>> routesByMethodEntry : routesByMethod.entrySet()) {
            if (!firstMethod) {
                result.append(",\n");
            }
            result.append("      \"").append(routesByMethodEntry.getKey().name()).append("\": {\n");
            boolean firstRoute = true;
            for (Route route : routesByMethodEntry.getValue()) {
                if (!firstRoute) {
                    result.append(",\n");
                }
                result.append("        \"").append(route.getPath()).append("\": ").append(getRouteJson(route));
                firstRoute = false;
            }
            result.append("\n      }");
            firstMethod = false;
        }
        result.append("\n    }");
        return result.toString();
    }

    private Map<Route.Method, List<Route>> getRoutesByMethod(Router router) {
        Map<Route.Method, List<Route>> results = new HashMap<>();
        for (Map.Entry<Route.Method, List<Route>> routeEntry : router.getRoutes().entrySet()) {
            for (Route route : routeEntry.getValue()) {
                List<Route> byMethod = results.get(route.getMethod());
                if (byMethod == null) {
                    byMethod = new ArrayList<>();
                    results.put(route.getMethod(), byMethod);
                }
                byMethod.add(route);
            }
        }
        return results;
    }

    protected String getRoutesHtml(Router router) {
        StringBuilder result = new StringBuilder();
        List<Route> routes = getSortedRoutes(router.getRoutes());
        for (Route route : routes) {
            result
                    .append("<li title=\"").append(route.getPath()).append("\" onclick=\"javascript:loadRoute('").append(route.getMethod().name()).append("', '").append(route.getPath()).append("')\">\n")
                    .append("<div class='method method-").append(route.getMethod().name()).append("'>").append(route.getMethod().name()).append("</div>")
                    .append(" ")
                    .append("<div class='path'>").append(route.getPath()).append("</div>")
                    .append("</li>\n");
        }
        return result.toString();
    }

    private List<Route> getSortedRoutes(Map<Route.Method, List<Route>> routes) {
        List<Route> results = new ArrayList<>();
        for (Map.Entry<Route.Method, List<Route>> routeEntry : routes.entrySet()) {
            for (Route route : routeEntry.getValue()) {
                results.add(route);
            }
        }
        Collections.sort(results, new Comparator<Route>() {
            @Override
            public int compare(Route route1, Route route2) {
                int r = route1.getPath().compareTo(route2.getPath());
                if (r != 0) {
                    return r;
                }
                return route1.getMethod().name().compareTo(route2.getMethod().name());
            }
        });
        return results;
    }

    private String getRouteJson(Route route) {
        StringBuilder result = new StringBuilder()
                .append("{")
                .append("\"method\":\"").append(route.getMethod().name()).append("\",")
                .append("\"path\":\"").append(route.getPath()).append("\",")
                .append("\"parameters\":[");
        List<String> parametersJsonItems = getParametersJsonItems(route);
        for (int i = 0; i < parametersJsonItems.size(); i++) {
            if (i > 0) {
                result.append(",");
            }
            result.append(parametersJsonItems.get(i));
        }
        result
                .append("]")
                .append("}");
        return result.toString();
    }

    protected List<String> getParametersJsonItems(Route route) {
        List<String> results = new ArrayList<>();
        RequestResponseHandler lastHandler = route.getHandlers()[route.getHandlers().length - 1];
        if (lastHandler instanceof RequestResponseHandlerParameterizedHandlerWrapper) {
            ParameterProvider[] parameterProviders = ((RequestResponseHandlerParameterizedHandlerWrapper) lastHandler).getParameterProviders();
            for (ParameterProvider parameterProvider : parameterProviders) {
                getParameterJson(parameterProvider, results);
            }
        }
        return results;
    }

    protected void getParameterJson(ParameterProvider parameterProvider, List<String> results) {
        if (parameterProvider instanceof RequiredParameterProvider) {
            RequiredParameterProvider req = (RequiredParameterProvider) parameterProvider;
            results.add("{\"required\":true,\"name\":\"" + req.getParameterName() + "\",\"type\":\"" + req.getParameterType().getName() + "\"}");
        } else if (parameterProvider instanceof OptionalParameterProvider) {
            OptionalParameterProvider opt = (OptionalParameterProvider) parameterProvider;
            String json = "{\"required\":false,\"name\":\"" + opt.getParameterName() + "\",\"type\":\"" + opt.getParameterType().getName() + "\"";
            if (opt.getDefaultValue() != null) {
                json += ",\"defaultValue\":\"" + opt.getDefaultValue() + "\"";
            }
            json += "}";
            results.add(json);
        }
    }
}
