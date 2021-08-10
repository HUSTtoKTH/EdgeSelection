package com.lwh.edgeselection.Application;

import com.lwh.edgeselection.DTO.FormForExcel;
import com.lwh.edgeselection.Functions.Functions;
import com.lwh.edgeselection.domain.Application;
import com.lwh.edgeselection.repository.ApplicationRepository;
import ilog.concert.IloException;
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
class ServiceGenerateTest {
    @Autowired
    private ApplicationService serviceFunctionLatency;

    @Test
    public void generateEIS() {
        serviceFunctionLatency.generateEISWithLatency();
    }

    @Test
    public void generateApp1() {
        serviceFunctionLatency.generateApp1(40);
    }

    @Test
    public void generateApp2() {
        serviceFunctionLatency.generateApp2(40);
    }

    @Test
    public void generateApp3() {
        serviceFunctionLatency.generateApp3(40);
    }



}