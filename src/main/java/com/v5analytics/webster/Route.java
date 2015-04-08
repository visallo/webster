package com.v5analytics.webster;

import com.v5analytics.webster.utils.UrlUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Route {
    public static final String MATCHED_ROUTE = "websterMatchedRoute";

    public static enum Method {GET, POST, PUT, DELETE}

    private static final char[] REGEX_SPECIAL_CHARS = new char[]{
            '\\', '^', '$', '.', '|', '?', '*', '+', '(', ')', '[', ']', '{', '}'
    };
    private static final Pattern COMPONENT_NAME_GREEDY_PATTERN = Pattern.compile("^(.*)\\*$");
    private static final Pattern COMPONENT_NAME_REGEX_PATTERN = Pattern.compile("^(.*?)<(.*)>$");

    private Method method;
    private String path;
    private Handler[] handlers;
    private List<String> componentNames = new ArrayList<String>();
    private Pattern routePathPattern;

    public Route(Method method, String path, Handler... handlers) {
        this.method = method;
        this.path = path;
        this.handlers = handlers;
        this.routePathPattern = convertPathToRegex(path, componentNames);
    }

    private Pattern convertPathToRegex(String path, List<String> componentNames) {
        Matcher m;
        StringBuilder regex = new StringBuilder();
        regex.append('^');
        for (int i = 0; i < path.length(); i++) {
            char ch = path.charAt(i);
            if (ch == '{') {
                i++;
                StringBuilder componentNameStringBuilder = new StringBuilder();
                for (; i < path.length(); i++) {
                    ch = path.charAt(i);
                    if (ch == '}') {
                        break;
                    }
                    componentNameStringBuilder.append(ch);
                }
                String componentName = componentNameStringBuilder.toString();
                if ((m = COMPONENT_NAME_GREEDY_PATTERN.matcher(componentName)) != null && m.matches()) {
                    componentNames.add(m.group(1));
                    regex.append("(.*)");
                } else if ((m = COMPONENT_NAME_REGEX_PATTERN.matcher(componentName)) != null && m.matches()) {
                    componentNames.add(m.group(1));
                    regex.append('(');
                    regex.append(m.group(2));
                    regex.append(')');
                } else {
                    componentNames.add(componentName);
                    regex.append("(.*?)");
                }
            } else {
                if (isRegexSpecialChar(ch)) {
                    regex.append('\\');
                }
                regex.append(ch);
            }
        }
        regex.append('$');
        return Pattern.compile(regex.toString());
    }

    private boolean isRegexSpecialChar(char ch) {
        for (char regexChar : REGEX_SPECIAL_CHARS) {
            if (regexChar == ch) {
                return true;
            }
        }
        return false;
    }

    public boolean isMatch(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath == null) {
            contextPath = "";
        }
        String relativeUri = requestURI.substring(contextPath.length());
        return isMatch(request, relativeUri);
    }

    public boolean isMatch(HttpServletRequest request, String relativeUri) {
        Method requestMethod = Method.valueOf(request.getMethod().toUpperCase());
        if (!requestMethod.equals(method)) {
            return false;
        }

        Matcher m = this.routePathPattern.matcher(relativeUri);
        if (!m.matches()) {
            return false;
        }
        if (m.groupCount() != this.componentNames.size()) {
            return false;
        }

        request.setAttribute(MATCHED_ROUTE, this);

        for (int i = 0; i < m.groupCount(); i++) {
            String routeComponent = m.group(i + 1);
            String requestComponent = UrlUtils.urlDecode(routeComponent);
            request.setAttribute(this.componentNames.get(i), requestComponent);
        }

        return true;
    }

    public Handler[] getHandlers() {
        return handlers;
    }

    public Method getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !obj.getClass().isAssignableFrom(Route.class)) {
            return false;
        }
        Route otherRoute = (Route)obj;
        return this.getMethod().equals(otherRoute.getMethod()) && this.getPath().equals(otherRoute.getPath());
    }

    @Override
    public int hashCode() {
        return this.getPath().hashCode() + 37 * this.getMethod().hashCode();
    }
}
