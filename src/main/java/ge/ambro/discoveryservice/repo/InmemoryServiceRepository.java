/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.ambro.discoveryservice.repo;

import ge.ambro.discoveryservice.dto.EventResponseDTO;
import ge.ambro.discoveryservice.dto.ResolvedTargetResponseDTO;
import ge.ambro.discoveryservice.dto.ServiceDTO;
import ge.ambro.discoveryservice.dto.TargetResponseDTO;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javax.ws.rs.core.MultivaluedHashMap;

/**
 *
 * @author tabramishvili
 */
public class InmemoryServiceRepository implements ServiceRepository {

    private final Map<Integer, ServiceDTO> servicesById;
    private final MultivaluedHashMap<String, ServiceDTO> servicesByName;
    private final MultivaluedHashMap<String, EventResponseDTO> servicesByEvent;

    private final AtomicInteger idCounter;
    private long lastModifiedTime;

    public InmemoryServiceRepository() {
        servicesById = new ConcurrentHashMap<>();
        servicesByName = new MultivaluedHashMap<>();
        servicesByEvent = new MultivaluedHashMap<>();
        idCounter = new AtomicInteger();
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
        return servicesByName.getOrDefault(name, Collections.emptyList()).stream()
                .filter(ServiceDTO::isAlive)
                .collect(Collectors.toList());
    }

    @Override
    public ResolvedTargetResponseDTO getByAddress(String address) {
        ResolvedTargetResponseDTO response = new ResolvedTargetResponseDTO();
        response.setTargets(getTargetResponses(address));
        response.setResolves(resolve(response.getTargets(), new HashMap<>()));
        return response;
    }

    protected Map<String, Collection<TargetResponseDTO>> resolve(
            Collection<TargetResponseDTO> targets,
            Map<String, Collection<TargetResponseDTO>> resolves) {
        if (targets == null) {
            return null;
        }
        targets.forEach((target) -> {
            if (target.getDependencies() == null) {
                return;
            }
            target.getDependencies().forEach((dependency) -> {
                if (!resolves.containsKey(dependency.getAddress())) {
                    Collection<TargetResponseDTO> tmp = getTargetResponses(dependency.getAddress());
                    resolves.put(dependency.getAddress(), tmp);
                    resolve(tmp, resolves);
                }
            });
        });
        return resolves;
    }

    protected Collection<TargetResponseDTO> getTargetResponses(String address) {
        String service = address.split(":")[0];
        String name = address.split(":")[1];
        List<TargetResponseDTO> response = new LinkedList<>();
        getByName(service).forEach((s) -> {
            if (s.getTargets() != null) {
                s.getTargets().forEach((target) -> {
                    if (name.equals(target.getName())) {
                        TargetResponseDTO temp = new TargetResponseDTO(target);
                        temp.setAddress(service + ":" + name);
                        temp.setBase(s.getBase());
                        temp.setService(s.getName());
                        temp.setServiceId(s.getId());
                        response.add(temp);
                    }
                });
            }
        });
        return response;
    }

    @Override
    public synchronized int add(ServiceDTO item) {
        servicesByName.getOrDefault(item.getName(), Arrays.asList())
                .stream()
                .filter((s) -> {
                    return s.getName().equals(item.getName()) && s.getBase().equals(item.getBase());
                }).collect(Collectors.toList())
                .forEach((old) -> {
                    remove(old.getId());
                });
        item.setId(idCounter.getAndIncrement());
        item.setAlive(true);
        servicesById.put(item.getId(), item);
        servicesByName.add(item.getName(), item);
        if (item.getEvents() != null) {
            item.getEvents().forEach((event) -> {
                EventResponseDTO ev = new EventResponseDTO();
                ev.setName(event.getName());
                ev.setPath(event.getPath());
                ev.setMethod(event.getMethod());
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
                servicesByEvent.get(event.getName()).removeIf((ev) -> {
                    return ev.getServiceId() == id;
                });
            });
        }
        lastModifiedTime = System.currentTimeMillis();
    }

    @Override
    public synchronized void setAlive(int id, boolean value) {
        get(id).setAlive(value);
        lastModifiedTime = System.currentTimeMillis();
    }

    @Override
    public long getLastModifiedTime() {
        return lastModifiedTime;
    }

    @Override
    public Collection<EventResponseDTO> getEventListeners(String address) {
        return servicesByEvent.get(address).stream().filter((ev) -> {
            return get(ev.getServiceId()).isAlive();
        }).collect(Collectors.toList());
    }

}
