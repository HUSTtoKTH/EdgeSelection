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
import java.util.concurrent.*;

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


//            Functions.generateEIS(eisRepository,areaRepository,latencyRepository,cspRepository);
            Functions.generateApp1 (cspRepository,applicationRepository,preferedCSPRepository,unpreferedCSPRepository,areaRepository);
//            Functions.generateApp(cspRepository,applicationRepository,preferedCSPRepository,unpreferedCSPRepository);

//Selection By explicit
            List<Application> applications = applicationRepository.findAll();
            for(Application application:applications){
                ExecutorService executor = Executors.newSingleThreadExecutor();
                Future<ServiceTable> future = executor.submit(new Task(tableRepository,eisRepository,applicationRepository,application));
                try {
                    System.out.println("Started..");
                    System.out.println(future.get(10, TimeUnit.MINUTES));
                    System.out.println("Finished!");
                } catch (TimeoutException e) {
                    future.cancel(true);
                    System.out.println("Terminated!");
                }
                executor.shutdownNow();
//                Functions.bruteForce(tableRepository,eisRepository,applicationRepository,application);
            }
        };
    }
}

class Task implements Callable<ServiceTable> {
    TableRepository tableRepository;
    EISRepository eisRepository;
    ApplicationRepository applicationRepository;
    Application application;

    public Task(TableRepository tableRepository, EISRepository eisRepository, ApplicationRepository applicationRepository, Application application) {
        this.tableRepository = tableRepository;
        this.eisRepository = eisRepository;
        this.applicationRepository = applicationRepository;
        this.application = application;
    }

    @Override
    public ServiceTable call() throws Exception {
        return Functions.bruteForce(tableRepository,eisRepository,applicationRepository,application);
    }
}








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



//            cspRepository.findByAreasInAndIdIsNotIn()
//            Area area1 = new Area("Danmark");
//            Area area2 = new Area("Sweden");
//            areaRepository.save(area1);
//            areaRepository.save(area2);
//            for(AreaEnum name:AreaEnum.values()){
//                Area area = new Area(String.valueOf(name));
//                areaRepository.save(area);
//            }