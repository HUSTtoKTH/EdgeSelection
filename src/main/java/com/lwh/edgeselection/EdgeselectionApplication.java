package com.lwh.edgeselection;

import com.lwh.edgeselection.Functions.Functions;
import com.lwh.edgeselection.domain.*;
import com.lwh.edgeselection.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class EdgeselectionApplication {

    public static void main(String[] args) {
        SpringApplication.run(EdgeselectionApplication.class, args);
    }
    @Bean
    public CommandLineRunner mappingDemo(EISRepository eisRepository, AreaRepository areaRepository,
                                         LatencyRepository latencyRepository, CSPRepository cspRepository,
                                         TableRepository tableRepository, ApplicationRepository applicationRepository) {
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
//            for(int i = 0; i < 20; i++){
//                Application application = new Application(1+(int)(Math.random()*3),1+(int)(Math.random()*3), Math.random(),Math.random()*30 + 20);
//                applicationRepository.save(application);
//            }

            List<Application> applications = applicationRepository.findAll();
            for(Application application:applications){
                StopWatch sw = new StopWatch("test");
                System.out.println("Application" + application.getId() + ":");
                sw.start("retrive All Service By Areas");
                List<ServiceForm> testTable = tableRepository.retriveAllServiceByAreasIn(Arrays.asList("test"));
                sw.stop();
                sw.start("find EIS qualified");
                List<EIS> testEIS = eisRepository.findByCapacityGreaterThanEqual(application.getCapacity());
                sw.stop();
                List<CSP> unlikeCSP= new ArrayList<>();
                sw.start("filter table by unlikeCSP, unqualified EIS, latency");
                Functions.filterTable(testTable,unlikeCSP,testEIS,application.getLatency());
                sw.stop();
                sw.start("find all combination");
                List<int[]> combinationBinary = Functions.CombinationBinary(testTable);
//                List<ServiceTable> allcombination = Functions.CombinationResult(testTable);
                sw.stop();
                sw.start("check reliability and calculate cost for each combination");
                ServiceTable optimalComb = new ServiceTable();
                double optimalcost = -1;
                for(int[] binary : combinationBinary){
                    ServiceTable serviceTable = Functions.binaryToServiceTable(binary, testTable);
                    if(serviceTable.checkReliability(application.getNum_EIS_per_Country(),application.getNum_CSP_per_EIS())){
                        double cost = serviceTable.calculateCost();
                        if(optimalcost > cost || optimalcost == -1){
                            optimalcost = cost;
                            optimalComb = serviceTable;
                        }
                    }
                }
                sw.stop();

                System.out.println("opimal cost: "+ optimalcost);
                System.out.println(optimalComb);
//                for(ServiceTable serviceTable : allcombination){
//                    if(serviceTable.checkReliability(application.getNum_EIS_per_Country(),application.getNum_CSP_per_EIS())){
//                        double cost = serviceTable.calculateCost();
//                        if(optimalcost > cost && optimalcost != -1){
//                            optimalcost = cost;
//                            optimalComb = serviceTable;
//                        }
//                    }
//                    System.out.println("opimal cost: "+ optimalcost);
//                    System.out.println(optimalComb);
//                }
                System.out.println("sw.prettyPrint()~~~~~~~~~~~~~~~~~");
                System.out.println(sw.prettyPrint());
            }



        };
    }

}
