package com.lwh.edgeselection.repository;

import com.lwh.edgeselection.domain.Area;
import com.lwh.edgeselection.domain.DetailedArea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AreaRepository extends JpaRepository<Area,Long> {
//    List<Area> findByDetailedAreasEquals(DetailedArea detailedArea);\

}
