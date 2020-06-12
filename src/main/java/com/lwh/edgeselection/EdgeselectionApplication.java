package com.lwh.edgeselection;

import com.lwh.edgeselection.Application.ServiceFunctionCost;
import com.lwh.edgeselection.DTO.FormForExcel;
import com.lwh.edgeselection.domain.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.*;

@SpringBootApplication
public class EdgeselectionApplication {

    public static void main(String[] args) {
        SpringApplication.run(EdgeselectionApplication.class, args);
    }
//    @Bean
//    public CommandLineRunner mappingDemo(ServiceFunction serviceFunction, ApplicationRepository applicationRepository) {
////        return args -> {
////            serviceFunction.generateEIS();
////            serviceFunction.generateNewApp1();
////            serviceFunction.generateApp3();
//
////            serviceFunction.updateLatency(30,20);
////            serviceFunction.updateReliability(1,3);
////Selection By explicit
//                List<Application> applications = applicationRepository.findAll();
//                List<FormForExcel> excels = new ArrayList<>();
//                for (Application application : applications) {
//                    ExecutorService executor = Executors.newSingleThreadExecutor();
////                    Future<FormForExcel> future = executor.submit(new Exhaustive(serviceFunction,application));
////                    Future<FormForExcel> future = executor.submit(new BestFit(serviceFunction, application));
//                    double old = application.getLatency();
//                    application.setLatency(old*0.9);
////                    int old = application.getNum_EIS_per_Country();
////                    application.setNum_EIS_per_Country(old-2);
////                    int old = application.getNum_CSP_per_EIS();
////                    application.setNum_CSP_per_EIS(old-2);
//                    Future<FormForExcel> future = executor.submit(new BestFitV2(serviceFunction, application));
//                    try {
//                        System.out.println("Started..");
//                        excels.add(future.get(20, TimeUnit.MINUTES));
//                        System.out.println("Finished!");
//                    } catch (TimeoutException e) {
//                        future.cancel(true);
//                        FormForExcel formForExcel = new FormForExcel();
//                        formForExcel.setResult("timeout");
//                        formForExcel.setTime(-1);
//                        excels.add(formForExcel);
//                        System.out.println("Terminated!");
//                    }
//                    executor.shutdownNow();
//                }
////            Functions.writeExcel(excels, "./result/bestfitV1App1.xlsx");
//            Functions.writeExcel(excels, "./result/new/bestfitV2NewApp1Latency90.xlsx");
////                Functions.writeExcel(excels,"./result/15-27App1.xlsx");
//        };
//    }
}

class Exhaustive implements Callable<FormForExcel> {
    ServiceFunctionCost serviceFunctionCost;
    Application application;

    public Exhaustive(ServiceFunctionCost serviceFunctionCost, Application application) {
        this.serviceFunctionCost = serviceFunctionCost;
        this.application = application;
    }

    @Override
    public FormForExcel call() throws Exception {
        return serviceFunctionCost.bruteForce(application);
    }
}

class BestFit implements Callable<FormForExcel> {
    ServiceFunctionCost serviceFunctionCost;
    Application application;

    public BestFit(ServiceFunctionCost serviceFunctionCost, Application application) {
        this.serviceFunctionCost = serviceFunctionCost;
        this.application = application;
    }

    @Override
    public FormForExcel call() throws Exception {
        return serviceFunctionCost.BestFit(application);
    }
}

class BestFitV2 implements Callable<FormForExcel> {
    ServiceFunctionCost serviceFunctionCost;
    Application application;

    public BestFitV2(ServiceFunctionCost serviceFunctionCost, Application application) {
        this.serviceFunctionCost = serviceFunctionCost;
        this.application = application;
    }

    @Override
    public FormForExcel call() throws Exception {
        return serviceFunctionCost.BestFitV2(application);
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