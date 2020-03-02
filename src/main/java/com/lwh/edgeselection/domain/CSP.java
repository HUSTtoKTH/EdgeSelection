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
    private Set<Area> cspareas = new HashSet<>();

    public CSP() {
    }

    public CSP(double cost) {
        this.cost_scond = cost;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CSP csp = (CSP) o;
        return getId() == csp.getId();
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
