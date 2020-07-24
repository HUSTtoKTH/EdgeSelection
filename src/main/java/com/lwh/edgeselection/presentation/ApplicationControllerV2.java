package com.lwh.edgeselection.presentation;


import com.lwh.edgeselection.Application.*;
import com.lwh.edgeselection.DTO.BinaryRepresent;
import com.lwh.edgeselection.DTO.FormForExcel;
import com.lwh.edgeselection.DTO.ServiceForm;
import com.lwh.edgeselection.DTO.ServiceTable;
import com.lwh.edgeselection.Functions.Functions;
import com.lwh.edgeselection.domain.*;
import com.lwh.edgeselection.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.math.BigInteger;
import java.util.*;

@Controller
@RequestMapping("/v2")
public class ApplicationControllerV2 {
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
        return "application";
    }

    @PostMapping("/appareainput")
    public String appareapost(Application application, Model model, HttpSession session) {
        Set<Area> areaSelect = application.getAppareas();
        if (areaSelect != null) {
//            applicationRepository.save(application);
        }
        List<CSP> CSPList = cspService.filterArea(areaSelect);
        session.setAttribute("area",areaSelect);
        model.addAttribute("CSPList",CSPList);
        return "application::table_refresh";
    }


    @PostMapping("/appinput")
    public String apppost(Application application, Model model) {
        ServiceTable result = BestFitV2ForWeb(application);
        model.addAttribute("result",result.getList());
        return "application::result_refresh";
    }

    @PostMapping("/cost")
    public String cost(Application application, Model model) {
        ServiceTable result = BestFitV2ForWeb(application);
        model.addAttribute("result",result.getList());
        return "application::result_refresh";
    }

    @PostMapping("/latency")
    public String latency(Application application, Model model) {
        ServiceTable result = MILP(application);
        model.addAttribute("result",result.getList());
        return "application::result_refresh";
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
        int n = filterTable.getList().size();
        for(CSP csp:likeCSP){
            optimalComb.add(filterTable.retrieveCheapestRowBasedOnCSP(csp, application.getNum_CSP_per_EIS()));
        }
        while(!optimalComb.checkNumberOfEIS(application.getNum_EIS_per_Country())){
            ServiceForm serviceForm = filterTable.retrieveCheapestLineWithNewEIS(optimalComb, application.getNum_CSP_per_EIS());
            if(serviceForm != null){
                optimalComb.add(serviceForm);
            }else {
                return new ServiceTable();
            }

        }
        while(!optimalComb.checkNumberOfCSPGreaterEqual(application.getNum_CSP_per_EIS())){
            EIS unsatistfiedEIS = optimalComb.findLowReliabilityService(application.getNum_CSP_per_EIS());
            ServiceForm serviceForm = filterTable.retrieveCheapestRowBasedOnEIS(unsatistfiedEIS);
            if(serviceForm != null){
                optimalComb.add(serviceForm);
            }else {
                return new ServiceTable();
            }
        }
        return optimalComb;
    }

    public ServiceTable MILP(Application application){
        Set<CSP> likeCSP= application.getPreferedCSPs();
        Set<CSP> unlikeCSP= application.getUnpreferedCSPs();
        List<ServiceForm> testTable = tableService.retriveAllServiceByAreasIn(application.getAppareas());
        ServiceTable originalTable = new ServiceTable();
        originalTable.addAll(testTable);
        List<EIS> testEIS = eisService.findByCapacity(application.getBandwidth(),application.getCpu_frequency(),application.getDisk_size(),application.getMem_size(),application.getNum_cpus());
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
        filterTable.orderListByLatency();
        int n = filterTable.getList().size();
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
        return optimalComb;
    }
}
