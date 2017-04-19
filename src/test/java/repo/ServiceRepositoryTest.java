/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package repo;

import ge.ambro.discoveryservice.dto.EventDTO;
import ge.ambro.discoveryservice.dto.EventResponseDTO;
import ge.ambro.discoveryservice.dto.ServiceDTO;
import ge.ambro.discoveryservice.repo.InmemoryServiceRepository;
import ge.ambro.discoveryservice.repo.ServiceRepository;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author tabramishvili
 */
public class ServiceRepositoryTest {

    ServiceRepository repo;
    ServiceDTO tempServiceDTO;
    int id = 1;

    private ServiceDTO createServiceDTO(String name, String path) {
        ServiceDTO temp = new ServiceDTO();
        temp.setId(id++);
        temp.setName(name);
        temp.setBase("http://localhost/api/" + path);
        temp.setServiceDescrip("local service: " + path);
        return temp;
    }

    private ServiceDTO createServiceDTO(String name) {
        return createServiceDTO(name, name);
    }

    private EventDTO createEventDTO(String name) {
        EventDTO event = new EventDTO();
        event.setName(name);
        event.setPath("path/" + name);
        return event;
    }

    @Before
    public void before() {
        repo = new InmemoryServiceRepository();
        tempServiceDTO = createServiceDTO("service");
    }

    @Test
    public void testEmptyRepo() {
        Assert.assertTrue("Repo should be empty", repo.getByName("temp").isEmpty());
    }

    @Test
    public void testAddAndGetById() {
        int index = repo.add(tempServiceDTO);

        Assert.assertEquals("Should return the same object on the given id", tempServiceDTO, repo.get(index));
    }

    @Test
    public void testAddAndGetByName() {
        repo.add(tempServiceDTO);

        Collection<ServiceDTO> response = repo.getByName(tempServiceDTO.getName());
        Assert.assertEquals("Should be only one item", 1, response.size());

        ServiceDTO temp = (ServiceDTO) response.toArray()[0];
        Assert.assertEquals("Should return the same object on the given name", tempServiceDTO, temp);
    }

    @Test
    public void testAddTwoAndGetById() {
        ServiceDTO temp = createServiceDTO("other_service");
        int i1 = repo.add(tempServiceDTO);
        int i2 = repo.add(temp);

        Assert.assertEquals("Should return the same object on the given id", tempServiceDTO, repo.get(i1));
        Assert.assertEquals("Should return the same object on the given id", temp, repo.get(i2));

        ServiceDTO temp2 = createServiceDTO("other_service");
        int i3 = repo.add(temp2);

        Assert.assertEquals("Should return the same object on the given id", temp2, repo.get(i3));
    }

    @Test
    public void testAddTwoAndGetByName() {
        ServiceDTO temp = createServiceDTO("other_service");
        ServiceDTO temp2 = createServiceDTO("other_service", "other_path");
        int i = repo.add(temp);
        int i2 = repo.add(temp2);

        Collection<ServiceDTO> response = repo.getByName(temp.getName());
        Assert.assertEquals("Should be two items", 2, response.size());

        ServiceDTO test = (ServiceDTO) response.toArray()[0];
        ServiceDTO test2 = (ServiceDTO) response.toArray()[1];
        Assert.assertEquals("Should return the same object on the given name", temp, test);
        Assert.assertEquals("Should return the same object on the given name", temp2, test2);
    }

    @Test
    public void checkSingleEventListener() {
        ServiceDTO service = createServiceDTO("service");
        EventDTO event = createEventDTO("event");
        service.setEvents(Arrays.asList(event));
        repo.add(service);

        Collection<EventResponseDTO> listeners = repo.getEventListeners("event");
        Assert.assertEquals("Check number of listeners", 1, listeners.size());

        EventResponseDTO ev = (EventResponseDTO) listeners.toArray()[0];
        Assert.assertEquals(event.getPath(), ev.getPath());
        Assert.assertEquals(service.getId(), ev.getServiceId());
    }

    @Test
    public void checkMultipleEventListeners() {
        ServiceDTO service = createServiceDTO("service");
        EventDTO event = createEventDTO("event");
        EventDTO event2 = createEventDTO("event2");
        service.setEvents(Arrays.asList(event, event2));
        repo.add(service);

        ServiceDTO service2 = createServiceDTO("service2");
        EventDTO event21 = createEventDTO("event");
        service2.setEvents(Arrays.asList(event21));
        repo.add(service2);

        Collection<EventResponseDTO> listeners = repo.getEventListeners("event");
        Assert.assertEquals("Check number of listeners", 2, listeners.size());

        EventResponseDTO ev1 = (EventResponseDTO) listeners.toArray()[0];
        EventResponseDTO ev2 = (EventResponseDTO) listeners.toArray()[1];
        Assert.assertEquals(event.getPath(), ev1.getPath());
        Assert.assertEquals(service.getId(), ev1.getServiceId());

        Assert.assertEquals(event21.getPath(), ev2.getPath());
        Assert.assertEquals(service2.getId(), ev2.getServiceId());
    }

    @Test
    public void testRemove() {
        ServiceDTO service = createServiceDTO("service");
        EventDTO event = createEventDTO("event");
        service.setEvents(Arrays.asList(event));
        int index = repo.add(service);

        repo.remove(index);

        Collection<ServiceDTO> response = repo.getByName("service");
        Assert.assertTrue("Has no targets", response.isEmpty());

        Collection<EventResponseDTO> listeners = repo.getEventListeners("event");
        Assert.assertTrue("Has no listeners", listeners.isEmpty());
    }

}
