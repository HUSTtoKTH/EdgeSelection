package com.lwh.edgeselection.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Entity
@Table(name = "applications")
@Data
public class Application implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinTable(name = "apps_areas",
            joinColumns = {
                    @JoinColumn(name = "app_id", referencedColumnName = "id",
                            nullable = false, updatable = false)},
            inverseJoinColumns = {
                    @JoinColumn(name = "area_id", referencedColumnName = "id",
                            nullable = false, updatable = false)})
    private Set<Area> appareas = new HashSet<>();
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinTable(name = "apps_csps",
            joinColumns = {
                    @JoinColumn(name = "app_id", referencedColumnName = "id",
                            nullable = false, updatable = false)},
            inverseJoinColumns = {
                    @JoinColumn(name = "csp_id", referencedColumnName = "id",
                            nullable = false, updatable = false)})
    private Set<CSP> preferedCSPs = new HashSet<>();
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinTable(name = "apps_nocsps",
            joinColumns = {
                    @JoinColumn(name = "app_id", referencedColumnName = "id",
                            nullable = false, updatable = false)},
            inverseJoinColumns = {
                    @JoinColumn(name = "nocsp_id", referencedColumnName = "id",
                            nullable = false, updatable = false)})
    private Set<CSP> unpreferedCSPs = new HashSet<>();
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

    public void addAll(Iterable<Area> areas){
        for(Area area:areas){
            appareas.add(area);
        }
    }

    public void addpreferedCSPs(CSP csp){
        preferedCSPs.add(csp);
    }

    public void addunpreferedCSPs(CSP csp){
        unpreferedCSPs.add(csp);
    }
//
//    @Override
//    public String toString() {
//        StringBuilder sb = new StringBuilder();
//        sb.append("Application" + getId());
////        sb.append(", capacity: "+ getCapacity());
//        sb.append(", latency: "+getLatency());
//        sb.append(", EIS: "+getNum_EIS_per_Country());
//        sb.append(", CSP: "+ getNum_CSP_per_EIS());
//        sb.append(", bandwidth: "+ getBandwidth());
//        sb.append(", Cpu_frequency: "+ getCpu_frequency());
//        sb.append(", Disk_size: "+ getDisk_size());
//        sb.append(", Mem_size: "+ getMem_size());
//        sb.append(", Num_cpus: "+ getNum_cpus());
//        return sb.toString();
//    }
}

