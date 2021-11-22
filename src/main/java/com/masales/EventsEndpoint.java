package com.masales;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameters;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/events")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Events REST API", description = "A list of information about test events generated")
public class EventsEndpoint {

    @Inject
    MongodbServices mongodbServices;

    @Inject
    EventService eventService;

    @Operation(summary = "List all events generated during the test")
    @GET
    public Response list() {
        return Response.ok(new ResponseList(mongodbServices.list())).build();
    }

    @Operation(operationId = "Count Events",summary = "Count the number of events generated during test",description = "The total number of events generated")
    @GET
    @Path("/count")
    public Long count() {
        return mongodbServices.count();
    }

    @Operation(summary = "List the events of required type")
    @Parameters({
            @Parameter(name = "type",description = "The type of event",in = ParameterIn.PATH,schema = @Schema(enumeration = {"ontRangingEvent","ObjectDuplicated","objectCreated"}))
    })
    @GET
    @Path("/type/{type}")
    public Response findByEventType(@PathParam("type") String type) {
        List<Event> events = mongodbServices.listByType(type);
        ResponseList resp = new ResponseList(events);
        return Response.ok(resp).build();
    }

    @Operation(summary = "List the events of type ontRangingEvent")
    @GET
    @Path("/ontRangingEvent")
    public Response getEventsOfTypeOntRangingEvent() {
        return Response.ok(new ResponseList(mongodbServices.listEventsOfTypeOntRangingEvent())).build();
    }

    @Operation(summary = "List the events of type objectDuplicated")
    @GET
    @Path("/objectDuplicated")
    public Response getEventsOfTypeObjectDuplicated() {
        return Response.ok(new ResponseList(mongodbServices.listEventsOfTypeObjectDuplicated())).build();
    }

    @Operation(summary = "List the events of type objectCreated")
    @GET
    @Path("/objectCreated")
    public Response getEventsOfTypeObjectCreated() {
        return Response.ok(new ResponseList(mongodbServices.listEventsOfTypeObjectCreated())).build();
    }

    @Operation(summary = "Get the notification event by notification id")
    @GET
    @Path("/id/{id}")
    public Event getByNotificationId(@PathParam("id") String id) {
        return mongodbServices.findByNotificationId(id);
    }

    @Operation(summary = "Number of events generated during the test which is not present in POD repository")
    @GET
    @Path("/pod/notfound/count")
    public Response getCountEventsNotFoundInPod() {
        return Response.ok(mongodbServices.listEventsNotFoundInPOD().size()).build();
    }

    @Operation(summary = "List events generated during the test which is not present in POD repository")
    @GET
    @Path("/pod/notfound")
    public Response getEventsNotFoundInPod() {
        return Response.ok(new ResponseList(mongodbServices.listEventsNotFoundInPOD())).build();
    }

    @Operation(summary = "Number of events generated during the test which is not present in EMS repository")
    @GET
    @Path("/ems/notfound/count")
    public Response getCountEventsNotFoundInEms() {
        return Response.ok(mongodbServices.listEventsNotFoundInEMS().size()).build();
    }

    @Operation(summary = "List events generated during the test which is not present in EMS repository")
    @GET
    @Path("/ems/notfound")
    public Response getEventsNotFoundInEms() {
        return Response.ok(new ResponseList(mongodbServices.listEventsNotFoundInEMS())).build();
    }

    @Operation(summary = "Lists events generated during test that are present in both the POD repository and the EMS repository")
    @GET
    @Path("/complete")
    public Response getEventsFoundInEmsAndPOD() {
        return Response.ok(new ResponseList(mongodbServices.listEventsFoundOnPODAndEMS())).build();
    }

    @Operation(summary = "Number of events generated during the test that are present in both the POD repository and the EMS repository")
    @GET
    @Path("/complete/count")
    public Response getCountEventsFoundInEmsAndPOD() {
        return Response.ok(mongodbServices.countEventsFoundOnPODAndEMS()).build();
    }

    @Operation(summary = "Get the first and the last one event generated during the Test and published on EMS cluster")
    @GET
    @Path("/complete/firstAndLast")
    public Response getFirstAndLastEventCompleted(){
        return Response.ok(mongodbServices.findFirstAndLastEventCompleted()).build();
    }

    @Operation(summary = "Get the first and the last one event generated during the Test")
    @GET
    @Path("/created/firstAndLast")
    public Response getFirstAndLastEventGenerated(){
        return Response.ok(mongodbServices.findFirstAndLastEventGenerated()).build();
    }

    @Operation(summary = "Generate data to plot difference between creation time and publish on EMS")
    @GET
    @Path("/plot/general")
    public Response plotDifference() {
        return Response.ok(new ResponseData(eventService.flop())).build();
    }

    @Operation(summary = "Generate data to plot difference between creation time and publish on EMS grouped by a interval")
    @GET
    @Path("/plot/grouped/{interval}")
    public Response plotGroupedDifference(@PathParam("interval") Integer interval) {
        return Response.ok(new ResponseData(eventService.flopWithGroupIntervalSeconds(interval))).build();
    }
}
