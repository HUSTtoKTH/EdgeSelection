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
class ServiceFunctionCostTest {
    @Autowired
    private ServiceFunctionCost serviceFunctionCost;
    @Autowired
    private ApplicationRepository applicationRepository;

    @Test
    public void testCost() throws IOException, IloException {
        String path = "./2021-08-11/cost";
        String application = "/application3";
        bruteForce(path, application);
        BestFit(path, application);
        ImprovedBestFit(path, application);
        CplexSolver(path, application);
    }

    @Test
    public void testLatencyInfluenceCost() throws IOException, IloException {
        String path = "./2021-08-11/latency";

        double[] list = new double[]{0.95, 0.85};
        for (double i : list) {
            List<Application> applications = applicationRepository.findAll();
            serviceFunctionCost.updateLatency(applications, i);
            String name = "/application1_ibf_"+i;
            ImprovedBestFit(path, name, applications);
        }

    }

    @Test
    public void testReliability() throws IOException, IloException {
        String path = "./2021-08-11/reliability";
        for (int i =1; i<=3; i++) {
            for (int j = 1; j <=3; j++) {
                List<Application> applications = applicationRepository.findAll();
                serviceFunctionCost.updateReliability(applications, i, j);
                String name = "/application1_IBF_EIS"+i+"_CSP"+j;
                ImprovedBestFit(path, name, applications);
            }
        }
    }

    @Test
    public void bruteForce(String path, String applicatoin) throws IOException {
        List<Application> applications = applicationRepository.findAll();
        List<FormForExcel> excels = new ArrayList<>();
        for (Application application : applications) {
            excels.add(serviceFunctionCost.bruteForce(application));
        }
        String filePath = path + applicatoin + "_bruteforce.xlsx";
        Functions.writeExcel(excels, filePath);
    }

    @Test
    public void BestFit(String path, String applicatoin) throws IOException {
        List<Application> applications = applicationRepository.findAll();
        List<FormForExcel> excels = new ArrayList<>();
        for (Application application : applications) {
            excels.add(serviceFunctionCost.BestFit(application));
        }
        String filePath = path + applicatoin + "_bestfit.xlsx";
        Functions.writeExcel(excels, filePath);
    }

    @Test
    public void ImprovedBestFit(String path, String applicatoin) throws IOException {
        List<Application> applications = applicationRepository.findAll();
        List<FormForExcel> excels = new ArrayList<>();
        for (Application application : applications) {
            excels.add(serviceFunctionCost.ImprovedBestFit(application));
        }
        String filePath = path + applicatoin + "_improvedbestfit.xlsx";
        Functions.writeExcel(excels, filePath);
    }


    @Test
    public void ImprovedBestFit(String path, String applicatoin, List<Application> applications) throws IOException {
        List<FormForExcel> excels = new ArrayList<>();
        for (Application application : applications) {
            excels.add(serviceFunctionCost.ImprovedBestFit(application));
        }
        String filePath = path + applicatoin + "_improvedbestfit.xlsx";
        Functions.writeExcel(excels, filePath);
    }

    @Test
    public void CplexSolver(String path, String applicatoin) throws IOException, IloException {
        List<Application> applications = applicationRepository.findAll();
        List<FormForExcel> excels = new ArrayList<>();
        int i = 0;
        for (Application application : applications) {
            excels.add( serviceFunctionCost.cplexSolver(application));
            i++;
        }
        String filePath = path + applicatoin + "_cplex.xlsx";
        Functions.writeExcel(excels, filePath);
    }


}