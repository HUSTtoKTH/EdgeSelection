package com.lwh.edgeselection.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "areas")
public class Area implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String name;
    private String description;
    private double cost_scond;


    @ManyToMany(mappedBy = "cspareas", fetch = FetchType.LAZY)
    private Set<CSP> csps = new HashSet<>();

    @ManyToMany(mappedBy = "appareas", fetch = FetchType.LAZY)
    private Set<Application> applicationss = new HashSet<>();

//    @OneToMany(mappedBy = "area", fetch = FetchType.LAZY,
//            cascade = CascadeType.ALL)
//    private Set<DetailedArea> detailedAreas;

//    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
//    @JoinTable(name = "areas_detailedareas",
//            joinColumns = {
//                    @JoinColumn(name = "area_id", referencedColumnName = "id",
//                            nullable = false, updatable = false)},
//            inverseJoinColumns = {
//                    @JoinColumn(name = "detail" +
//                            "area_id", referencedColumnName = "id",
//                            nullable = false, updatable = false)})
//    private  Set<DetailedArea> detailedAreas = new HashSet<>();

    public Area() {
    }
    public Area(String name) {
        this.name = name;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Area area = (Area) o;
        return getId() == area.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
