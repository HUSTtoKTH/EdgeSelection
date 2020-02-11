package com.lwh.edgeselection.Application;

import com.lwh.edgeselection.domain.Area;
import com.lwh.edgeselection.repository.AreaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * The type Area service.
 */
@Service
public class AreaService {
    @Autowired
    private AreaRepository areaRepository;

    /**
     * Gets all.
     *
     * @return the all
     */
    public List<Area> getAll()
    {
        return areaRepository.findAll();
    }
}
