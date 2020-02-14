package com.lwh.edgeselection;

import com.lwh.edgeselection.Functions.Functions;
import com.lwh.edgeselection.domain.*;
import com.lwh.edgeselection.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StopWatch;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class EdgeselectionApplication {

    public static void main(String[] args) {
        SpringApplication.run(EdgeselectionApplication.class, args);
    }
    @Bean
    public CommandLineRunner mappingDemo(EISRepository eisRepository, AreaRepository areaRepository,
                                         LatencyRepository latencyRepository, CSPRepository cspRepository,
                                         TableRepository tableRepository, ApplicationRepository applicationRepository,
                                         PreferedCSPRepository preferedCSPRepository, UnpreferedCSPRepository unpreferedCSPRepository) {
        return args -> {

//            List<ServiceForm> testTable = tableRepository.retriveAllServiceByAreasIn(Arrays.asList("Sweden","Danmark"));
//            List<EIS> testEIS = eisService.findByCapacity(-1,-1,-1,-1,-1);
//            List<CSP> unlikeCSP= new ArrayList<>();
//            unlikeCSP.add(cspRepository.findById(5));
//            Functions.filterTable(testTable,unlikeCSP,testEIS,30);
//            List<List<ServiceForm>> allcombination = Functions.CombinationResult(testTable);
//            int i = 0;
//            for(List<ServiceForm> combination : allcombination){
//                System.out.println(i);
//                i++;
//                System.out.println(Functions.calculateCost(combination));
//            }
//            System.out.println("done");
//
//            cspRepository.findByAreasInAndIdIsNotIn()
//            Area area1 = new Area("Danmark");
//            Area area2 = new Area("Sweden");
//            areaRepository.save(area1);
//            areaRepository.save(area2);
//            for(AreaEnum name:AreaEnum.values()){
//                Area area = new Area(String.valueOf(name));
//                areaRepository.save(area);
//            }

//            generate EISs
//            Area testArea = new Area("test");
//            areaRepository.save(testArea);
//            List<CSP> csps = new ArrayList<>();
//            for(int i = 0; i < 10; i++){
//                CSP csp = new CSP(Math.random()*10);
//                cspRepository.save(csp);
//                csp.getAreas().add(testArea);
//                cspRepository.save(csp);
//                csps.add(csp);
//            }
//            for(int i = 0; i < 50; i++){
//                EIS eis = new EIS(Math.random(), Math.random()*10);
//                eis.setBandwidth((int)(Math.random()*90)+10);
//                eis.setNum_cpus(1+(int)(Math.random()*16));
//                eis.setCpu_frequency(Math.random()*4);
//                eis.setDisk_size(500+(int)(Math.random()*499500));
//                eis.setMem_size(1000+(int)(Math.random()*29000));
//                eisRepository.save(eis);
//                for(CSP csp:csps){
//                    if(Math.random() > 0.5){
//                        eis.getCsps().add(csp);
//                        Latency latency = new Latency(testArea.getId(), csp.getId(), eis.getId(), Math.random()*30 + 20);
//                        latencyRepository.save(latency);
//                    }
//                }
//                eisRepository.save(eis);
//            }

//            generate Applications
//            List<CSP> csps = cspRepository.findAll();
//            for(int i = 0; i < 20; i++){
//                Application application = new Application(1+(int)(Math.random()*3),1+(int)(Math.random()*3), Math.random(),Math.random()*30 + 20);
//                application.setBandwidth((int)(Math.random()*90)+10);
//                application.setNum_cpus(1+(int)(Math.random()*16));
//                application.setCpu_frequency(Math.random()*4);
//                application.setDisk_size(500+(int)(Math.random()*499500));
//                application.setMem_size(1000+(int)(Math.random()*29000));
//                applicationRepository.save(application);
//                for(CSP csp:csps){
//                    if(Math.random() < 0.33){
//                        preferedCSPRepository.save(new PreferedCSP(application.getId(),csp.getId()));
//                    }else {
//                        if(Math.random() < 0.5){
//                            unpreferedCSPRepository.save(new UnpreferedCSP(application.getId(), csp.getId()));
//                        }
//                    }
//                }
//            }



//Selection By abstract
//            List<Application> applications = applicationRepository.findAll();
//            for(Application application:applications){
//                StopWatch sw = new StopWatch("test");
//                System.out.println(application);
//                sw.start("retrive All Service By Areas");
//                List<ServiceForm> testTable = tableRepository.retriveAllServiceByAreasIn(Arrays.asList("test"));
//                sw.stop();
//                sw.start("find EIS qualified");
//                List<EIS> testEIS = eisRepository.findByCapacityGreaterThanEqual(application.getCapacity());
//                sw.stop();
//                List<CSP> unlikeCSP= new ArrayList<>();
//                sw.start("filter table by unlikeCSP, unqualified EIS, latency");
//                Functions.filterTable(testTable,unlikeCSP,testEIS,application.getLatency());
//                sw.stop();
//                sw.start("check reliability and calculate cost for each combination");
//                ServiceTable optimalComb = new ServiceTable();
//                double optimalcost = -1;
//                int n = testTable.size();
//                //number of possible combination ：2^n
//                BigInteger max = new BigInteger("2");
//                max = max.pow(n);
//                System.out.println("number of possible combination：2^"+n + ", "+ max);
//                BigInteger min = BigInteger.ZERO;
//                while(!max.equals(min)){
//                    ServiceTable serviceTable = new ServiceTable();
//                    min = min.add(BigInteger.ONE);
//                    char[] binary = min.toString(2).toCharArray();
//                    for(int i = binary.length - 1; i >= 0; i--){
//                        if(binary[i] == '1'){
//                            serviceTable.add(testTable.get(i));
//                        }
//                    }
//                    if(serviceTable.checkReliability(application.getNum_EIS_per_Country(),application.getNum_CSP_per_EIS())){
//                        double cost = serviceTable.calculateCost();
//                        if(optimalcost > cost || optimalcost == -1){
//                            optimalcost = cost;
//                            optimalComb = serviceTable;
//                        }
//                    }
//                }
//                System.out.println(min);
//                sw.stop();
//                System.out.println("opimal cost: "+ optimalcost);
//                System.out.println(optimalComb);
//                System.out.println("sw.prettyPrint()~~~~~~~~~~~~~~~~~");
//                System.out.println(sw.prettyPrint());
//            }


//Selection By explicit
            List<Application> applications = applicationRepository.findAll();
            for(Application application:applications){
                StopWatch sw = new StopWatch("test");
                System.out.println(application);
                List<CSP> unlikeCSP= new ArrayList<>();
                List<CSP> likeCSP= new ArrayList<>();
                System.out.print("Prefered CSP: ");
                for(PreferedCSP preferedCSP:preferedCSPRepository.findAllByApplicationidEquals(application.getId())){
                    System.out.print(preferedCSP.getCSPid()+" ");
                    likeCSP.add(cspRepository.findById((long)preferedCSP.getCSPid()));
                }
                System.out.println();
                System.out.print("UnPrefered CSP: ");
                for(UnpreferedCSP unpreferedCSP: unpreferedCSPRepository.findByApplicationidEquals(application.getId())){
                    System.out.print(unpreferedCSP.getCSPid()+" ");
                    unlikeCSP.add(cspRepository.findById((long)unpreferedCSP.getCSPid()));
                }
                System.out.println();
                sw.start("retrive All Service By Areas");
                List<ServiceForm> testTable = tableRepository.retriveAllServiceByAreasIn(Arrays.asList("test"));
                sw.stop();
                sw.start("find EIS qualified");
                List<EIS> testEIS = eisRepository.findEISByCapability(application.getBandwidth(),application.getCpu_frequency(),application.getDisk_size(),application.getMem_size(),application.getNum_cpus());
                sw.stop();
                sw.start("filter table by unlikeCSP, unqualified EIS, latency");
                Functions.filterTable(testTable,unlikeCSP,testEIS,application.getLatency());
                sw.stop();
                sw.start("check reliability and calculate cost for each combination");
                ServiceTable optimalComb = new ServiceTable();
                double optimalcost = -1;
                int n = testTable.size();
                if(n == 0) {
                    System.out.println("no result");
                    continue;
                }
                //number of possible combination ：2^n
                BigInteger max = new BigInteger("2");
                max = max.pow(n);
                System.out.println("number of possible combination：2^"+n + ", "+ max);
                BigInteger min = BigInteger.ZERO;
                while(!max.equals(min)){
                    ServiceTable serviceTable = new ServiceTable();
                    min = min.add(BigInteger.ONE);
                    char[] binary = min.toString(2).toCharArray();
                    for(int i = binary.length - 1; i >= 0; i--){
                        if(binary[i] == '1'){
                            serviceTable.add(testTable.get(i));
                        }
                    }
                    if(serviceTable.checkReliability(application.getNum_EIS_per_Country(),application.getNum_CSP_per_EIS()) &&
                    serviceTable.checkCSP(likeCSP)){
                        double cost = serviceTable.calculateCost();
                        if(optimalcost > cost || optimalcost == -1){
                            optimalcost = cost;
                            optimalComb = serviceTable;
                        }
                    }
                }
                System.out.println(min);
                sw.stop();
                System.out.println("opimal cost: "+ optimalcost);
                System.out.println(optimalComb);
                System.out.println("sw.prettyPrint()~~~~~~~~~~~~~~~~~");
                System.out.println(sw.prettyPrint());
            }
        };
    }

}
