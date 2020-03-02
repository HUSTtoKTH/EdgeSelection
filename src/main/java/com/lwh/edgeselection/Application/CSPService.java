package com.lwh.edgeselection.Application;


import com.lwh.edgeselection.domain.Area;
import com.lwh.edgeselection.domain.CSP;
import com.lwh.edgeselection.domain.EIS;
import com.lwh.edgeselection.repository.CSPRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CSPService {
    @Autowired
    private CSPRepository cspRepository;

    public List<CSP> getAll()
    {
        return cspRepository.findAll();
    }
    public CSP save(CSP csp) {
        return cspRepository.save(csp);
    }
//    public void saveAll(CSP[] csps){
//        cspRepository.sa;
//    }
    public List<CSP> filterArea(Iterable<Area> areas){
        return cspRepository.findCSPByCspareasIn(areas);
    }

    public List<CSP> filterEIS(Iterable<EIS> eiss){
        return cspRepository.findCSPByServicesIn(eiss);
    }
}
