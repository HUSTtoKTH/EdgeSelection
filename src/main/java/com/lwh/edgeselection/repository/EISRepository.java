package com.lwh.edgeselection.repository;

import com.lwh.edgeselection.domain.CSP;
import com.lwh.edgeselection.domain.EIS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EISRepository extends JpaRepository<EIS,Long> {
    List<EIS> findEISByCspsInAndCspsIsNotIn(List<CSP> csps, List<CSP> nocsps);

    @Query("select s " +
            "FROM EIS s " +
            "WHERE s.instance_num >= 1 AND " +
            "s.bandwidth >= ?1 AND " +
            "s.cpu_frequency >= ?2 AND " +
            "s.disk_size >= ?3 AND " +
            "s.mem_size >= ?4 AND " +
            "s.num_cpus >= ?5")
    List<EIS> finbEISByCapability(double bandwidth, double cpu_frequency, double disk_size, double mem_size, int num_cpus);

    List<EIS> findByCapacityGreaterThanEqual(double capacity);
}
