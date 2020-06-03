package com.lwh.edgeselection.repository;

import com.lwh.edgeselection.domain.Area;
import com.lwh.edgeselection.domain.Latency;
import com.lwh.edgeselection.domain.LatencyId;
import com.lwh.edgeselection.DTO.ServiceForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TableRepository extends JpaRepository<Latency, LatencyId> {
    @Query("select new com.lwh.edgeselection.DTO.ServiceForm(s, c, a, l) " +
            "FROM Latency l " +
            "INNER JOIN CSP c ON c.id = l.cspId " +
            "INNER JOIN EIS s ON s.id = l.serviceId " +
            "INNER JOIN Area a ON a.id = l.areaId " +
            "WHERE a IN ?1")
    List<ServiceForm> retrieveAllServiceByAreasIn(Iterable<Area> areas);
}