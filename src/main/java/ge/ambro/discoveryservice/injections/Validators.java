/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.ambro.discoveryservice.injections;

import ge.ambro.discoveryservice.dto.ServiceDTO;
import ge.ambro.jerseyutils.inject.Component;
import ge.ambro.jerseyutils.inject.ComponentFactory;
import java.util.function.Predicate;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 *
 * @author tabramishvili
 */
@ComponentFactory
public class Validators {

    @Component
    @Singleton
    public ServiceValidator getServiceValidator() {
        return new ServiceValidator();
    }

    static boolean stringNotNullOrEmpty(String str) {
        return str != null && str.length() > 0;
    }

    public static class ServiceValidator implements Predicate<ServiceDTO> {

        @Override
        public boolean test(ServiceDTO t) {
            return stringNotNullOrEmpty(t.getBase())
                    && stringNotNullOrEmpty(t.getName());
        }
    }
}
