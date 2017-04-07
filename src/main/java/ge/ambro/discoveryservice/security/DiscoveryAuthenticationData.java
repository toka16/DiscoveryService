/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.ambro.discoveryservice.security;

import ge.ambro.jerseyutils.security.model.AuthenticationData;

/**
 *
 * @author tabramishvili
 */
public class DiscoveryAuthenticationData implements AuthenticationData {

    private final String type;
    private final String token;

    public DiscoveryAuthenticationData(String type, String token) {
        this.type = type;
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "BearerAuthenticationData{" + "type=" + type + ", token=" + token + '}';
    }

}
