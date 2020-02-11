package com.lwh.edgeselection.Application;

import com.lwh.edgeselection.domain.Latency;
import com.lwh.edgeselection.repository.LatencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LatencyService {
    @Autowired
    private LatencyRepository latencyRepository;

    public void saveAll(List<Latency> latencies) {
        latencyRepository.saveAll(latencies);
    }
}
