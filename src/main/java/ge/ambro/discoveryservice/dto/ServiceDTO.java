/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.ambro.discoveryservice.dto;

import java.util.List;

/**
 *
 * @author tabramishvili
 */
public class ServiceDTO {

    private int id;
    private String name;
    private String serviceDescrip;
    private String base;
    private List<TargetDTO> targets;
    private List<EventDTO> events;
    private boolean alive;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServiceDescrip() {
        return serviceDescrip;
    }

    public void setServiceDescrip(String serviceDescrip) {
        this.serviceDescrip = serviceDescrip;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public List<TargetDTO> getTargets() {
        return targets;
    }

    public void setTargets(List<TargetDTO> targets) {
        this.targets = targets;
    }

    public List<EventDTO> getEvent() {
        return events;
    }

    public void setEvent(List<EventDTO> event) {
        this.events = event;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    @Override
    public String toString() {
        return "ServiceDTO{" + "id=" + id + ", name=" + name + ", serviceDescrip=" + serviceDescrip + ", base=" + base + ", targets=" + targets + ", event=" + events + ", alive=" + alive + '}';
    }

}
