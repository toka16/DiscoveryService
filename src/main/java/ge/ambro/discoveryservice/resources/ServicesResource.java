/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.ambro.discoveryservice.resources;

import ge.ambro.discoveryservice.dto.ServiceDTO;
import ge.ambro.discoveryservice.injections.Validators;
import ge.ambro.discoveryservice.repo.ServiceRepository;
import java.util.Collection;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author tabramishvili
 */
@Path("v1/services")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("microservice")
public class ServicesResource {

    @Inject
    Validators.ServiceValidator validator;

    @Inject
    ServiceRepository repo;

    /**
     * Creates a new instance of ServicesResource
     */
    public ServicesResource() {
    }

    @GET
    public Collection<ServiceDTO> getAll() {
        return repo.getAll();
    }

    @GET
    @Path("{service_id}")
    public ServiceDTO getService(@PathParam("service_id") int id) {
        return repo.get(id);
    }

    @PUT
    @Path("{service_id}")
    public void serviceSetAlive(@PathParam("service_id") int id, String data) {
        boolean val = Boolean.valueOf(data.substring(data.indexOf(":") + 1, data.length() - 1).trim());
        repo.setAlive(id, val);
    }

    @POST
    public int addService(ServiceDTO service) {
        System.out.println("new service: "+service);
        if (!validator.test(service)) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        return repo.add(service);
    }

}
