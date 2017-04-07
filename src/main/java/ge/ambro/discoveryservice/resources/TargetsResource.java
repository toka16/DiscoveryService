/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.ambro.discoveryservice.resources;

import ge.ambro.discoveryservice.dto.ResolvedTargetResponseDTO;
import ge.ambro.discoveryservice.repo.ServiceRepository;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author tabramishvili
 */
@Path("v1/targets")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("microservice")
public class TargetsResource {

    @Inject
    ServiceRepository repo;

    @GET
    public ResolvedTargetResponseDTO findTargetsByAddress(
            @QueryParam("address") String address,
            @DefaultValue("0") @QueryParam("ts") long ts) {
        if (repo.getLastModifiedTime() > ts) {
            return repo.getByAddress(address);
        } else {
            throw new WebApplicationException(Response.Status.NOT_MODIFIED);
        }
    }
}
