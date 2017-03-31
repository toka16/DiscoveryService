/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.ambro.discoveryservice.resources;

import ge.ambro.discoveryservice.injections.Repositories;
import ge.ambro.discoveryservice.injections.Validators;
import ge.ambro.jerseyutils.inject.ComponentInjectorFeature;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.server.ResourceConfig;

/**
 *
 * @author tabramishvili
 */
@javax.ws.rs.ApplicationPath("api")
@Produces(MediaType.APPLICATION_JSON)
public class ApplicationConfig extends ResourceConfig {

    public ApplicationConfig() throws NoSuchMethodException {
        packages(this.getClass().getPackage().getName());

        registerResources();
    }

    private void registerResources() {
        register(ComponentInjectorFeature.class);
        
        register(Validators.class);
        register(Repositories.class);
    }
}
