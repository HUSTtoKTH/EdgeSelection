package com.lwh.edgeselection.DTO;

import com.lwh.edgeselection.domain.Latency;

import java.util.List;

public class LatencyListContainer {
    private List<Latency> latencies;
    public List<Latency> getLatencies() {
        return latencies;
    }
    public void setLatencies(List<Latency> latencies) {
        this.latencies = latencies;
    }
}
