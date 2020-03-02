package com.lwh.edgeselection.repository;

import com.lwh.edgeselection.domain.Area;
import com.lwh.edgeselection.domain.CSP;
import com.lwh.edgeselection.domain.EIS;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CSPRepository extends JpaRepository<CSP,Long> {
    List<CSP> findByCspareasInAndIdIsNotIn(Iterable<Area> areas, Iterable<Long> unpreferedCSPIds);
    List<CSP> findByServicesIn(Iterable<EIS> eiss);

    List<CSP> findCSPByServicesIn(Iterable<EIS> eiss);

    List<CSP> findCSPByCspareasIn(Iterable<Area> areas);

    CSP findById(long id);

//    @Query("select t from Test t join User u where u.username = :username")
//    List<Test> findAllByUsername(@Param("username")String username);
}
