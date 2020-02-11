package com.lwh.edgeselection.domain;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

public class Application {
    private List<Area> areas;
    private List<CSP> csps;
    private List<CSP> nocsps;
    private List<EIS> services;
    private int num_cpus;
    private double bandwidth;
    private double cpu_frequency;
    private double disk_size;
    private double mem_size;
    private double reliability;
    private String gpu;
    private String fpga;
    private String special;
    private double latency;
    private String[] objectives;


    public String[] getObjectives() {
        return objectives;
    }

    public void setObjectives(String[] objectives) {
        this.objectives = objectives;
    }

    public double getLatency() {
        return latency;
    }

    public void setLatency(double latency) {
        this.latency = latency;
    }

    public double getReliability() {
        return reliability;
    }

    public void setReliability(double reliability) {
        this.reliability = reliability;
    }

    public List<Area> getAreas() {
        return areas;
    }

    public void setAreas(List<Area> areas) {
        this.areas = areas;
    }

    public List<CSP> getCsps() {
        return csps;
    }

    public void setCsps(List<CSP> csps) {
        this.csps = csps;
    }

    public List<CSP> getNocsps() {
        return nocsps;
    }

    public void setNocsps(List<CSP> nocsps) {
        this.nocsps = nocsps;
    }

    public List<EIS> getServices() {
        return services;
    }

    public void setServices(List<EIS> services) {
        this.services = services;
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
}

//@Entity
//@Table(name = "applications")
//public class Application {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id", nullable = false)
//    private Long id;
//    private int instance_num;
//    private int cluster_num;
//    private double cost_second;
//    //    private CSP[] CSP_list;
////    private Area[] area_list;
//    private double delay;
//    private double reliability;
////    @OneToOne(cascade = CascadeType.ALL)
////    @JoinColumn(name = "capability_id", referencedColumnName = "id")
////    private Capability capability;
//
//    private int num_cpus;
//    private double bandwidth;
//    private double cpu_frequency;
//    private double disk_size;
//    private double mem_size;
//    private String gpu;
//    private String fpga;
//    private String special;
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public int getInstance_num() {
//        return instance_num;
//    }
//
//    public void setInstance_num(int instance_num) {
//        this.instance_num = instance_num;
//    }
//
//    public int getCluster_num() {
//        return cluster_num;
//    }
//
//    public void setCluster_num(int cluster_num) {
//        this.cluster_num = cluster_num;
//    }
//
//    public double getCost_second() {
//        return cost_second;
//    }
//
//    public void setCost_second(double cost_second) {
//        this.cost_second = cost_second;
//    }
//
//    public double getDelay() {
//        return delay;
//    }
//
//    public void setDelay(double delay) {
//        this.delay = delay;
//    }
//
//    public double getReliability() {
//        return reliability;
//    }
//
//    public void setReliability(double reliability) {
//        this.reliability = reliability;
//    }
//
//    public int getNum_cpus() {
//        return num_cpus;
//    }
//
//    public void setNum_cpus(int num_cpus) {
//        this.num_cpus = num_cpus;
//    }
//
//    public double getBandwidth() {
//        return bandwidth;
//    }
//
//    public void setBandwidth(double bandwidth) {
//        this.bandwidth = bandwidth;
//    }
//
//    public double getCpu_frequency() {
//        return cpu_frequency;
//    }
//
//    public void setCpu_frequency(double cpu_frequency) {
//        this.cpu_frequency = cpu_frequency;
//    }
//
//    public double getDisk_size() {
//        return disk_size;
//    }
//
//    public void setDisk_size(double disk_size) {
//        this.disk_size = disk_size;
//    }
//
//    public double getMem_size() {
//        return mem_size;
//    }
//
//    public void setMem_size(double mem_size) {
//        this.mem_size = mem_size;
//    }
//
//    public String getGpu() {
//        return gpu;
//    }
//
//    public void setGpu(String gpu) {
//        this.gpu = gpu;
//    }
//
//    public String getFpga() {
//        return fpga;
//    }
//
//    public void setFpga(String fpga) {
//        this.fpga = fpga;
//    }
//
//    public String getSpecial() {
//        return special;
//    }
//
//    public void setSpecial(String special) {
//        this.special = special;
//    }
//}
