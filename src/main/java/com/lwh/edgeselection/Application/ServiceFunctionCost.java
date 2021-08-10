package com.lwh.edgeselection.Application;

import com.lwh.edgeselection.DTO.FormForExcel;
import com.lwh.edgeselection.DTO.ServiceForm;
import com.lwh.edgeselection.DTO.ServiceTable;
import com.lwh.edgeselection.DTO.ServiceToMatrix;
import com.lwh.edgeselection.Functions.Functions;
import com.lwh.edgeselection.domain.*;
import com.lwh.edgeselection.repository.*;
import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.math.BigInteger;
import java.util.*;

@Service
public class ServiceFunctionCost {
    @Autowired
    private AreaRepository areaRepository;
    @Autowired
    private EISRepository eisRepository;
    @Autowired
    private LatencyRepository latencyRepository;
    @Autowired
    private CSPRepository cspRepository;
    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private TableRepository tableRepository;


    public FormForExcel BestFit(Application application){
        FormForExcel formForExcel = new FormForExcel();
        formForExcel.setNum_EIS_per_Country(application.getNum_EIS_per_Country());
        formForExcel.setNum_CSP_per_EIS(application.getNum_CSP_per_EIS());
        System.out.println("Application: "+application.getId());
        Set<CSP> likeCSP= application.getPreferedCSPs();
        Set<CSP> unlikeCSP= application.getUnpreferedCSPs();
        System.out.print("Prefered CSP: ");
        StringBuilder sb = new StringBuilder();
        for(CSP csp:likeCSP){
            sb.append(csp.getId()+" ");
            System.out.print(csp.getId()+" ");
        }
        formForExcel.setPreferedCSP(sb.toString());
        System.out.println();
        System.out.print("UnPrefered CSP: ");
        sb = new StringBuilder();
        for(CSP csp:unlikeCSP){
            sb.append(csp.getId()+" ");
            System.out.print(csp.getId()+" ");
        }
        formForExcel.setUnPreferedCSP(sb.toString());
        System.out.println();
        StopWatch sw = new StopWatch("test");
        sw.start("retrive All Service By Areas");
        List<ServiceForm> testTable = tableRepository.retrieveAllServiceByAreasIn(application.getAppareas());
        ServiceTable originalTable = new ServiceTable();
        originalTable.addAll(testTable);
        System.out.println("Original table:");
        System.out.println("number of EIS:"+originalTable.getUsedEIS().size()
                +", number of CSP:"+originalTable.getUsedCSP().size()
                +", number of all service lines:"+originalTable.getList().size());
        sw.stop();
        sw.start("find EIS qualified");
        List<EIS> testEIS = eisRepository.findEISByCapability(application.getBandwidth(),application.getCpu_frequency(),application.getDisk_size(),application.getMem_size(),application.getNum_cpus());
        System.out.println("number qualified EIS:"+testEIS.size());
        formForExcel.setQualifiedEIS(testEIS.size());
        sw.stop();
        sw.start("filter table by unlikeCSP, unqualified EIS, latency");
        Functions.filterTable(testTable,application.getUnpreferedCSPs(),testEIS,application.getLatency());
        ServiceTable filterTable = new ServiceTable();
        filterTable.addAll(testTable);
        Iterator<ServiceForm> iterator = testTable.iterator();
        while (iterator.hasNext()) {
            ServiceForm serviceForm = iterator.next();
            if(!filterTable.checkSingleEISNumberOfCSP(serviceForm.getEis(),application.getNum_CSP_per_EIS())){
                iterator.remove();
            }
        }
        filterTable = new ServiceTable();
        filterTable.addAll(testTable);
        System.out.println("Filtered table:");
        System.out.println("number of EIS:"+filterTable.getUsedEIS().size()
                +", number of CSP:"+filterTable.getUsedCSP().size()
                +", number of all service lines:"+filterTable.getList().size());
        formForExcel.setNumberOfEIS(filterTable.getUsedEIS().size());
        formForExcel.setNumberOfCSP(filterTable.getUsedCSP().size());
        formForExcel.setNumberOfallservice(filterTable.getList().size());
        sw.stop();
        sw.start("best fit algorithm");
        ServiceTable optimalComb = new ServiceTable();
        double optimalcost = -1;
        int n = filterTable.getList().size();
        for(CSP csp:likeCSP){
            optimalComb.add(filterTable.retrieveCheapestRowBasedOnCSP(csp));
        }
        while(!optimalComb.checkNumberOfEIS(application.getNum_EIS_per_Country())){
            optimalComb.add(filterTable.retrieveCheapestLineWithNewEIS(optimalComb));
        }
        while(!optimalComb.checkNumberOfCSPGreaterEqual(application.getNum_CSP_per_EIS())){
            EIS unsatistfiedEIS = optimalComb.findLowReliabilityService(application.getNum_CSP_per_EIS());
            optimalComb.add(filterTable.retrieveCheapestRowBasedOnEIS(unsatistfiedEIS));
        }
        optimalcost = optimalComb.newCalculateCost();
        sw.stop();
        formForExcel.setTime(sw.getTotalTimeSeconds());
        System.out.println("Optimal cost: "+ optimalcost);
        System.out.println(optimalComb);
        System.out.println(sw.prettyPrint());
        formForExcel.setResult(optimalComb.toString());
        formForExcel.setCost(optimalcost);
        return formForExcel;
    }

    public FormForExcel bruteForce(Application application) {
        FormForExcel formForExcel = new FormForExcel();
        formForExcel.setNum_EIS_per_Country(application.getNum_EIS_per_Country());
        formForExcel.setNum_CSP_per_EIS(application.getNum_CSP_per_EIS());
        System.out.println("Application: "+application.getId());
        Set<CSP> likeCSP= application.getPreferedCSPs();
        Set<CSP> unlikeCSP= application.getUnpreferedCSPs();
        System.out.print("Prefered CSP: ");
        StringBuilder sb = new StringBuilder();
        for(CSP csp:likeCSP){
            sb.append(csp.getId()+" ");
            System.out.print(csp.getId()+" ");
        }
        formForExcel.setPreferedCSP(sb.toString());
        System.out.println();
        System.out.print("UnPrefered CSP: ");
        sb = new StringBuilder();
        for(CSP csp:unlikeCSP){
            sb.append(csp.getId()+" ");
            System.out.print(csp.getId()+" ");
        }
        formForExcel.setUnPreferedCSP(sb.toString());
        System.out.println();
        StopWatch sw = new StopWatch("test");
        sw.start("retrive All Service By Areas");
        List<ServiceForm> testTable = tableRepository.retrieveAllServiceByAreasIn(application.getAppareas());
        ServiceTable originalTable = new ServiceTable();
        originalTable.addAll(testTable);
        System.out.println("Original table:");
        System.out.println("number of EIS:"+originalTable.getUsedEIS().size()
                +", number of CSP:"+originalTable.getUsedCSP().size()
                +", number of all service lines:"+originalTable.getList().size());
        sw.stop();
        sw.start("find EIS qualified");
        List<EIS> testEIS = eisRepository.findEISByCapability(application.getBandwidth(),application.getCpu_frequency(),
                application.getDisk_size(),application.getMem_size(),application.getNum_cpus());
        System.out.println("number qualified EIS:"+testEIS.size());
        formForExcel.setQualifiedEIS(testEIS.size());
        sw.stop();
        sw.start("filter table by unlikeCSP, unqualified EIS, latency");
        Functions.filterTable(testTable,application.getUnpreferedCSPs(),testEIS,application.getLatency());
        ServiceTable filterTable = new ServiceTable();
        filterTable.addAll(testTable);
        Iterator<ServiceForm> iterator = testTable.iterator();
        while (iterator.hasNext()) {
            ServiceForm serviceForm = iterator.next();
            if(!filterTable.checkSingleEISNumberOfCSP(serviceForm.getEis(),application.getNum_CSP_per_EIS())){
                iterator.remove();
            }
        }
        filterTable = new ServiceTable();
        filterTable.addAll(testTable);
        System.out.println("Filtered table:");
        System.out.println("number of EIS:"+filterTable.getUsedEIS().size()
                +", number of CSP:"+filterTable.getUsedCSP().size()
                +", number of all service lines:"+filterTable.getList().size());
        formForExcel.setNumberOfEIS(filterTable.getUsedEIS().size());
        formForExcel.setNumberOfCSP(filterTable.getUsedCSP().size());
        formForExcel.setNumberOfallservice(filterTable.getList().size());
        sw.stop();
        sw.start("check reliability and calculate cost for each combination");
        ServiceTable optimalComb = new ServiceTable();
        double optimalcost = -1;
        int n = filterTable.getList().size();
        //number of possible combination ：2^n
        BigInteger max = new BigInteger("2");
        max = max.pow(n);
        formForExcel.setPossibleCombination("2^"+n + ": "+ max);
        System.out.println("number of possible combination：2^"+n + ", "+ max);
        BigInteger min = BigInteger.ONE;
        while(max.compareTo(min) == 1){
            ServiceTable serviceTable = new ServiceTable();
            char[] binary = min.toString(2).toCharArray();
            for(int i = 0; i < binary.length; i++){
                if(binary[i] == '1'){
                    serviceTable.add(filterTable.getList().get(binary.length-1-i));
                }
            }
            if(serviceTable.checkReliability(application.getNum_EIS_per_Country(),application.getNum_CSP_per_EIS()) &&
                    serviceTable.checkCSP(application.getPreferedCSPs())){
                double cost = serviceTable.newCalculateCost();
                if(optimalcost > cost || optimalcost == -1){
                    optimalcost = cost;
                    optimalComb = serviceTable;
                }
            }
            min = min.add(BigInteger.ONE);
        }
        sw.stop();
        formForExcel.setTime(sw.getTotalTimeSeconds());
        System.out.println("Optimal cost: "+ optimalcost);
        System.out.println(optimalComb);
        System.out.println(sw.prettyPrint());
        formForExcel.setResult(optimalComb.toString());
        formForExcel.setCost(optimalcost);
        if(optimalcost == -1){
            formForExcel.setResult("no result");
//            applicationRepository.delete(application);
        }
        return formForExcel;
    }


    public  FormForExcel ImprovedBestFit(Application application){
        FormForExcel formForExcel = new FormForExcel();
        formForExcel.setNum_EIS_per_Country(application.getNum_EIS_per_Country());
        formForExcel.setNum_CSP_per_EIS(application.getNum_CSP_per_EIS());
        System.out.println("Application: "+application.getId());
        Set<CSP> likeCSP= application.getPreferedCSPs();
        Set<CSP> unlikeCSP= application.getUnpreferedCSPs();
        System.out.print("Prefered CSP: ");
        StringBuilder sb = new StringBuilder();
        for(CSP csp:likeCSP){
            sb.append(csp.getId()+" ");
            System.out.print(csp.getId()+" ");
        }
        formForExcel.setPreferedCSP(sb.toString());
        System.out.println();
        System.out.print("UnPrefered CSP: ");
        sb = new StringBuilder();
        for(CSP csp:unlikeCSP){
            sb.append(csp.getId()+" ");
            System.out.print(csp.getId()+" ");
        }
        formForExcel.setUnPreferedCSP(sb.toString());
        System.out.println();
        StopWatch sw = new StopWatch("test");
        sw.start("retrive All Service By Areas");
        List<ServiceForm> testTable = tableRepository.retrieveAllServiceByAreasIn(application.getAppareas());
        ServiceTable originalTable = new ServiceTable();
        originalTable.addAll(testTable);
        System.out.println("Original table:");
        System.out.println("number of EIS:"+originalTable.getUsedEIS().size()
                +", number of CSP:"+originalTable.getUsedCSP().size()
                +", number of all service lines:"+originalTable.getList().size());
        sw.stop();
        sw.start("find EIS qualified");
        List<EIS> testEIS = eisRepository.findEISByCapability(application.getBandwidth(),application.getCpu_frequency(),application.getDisk_size(),application.getMem_size(),application.getNum_cpus());
        System.out.println("number qualified EIS:"+testEIS.size());
        formForExcel.setQualifiedEIS(testEIS.size());
        sw.stop();
        sw.start("filter table by unlikeCSP, unqualified EIS, latency");
        Functions.filterTable(testTable,application.getUnpreferedCSPs(),testEIS,application.getLatency());
        ServiceTable filterTable = new ServiceTable();
        filterTable.addAll(testTable);
        Iterator<ServiceForm> iterator = testTable.iterator();
        while (iterator.hasNext()) {
            ServiceForm serviceForm = iterator.next();
            if(!filterTable.checkSingleEISNumberOfCSP(serviceForm.getEis(),application.getNum_CSP_per_EIS())){
                iterator.remove();
            }
        }
        filterTable = new ServiceTable();
        filterTable.addAll(testTable);
        System.out.println("Filtered table:");
        System.out.println("number of EIS:"+filterTable.getUsedEIS().size()
                +", number of CSP:"+filterTable.getUsedCSP().size()
                +", number of all service lines:"+filterTable.getList().size());
        formForExcel.setNumberOfEIS(filterTable.getUsedEIS().size());
        formForExcel.setNumberOfCSP(filterTable.getUsedCSP().size());
        formForExcel.setNumberOfallservice(filterTable.getList().size());
        sw.stop();
        sw.start("best fit algorithm");
        ServiceTable optimalComb = new ServiceTable();
        double optimalcost = -1;
        int n = filterTable.getList().size();
        for(CSP csp:likeCSP){
            optimalComb.add(filterTable.retrieveCheapestRowBasedOnCSP(csp, application.getNum_CSP_per_EIS()));
        }
        while(!optimalComb.checkNumberOfEIS(application.getNum_EIS_per_Country())){
            optimalComb.add(filterTable.retrieveCheapestLineWithNewEIS(optimalComb, application.getNum_CSP_per_EIS()));
        }
        while(!optimalComb.checkNumberOfCSPGreaterEqual(application.getNum_CSP_per_EIS())){
            EIS unsatistfiedEIS = optimalComb.findLowReliabilityService(application.getNum_CSP_per_EIS());
            optimalComb.add(filterTable.retrieveCheapestRowBasedOnEIS(unsatistfiedEIS));
        }
        optimalcost = optimalComb.newCalculateCost();
        sw.stop();
        formForExcel.setTime(sw.getTotalTimeSeconds());
        System.out.println("Optimal cost: "+ optimalcost);
        System.out.println(optimalComb);
        System.out.println(sw.prettyPrint());
        formForExcel.setResult(optimalComb.toString());
        formForExcel.setCost(optimalcost);
        return formForExcel;
    }

    public FormForExcel cplexSolver(Application application) throws IloException {
        FormForExcel formForExcel = new FormForExcel();
        formForExcel.setNum_EIS_per_Country(application.getNum_EIS_per_Country());
        formForExcel.setNum_CSP_per_EIS(application.getNum_CSP_per_EIS());
        System.out.println("Application: "+application.getId());
        Set<CSP> likeCSP= application.getPreferedCSPs();
        Set<CSP> unlikeCSP= application.getUnpreferedCSPs();
        StopWatch sw = new StopWatch("test");
        sw.start("retrive All Service By Areas");
        List<ServiceForm> testTable = tableRepository.retrieveAllServiceByAreasIn(application.getAppareas());
        ServiceTable originalTable = new ServiceTable();
        originalTable.addAll(testTable);
        System.out.println("Original table:");
        System.out.println("number of EIS:"+originalTable.getUsedEIS().size()
                +", number of CSP:"+originalTable.getUsedCSP().size()
                +", number of all service lines:"+originalTable.getList().size());
        sw.stop();
        sw.start("find EIS qualified");
        List<EIS> testEIS = eisRepository.findEISByCapability(application.getBandwidth(),application.getCpu_frequency(),application.getDisk_size(),application.getMem_size(),application.getNum_cpus());
        System.out.println("number qualified EIS:"+testEIS.size());
        formForExcel.setQualifiedEIS(testEIS.size());
        sw.stop();
        sw.start("filter table by unlikeCSP, unqualified EIS, latency");
        Functions.filterTable(testTable,unlikeCSP,testEIS,application.getLatency(),application.getBudget());
        sw.stop();
        sw.start("MILP algorithm");
        ServiceTable optimalComb = new ServiceTable();

        double optimalLatency = -1;
        ServiceToMatrix serviceToMatrix = new ServiceToMatrix();
        serviceToMatrix.addAll(testTable);
        serviceToMatrix.transfer(likeCSP);

        IloCplex cplex = new IloCplex(); // creat a model
        int length = serviceToMatrix.getX_cost().length;
        IloNumVar[] x = cplex.boolVarArray(length);
        IloNumVar[][] y = new IloNumVar[length][];
        IloNumExpr xsum = null;
        IloNumExpr[] ysumRow = new IloNumExpr[length];
        IloNumExpr ysum = null;
        for (int i = 0; i < length; i++) {
            y[i] = cplex.boolVarArray(serviceToMatrix.getY_cost().get(i).length);
        }
//Constraint 1
        IloNumExpr costSum = cplex.scalProd(x, serviceToMatrix.getX_cost());
        for(int i = 0; i < length; i++){
            costSum = cplex.sum(costSum, cplex.scalProd(y[i], serviceToMatrix.getY_cost().get(i)));
        }
        cplex.addLe(costSum, application.getBudget());
//Constraint 2
        for(int i = 0; i<length; i++){
            for(int j = 0; j<y[i].length; j++){
                if(j == 0){
                    ysumRow[i] = y[i][j];
                }else {
                    ysumRow[i] = cplex.sum(ysumRow[i],y[i][j]);
                }
            }
            if(i == 0){
                xsum = x[i];
                ysum = ysumRow[i];
            }else {
                xsum = cplex.sum(xsum,x[i]);
                ysum = cplex.sum(ysum,ysumRow[i]);
            }
        }
        cplex.addGe(xsum, application.getNum_EIS_per_Country());
        for(int i = 0; i< length; i++){
            cplex.addGe(ysumRow[i], cplex.prod(application.getNum_CSP_per_EIS(), x[i]));
        }
//Constraint 3
        for(int i = 0; i< length; i++){
            cplex.addLe(ysumRow[i], cplex.prod(10000, x[i]));
        }
//Constraint 4
        for(Map.Entry<CSP, List<ServiceToMatrix.XY>> entry : serviceToMatrix.getMandatory_csp().entrySet()){
            IloNumExpr mandatory = null;
            List<ServiceToMatrix.XY> results  = entry.getValue();
            for(int i = 0; i<results.size(); i++){
                ServiceToMatrix.XY xy = results.get(i);
                if(mandatory == null){
                    mandatory = y[xy.getX()][xy.getY()];
                }else {
                    mandatory = cplex.sum(mandatory, y[xy.getX()][xy.getY()]);
                }
            }
            cplex.addEq(mandatory,1);
        }

//        IloNumExpr obj = null;
//        obj = cplex.diff(obj, cplex.prod(ysum,serviceToMatrix.averageLatency()));
        cplex.addMinimize(costSum);

        if (cplex.solve()) {
            cplex.output().println("Solution status = " + cplex.getStatus());
            cplex.output().println("Solution value = " + cplex.getObjValue());
            double[] valx = cplex.getValues(x);
            double[][] valy = new double[length][];
            for (int i = 0; i < y.length; i++){
                valy[i] = cplex.getValues(y[i]);
            }


            optimalComb.addAll(serviceToMatrix.transferBack(valx,valy));
            optimalLatency = optimalComb.calculateLatency();
            sw.stop();
            formForExcel.setTime(sw.getTotalTimeSeconds());
            System.out.println(optimalComb);
            System.out.println(sw.prettyPrint());
            formForExcel.setResult(optimalComb.toString());
            formForExcel.setLatency_avg(optimalLatency);
            formForExcel.setCost(optimalComb.newCalculateCost());
        }
        cplex.end();
        return formForExcel;
    }


    public void updateLatency(int low, int range) {
        List<Application> applications = applicationRepository.findAll();
        for(Application application:applications) {
//            1+(int)(Math.random()*3),1+(int)(Math.random()*3), Math.random(),Math.random()*30 + 20
            application.setLatency(Math.random()*range + low);
            applicationRepository.save(application);
        }
        System.out.println("Updated");
    }

    public void updateReliability(int low, int up) {
        List<Application> applications = applicationRepository.findAll();
        for(Application application:applications) {
//            1+(int)(Math.random()*3),1+(int)(Math.random()*3), Math.random(),Math.random()*30 + 20
            application.setNum_CSP_per_EIS(low+(int)(Math.random()*up));
            application.setNum_EIS_per_Country(low+(int)(Math.random()*up));
            applicationRepository.save(application);
        }
        System.out.println("Updated");
    }
}
