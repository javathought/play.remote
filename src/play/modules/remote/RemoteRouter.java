package play.modules.remote;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import jregex.Matcher;
import jregex.Pattern;
import jregex.REFlags;
import play.Logger;
import play.Play;
import play.mvc.Http;
import play.mvc.results.NotFound;
import play.mvc.results.RenderStatic;
import play.templates.TemplateLoader;
import play.utils.Utils;
import play.vfs.VirtualFile;

public class RemoteRouter {
	
//	static Pattern routePattern = new Pattern("^({method}GET|POST|PUT|DELETE|OPTIONS|HEAD|WS|\\*)[(]?({headers}[^)]*)(\\))?\\s+({path}.*/[^\\s]*)\\s+({action}[^\\s(]+)({params}.+)?(\\s*)$");
	static Pattern routePattern = new Pattern("^({method}GET|POST|PUT|DELETE|OPTIONS|HEAD|WS|\\*)\\s+({path}.*/[^\\s]*)\\s+({action}[^\\s(]+)({params}.+)?(\\s*)$");
    /**
     * Pattern used to locate a method override instruction in request.querystring
     */
    static Pattern methodOverride = new Pattern("^.*x-http-method-override=({method}GET|PUT|POST|DELETE).*$");
    /**
     * Timestamp the routes file was last loaded at.
     */
    public static long lastLoading = -1;
    /**
     * Remote routes file
     */	
    public static VirtualFile routesConf;
    /**
     * All the loaded routes.
     */
    public static List<Route> routes = new CopyOnWriteArrayList<Route>();
    
//    private static RemoteManager rm = new RemoteManager();

//    public RemoteRouter() {
//    	load();
//    }

//    public static RemoteManager rm() {
////    	if (lastLoading < 0) {
////        	load();    		
////    	}
//    	return rm;
//    	
//    }
	
    /**
     * Parse the routes file. This is called at startup.
     *
     * @param prefix The prefix that the path of all routes in this route file start with. This prefix should not end with a '/' character.
     */
    public static void load() {
//        routes.clear();

    	// Remote route file
    	//        routesConf = Play.appRoot.child("conf/remote.conf");
        routesConf = VirtualFile.fromRelativePath("/conf/remote.conf");
        parse(routesConf, null);
        lastLoading = System.currentTimeMillis();
        // Plugins
//        Play.pluginCollection.onRoutesLoaded();
    }
    
    /**
     * This is used internally when reading the route file. The order the routes are added matters and
     * we want the method to append the routes to the list.
     */
    public static void appendRoute(String method, String path, String action, String params, String headers, String sourceFile, int line) {
        routes.add(getRoute(method, path, action, params, headers, sourceFile, line));
    }
    
    public static Route getRoute(String method, String path, String action, String params, String headers) {
        return getRoute(method, path, action, params, headers, null, 0);
    }

    public static Route getRoute(String method, String path, String action, String params, String headers, String sourceFile, int line) {
        Route route = new Route();
        route.method = method;
        if (Logger.isTraceEnabled()) {
            Logger.trace("path = [" + path + "]");
        }
        route.path = path.replace("//", "/");
        route.action = action;
        route.routesFile = sourceFile;
        route.routesFileLine = line;
        route.addFormat(headers);
        route.addParams(params);
        route.compute();
        if (Logger.isTraceEnabled()) {
            Logger.trace("Adding [" + route.toString() + "] with params [" + params + "] and headers [" + headers + "]");
        }
        return route;
    }

	
    /**
     * Parse a route file.
     * If an action starts with <i>"plugin:name"</i>, replace that route by the ones declared
     * in the plugin route file denoted by that <i>name</i>, if found.
     *
     * @param routeFile
     * @param prefix    The prefix that the path of all routes in this route file start with. This prefix should not
     *                  end with a '/' character.
     */
    static void parse(VirtualFile routeFile, String prefix) {
        String fileAbsolutePath = routeFile.getRealFile().getAbsolutePath();
        String content = routeFile.contentAsString();
        if (content.indexOf("${") > -1 || content.indexOf("#{") > -1 || content.indexOf("%{") > -1) {
            // Mutable map needs to be passed in.
            content = TemplateLoader.load(routeFile).render(new HashMap<String, Object>(16));
        }
        if (Logger.isTraceEnabled()) {
        	Logger.trace("Content of route = [%s]", content);
        }
        parse(content, prefix, fileAbsolutePath);
    }

    static void parse(String content, String prefix, String fileAbsolutePath) {
        int lineNumber = 0;
        for (String line : content.split("\n")) {
            lineNumber++;
            line = line.trim().replaceAll("\\s+", " ");
            if (line.length() == 0 || line.startsWith("#")) {
                continue;
            }
            Matcher matcher = routePattern.matcher(line);
            if (matcher.matches()) {
                String action = matcher.group("action");
                // module:
                if (action.startsWith("module:")) {
                    String moduleName = action.substring("module:".length());
                    String newPrefix = prefix + matcher.group("path");
                    if (newPrefix.length() > 1 && newPrefix.endsWith("/")) {
                        newPrefix = newPrefix.substring(0, newPrefix.length() - 1);
                    }
                    if (moduleName.equals("*")) {
                        for (String p : Play.modulesRoutes.keySet()) {
                            parse(Play.modulesRoutes.get(p), newPrefix + p);
                        }
                    } else if (Play.modulesRoutes.containsKey(moduleName)) {
                        parse(Play.modulesRoutes.get(moduleName), newPrefix);
                    } else {
                        Logger.error("Cannot include routes for module %s (not found)", moduleName);
                    }
                } else {
                    String method = matcher.group("method");
                    String path = prefix + matcher.group("path");
                    String params = matcher.group("params");
//                    String headers = matcher.group("headers");
                    String headers = null;
                    appendRoute(method, path, action, params, headers, fileAbsolutePath, lineNumber);
                }
            } else {
                Logger.error("Invalid route definition : %s", line);
            }
        }
    }
    
    public static Route route(String action) {
        if (Logger.isTraceEnabled()) {
            Logger.trace("Route for : " + action );
        }
        // request method may be overriden if a x-http-method-override parameter is given
//        if (request.querystring != null && methodOverride.matches(request.querystring)) {
//            Matcher matcher = methodOverride.matcher(request.querystring);
//            if (matcher.matches()) {
//                if (Logger.isTraceEnabled()) {
//                    Logger.trace("request method %s overriden to %s ", request.method, matcher.group("method"));
//                }
//                request.method = matcher.group("method");
//            }
//        }
        for (Route route : routes) {
        	
//          if (Logger.isTraceEnabled()) {
        	  Logger.trace("Route = %s ", route.path);
//          }
            Map<String, String> args = route.matches(action);
            if (args != null) {
//                request.action = route.action;
//                if (args.containsKey("format")) {
//                    request.format = args.get("format");
//                }
//                if (request.action.indexOf("{") > -1) { // more optimization ?
//                    for (String arg : request.routeArgs.keySet()) {
//                        request.action = request.action.replace("{" + arg + "}", request.routeArgs.get(arg));
//                    }
//                }
//                if (request.action.equals("404")) {
//                    throw new NotFound(route.path);
//                }
                return route;
            }
        }
        // Not found - if the request was a HEAD, let's see if we can find a corresponding GET
//        if (request.method.equalsIgnoreCase("head")) {
//            request.method = "GET";
//            Route route = route(request);
//            request.method = "HEAD";
//            if (route != null) {
//                return route;
//            }
//        }
        throw new NotFound(action);
    }

    public static class Route {
    	
	    /**
	     * HTTP method, e.g. "GET".
	     */
	    public String method;
	    public String path;
	    /**
	     * @todo - what is this?
	     */
	    public String action;
	    Pattern actionPattern;
	    List<String> actionArgs = new ArrayList<String>(3);
	    String staticDir;
	    boolean staticFile;
	    Pattern pattern;
	    Pattern hostPattern;
	    List<Arg> args = new ArrayList<Arg>(3);
	    Map<String, String> staticArgs = new HashMap<String, String>(3);
	    List<String> formats = new ArrayList<String>(1);
	    String host;
	    Arg hostArg = null;
	    public int routesFileLine;
	    public String routesFile;
	    static Pattern customRegexPattern = new Pattern("\\{([a-zA-Z_][a-zA-Z_0-9]*)\\}");
	    static Pattern argsPattern = new Pattern("\\{<([^>]+)>([a-zA-Z_0-9]+)\\}");
	    static Pattern paramPattern = new Pattern("([a-zA-Z_0-9]+):'(.*)'");
	
	    public void compute() {
	        this.host = "";
	        this.hostPattern = new Pattern(".*");
	        if (action.startsWith("staticDir:") || action.startsWith("staticFile:")) {
	            // Is there is a host argument, append it.
	            if (!path.startsWith("/")) {
	                String p = this.path;
	                this.path = p.substring(p.indexOf("/"));
	                this.host = p.substring(0, p.indexOf("/"));
	                if (this.host.contains("{")) {
	                    Logger.warn("Static route cannot have a dynamic host name");
	                    return;
	                }
	            }
	            if (!method.equalsIgnoreCase("*") && !method.equalsIgnoreCase("GET")) {
	                Logger.warn("Static route only support GET method");
	                return;
	            }
	        }
	        // staticDir
	        if (action.startsWith("staticDir:")) {
	            if (!this.path.endsWith("/") && !this.path.equals("/")) {
	                Logger.warn("The path for a staticDir route must end with / (%s)", this);
	                this.path += "/";
	            }
	            this.pattern = new Pattern("^" + path + "({resource}.*)$");
	            this.staticDir = action.substring("staticDir:".length());
	        } else if (action.startsWith("staticFile:")) {
	            this.pattern = new Pattern("^" + path + "$");
	            this.staticFile = true;
	            this.staticDir = action.substring("staticFile:".length());
	        } else {
	            // URL pattern
	            // Is there is a host argument, append it.
	            if (!path.startsWith("/")) {
	                String p = this.path;
	                this.path = p.substring(p.indexOf("/"));
	                this.host = p.substring(0, p.indexOf("/"));
	                String pattern = host.replaceAll("\\.", "\\\\.").replaceAll("\\{.*\\}", "(.*)");
	
	                if (Logger.isTraceEnabled()) {
	                    Logger.trace("pattern [" + pattern + "]");
	                    Logger.trace("host [" + host + "]");
	                }
	
	                Matcher m = new Pattern(pattern).matcher(host);
	                this.hostPattern = new Pattern(pattern);
	
	                if (m.matches()) {
	                    if (this.host.contains("{")) {
	                        String name = m.group(1).replace("{", "").replace("}", "");
	                        if (!name.equals("_")) {
	                            hostArg = new Arg();
	                            hostArg.name = name;
	                            if (Logger.isTraceEnabled()) {
	                                Logger.trace("hostArg name [" + name + "]");
	                            }
	                            // The default value contains the route version of the host ie {client}.bla.com
	                            // It is temporary and it indicates it is an url route.
	                            // TODO Check that default value is actually used for other cases.
	                            hostArg.defaultValue = host;
	                            hostArg.constraint = new Pattern(".*");
	
	                            if (Logger.isTraceEnabled()) {
	                                Logger.trace("adding hostArg [" + hostArg + "]");
	                            }
	
	                            args.add(hostArg);
	                        }
	                    }
	                }
	
	
	            }
	            String patternString = path;
	            patternString = customRegexPattern.replacer("\\{<[^/]+>$1\\}").replace(patternString);
	            Matcher matcher = argsPattern.matcher(patternString);
	            while (matcher.find()) {
	                Arg arg = new Arg();
	                arg.name = matcher.group(2);
	                arg.constraint = new Pattern(matcher.group(1));
	                args.add(arg);
	            }
	
	            patternString = argsPattern.replacer("({$2}$1)").replace(patternString);
	            this.pattern = new Pattern(patternString);
	            // Action pattern
	            patternString = action;
	            patternString = patternString.replace(".", "[.]");
	            for (Arg arg : args) {
	                if (patternString.contains("{" + arg.name + "}")) {
	                    patternString = patternString.replace("{" + arg.name + "}", "({" + arg.name + "}" + arg.constraint.toString() + ")");
	                    actionArgs.add(arg.name);
	                }
	            }
	            actionPattern = new Pattern(patternString, REFlags.IGNORE_CASE);
	        }
	    }
	
	    public void addParams(String params) {
	        if (params == null || params.length() < 1) {
	            return;
	        }
	        params = params.substring(1, params.length() - 1);
	        for (String param : params.split(",")) {
	            Matcher matcher = paramPattern.matcher(param);
	            if (matcher.matches()) {
	                staticArgs.put(matcher.group(1), matcher.group(2));
	            } else {
	                Logger.warn("Ignoring %s (static params must be specified as key:'value',...)", params);
	            }
	        }
	    }
	
	    // TODO: Add args names
	    public void addFormat(String params) {
	        if (params == null || params.length() < 1) {
	            return;
	        }
	        params = params.trim();
	        formats.addAll(Arrays.asList(params.split(",")));
	    }
	
	    private boolean contains(String accept) {
	        boolean contains = (accept == null);
	        if (accept != null) {
	            if (this.formats.isEmpty()) {
	                return true;
	            }
	            for (String format : this.formats) {
	                contains = format.startsWith(accept);
	                if (contains) {
	                    break;
	                }
	            }
	        }
	        return contains;
	    }
	
	    public Map<String, String> matches(String action) {
            Map<String, String> localArgs = new HashMap<String, String>();
            if (this.action.equals(action)) {
            	localArgs.put("method", this.method);
            	return localArgs;
            }
            return null;
	    }

	    public Map<String, String> matches(String method, String path) {
	        return matches(method, path, null, null);
	    }
	
	    public Map<String, String> matches(String method, String path, String accept) {
	        return matches(method, path, accept, null);
	    }
	
	    /**
	     * Check if the parts of a HTTP request equal this Route.
	     *
	     * @param method GET/POST/etc.
	     * @param path   Part after domain and before query-string. Starts with a "/".
	     * @param accept Format, e.g. html.
	     * @param domain The domain (host without port).
	     * @return ???
	     */
	    public Map<String, String> matches(String method, String path, String accept, String domain) {
	        // Normalize
	        if (path.equals(Play.ctxPath)) {
	            path = path + "/";
	        }
	        // If method is HEAD and we have a GET
	        if (method == null || this.method.equals("*") || method.equalsIgnoreCase(this.method) || (method.equalsIgnoreCase("head") && ("get").equalsIgnoreCase(this.method))) {
	
	            Matcher matcher = pattern.matcher(path);
	
	            boolean hostMatches = (domain == null);
	            if (domain != null) {
	
	                Matcher hostMatcher = hostPattern.matcher(domain);
	                hostMatches = hostMatcher.matches();
	            }
	            // Extract the host variable
	            if (matcher.matches() && contains(accept) && hostMatches) {
	                // 404
	                if (action.equals("404")) {
	                    throw new NotFound(method, path);
	                }
	                // Static dir
	                if (staticDir != null) {
	                    String resource = null;
	                    if (!staticFile) {
	                        resource = matcher.group("resource");
	                    }
	                    try {
	                        String root = new File(staticDir).getCanonicalPath();
	                        String urlDecodedResource = Utils.urlDecodePath(resource);
	                        String childResourceName = staticDir + (staticFile ? "" : "/" + urlDecodedResource);
	                        String child = new File(childResourceName).getCanonicalPath();
	                        if (child.startsWith(root)) {
	                            throw new RenderStatic(childResourceName);
	                        }
	                    } catch (IOException e) {
	                    }
	                    throw new NotFound(resource);
	                } else {
	                    Map<String, String> localArgs = new HashMap<String, String>();
	                    for (Arg arg : args) {
	                        // FIXME: Careful with the arguments that are not matching as they are part of the hostname
	                        // Defaultvalue indicates it is a one of these urls. This is a trick and should be changed.
	                        if (arg.defaultValue == null) {
	                           localArgs.put(arg.name, Utils.urlDecodePath(matcher.group(arg.name)));
	                        }
	                    }
	                    if (hostArg != null && domain != null) {
	                        // Parse the hostname and get only the part we are interested in
	                        String routeValue = hostArg.defaultValue.replaceAll("\\{.*}", "");
	                        domain = domain.replace(routeValue, "");
	                        localArgs.put(hostArg.name, domain);
	                    }
	                    localArgs.putAll(staticArgs);
	                    return localArgs;
	                }
	            }
	        }
	        return null;
	    }
	
	    static class Arg {
	
	        String name;
	        Pattern constraint;
	        String defaultValue;
	        Boolean optional = false;
	    }
	
	    @Override
	    public String toString() {
	        return method + " " + path + " -> " + action;
	    }
	}


}
