package com.lwh.edgeselection.repository;

import com.lwh.edgeselection.domain.Latency;
import com.lwh.edgeselection.domain.LatencyId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LatencyRepository extends JpaRepository<Latency, LatencyId> {

}


//    @Query("select course.cname,scores.score from Scores scores inner join Course   course  on scores.cno = course.cno where scores.sno = ?1")

//    @Query("select new com.lwh.edgeselection.domain.FormTest(s, c, a) FROM EIS s JOIN s.csps c JOIN c.areas a")
//    List<FormTest> findByEmailAddress();
//    @Query("select new com.lwh.edgeselection.domain.FormTest(s, c) FROM EIS s JOIN s.csps c JOIN c.areas a")
//    List<FormTest> findByEmailAddress();
//wortking
//    @Query("select s FROM EIS s JOIN s.csps c")
//    List<EIS> findByEmailAddress();
//        @Query("select new com.lwh.edgeselection.domain.ServiceForm(EIS, CSP) FROM EIS s JOIN s.csps c")
//    List<EIS> findByEmailAddress();
//    @Query("select new com.lwh.edgeselection.domain.ServiceForm(EIS, CSP, Latency) " +
//            "FROM latencys l" +
//            "INNER JOIN csps c ON c.id = l.csp_id" +
//            "INNER JOIN services s ON s.id = l.service_id" +
//            "INNER JOIN areas a ON a.id = l.area_id")
//    List<ServiceForm> findByEmailAddress();


//    public void whenMultipleEntitiesAreListedWithJoin_ThenCreatesMultipleJoins() {
//        TypedQuery<Phone> query
//                = entityManager.createQuery(
//                "SELECT ph FROM Employee e
//                JOIN e.department d
//                JOIN e.phones ph
//                WHERE d.name IS NOT NULL", Phone.class);
//                List<Phone> resultList = query.getResultList();
//    }