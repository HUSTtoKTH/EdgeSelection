package com.lwh.edgeselection.repository;

import com.lwh.edgeselection.domain.Application;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application,Long> {
}
