package com.lwh.edgeselection.domain;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "areas")
public class Area {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String name;


    @ManyToMany(mappedBy = "areas", fetch = FetchType.LAZY)
    private Set<CSP> csps = new HashSet<>();

    @OneToMany(mappedBy = "area", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private Set<DetailedArea> detailedAreas;

    public Area() {
    }
    public Area(String name) {
        this.name = name;
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

    public Set<CSP> getCsps() {
        return csps;
    }

    public void setCsps(Set<CSP> csps) {
        this.csps = csps;
    }

    public Set<DetailedArea> getDetailedAreas() {
        return detailedAreas;
    }

    public void setDetailedAreas(Set<DetailedArea> detailedAreas) {
        this.detailedAreas = detailedAreas;
    }
}
