/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.ambro.discoveryservice.resources;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import ge.ambro.discoveryservice.injections.Repositories;
import ge.ambro.discoveryservice.injections.Validators;
import ge.ambro.discoveryservice.security.DiscoveryAuthenticationDataExtractor;
import ge.ambro.discoveryservice.security.DiscoveryAuthenticator;
import ge.ambro.jerseyutils.inject.ComponentInjectorFeature;
import ge.ambro.jerseyutils.security.BaseSecurityFeature;
import ge.ambro.jerseyutils.security.DynamicSecurityFeature;
import ge.ambro.jerseyutils.security.JerseyUtilsSecurityLogger;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
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

        // security
        try {
            Properties props = readProperties("security.properties");
            register(new DiscoveryAuthenticator(
                    JWT.require(Algorithm.HMAC256(props.getProperty("secret", "")))
                            .withIssuer(props.getProperty("issuer", ""))
                            .build())
            );
            register(DiscoveryAuthenticationDataExtractor.class);
        } catch (IllegalArgumentException | UnsupportedEncodingException ex) {
            Logger.getLogger(ApplicationConfig.class.getName()).log(Level.SEVERE, null, ex);
        }

        register(BaseSecurityFeature.class);
        register(DynamicSecurityFeature.class);
    }

    private Properties readProperties(String name) {
        Properties props = new Properties();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(name)) {
            props.load(inputStream);
        } catch (IOException ex) {
            Logger.getLogger(ApplicationConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
        return props;
    }
}
