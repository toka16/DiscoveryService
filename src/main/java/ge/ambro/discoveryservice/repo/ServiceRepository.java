/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.ambro.discoveryservice.repo;

import ge.ambro.discoveryservice.dto.EventResponseDTO;
import ge.ambro.discoveryservice.dto.ServiceDTO;
import java.util.Collection;

/**
 *
 * @author tabramishvili
 * @param <T>
 */
public interface ServiceRepository {

    Collection<ServiceDTO> getAll();

    ServiceDTO get(int id);

    Collection<ServiceDTO> getByName(String name);

    Collection<EventResponseDTO> getEventListeners(String address);

    int add(ServiceDTO item);

    void remove(int id);

    long getLastModifiedTime();
}
