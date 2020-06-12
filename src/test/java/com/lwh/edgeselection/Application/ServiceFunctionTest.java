package com.lwh.edgeselection.Application;

import com.lwh.edgeselection.DTO.FormForExcel;
import com.lwh.edgeselection.Functions.Functions;
import com.lwh.edgeselection.domain.Application;
import com.lwh.edgeselection.repository.ApplicationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ServiceFunctionTest {
    @Autowired
    private ServiceFunctionLatency serviceFunctionLatency;
    @Autowired
    private ApplicationRepository applicationRepository;

    @Test
    public void generateEIS() {
        serviceFunctionLatency.generateEISWithLatency();
    }

    @Test
    public void generateApp1() {
        serviceFunctionLatency.generateApp1();
    }

    @Test
    public void generateApp2() {
        serviceFunctionLatency.generateApp2();
    }

    @Test
    public void bruteForce() throws IOException {
        List<Application> applications = applicationRepository.findAll();
        List<FormForExcel> excels = new ArrayList<>();
        for (Application application : applications) {
            excels.add(serviceFunctionLatency.bruteForce(application));
        }
        Functions.writeExcel(excels, "./result/latency/bruteApp2.xlsx");
    }

    @Test
    public void BestFit() throws IOException {
        List<Application> applications = applicationRepository.findAll();
        List<FormForExcel> excels = new ArrayList<>();
        for (Application application : applications) {
            excels.add(serviceFunctionLatency.BestFit(application));
        }
        Functions.writeExcel(excels, "./result/latency/BFApp2.xlsx");
    }



}