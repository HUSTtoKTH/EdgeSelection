package com.lwh.edgeselection.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class LatencyId implements Serializable {

    private Long areaId;

    private Long cspId;

    private Long serviceId;

    public LatencyId() {
    }

    public LatencyId(Long areaId, Long cspId, Long serviceId) {
        this.areaId = areaId;
        this.cspId = cspId;
        this.serviceId = serviceId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LatencyId that = (LatencyId) o;
        return areaId.equals(that.areaId) &&
                cspId.equals(that.cspId) &&
                serviceId.equals(that.serviceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(areaId, cspId, serviceId);
    }
}
