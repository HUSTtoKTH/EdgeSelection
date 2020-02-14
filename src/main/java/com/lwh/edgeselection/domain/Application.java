package com.lwh.edgeselection.domain;

import javax.persistence.*;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Entity
@Table(name = "applications")
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
//    private List<Area> areas;
//    private List<CSP> csps;
//    private List<CSP> nocsps;
//    private List<EIS> services;
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
//    private String[] objectives;
    private int num_EIS_per_Country;
    private int num_CSP_per_EIS;
    private double capacity;

    public Application() {
    }

    public Application(int num_EIS_per_Country,int num_CSP_per_EIS, double capacity, double latency) {
        this.num_EIS_per_Country = num_EIS_per_Country;
        this.num_CSP_per_EIS = num_CSP_per_EIS;
        this.capacity = capacity;
        this.latency = latency;
    }

    public double getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public double getReliability() {
        return reliability;
    }

    public void setReliability(double reliability) {
        this.reliability = reliability;
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

    public double getLatency() {
        return latency;
    }

    public void setLatency(double latency) {
        this.latency = latency;
    }

    public int getNum_EIS_per_Country() {
        return num_EIS_per_Country;
    }

    public void setNum_EIS_per_Country(int num_EIS_per_Country) {
        this.num_EIS_per_Country = num_EIS_per_Country;
    }

    public int getNum_CSP_per_EIS() {
        return num_CSP_per_EIS;
    }

    public void setNum_CSP_per_EIS(int num_CSP_per_EIS) {
        this.num_CSP_per_EIS = num_CSP_per_EIS;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Application" + getId());
//        sb.append(", capacity: "+ getCapacity());
        sb.append(", latency: "+getLatency());
        sb.append(", EIS: "+getNum_EIS_per_Country());
        sb.append(", CSP: "+ getNum_CSP_per_EIS());
        sb.append(", bandwidth: "+ getBandwidth());
        sb.append(", Cpu_frequency: "+ getCpu_frequency());
        sb.append(", Disk_size: "+ getDisk_size());
        sb.append(", Mem_size: "+ getMem_size());
        sb.append(", Num_cpus: "+ getNum_cpus());
        return sb.toString();
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
