package com.lwh.edgeselection.Application;

import com.lwh.edgeselection.domain.Area;
import com.lwh.edgeselection.domain.ServiceForm;
import com.lwh.edgeselection.repository.TableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TableService {
    @Autowired
    private TableRepository tableRepository;

    public List<ServiceForm> retriveAllServiceByAreasIn(Iterable<Area> areas){
        return tableRepository.retrieveAllServiceByAreasIn(areas);
    }
}
