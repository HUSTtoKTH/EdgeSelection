package com.lwh.edgeselection.Application;

import com.lwh.edgeselection.domain.CSP;
import com.lwh.edgeselection.domain.EIS;
import com.lwh.edgeselection.repository.EISRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class EISService {
    @Autowired
    private EISRepository eisRepository;

    public List<EIS> getAll()
    {
        return eisRepository.findAll();
    }
    public EIS save(EIS eis) {
        return eisRepository.save(eis);
    }

    public List<EIS> filterCSP(List<CSP> csps, List<CSP> nocsps){
        return eisRepository.findEISByCspsInAndCspsIsNotIn(csps,nocsps);
    }

    public List<EIS> findByCapacity(double bandwidth, double cpu_frequency, double disk_size, double mem_size, int num_cpus){
        return eisRepository.findEISByCapability(bandwidth, cpu_frequency, disk_size, mem_size, num_cpus);
    }

}
