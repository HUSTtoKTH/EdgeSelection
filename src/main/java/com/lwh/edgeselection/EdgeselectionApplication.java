package com.lwh.edgeselection;

import com.lwh.edgeselection.Application.EISService;
import com.lwh.edgeselection.Functions.Functions;
import com.lwh.edgeselection.domain.CSP;
import com.lwh.edgeselection.domain.EIS;
import com.lwh.edgeselection.domain.ServiceForm;
import com.lwh.edgeselection.repository.AreaRepository;
import com.lwh.edgeselection.repository.CSPRepository;
import com.lwh.edgeselection.repository.LatencyRepository;
import com.lwh.edgeselection.repository.TableRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class EdgeselectionApplication {

    public static void main(String[] args) {
        SpringApplication.run(EdgeselectionApplication.class, args);
    }
    @Bean
    public CommandLineRunner mappingDemo(EISService eisService, AreaRepository areaRepository, LatencyRepository latencyRepository, CSPRepository cspRepository, TableRepository tableRepository) {
        return args -> {

            List<ServiceForm> testTable = tableRepository.retriveAllServiceByAreasIn(Arrays.asList("Sweden","Danmark"));
//            System.out.println("done");
            List<EIS> testEIS = eisService.findByCapacity(-1,-1,-1,-1,-1);


            List<CSP> unlikeCSP= new ArrayList<>();
            unlikeCSP.add(cspRepository.findById(5));
//            List<CSP> likeCSP = new ArrayList<>();
//            likeCSP.add(cspRepository.findById(1));
//            likeCSP.add(cspRepository.findById(2));
//            likeCSP.add(cspRepository.findById(4));

            Functions.filterTable(testTable,unlikeCSP,testEIS,30);
            List<List<ServiceForm>> allcombination = Functions.CombinationResult(testTable);
            int i = 0;
            for(List<ServiceForm> combination : allcombination){
                System.out.println(i);
                i++;
                System.out.println(Functions.calculateCost(combination));
            }
            System.out.println("done");

//            cspRepository.findByAreasInAndIdIsNotIn()
//            Area area1 = new Area("Danmark");
//            Area area2 = new Area("Sweden");
//            areaRepository.save(area1);
//            areaRepository.save(area2);
//            for(AreaEnum name:AreaEnum.values()){
//                Area area = new Area(String.valueOf(name));
//                areaRepository.save(area);
//            }
        };
    }

}
