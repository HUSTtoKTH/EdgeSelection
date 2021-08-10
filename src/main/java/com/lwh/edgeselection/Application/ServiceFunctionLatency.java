package com.lwh.edgeselection.Application;

import com.lwh.edgeselection.DTO.*;
import com.lwh.edgeselection.Functions.Functions;
import com.lwh.edgeselection.domain.*;
import com.lwh.edgeselection.repository.*;
import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.math.BigInteger;
import java.util.*;

@Service
public class ServiceFunctionLatency {
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


    public FormForExcel bruteForce(Application application) {
        FormForExcel formForExcel = new FormForExcel();
        formForExcel.setNum_EIS_per_Country(application.getNum_EIS_per_Country());
        formForExcel.setNum_CSP_per_EIS(application.getNum_CSP_per_EIS());
        System.out.println("Application: "+application.getId());
        Set<CSP> likeCSP= application.getPreferedCSPs();
        Set<CSP> unlikeCSP= application.getUnpreferedCSPs();
        StopWatch sw = new StopWatch("test");
        sw.start("retrive All Service By Areas");
        List<ServiceForm> testTable = tableRepository.retrieveAllServiceByAreasIn(application.getAppareas());
        sw.stop();
        sw.start("find EIS qualified");
        List<EIS> testEIS = eisRepository.findEISByCapability(application.getBandwidth(),application.getCpu_frequency(),
                application.getDisk_size(),application.getMem_size(),application.getNum_cpus());
        System.out.println("number qualified EIS:"+testEIS.size());
        formForExcel.setQualifiedEIS(testEIS.size());
        sw.stop();
        sw.start("filter table by unlikeCSP, unqualified EIS, latency, budget");
        Functions.filterTable(testTable,unlikeCSP,testEIS,application.getLatency(),application.getBudget());
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
        double optimalLatency = -1;
        double optimalCost = -1;
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
                    serviceTable.checkCSP(likeCSP) &&
                    serviceTable.checkBudget(application.getBudget())){
                double curLatency = serviceTable.calculateLatency();
                if(optimalLatency > curLatency || optimalLatency == -1){
                    optimalLatency = curLatency;
                    optimalCost = serviceTable.newCalculateCost();
                    optimalComb = serviceTable;
                }
            }
            min = min.add(BigInteger.ONE);
        }
        sw.stop();
        formForExcel.setTime(sw.getTotalTimeSeconds());
        System.out.println("Optimal cost: "+ optimalCost);
        System.out.println(optimalComb);
        System.out.println(sw.prettyPrint());
        formForExcel.setResult(optimalComb.toString());
        formForExcel.setCost(optimalCost);
        formForExcel.setLatency_avg(optimalLatency);
        if(optimalLatency == -1){
            formForExcel.setResult("no result");
        }
        return formForExcel;
    }


    public FormForExcel BestFit(Application application){
        FormForExcel formForExcel = new FormForExcel();
        formForExcel.setNum_EIS_per_Country(application.getNum_EIS_per_Country());
        formForExcel.setNum_CSP_per_EIS(application.getNum_CSP_per_EIS());
        System.out.println("Application: "+application.getId());
        Set<CSP> likeCSP= application.getPreferedCSPs();
        Set<CSP> unlikeCSP= application.getUnpreferedCSPs();
        StopWatch sw = new StopWatch("test");
        sw.start("retrive All Service By Areas");
        List<ServiceForm> testTable = tableRepository.retrieveAllServiceByAreasIn(application.getAppareas());
        sw.stop();
        sw.start("find EIS qualified");
        List<EIS> testEIS = eisRepository.findEISByCapability(application.getBandwidth(),application.getCpu_frequency(),application.getDisk_size(),application.getMem_size(),application.getNum_cpus());
        System.out.println("number qualified EIS:"+testEIS.size());
        formForExcel.setQualifiedEIS(testEIS.size());
        sw.stop();
        sw.start("filter table by unlikeCSP, unqualified EIS, latency");
        Functions.filterTable(testTable,unlikeCSP,testEIS,application.getLatency(),application.getBudget());
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
        formForExcel.setNumberOfEIS(filterTable.getUsedEIS().size());
        formForExcel.setNumberOfCSP(filterTable.getUsedCSP().size());
        formForExcel.setNumberOfallservice(filterTable.getList().size());
        sw.stop();
        sw.start("best fit algorithm");
        ServiceTable optimalComb = new ServiceTable();
        boolean hasResult = true;
        while(
                hasResult &&
                !filterTable.getList().isEmpty() &&
                        (!optimalComb.checkReliability(application.getNum_EIS_per_Country(),application.getNum_CSP_per_EIS()) ||
                !optimalComb.checkCSP(application.getPreferedCSPs()) ||
                !optimalComb.checkBudget(application.getBudget()))
        ){
            while(!optimalComb.checkBudget(application.getBudget())){
                optimalComb.removeExpensive();
            }
            for (CSP csp : likeCSP) {
                if(!optimalComb.getUsedCSP().contains(csp)) {
                    ServiceForm serviceForm = filterTable.retrieveFastestRowBasedOnCSP(csp);
                    if(serviceForm != null){
                        optimalComb.add(serviceForm);
                    }else {
                        hasResult = false;
                        break;
                    }
                }
            }
            while (hasResult && !optimalComb.checkNumberOfEIS(application.getNum_EIS_per_Country())) {
                ServiceForm serviceForm = filterTable.retrieveFastestLineWithNewEIS(optimalComb.getUsedEIS());
                if(serviceForm != null){
                    optimalComb.add(serviceForm);
                }else {
                    hasResult = false;
                    break;
                }
            }
            while (hasResult && !optimalComb.checkNumberOfCSPGreaterEqual(application.getNum_CSP_per_EIS())) {
                EIS unsatistfiedEIS = optimalComb.findLowReliabilityService(application.getNum_CSP_per_EIS());
                ServiceForm serviceForm = filterTable.retrieveFastestRowBasedOnEIS(unsatistfiedEIS);
                if(serviceForm != null){
                    optimalComb.add(serviceForm);
                }else {
                    optimalComb.removeByEIS(unsatistfiedEIS);
                    break;
                }
            }
        }
        double optimalLatency = optimalComb.calculateLatency();
        double optimalCost = optimalComb.newCalculateCost();
        if(hasResult == false ||
                !optimalComb.checkReliability(application.getNum_EIS_per_Country(),application.getNum_CSP_per_EIS()) ||
                        !optimalComb.checkCSP(application.getPreferedCSPs()) ||
                        !optimalComb.checkBudget(application.getBudget())
        ){
            optimalLatency = -1;
            optimalCost = -1;
        }
        sw.stop();
        formForExcel.setTime(sw.getTotalTimeSeconds());
        formForExcel.setResult(optimalComb.toString());
        formForExcel.setCost(optimalCost);
        formForExcel.setLatency_avg(optimalLatency);
        if(optimalLatency == -1){
            formForExcel.setResult("no result");
        }
        return formForExcel;
    }



    public FormForExcel MILP(Application application){
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
        ServiceTable filterTable = new ServiceTable();
        filterTable.addAll(testTable);
        System.out.println(filterTable.getList().size());
        Iterator<ServiceForm> iterator = testTable.iterator();
        while (iterator.hasNext()) {
            ServiceForm serviceForm = iterator.next();
            if(!filterTable.checkSingleEISNumberOfCSP(serviceForm.getEis(),application.getNum_CSP_per_EIS())){
                iterator.remove();
            }
        }
        filterTable = new ServiceTable();
        filterTable.addAll(testTable);
        formForExcel.setNumberOfEIS(filterTable.getUsedEIS().size());
        formForExcel.setNumberOfCSP(filterTable.getUsedCSP().size());
        formForExcel.setNumberOfallservice(filterTable.getList().size());
        sw.stop();
        sw.start("MILP algorithm");
        filterTable.orderListByLatency();
        int n = filterTable.getList().size();
        System.out.println("Table size: "+n);
        ServiceTable optimalComb = new ServiceTable();
        double optimalLatency = -1;
        double optimalCost = -1;
        BinaryRepresent binaryRepresent1 = new BinaryRepresent(n,1);
        binaryRepresent1.getBinaryRepresent()[0] = 1;
        binaryRepresent1.setLatencySum(filterTable.getList().get(0).getLatency().getDelay());
        BinaryRepresent binaryRepresent0 = new BinaryRepresent(n,1);
        binaryRepresent0.getBinaryRepresent()[0] = 0;
        binaryRepresent0.setLatencySum(filterTable.getList().get(1).getLatency().getDelay());
        PriorityQueue<BinaryRepresent> minHeap = new PriorityQueue<>(Comparator.comparingDouble(s -> s.getLatencySum()));
        minHeap.add(binaryRepresent0);
        minHeap.add(binaryRepresent1);
        int count = 0;
        while(!minHeap.isEmpty()){
            count++;
            BinaryRepresent binaryRepresent = minHeap.poll();
            ServiceTable cur = binaryRepresent.transfer(filterTable);
            int preCount = binaryRepresent.getValidCount();
            double preLatency = cur.getLatencySum();
            int[] preArray = binaryRepresent.getBinaryRepresent();
            int valid = cur.validBnB(application);
            if(valid == 1){
                optimalComb = cur;
                optimalCost = optimalComb.newCalculateCost();
                optimalLatency = optimalComb.calculateLatency();
                break;
            }
            if(valid == -1){
                continue;
            }

            if(valid == 0 && preCount < n){
                BinaryRepresent select = new BinaryRepresent(Arrays.copyOf(preArray,preArray.length),preCount+1);
                select.getBinaryRepresent()[preCount] = 1;
                select.setLatencySum(preLatency+filterTable.getList().get(preCount).getLatency().getDelay());
                minHeap.add(select);
                if(preCount != n-1){
                    BinaryRepresent unselect = new BinaryRepresent(Arrays.copyOf(preArray,preArray.length),preCount+1);
                    unselect.getBinaryRepresent()[preCount] = 0;
                    unselect.setLatencySum(preLatency+filterTable.getList().get(preCount+1).getLatency().getDelay());
                    minHeap.add(unselect);
                }
            }
        }
        formForExcel.setPossibleCombination(String.valueOf(count));
        sw.stop();
        formForExcel.setTime(sw.getTotalTimeSeconds());
        System.out.println("Optimal cost: "+ optimalCost);
        System.out.println(optimalComb);
        System.out.println(sw.prettyPrint());
        formForExcel.setResult(optimalComb.toString());
        formForExcel.setCost(optimalCost);
        formForExcel.setLatency_avg(optimalLatency);
        return formForExcel;
    }

    public FormForExcel MILPV2(Application application) {

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
        ServiceTable filterTable = new ServiceTable();
        filterTable.addAll(testTable);
        System.out.println(filterTable.getList().size());
        Iterator<ServiceForm> iterator = testTable.iterator();
        while (iterator.hasNext()) {
            ServiceForm serviceForm = iterator.next();
            if(!filterTable.checkSingleEISNumberOfCSP(serviceForm.getEis(),application.getNum_CSP_per_EIS())){
                iterator.remove();
            }
        }
        filterTable = new ServiceTable();
        filterTable.addAll(testTable);
        formForExcel.setNumberOfEIS(filterTable.getUsedEIS().size());
        formForExcel.setNumberOfCSP(filterTable.getUsedCSP().size());
        formForExcel.setNumberOfallservice(filterTable.getList().size());
        sw.stop();
        sw.start("MILP algorithm");
        filterTable.orderListByLatency();
        int n = filterTable.getList().size();
        System.out.println("Table size: "+n);
        ServiceTable optimalComb = new ServiceTable();
        double optimalLatency = -1;
        double optimalCost = -1;
        BinaryRepresent binaryRepresent1 = new BinaryRepresent(n,1);
        binaryRepresent1.getBinaryRepresent()[0] = 1;
        binaryRepresent1.setLatencySum(filterTable.getList().get(0).getLatency().getDelay());
        BinaryRepresent binaryRepresent0 = new BinaryRepresent(n,1);
        binaryRepresent0.getBinaryRepresent()[0] = 0;
        binaryRepresent0.setLatencySum(filterTable.getList().get(1).getLatency().getDelay());
        PriorityQueue<BinaryRepresent> minHeap = new PriorityQueue<>(Comparator.comparingDouble(s -> s.getLatencySum()));
        minHeap.add(binaryRepresent0);
        minHeap.add(binaryRepresent1);
        int count = 0;
        while(!minHeap.isEmpty()){
            count++;
            BinaryRepresent binaryRepresent = minHeap.poll();
            ServiceTable cur = binaryRepresent.transfer(filterTable);
            int preCount = binaryRepresent.getValidCount();
            double preLatency = cur.getLatencySum();
            int[] preArray = binaryRepresent.getBinaryRepresent();
            int valid = cur.validBnB(application);
            if(valid == 1){
                if(optimalLatency == -1 || cur.calculateLatency() < optimalLatency){
                    optimalComb = cur;
                    optimalCost = optimalComb.newCalculateCost();
                    optimalLatency = optimalComb.calculateLatency();
                    continue;
                }
            }
            if(valid == -1){
                continue;
            }

            if(valid == 0 && preCount < n){
                BinaryRepresent select = new BinaryRepresent(Arrays.copyOf(preArray,preArray.length),preCount+1);
                select.getBinaryRepresent()[preCount] = 1;
                select.setLatencySum(preLatency+filterTable.getList().get(preCount).getLatency().getDelay());
                minHeap.add(select);
                if(preCount != n-1){
                    BinaryRepresent unselect = new BinaryRepresent(Arrays.copyOf(preArray,preArray.length),preCount+1);
                    unselect.getBinaryRepresent()[preCount] = 0;
                    unselect.setLatencySum(preLatency+filterTable.getList().get(preCount+1).getLatency().getDelay());
                    minHeap.add(unselect);
                }
            }
        }
        formForExcel.setPossibleCombination(String.valueOf(count));
        sw.stop();
        formForExcel.setTime(sw.getTotalTimeSeconds());
        System.out.println("Optimal cost: "+ optimalCost);
        System.out.println(optimalComb);
        System.out.println(sw.prettyPrint());
        formForExcel.setResult(optimalComb.toString());
        formForExcel.setCost(optimalCost);
        formForExcel.setLatency_avg(optimalLatency);
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

        IloNumExpr obj = null;
        for(int i = 0; i<length; i++) {
            if (i == 0) {
                obj = cplex.scalProd(serviceToMatrix.getY_latency().get(i),y[i]);
            } else {
                obj = cplex.sum(obj, cplex.scalProd(serviceToMatrix.getY_latency().get(i),y[i]));
            }
        }
//        obj = cplex.diff(obj, cplex.prod(ysum,serviceToMatrix.averageLatency()));
        cplex.addMinimize(obj);

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
