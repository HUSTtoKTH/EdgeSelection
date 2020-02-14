package com.lwh.edgeselection.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "services")
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

    public double getCapacity() {
        return capacity;
    }

    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getInstance_num() {
        return instance_num;
    }

    public void setInstance_num(int instance_num) {
        this.instance_num = instance_num;
    }

    public int getCluster_num() {
        return cluster_num;
    }

    public void setCluster_num(int cluster_num) {
        this.cluster_num = cluster_num;
    }

    public double getCost_second() {
        return cost_second;
    }

    public void setCost_second(double cost_second) {
        this.cost_second = cost_second;
    }



    public double getReliability() {
        return reliability;
    }

    public void setReliability(double reliability) {
        this.reliability = reliability;
    }

    public int getNum_cpus() {
        return num_cpus;
    }

    public void setNum_cpus(int num_cpus) {
        this.num_cpus = num_cpus;
    }

    public double getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(double bandwidth) {
        this.bandwidth = bandwidth;
    }

    public double getCpu_frequency() {
        return cpu_frequency;
    }

    public void setCpu_frequency(double cpu_frequency) {
        this.cpu_frequency = cpu_frequency;
    }

    public double getDisk_size() {
        return disk_size;
    }

    public void setDisk_size(double disk_size) {
        this.disk_size = disk_size;
    }

    public double getMem_size() {
        return mem_size;
    }

    public void setMem_size(double mem_size) {
        this.mem_size = mem_size;
    }

    public String getGpu() {
        return gpu;
    }

    public void setGpu(String gpu) {
        this.gpu = gpu;
    }

    public String getFpga() {
        return fpga;
    }

    public void setFpga(String fpga) {
        this.fpga = fpga;
    }

    public String getSpecial() {
        return special;
    }

    public void setSpecial(String special) {
        this.special = special;
    }

    public Set<CSP> getCsps() {
        return csps;
    }

    public void setCsps(Set<CSP> csps) {
        this.csps = csps;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
