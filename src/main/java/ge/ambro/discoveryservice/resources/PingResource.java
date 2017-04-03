/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.ambro.discoveryservice.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * @author tabramishvili
 */
@Path("ping")
@Consumes(MediaType.WILDCARD)
public class PingResource {

    @GET
    public String ping() {
        return "pong";
    }
}
