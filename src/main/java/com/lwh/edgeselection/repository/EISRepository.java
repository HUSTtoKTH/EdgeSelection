package com.lwh.edgeselection.repository;

import com.lwh.edgeselection.domain.CSP;
import com.lwh.edgeselection.domain.EIS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * The interface Eis repository.
 */
public interface EISRepository extends JpaRepository<EIS,Long> {
    /**
     * Find eis by csps in and csps is not in list.
     *
     * @param csps   the csps
     * @param nocsps the nocsps
     * @return the list
     */
    List<EIS> findEISByCspsInAndCspsIsNotIn(List<CSP> csps, List<CSP> nocsps);

    /**
     * Find eis by capability list.
     *
     * @param bandwidth     the bandwidth
     * @param cpu_frequency the cpu frequency
     * @param disk_size     the disk size
     * @param mem_size      the mem size
     * @param num_cpus      the num cpus
     * @return the list
     */
    @Query("select s " +
            "FROM EIS s " +
            "WHERE " +
//            "s.instance_num >= 1 AND " +
            "s.bandwidth >= ?1 AND " +
            "s.cpu_frequency >= ?2 AND " +
            "s.disk_size >= ?3 AND " +
            "s.mem_size >= ?4 AND " +
            "s.num_cpus >= ?5")
    List<EIS> findEISByCapability(double bandwidth, double cpu_frequency, double disk_size, double mem_size, int num_cpus);

}
