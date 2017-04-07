/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package repo;

import ge.ambro.discoveryservice.dto.DependencyDTO;
import ge.ambro.discoveryservice.dto.EventDTO;
import ge.ambro.discoveryservice.dto.EventResponseDTO;
import ge.ambro.discoveryservice.dto.HttpMethod;
import ge.ambro.discoveryservice.dto.ResolvedTargetResponseDTO;
import ge.ambro.discoveryservice.dto.ServiceDTO;
import ge.ambro.discoveryservice.dto.TargetDTO;
import ge.ambro.discoveryservice.dto.TargetResponseDTO;
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

    private ServiceDTO createServiceDTO(String name, String path) {
        ServiceDTO temp = new ServiceDTO();
        temp.setName(name);
        temp.setBase("http://localhost/api/" + path);
        temp.setServiceDescrip("local service: " + path);
        return temp;
    }

    private ServiceDTO createServiceDTO(String name) {
        return createServiceDTO(name, name);
    }

    private TargetDTO createTargetDTO(String name, String path) {
        TargetDTO target = new TargetDTO();
        target.setName(name);
        target.setMethod(HttpMethod.GET);
        target.setPath(path);
        return target;
    }

    private DependencyDTO createDependencyDTO(String address, int priority) {
        DependencyDTO dep = new DependencyDTO();
        dep.setAddress(address);
        dep.setPriority(priority);
        return dep;
    }

    private EventDTO createEventDTO(String name) {
        EventDTO event = new EventDTO();
        event.setName(name);
        event.setMethod(HttpMethod.GET);
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
    public void testSearchByAddressWithOneTargetWithoutDependencies() {
        ServiceDTO service = createServiceDTO("service");
        TargetDTO target = createTargetDTO("target", "/path");
        service.setTargets(Arrays.asList(target));
        repo.add(service);

        ResolvedTargetResponseDTO response = repo.getByAddress("service:target");
        Assert.assertEquals("Number of targets must be 1", 1, response.getTargets().size());

        TargetResponseDTO resTarget = (TargetResponseDTO) response.getTargets().toArray()[0];
        checkTargetResponse(service, target, resTarget);
    }

    @Test
    public void testSearchByAddressWithMultipleTargetsWithoutDependencies() {
        ServiceDTO service = createServiceDTO("service");
        TargetDTO target = createTargetDTO("target", "path");
        TargetDTO target2 = createTargetDTO("target", "path2");
        service.setTargets(Arrays.asList(target, target2));
        repo.add(service);

        ResolvedTargetResponseDTO response = repo.getByAddress("service:target");
        Assert.assertEquals("Check number of targets", service.getTargets().size(), response.getTargets().size());

        TargetResponseDTO resTarget = (TargetResponseDTO) response.getTargets().toArray()[0];
        TargetResponseDTO resTarget2 = (TargetResponseDTO) response.getTargets().toArray()[1];

        checkTargetResponse(service, target, resTarget);
        checkTargetResponse(service, target2, resTarget2);
    }

    @Test
    public void testSearchByAddressWithMultipleTargetsWithDependencies() {
        ServiceDTO service = createServiceDTO("service");
        TargetDTO target = createTargetDTO("target", "path");
        service.setTargets(Arrays.asList(target));
        repo.add(service);

        ServiceDTO service2 = createServiceDTO("service2");
        TargetDTO target2 = createTargetDTO("target2", "path2");
        DependencyDTO dep = createDependencyDTO("service:target", 5);
        target2.setDependencies(Arrays.asList(dep));
        service2.setTargets(Arrays.asList(target2));
        repo.add(service2);

        ResolvedTargetResponseDTO response = repo.getByAddress("service2:target2");
        Assert.assertEquals("Check number of targets", service.getTargets().size(), response.getTargets().size());

        Map<String, Collection<TargetResponseDTO>> resolves = response.getResolves();
        Assert.assertEquals("Check number of resolves", 1, resolves.size());

        Collection<TargetResponseDTO> resTargetColl = resolves.get("service:target");
        Assert.assertEquals(1, resTargetColl.size());
        TargetResponseDTO resTarget = (TargetResponseDTO) resTargetColl.toArray()[0];

        checkTargetResponse(service, target, resTarget);
    }

    @Test
    public void testSearchByAddressWithMultipleServicesMultipleTargetsMultipleDependencies() {
        ServiceDTO service1 = createServiceDTO("service1");
        TargetDTO target11 = createTargetDTO("target11", "path11");
        TargetDTO target12 = createTargetDTO("target", "path");
        service1.setTargets(Arrays.asList(target11, target12));
        repo.add(service1);

        ServiceDTO service2 = createServiceDTO("service2");
        TargetDTO target21 = createTargetDTO("target", "other/path");
        DependencyDTO dep21 = createDependencyDTO("service1:target11", 10);
        target21.setDependencies(Arrays.asList(dep21));
        TargetDTO target22 = createTargetDTO("target22", "path22");
        DependencyDTO dep221 = createDependencyDTO("service1:target", 10);
        DependencyDTO dep222 = createDependencyDTO("service2:target", 5);
        target22.setDependencies(Arrays.asList(dep221, dep222));
        service2.setTargets(Arrays.asList(target21, target22));
        repo.add(service2);

        ResolvedTargetResponseDTO response;
        response = repo.getByAddress("service1:target");
        Assert.assertTrue("Target has no dependencies", response.getResolves().isEmpty());

        response = repo.getByAddress("service2:target");
        Assert.assertEquals("Target has 1 dependencies", 1, response.getResolves().size());

        response = repo.getByAddress("service2:target22");
        Assert.assertEquals("Target has 3 dependencies", 3, response.getResolves().size());
        Assert.assertTrue("Taregt depends on service1:target", response.getResolves().containsKey("service1:target"));
        Assert.assertTrue("Taregt depends on service2:target", response.getResolves().containsKey("service2:target"));
        Assert.assertTrue("Taregt transparently depends on service1:target11", response.getResolves().containsKey("service1:target11"));
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
    public void testSearchByUnknownAddress() {
        ResolvedTargetResponseDTO response = repo.getByAddress("some:address");
        Assert.assertTrue("Has no targets", response.getTargets().isEmpty());
        Assert.assertTrue("Has no dependencies", response.getResolves().isEmpty());
    }

    @Test
    public void testRemove() {
        ServiceDTO service = createServiceDTO("service");
        TargetDTO target = createTargetDTO("target", "path");
        service.setTargets(Arrays.asList(target));
        EventDTO event = createEventDTO("event");
        service.setEvents(Arrays.asList(event));
        int index = repo.add(service);

        repo.remove(index);

        ResolvedTargetResponseDTO response = repo.getByAddress("service:target");
        Assert.assertTrue("Has no targets", response.getTargets().isEmpty());
        Assert.assertTrue("Has no dependencies", response.getResolves().isEmpty());

        Collection<EventResponseDTO> listeners = repo.getEventListeners("event");
        Assert.assertTrue("Has no listeners", listeners.isEmpty());
    }

    @Test
    public void checkAliveServices() {
        ServiceDTO service = createServiceDTO("service");
        TargetDTO target = createTargetDTO("target", "path");
        service.setTargets(Arrays.asList(target));
        EventDTO event = createEventDTO("event");
        service.setEvents(Arrays.asList(event));
        int index = repo.add(service);

        repo.setAlive(index, false);

        ResolvedTargetResponseDTO response = repo.getByAddress("service:target");
        Assert.assertTrue("Has no targets", response.getTargets().isEmpty());
        Assert.assertTrue("Has no dependencies", response.getResolves().isEmpty());

        Collection<EventResponseDTO> listeners = repo.getEventListeners("event");
        Assert.assertTrue("Has no listeners", listeners.isEmpty());
    }

    @Test
    public void checkMixedAliveServices() {
        ServiceDTO service = createServiceDTO("service");
        TargetDTO target = createTargetDTO("target", "path");
        service.setTargets(Arrays.asList(target));
        EventDTO event = createEventDTO("event");
        service.setEvents(Arrays.asList(event));
        int index = repo.add(service);

        ServiceDTO service2 = createServiceDTO("service2");
        TargetDTO target2 = createTargetDTO("target2", "path");
        service2.setTargets(Arrays.asList(target2));
        EventDTO event2 = createEventDTO("event2");
        service2.setEvents(Arrays.asList(event2));
        int index2 = repo.add(service2);

        repo.setAlive(index, false);

        ResolvedTargetResponseDTO response = repo.getByAddress("service:target");
        Assert.assertTrue("Has no targets", response.getTargets().isEmpty());
        Assert.assertTrue("Has no dependencies", response.getResolves().isEmpty());

        Collection<EventResponseDTO> listeners = repo.getEventListeners("event");
        Assert.assertTrue("Has no listeners", listeners.isEmpty());

        Assert.assertEquals("Should have one target", 1, repo.getByAddress("service2:target2").getTargets().size());
        Assert.assertEquals("Should have one listener", 1, repo.getEventListeners("event2").size());
    }

    private void checkTargetResponse(ServiceDTO service, TargetDTO target, TargetResponseDTO resTarget) {
        Assert.assertEquals(target.getName(), resTarget.getName());
        Assert.assertEquals(target.getMethod(), resTarget.getMethod());
        Assert.assertEquals(service.getBase(), resTarget.getBase());
        Assert.assertEquals(service.getName(), resTarget.getService());
        Assert.assertEquals(service.getId(), resTarget.getServiceId());
    }

}
