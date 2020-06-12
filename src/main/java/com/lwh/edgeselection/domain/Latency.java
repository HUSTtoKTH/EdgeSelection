package com.lwh.edgeselection.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "latencys")
@IdClass(LatencyId.class)
@Setter
@Getter
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

    public Latency(Long areaId, Long cspId, Long serviceId, double delay, double low_bound, double up_bound) {
        this.areaId = areaId;
        this.cspId = cspId;
        this.serviceId = serviceId;
        this.delay = delay;
        this.low_bound = low_bound;
        this.up_bound = up_bound;
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

}
