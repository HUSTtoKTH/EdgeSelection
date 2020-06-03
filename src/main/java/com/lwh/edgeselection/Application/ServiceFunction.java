package com.lwh.edgeselection.Application;

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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Service
public class ServiceFunction {
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


    public void generateAppRand() {
        //generate Applications
        List<CSP> csps = cspRepository.findAll();
        for(int i = 0; i < 20; i++){
            Application application = new Application(1+(int)(Math.random()*3),1+(int)(Math.random()*3), Math.random(),Math.random()*30 + 20);
//            setCPUIntensive(application);
            application.addAll(areaRepository.findAll());
            if(Math.random() < 0.33){
                setCPUIntensive(application);
            }else {
                if(Math.random() < 0.5){
                    setDataIntensive(application);
                }else {
                    setCommunicationIntensive(application);
                }
            }
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
            applicationRepository.save(application);

        }
    }

    public  void generateGaussionApp1() {
        //generate Applications
        List<CSP> csps = cspRepository.findAll();
        for(int i = 0; i < 20; i++){
            Application application = new Application(1+(int)(Math.random()*3),1+(int)(Math.random()*3), Math.random(),new NormalDistribution(35,5).sample());
            application.addAll(areaRepository.findAll());
            setCPUIntensive(application);
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

            applicationRepository.save(application);
        }
    }

    public void generateNewApp1() {
        //generate Applications
        List<CSP> csps = cspRepository.findAll();
        int i = 0;
        while(i < 20){
            Application application = new Application(3,3, Math.random(),Math.random()*30 + 20);
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
            Application application = new Application(1+(int)(Math.random()*3),1+(int)(Math.random()*3), Math.random(),Math.random()*30 + 20);
            application.addAll(areaRepository.findAll());
            setCPUIntensive(application);
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

    public  void generateApp2() {
        //generate Applications
        List<CSP> csps = cspRepository.findAll();
        int i = 0;
        while(i < 20){
            Application application = new Application(1+(int)(Math.random()*3),1+(int)(Math.random()*3), Math.random(),Math.random()*30 + 20);
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
            if(complexity >= 15 && complexity <= 27){
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
        System.out.print("Prefered CSP: ");
        StringBuilder sb = new StringBuilder();
        for(CSP csp:likeCSP){
            sb.append(csp.getId()+" ");
            System.out.print(csp.getId()+" ");
        }
        System.out.println();
        System.out.print("UnPrefered CSP: ");
        sb = new StringBuilder();
        for(CSP csp:unlikeCSP){
            sb.append(csp.getId()+" ");
            System.out.print(csp.getId()+" ");
        }
        System.out.println();
        List<ServiceForm> testTable = tableRepository.retrieveAllServiceByAreasIn(application.getAppareas());
        ServiceTable originalTable = new ServiceTable();
        originalTable.addAll(testTable);
        System.out.println("Original table:");
        System.out.println("number of EIS:"+originalTable.getUsedEIS().size());
        System.out.println("number of CSP:"+originalTable.getUsedCSP().size());
        System.out.println("number of all service lines:"+originalTable.getList().size());

        List<EIS> testEIS = eisRepository.findEISByCapability(application.getBandwidth(),application.getCpu_frequency(),application.getDisk_size(),application.getMem_size(),application.getNum_cpus());
        System.out.println("number qualified EIS:"+testEIS.size());
        Functions.filterTable(testTable,application.getUnpreferedCSPs(),testEIS,application.getLatency());
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
        while(!optimalComb.checkNumberOfCSP(application.getNum_CSP_per_EIS())){
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


    public  FormForExcel BestFitV2(Application application){
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
        while(!optimalComb.checkNumberOfCSP(application.getNum_CSP_per_EIS())){
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
