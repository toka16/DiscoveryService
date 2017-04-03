/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.ambro.discoveryservice.resources;

import ge.ambro.discoveryservice.dto.EventResponseDTO;
import ge.ambro.discoveryservice.repo.ServiceRepository;
import java.util.Collection;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author tabramishvili
 */
@Path("v1/event-listeners")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EventListenersResource {

    @Inject
    ServiceRepository repo;

    @GET
    public Collection<EventResponseDTO> getEventListeners(
            @QueryParam("name") String name,
            @QueryParam("ts") long ts) {
        if (repo.getLastModifiedTime() > ts) {
            return repo.getEventListeners(name);
        } else {
            throw new WebApplicationException(Response.Status.NOT_MODIFIED);
        }

    }
}
