/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.ambro.discoveryservice.security;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import ge.ambro.jerseyutils.security.Authenticator;
import ge.ambro.jerseyutils.security.impl.SimpleSecurityContext;
import ge.ambro.jerseyutils.security.impl.UsernamePrincipal;
import ge.ambro.jerseyutils.security.model.AuthenticationData;
import java.util.Objects;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.SecurityContext;

/**
 *
 * @author tabramishvili
 */
public class DiscoveryAuthenticator implements Authenticator {

    private final JWTVerifier verifier;

    public DiscoveryAuthenticator(JWTVerifier verifier) {
        this.verifier = verifier;
    }

    @Override
    public boolean supports(String type) {
        return Objects.equals(DiscoveryAuthenticationDataExtractor.AUTHENTICATION_TYPE, type);
    }

    @Override
    public SecurityContext authenticateUser(AuthenticationData token, ContainerRequestContext context) {
        DiscoveryAuthenticationData data = (DiscoveryAuthenticationData) token;
        if (data.getToken() == null) {
            return null;
        }

        return authenticateUser(data, context.getUriInfo().getAbsolutePath().getScheme());
    }

    protected SecurityContext authenticateUser(DiscoveryAuthenticationData authData, String scheme) {
        String token = authData.getToken();
        try {
            DecodedJWT jwt = verifier.verify(token);
            return new SimpleSecurityContext(new UsernamePrincipal(jwt.getClaim("name").asString()), scheme)
                    .addRoles(jwt.getClaim("roles").asArray(String.class));
        } catch (JWTVerificationException ex) {
            return null;
        }

    }

}
