package com.lwh.edgeselection.repository;

import com.lwh.edgeselection.domain.Area;
import com.lwh.edgeselection.domain.CSP;
import com.lwh.edgeselection.domain.EIS;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * The interface Csp repository.
 */
public interface CSPRepository extends JpaRepository<CSP,Long> {


    /**
     * Find csp by services in list.
     *
     * @param eiss the eiss
     * @return the list
     */
    List<CSP> findCSPByServicesIn(Iterable<EIS> eiss);

    /**
     * Find csp by cspareas in list.
     *
     * @param areas the areas
     * @return the list
     */
    List<CSP> findCSPByCspareasIn(Iterable<Area> areas);

    /**
     * Find by id csp.
     *
     * @param id the id
     * @return the csp
     */
    CSP findById(long id);

//    @Query("select t from Test t join User u where u.username = :username")
//    List<Test> findAllByUsername(@Param("username")String username);
}
