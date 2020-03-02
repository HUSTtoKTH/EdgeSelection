package com.lwh.edgeselection.presentation;


import com.lwh.edgeselection.Application.AreaService;
import com.lwh.edgeselection.Application.CSPService;
import com.lwh.edgeselection.Application.EISService;
import com.lwh.edgeselection.Application.LatencyService;
import com.lwh.edgeselection.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller
public class EdgeController {
    @Autowired
    private CSPService cspService;
    @Autowired
    private EISService eisService;
    @Autowired
    private AreaService areaService;
    @Autowired
    private LatencyService latencyService;

//    @GetMapping("/test")
//    public void test(Model model) {
//        List<CSP> CSPList = cspService.getAll();
//        System.out.println(CSPList);
//    }

    @GetMapping("/cspinput")
    public String servicecspInput(Model model, HttpSession session) {
        List<Area> areaList = areaService.getAll();
        model.addAttribute("areaList", areaList);
        return "cspinput";
    }

    @PostMapping("/cspinput")
    public String servicecspInput(CSP csp, Model model) {
        cspService.save(csp);
        return "redirect:/cspselection";
    }

    @GetMapping("/cspselection")
    public String servicecspSelection(Model model) {
        List<CSP> CSPList = cspService.getAll();
        model.addAttribute("CSPList",CSPList);
        return "cspselection";
    }

    @PostMapping("/cspselection")
    public String servicecspSelection(EIS eis, RedirectAttributes attributes) {
        Set<CSP> CSPSelect = eis.getCsps();
        if (CSPSelect != null) {
            attributes.addAttribute("csps",CSPSelect);
        }
        return "redirect:/service";
    }

//    @GetMapping("/test")
//    public void test(@RequestParam(value = "csps")Set<CSP>  CSPSelect, Model model) {
//        System.out.println(CSPSelect);
//    }

    @GetMapping("/service")
    public String serviceInput(@RequestParam(value = "csps")Set<CSP> CSPSelect,
                            Model model) {
        List<CSP> CSPList = new ArrayList<>();
        CSPList.addAll(CSPSelect);
        model.addAttribute("CSPList", CSPList);
        return "serviceinput";
    }

    @PostMapping("/service")
    public String serviceInput(EIS eis, final RedirectAttributes attributes) {
        EIS eis1 = eisService.save(eis);
        if (eis1 != null) {
            attributes.addAttribute("inputEIS",eis);
        }
        return "redirect:/latency";
    }

    @GetMapping("/latency")
    public String latencyInput(@RequestParam(value = "inputEIS") EIS eis,
                            Model model) {
        List<Latency> latencies = new ArrayList<>();
        for(CSP csp:eis.getCsps()){
            for(Area area:csp.getCspareas()){
                latencies.add(new Latency(
                    area.getId(),csp.getId(),eis.getId(),
                        area.getName(),csp.getName(),eis.getName()
                ));
            }
        }
        LatencyListContainer latencyList = new LatencyListContainer();
        latencyList.setLatencies(latencies);
        model.addAttribute("form", latencyList);
        return "latencyinput";
    }

    @PostMapping("/latency")
    public String latencyInput(LatencyListContainer form) {
        latencyService.saveAll(form.getLatencies());
        return "redirect:/cspselection";
    }


}
