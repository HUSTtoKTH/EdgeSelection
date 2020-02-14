package com.lwh.edgeselection.domain;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "latencys")
@IdClass(LatencyId.class)
public class Latency implements Serializable {
    @Id
    @Column(name = "area_id")
    private Long areaId;
    @Id
    @Column(name = "csp_id")
    private Long cspId;
    @Id
    @Column(name = "service_id")
    private Long serviceId;
    private String csp_name;
    private String area_name;
    private String service_name;

    private double up_bound;
    private double low_bound;
    private double delay;

    public Latency() {
    }

    public Latency(Long areaId, Long cspId, Long serviceId, double delay) {
        this.areaId = areaId;
        this.cspId = cspId;
        this.serviceId = serviceId;
        this.delay = delay;
    }


    public Latency(Long areaId, Long cspId, Long serviceId) {
        this.areaId = areaId;
        this.cspId = cspId;
        this.serviceId = serviceId;
    }

    public Latency(Long areaId, Long cspId, Long serviceId, String area_name,String csp_name, String service_name) {
        this.areaId = areaId;
        this.cspId = cspId;
        this.serviceId = serviceId;
        this.area_name = area_name;
        this.csp_name = csp_name;
        this.service_name = service_name;
    }

    public double getUp_bound() {
        return up_bound;
    }

    public void setUp_bound(double up_bound) {
        this.up_bound = up_bound;
    }

    public double getLow_bound() {
        return low_bound;
    }

    public void setLow_bound(double low_bound) {
        this.low_bound = low_bound;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public Long getCspId() {
        return cspId;
    }

    public void setCspId(Long cspId) {
        this.cspId = cspId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public double getDelay() {
        return delay;
    }

    public void setDelay(double latency) {
        this.delay = latency;
    }

    public String getCsp_name() {
        return csp_name;
    }

    public void setCsp_name(String csp_name) {
        this.csp_name = csp_name;
    }

    public String getArea_name() {
        return area_name;
    }

    public void setArea_name(String area_name) {
        this.area_name = area_name;
    }

    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }
}
