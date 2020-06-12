package com.lwh.edgeselection.presentation;


import com.lwh.edgeselection.Application.*;
import com.lwh.edgeselection.DTO.ServiceForm;
import com.lwh.edgeselection.DTO.ServiceTable;
import com.lwh.edgeselection.Functions.Functions;
import com.lwh.edgeselection.domain.*;
import com.lwh.edgeselection.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Controller
public class ApplicationController {
    @Autowired
    private EISService eisService;
    @Autowired
    private CSPService cspService;
    @Autowired
    private AreaService areaService;
    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private ServiceFunctionCost serviceFunctionCost;
    @Autowired
    private TableService tableService;

    @GetMapping("/appareainput")
    public String appareaget(Model model) {
        List<Area> areaList = areaService.getAll();
        model.addAttribute("areaList", areaList);
        return "apparea";
    }

    @PostMapping("/appareainput")
    public String appareapost(Application application, Model model, RedirectAttributes attributes, HttpSession session) {
        Set<Area> areaSelect = application.getAppareas();
        if (areaSelect != null) {
//            applicationRepository.save(application);
        }
        List<CSP> CSPList = cspService.filterArea(areaSelect);
        session.setAttribute("area",areaSelect);
        attributes.addAttribute("csp",CSPList);
        return "redirect:/appcspinput";
    }


    @GetMapping("/appcspinput")
    public String appcspget(@RequestParam(value = "csp")List<CSP> CSPList,
                              Model model, HttpSession session) {
        model.addAttribute("CSPList",CSPList);
        return "appcsp";
    }

    @PostMapping("/appcspinput")
    public String appcsppost(Application application, Model model, HttpSession session) {
        Set<CSP> likeCSP = application.getPreferedCSPs();
        Set<CSP> unlikeCSP = application.getUnpreferedCSPs();
        session.setAttribute("likeCSP",likeCSP);
        session.setAttribute("unlikeCSP",unlikeCSP);
        return "redirect:/appinput";
    }

    @GetMapping("/appinput")
    public String appget(Model model, HttpSession session) {
        return "appservice";
    }

    @PostMapping("/appinput")
//    @ResponseBody
    public String apppost(Application application, Model model, HttpSession session) {
        application.setPreferedCSPs((Set<CSP>) session.getAttribute("likeCSP"));
        application.setUnpreferedCSPs((Set<CSP>) session.getAttribute("unlikeCSP"));
        application.setAppareas((Set<Area>) session.getAttribute("area"));
//        applicationRepository.save(application);
        ServiceTable result = BestFitV2ForWeb(application);
        model.addAttribute("result",result.getList());
        return "selectionoutput";
//        return result.toString();
    }

    public ServiceTable bruteForceForWeb(Application application){
        Set<CSP> likeCSP= application.getPreferedCSPs();
        Set<CSP> unlikeCSP= application.getUnpreferedCSPs();
        List<ServiceForm> testTable = tableService.retriveAllServiceByAreasIn(application.getAppareas());
        ServiceTable originalTable = new ServiceTable();
        originalTable.addAll(testTable);
        List<EIS> testEIS = eisService.findByCapacity(application.getBandwidth(),application.getCpu_frequency(),application.getDisk_size(),application.getMem_size(),application.getNum_cpus());
        Functions.filterTable(testTable,unlikeCSP,testEIS,application.getLatency());
        ServiceTable filterTable = new ServiceTable();
        filterTable.addAll(testTable);
        ServiceTable optimalComb = new ServiceTable();
        double optimalcost = -1;
        int n = testTable.size();
        if(n == 0) {
            System.out.println("no result, next one");
            return optimalComb;
        }
        if(!filterTable.checkCSP(likeCSP)) {
            System.out.println("Too much liked CSP, no result, next one");
            return optimalComb;
        }
        //number of possible combination ：2^n
        BigInteger max = new BigInteger("2");
        max = max.pow(n);
        System.out.println("number of possible combination：2^"+n + ", "+ max);
        if(n >= 28) {
            System.out.println("Too much combination, next one");
            return optimalComb;
        }
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
                double cost = serviceTable.calculateCost();
                if(optimalcost > cost || optimalcost == -1){
                    optimalcost = cost;
                    optimalComb = serviceTable;
                }
            }
            min = min.add(BigInteger.ONE);
        }
        return optimalComb;
    }


    public ServiceTable BestFitV2ForWeb(Application application){
        Set<CSP> likeCSP= application.getPreferedCSPs();
        Set<CSP> unlikeCSP= application.getUnpreferedCSPs();
        List<ServiceForm> testTable = tableService.retriveAllServiceByAreasIn(application.getAppareas());
        ServiceTable originalTable = new ServiceTable();
        originalTable.addAll(testTable);
        List<EIS> testEIS = eisService.findByCapacity(application.getBandwidth(),application.getCpu_frequency(),application.getDisk_size(),application.getMem_size(),application.getNum_cpus());
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
        return optimalComb;
    }
}
