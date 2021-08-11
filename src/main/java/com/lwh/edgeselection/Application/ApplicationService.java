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
public class ApplicationService {
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
                            Math.random() * lower + (upper-lower),
                            lower,
                            upper);
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

    public void generateApp1(int numOfApplication) {
        //generate Applications
        List<CSP> csps = cspRepository.findAll();
        int i = 0;
        while(i < numOfApplication){
//            int numEIS = 1+(int)(Math.random()*3);
//            int numCSP = 1+(int)(Math.random()*3);
            int numEIS = 3;
            int numCSP = 3;
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
            if(complexity >= 20 && complexity <= 27){
                application.setComplexity(complexity);
                applicationRepository.save(application);
                i++;
            }
        }
    }

    public  void generateApp2(int numOfApplication) {
        //generate Applications
        List<CSP> csps = cspRepository.findAll();
        int i = 0;
        while(i < numOfApplication){
            int numEIS = 1+(int)(Math.random()*3);
            int numCSP = 1+(int)(Math.random()*3);
            double  budget = numEIS * Math.random() * 10 + numCSP*numEIS*Math.random() * 4;
            Application application = new Application(numEIS,numCSP, budget,Math.random()*70 + 40);
            application.addAll(areaRepository.findAll());
//            Application application = new Application(1+(int)(Math.random()*3),1+(int)(Math.random()*3), Math.random(),Math.random()*30 + 20);
//            application.addAll(areaRepository.findAll());
            setDataIntensive(application);
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
            if(complexity >= 20 && complexity <= 27){
                application.setComplexity(complexity);
                applicationRepository.save(application);
                i++;
            }
        }
    }

    public void generateApp3(int numOfApplication) {
        //generate Applications
        List<CSP> csps = cspRepository.findAll();
        int i = 0;
        while(i < numOfApplication){
            int numEIS = 1+(int)(Math.random()*3);
            int numCSP = 1+(int)(Math.random()*3);
            double  budget = numEIS * Math.random() * 10 + numCSP*numEIS*Math.random() * 4;
            Application application = new Application(numEIS,numCSP, budget,Math.random()*70 + 40);
            application.addAll(areaRepository.findAll());
//            Application application = new Application(1+(int)(Math.random()*3),1+(int)(Math.random()*3), Math.random(),Math.random()*30 + 20);
//            application.addAll(areaRepository.findAll());
            setCommunicationIntensive(application);
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
            if(complexity >= 20 && complexity <= 27){
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
