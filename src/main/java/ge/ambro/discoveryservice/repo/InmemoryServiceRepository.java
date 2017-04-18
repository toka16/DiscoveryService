/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.ambro.discoveryservice.repo;

import ge.ambro.discoveryservice.dto.EventResponseDTO;
import ge.ambro.discoveryservice.dto.ServiceDTO;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.ws.rs.core.MultivaluedHashMap;

/**
 *
 * @author tabramishvili
 */
public class InmemoryServiceRepository implements ServiceRepository {

    private final Map<Integer, ServiceDTO> servicesById;
    private final MultivaluedHashMap<String, ServiceDTO> servicesByName;
    private final MultivaluedHashMap<String, EventResponseDTO> servicesByEvent;

    private long lastModifiedTime;

    public InmemoryServiceRepository() {
        servicesById = new ConcurrentHashMap<>();
        servicesByName = new MultivaluedHashMap<>();
        servicesByEvent = new MultivaluedHashMap<>();
    }

    @Override
    public Collection<ServiceDTO> getAll() {
        return servicesById.values();
    }

    @Override
    public ServiceDTO get(int id) {
        return servicesById.get(id);
    }

    @Override
    public Collection<ServiceDTO> getByName(String name) {
        return servicesByName.getOrDefault(name, Collections.emptyList());
    }

    @Override
    public synchronized int add(ServiceDTO item) {
        if (servicesById.containsKey(item.getId())) {
            return 0;
        }
        servicesById.put(item.getId(), item);
        servicesByName.add(item.getName(), item);
        if (item.getEvents() != null) {
            item.getEvents().forEach((event) -> {
                EventResponseDTO ev = new EventResponseDTO();
                ev.setPath(event.getPath());
                ev.setName(event.getName());
                ev.setBase(item.getBase());
                ev.setService(item.getName());
                ev.setServiceId(item.getId());
                servicesByEvent.add(ev.getName(), ev);
            });
        }
        lastModifiedTime = System.currentTimeMillis();
        return item.getId();
    }

    @Override
    public synchronized void remove(int id) {
        ServiceDTO item = servicesById.remove(id);
        if (item == null) {
            return;
        }
        servicesByName.get(item.getName()).remove(item);
        if (item.getEvents() != null) {
            item.getEvents().forEach((event) -> {
                servicesByEvent.getOrDefault(event.getName(), Collections.emptyList()).removeIf((ev) -> {
                    return ev.getServiceId() == id;
                });
            });
        }
        lastModifiedTime = System.currentTimeMillis();
    }

    @Override
    public long getLastModifiedTime() {
        return lastModifiedTime;
    }

    @Override
    public Collection<EventResponseDTO> getEventListeners(String address) {
        return servicesByEvent.getOrDefault(address, Collections.emptyList());
    }

}
