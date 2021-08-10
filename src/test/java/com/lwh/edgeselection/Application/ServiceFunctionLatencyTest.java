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
class ServiceFunctionLatencyTest {
    @Autowired
    private ServiceFunctionLatency serviceFunctionLatency;
    @Autowired
    private ApplicationRepository applicationRepository;

    @Test
    public void testSuit() throws IOException {
        String path = "./2021-08-10/latency";
        String application = "/application1";
        bruteForce(path, application);
        BestFit(path, application);

    }

    @Test
    public void bruteForce(String path, String applicatoin) throws IOException {
        List<Application> applications = applicationRepository.findAll();
        List<FormForExcel> excels = new ArrayList<>();
        for (Application application : applications) {
            excels.add(serviceFunctionLatency.bruteForce(application));
        }
        String filePath = path + applicatoin + "_bruteforce.xlsx";
        Functions.writeExcel(excels, filePath);
    }

    @Test
    public void BestFit(String path, String applicatoin) throws IOException {
        List<Application> applications = applicationRepository.findAll();
        List<FormForExcel> excels = new ArrayList<>();
        for (Application application : applications) {
            excels.add(serviceFunctionLatency.BestFit(application));
        }
        String filePath = path + applicatoin + "_bestfit.xlsx";
        Functions.writeExcel(excels, filePath);
    }

    @Test
    public void MILP(String path, String applicatoin) throws IOException {
        List<Application> applications = applicationRepository.findAll();
        List<FormForExcel> excels = new ArrayList<>();
        for (Application application : applications) {
            excels.add(serviceFunctionLatency.MILP(application));
        }
        String filePath = path + applicatoin + "_BnB.xlsx";
        Functions.writeExcel(excels, filePath);
    }


    @Test
    public void MILPOptimal(String path, String applicatoin) throws IOException {
        List<Application> applications = applicationRepository.findAll();
        List<FormForExcel> excels = new ArrayList<>();
        for (Application application : applications) {
            excels.add( serviceFunctionLatency.MILPV2(application));
        }
        String filePath = path + applicatoin + "_BnBOptimize.xlsx";
        Functions.writeExcel(excels, filePath);
    }

    @Test
    public void cplexSolver(String path, String applicatoin) throws IOException, IloException {
        List<Application> applications = applicationRepository.findAll();
        List<FormForExcel> excels = new ArrayList<>();
        int i = 0;
        for (Application application : applications) {
            if(i<20){
                excels.add( serviceFunctionLatency.cplexSolver(application));
                i++;
            }else {
                break;
            }
        }
        String filePath = path + applicatoin + "_cplex.xlsx";
        Functions.writeExcel(excels, filePath);
    }


}