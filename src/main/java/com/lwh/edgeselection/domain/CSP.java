package com.lwh.edgeselection.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "csps")
public class CSP implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String name;
    private String description;
    private double cost_scond;

    @ManyToMany(mappedBy = "csps", fetch = FetchType.LAZY)
    private Set<EIS> services = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "csps_areas",
            joinColumns = {
                    @JoinColumn(name = "csp_id", referencedColumnName = "id",
                            nullable = false, updatable = false)},
            inverseJoinColumns = {
                    @JoinColumn(name = "area_id", referencedColumnName = "id",
                            nullable = false, updatable = false)})
    private Set<Area> areas = new HashSet<>();

    public CSP() {
    }

    public CSP(double cost) {
        this.cost_scond = cost;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<EIS> getServices() {
        return services;
    }

    public void setServices(Set<EIS> services) {
        this.services = services;
    }

    public Set<Area> getAreas() {
        return areas;
    }

    public void setAreas(Set<Area> areas) {
        this.areas = areas;
    }

    public double getCost_scond() {
        return cost_scond;
    }

    public void setCost_scond(double cost_scond) {
        this.cost_scond = cost_scond;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CSP eis = (CSP) o;
        return getId() == eis.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
