package com.lwh.edgeselection.Application;

import com.lwh.edgeselection.DTO.BinaryRepresent;
import com.lwh.edgeselection.DTO.FormForExcel;
import com.lwh.edgeselection.DTO.ServiceForm;
import com.lwh.edgeselection.DTO.ServiceTable;
import com.lwh.edgeselection.Functions.Functions;
import com.lwh.edgeselection.domain.*;
import com.lwh.edgeselection.repository.*;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.math3.distribution.NormalDistribution;
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

    public void generateEIS() {
//            generate EISs
        Area testArea = new Area("test");
        areaRepository.save(testArea);
        List<CSP> csps = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            CSP csp = new CSP(Math.random() * 4);
            cspRepository.save(csp);
            csp.getCspareas().add(testArea);
            cspRepository.save(csp);
            csps.add(csp);
        }
        for(int i = 0; i < 100; i++) {
            EIS eis = new EIS(Math.random(), Math.random() * 10);
            //                bandwidth[100MB, 10GB]
            eis.setBandwidth(RandomUtils.nextDouble(100, 10000));
            //                cpu cores [1, 8]
            eis.setNum_cpus(RandomUtils.nextInt(1, 8));
            //                cpu frequency [1.8, 4]
            eis.setCpu_frequency(RandomUtils.nextDouble(1.8, 4));
            //                disk size [100GB, 10000GB]
            eis.setDisk_size(RandomUtils.nextInt(100, 10000));
            //                memory size [10MB, 10GB]
            eis.setMem_size(RandomUtils.nextInt(10, 10000));
            eisRepository.save(eis);
            for (CSP csp : csps) {
                if (Math.random() > 0.5) {
                    eis.getCsps().add(csp);
                    Latency latency = new Latency(testArea.getId(), csp.getId(), eis.getId(), Math.random() * 30 + 20);
                    latencyRepository.save(latency);
                }
            }
            eisRepository.save(eis);
        }
    }

    public void generateEISWithLatency() {
//            generate EISs
        Area testArea = new Area("test");
        areaRepository.save(testArea);
        List<CSP> csps = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            CSP csp = new CSP(Math.random() * 4);
            cspRepository.save(csp);
            csp.getCspareas().add(testArea);
            cspRepository.save(csp);
            csps.add(csp);
        }
        for(int i = 0; i < 100; i++) {
            EIS eis = new EIS(Math.random(), Math.random() * 10);
            //                bandwidth[100MB, 10GB]
            eis.setBandwidth(RandomUtils.nextDouble(100, 10000));
            //                cpu cores [1, 8]
            eis.setNum_cpus(RandomUtils.nextInt(1, 8));
            //                cpu frequency [1.8, 4]
            eis.setCpu_frequency(RandomUtils.nextDouble(1.8, 4));
            //                disk size [100GB, 10000GB]
            eis.setDisk_size(RandomUtils.nextInt(100, 10000));
            //                memory size [10MB, 10GB]
            eis.setMem_size(RandomUtils.nextInt(10, 10000));
            eisRepository.save(eis);
            for (CSP csp : csps) {
                if (Math.random() > 0.5) {
                    eis.getCsps().add(csp);
                    double lower = Math.random() * 20 + 10;
                    double upper = Math.random() * 30 + 70;
                    Latency latency = new Latency(testArea.getId(), csp.getId(), eis.getId(),
                             lower + Math.random() *(upper-lower),
                            lower,
                            upper);
                    latencyRepository.save(latency);
                }
            }
            eisRepository.save(eis);
        }
    }


    public void generateNewApp1() {
        //generate Applications
        List<CSP> csps = cspRepository.findAll();
        int i = 0;
        while(i < 20){
            Application application = new Application(3,3, Math.random(),Math.random()*70 + 40);
            application.addAll(areaRepository.findAll());
            setCPUIntensive(application);
            int complexity = checkComplexity(application);
            if(complexity >= 18 && application.getNum_CSP_per_EIS() == 3 && application.getNum_EIS_per_Country() == 3 ){
                application.setComplexity(complexity);
                applicationRepository.save(application);
                i++;
            }
        }
    }

    public void generateApp1() {
        //generate Applications
        List<CSP> csps = cspRepository.findAll();
        int i = 0;
        while(i < 20){
            int numEIS = 1+(int)(Math.random()*3);
            int numCSP = 1+(int)(Math.random()*3);
            double  budget = numEIS * Math.random() * 10 + numCSP*numEIS*Math.random() * 4;
            Application application = new Application(numEIS,numCSP, budget,Math.random()*70 + 40);
            application.addAll(areaRepository.findAll());
            setCPUIntensive(application);
//            for(CSP csp:csps){
//                double x = Math.random();
//                if(x < 0.1){
//                    application.addpreferedCSPs(csp);
//                }else {
//                    if(x > 0.9){
//                        application.addunpreferedCSPs(csp);
//                    }
//                }
//            }
            int complexity = checkComplexity(application);
            if(complexity >= 18 && complexity <= 27){
                application.setComplexity(complexity);
                applicationRepository.save(application);
                i++;
            }
        }
    }

    public  void generateApp2() {
        //generate Applications
        List<CSP> csps = cspRepository.findAll();
        int i = 0;
        while(i < 20){
            int numEIS = 1+(int)(Math.random()*3);
            int numCSP = 1+(int)(Math.random()*3);
            double  budget = numEIS * Math.random() * 10 + numCSP*numEIS*Math.random() * 4;
            Application application = new Application(numEIS,numCSP, budget,Math.random()*70 + 40);
            application.addAll(areaRepository.findAll());
            setDataIntensive(application);
            for(CSP csp:csps){
                double x = Math.random();
                if(x < 0.1){
                    application.addpreferedCSPs(csp);
                }else {
                    if(x > 0.9){
                        application.addunpreferedCSPs(csp);
                    }
                }
            }
            int complexity = checkComplexity(application);
            if(complexity >= 18 && complexity <= 27){
                application.setComplexity(complexity);
                applicationRepository.save(application);
                i++;
            }
        }
    }

    public void generateApp3() {
        //generate Applications
        List<CSP> csps = cspRepository.findAll();
        int i = 0;
        while(i < 20){
            Application application = new Application(1+(int)(Math.random()*3),1+(int)(Math.random()*3), Math.random(),Math.random()*30 + 20);
            application.addAll(areaRepository.findAll());
            setCommunicationIntensive(application);
            for(CSP csp:csps){
                double x = Math.random();
                if(x < 0.1){
                    application.addpreferedCSPs(csp);
                }else {
                    if(x > 0.9){
                        application.addunpreferedCSPs(csp);
                    }
                }
            }
            int complexity = checkComplexity(application);
            if(complexity >= 15 && complexity <= 27){
                application.setComplexity(complexity);
                applicationRepository.save(application);
                i++;
            }
        }
    }

    private static void setAppType(Application application, double bandwidth, int cpu_num, double cpu_frequency, int disk_size, int memory){
//                bandwidth[100MB, 10GB]
        application.setBandwidth(bandwidth);
//                cpu cores [1, 8]
        application.setNum_cpus(cpu_num);
//                cpu frequency [1.8, 4]
        application.setCpu_frequency(cpu_frequency);
//                disk size [100GB, 10000GB]
        application.setDisk_size(disk_size);
//                memory size [10MB, 10GB]
        application.setMem_size(memory);
    }

    private static void setCPUIntensive(Application application){
        setAppType(application,
                //                bandwidth[100MB, 1GB]
                RandomUtils.nextDouble(100, 1000),
                //                cpu cores [4, 8]
                RandomUtils.nextInt(4, 8),
                //                cpu frequency [1.8, 4]
                RandomUtils.nextDouble(1.8, 4),
                //                disk size [2GB, 100GB]
                RandomUtils.nextInt(2, 100),
                //                memory size [10MB, 5GB]
                RandomUtils.nextInt(10, 5000)
        );
    }

    private static void setDataIntensive(Application application){
        setAppType(application,
                //                bandwidth[100MB, 2GB]
                RandomUtils.nextDouble(100, 2000),
                //                cpu cores [1, 4]
                RandomUtils.nextInt(1, 4),
                //                cpu frequency [1.8, 2.2]
                RandomUtils.nextDouble(1.8, 2.2),
                //                disk size [100GB, 10000GB]
                RandomUtils.nextInt(100, 10000),
                //                memory size [100MB, 10GB]
                RandomUtils.nextInt(100, 10000)
        );
    }

    private static void setCommunicationIntensive(Application application){
        setAppType(application,
                //                bandwidth[100MB, 10 GB]
                RandomUtils.nextDouble(100, 10000),
                //                cpu cores [1, 4]
                RandomUtils.nextInt(1, 4),
                //                cpu frequency [1.8, 2.2]
                RandomUtils.nextDouble(1.8, 2.2),
                //                disk size [2GB, 20GB]
                RandomUtils.nextInt(2, 20),
                //                memory size [100MB, 1GB]
                RandomUtils.nextInt(100, 1000)
        );
    }

    public int checkComplexity(Application application){
        Set<CSP> likeCSP= application.getPreferedCSPs();
        Set<CSP> unlikeCSP= application.getUnpreferedCSPs();
        List<ServiceForm> testTable = tableRepository.retrieveAllServiceByAreasIn(application.getAppareas());
        ServiceTable originalTable = new ServiceTable();
        originalTable.addAll(testTable);
        List<EIS> testEIS = eisRepository.findEISByCapability(application.getBandwidth(),application.getCpu_frequency(),application.getDisk_size(),application.getMem_size(),application.getNum_cpus());
        Functions.filterTable(testTable,unlikeCSP,testEIS,application.getLatency(),application.getBudget(),application.getNum_EIS_per_Country());
        ServiceTable filterTable = new ServiceTable();
        filterTable.addAll(testTable);
        System.out.println("Filtered table:");
        System.out.println("number of EIS:"+filterTable.getUsedEIS().size());
        System.out.println("number of CSP:"+filterTable.getUsedCSP().size());
        System.out.println("number of all service lines:"+filterTable.getList().size());
        Iterator<ServiceForm> iterator = testTable.iterator();
        while (iterator.hasNext()) {
            ServiceForm serviceForm = iterator.next();
            if(!filterTable.checkSingleEISNumberOfCSP(serviceForm.getEis(),application.getNum_CSP_per_EIS())){
                iterator.remove();
            }
        }
        filterTable = new ServiceTable();
        filterTable.addAll(testTable);
        int n = testTable.size();
        if(!filterTable.checkCSP(likeCSP)) {
            System.out.println("Too much liked CSP, no result, next one");
            return -1;
        }
        return n;
    }

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
        Functions.filterTable(testTable,unlikeCSP,testEIS,application.getLatency(),application.getBudget(),application.getNum_EIS_per_Country());
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
        Functions.filterTable(testTable,unlikeCSP,testEIS,application.getLatency(),application.getBudget(),application.getNum_EIS_per_Country());
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
            while (hasResult && !optimalComb.checkNumberOfCSP(application.getNum_CSP_per_EIS())) {
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
        Functions.filterTable(testTable,unlikeCSP,testEIS,application.getLatency());
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
        sw.start("MILP algorithm");
        filterTable.orderListByLatency();
        int n = filterTable.getList().size();
        ServiceTable optimalComb = new ServiceTable();
        double optimalcost = -1;
        BinaryRepresent binaryRepresent1 = new BinaryRepresent(n,1);
        binaryRepresent1.getBinaryRepresent()[0] = 1;
        binaryRepresent1.setLatencySum(filterTable.getList().get(0).getLatency().getDelay());
        BinaryRepresent binaryRepresent0 = new BinaryRepresent(n,1);
        binaryRepresent0.getBinaryRepresent()[0] = 0;
        binaryRepresent0.setLatencySum(filterTable.getList().get(1).getLatency().getDelay());
        PriorityQueue<BinaryRepresent> minHeap = new PriorityQueue<>(Comparator.comparingDouble(s -> s.getLatencySum()));
        minHeap.add(binaryRepresent0);
        minHeap.add(binaryRepresent1);
        while(!minHeap.isEmpty()){
            BinaryRepresent binaryRepresent = minHeap.poll();
            ServiceTable cur = binaryRepresent.transfer(filterTable);
            int valid = cur.validBnB(application);
            if(valid == 1){
                optimalComb = cur;
                break;
            }
            if(valid == -1){
                continue;
            }
            int preCount = binaryRepresent.getValidCount();
            double preLatency = cur.getLatencySum();
            if(valid == 0 && preCount < n){
                BinaryRepresent select = new BinaryRepresent(n,preCount+1);
                select.getBinaryRepresent()[preCount] = 1;
                select.setLatencySum(preLatency+filterTable.getList().get(preCount).getLatency().getDelay());
                if(preCount != n-1){
                    BinaryRepresent unselect = new BinaryRepresent(n,preCount+1);
                    unselect.getBinaryRepresent()[preCount] = 0;
                    unselect.setLatencySum(preLatency+filterTable.getList().get(preCount+1).getLatency().getDelay());
                }
            }
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
