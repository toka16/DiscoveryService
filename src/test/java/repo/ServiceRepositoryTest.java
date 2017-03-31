/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package repo;

import ge.ambro.discoveryservice.dto.ServiceDTO;
import ge.ambro.discoveryservice.repo.InmemoryServiceRepository;
import ge.ambro.discoveryservice.repo.ServiceRepository;
import java.util.Collection;
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

    private ServiceDTO createServiceDTO(String name) {
        ServiceDTO temp = new ServiceDTO();
        temp.setName(name);
        temp.setBase("http://localhost/api/" + name);
        temp.setServiceDescrip("local service: " + name);
        return temp;
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
        ServiceDTO temp2 = createServiceDTO("other_service");
        int i = repo.add(temp);
        int i2 = repo.add(temp2);

        Collection<ServiceDTO> response = repo.getByName(temp.getName());
        Assert.assertEquals("Should be two items", 2, response.size());

        ServiceDTO test = (ServiceDTO) response.toArray()[0];
        ServiceDTO test2 = (ServiceDTO) response.toArray()[1];
        Assert.assertEquals("Should return the same object on the given name", temp, test);
        Assert.assertEquals("Should return the same object on the given name", temp2, test2);
    }

}
