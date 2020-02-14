package com.lwh.edgeselection.repository;

import com.lwh.edgeselection.domain.EIS;
import com.lwh.edgeselection.domain.PreferedCSP;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PreferedCSPRepository extends JpaRepository<PreferedCSP,Long> {
    List<PreferedCSP> findAllByApplicationidEquals(Long id);
}
