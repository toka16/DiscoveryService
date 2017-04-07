/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.ambro.discoveryservice.injections;

import ge.ambro.discoveryservice.repo.InmemoryServiceRepository;
import ge.ambro.discoveryservice.repo.ServiceRepository;
import ge.ambro.jerseyutils.inject.Component;
import ge.ambro.jerseyutils.inject.ComponentFactory;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.glassfish.hk2.api.ServiceLocator;

/**
 *
 * @author tabramishvili
 */
@ComponentFactory
public class Repositories {

    @Inject
    ServiceLocator locator;

    @Component
    @Singleton
    public ServiceRepository getServiceRepository() {
        return locator.createAndInitialize(InmemoryServiceRepository.class);
    }

}
