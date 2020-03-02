package com.lwh.edgeselection.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "services")
@Getter
@Setter
public class EIS implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private double capacity;
    private int instance_num;
    private int cluster_num;
    private double cost_second;
//    @Enumerated(EnumType.STRING)

    private double reliability;
//    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "capability_id", referencedColumnName = "id")
//    private Capability capability;

    private int num_cpus;
    private double bandwidth;
    private double cpu_frequency;
    private double disk_size;
    private double mem_size;
    private String gpu;
    private String fpga;
    private String special;
    private String name;


    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "services_csps",
            joinColumns = {
                    @JoinColumn(name = "service_id", referencedColumnName = "id",
                            nullable = false, updatable = false)},
            inverseJoinColumns = {
                    @JoinColumn(name = "csp_id", referencedColumnName = "id",
                            nullable = false, updatable = false)})
    private Set<CSP> csps = new HashSet<>();

    public EIS() {
    }

    public EIS(double capacity, double cost_second) {
        this.capacity = capacity;
        this.cost_second = cost_second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
         EIS eis = (EIS) o;
        return getId() == eis.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
