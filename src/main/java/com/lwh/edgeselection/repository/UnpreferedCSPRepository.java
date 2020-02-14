package com.lwh.edgeselection.repository;

import com.lwh.edgeselection.domain.EIS;
import com.lwh.edgeselection.domain.UnpreferedCSP;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UnpreferedCSPRepository extends JpaRepository<UnpreferedCSP,Long> {
    List<UnpreferedCSP> findByApplicationidEquals(Long id);
}
