package com.masales;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.vertx.core.http.HttpServerRequest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.nio.file.Files;
import java.util.Map;
import java.util.Scanner;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

@Path("/")
public class StaticContentEndpoint {

    @ConfigProperty(name = "app.report.path")
    String reportPath;

    File eventsFolder;
    File dashboardFolder;
    File eventIndexHtml;
    File events;
    Logger logger = LoggerFactory.getLogger(getClass());
    Map<String, File> cache = new WeakHashMap<>();

    @PostConstruct
    public void config() {
        eventsFolder = new File(reportPath, "events");
        eventIndexHtml = new File(eventsFolder, "index.html");
        events = new File(eventsFolder, "events.js");
        dashboardFolder = new File(reportPath, "html");
    }

    @Operation(hidden = true)
    @GET
    @Path("table")
    public Response table() {
        StringBuilder sb = new StringBuilder();
        try (Scanner sc = new Scanner(eventIndexHtml)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.contains("<script type=\"text/javascript\" src=\"events.js\"></script>")) {
                    line = "";
                }

                if (line.contains("</table>")) {
                    String script = Files.readString(events.toPath()).replace("let ", "var ");
                    script += ";";
                    line = "</table>\n<script>\n" + script + "\n</script>";
                }
                sb.append(line).append("\n");
            }
        } catch (FileNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok(sb.toString()).build();
    }

    @Operation(hidden = true)
    @GET
    @Path("/")
    public Response dashboard() {
        File dashboard = new File(dashboardFolder, "index.html");
        return Response.ok(dashboard).build();
    }

    @Operation(hidden = true)
    @GET
    @Path("index.html")
    public Response dashboardIndex() {
        return dashboard();
    }

    @Operation(hidden = true)
    @GET
    @Path("sbadmin2-1.0.7/{var:.+}")
    public Response sbadmin(@PathParam("var") String var, @Context HttpServerRequest request) {
        if (cache.containsKey(var)) return Response.ok(cache.get(var)).build();
        File sbadminFolder = new File(dashboardFolder, "sbadmin2-1.0.7");
        File resource = new File(sbadminFolder, var);
        if (!resource.exists()) return Response.status(Response.Status.NOT_FOUND).build();
        cache.put(var, resource);
        return Response.ok(resource).build();
    }

    @Operation(hidden = true)
    @GET
    @Path("content/{var:.+}")
    public Response content(@PathParam("var") String var, @Context HttpServerRequest request) {
        if (cache.containsKey(var)) return Response.ok(cache.get(var)).build();
        File contentFolder = new File(dashboardFolder, "content");
        File resource = new File(contentFolder, var);
        if (!resource.exists()) return Response.status(Response.Status.NOT_FOUND).build();
        cache.put(var, resource);
        return Response.ok(resource).build();
    }

    @Operation(hidden = true)
    @GET
    @Path("assets/{var:.+}")
    public Response assets(@PathParam("var") String var, @Context HttpServerRequest request) {
        try {
            try (InputStream inputStream = getClass().getResourceAsStream("/assets/" + var)){
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String contents = reader.lines()
                        .collect(Collectors.joining(System.lineSeparator()));
                return Response.ok(contents).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e).build();
        }
    }

    @GET
    @Path("graph")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get() {
        return Templates.graph("Test analysis " + reportPath);
    }

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance graph(String title);
    }

}
