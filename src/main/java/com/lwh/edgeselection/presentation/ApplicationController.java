package com.lwh.edgeselection.presentation;


import com.lwh.edgeselection.Application.AreaService;
import com.lwh.edgeselection.Application.CSPService;
import com.lwh.edgeselection.Application.EISService;
import com.lwh.edgeselection.domain.*;
import com.lwh.edgeselection.repository.LatencyRepository;
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
public class ApplicationController {
    @Autowired
    private CSPService cspService;
    @Autowired
    private EISService eisService;
    @Autowired
    private AreaService areaService;
    @Autowired
    private LatencyRepository latencyRepository;

    @GetMapping("/applicationinput")
    public String cspInputPage(Model model, HttpSession session) {
        List<CSP> CSPList = cspService.getAll();
        List<Area> areaList = areaService.getAll();
        List<EIS> EISList = eisService.getAll();
        model.addAttribute("CSPList",CSPList);
        model.addAttribute("areaList", areaList);
        model.addAttribute("EISList", EISList);
        return "applicationinput";
    }

    @PostMapping("/applicationinput")
    public void newCSP(Application application, Model model) {
        List<CSP> csps = cspService.filterArea(application.getAreas());
        List<EIS> eisresult = eisService.filterCSP(csps, application.getNocsps());
        model.addAttribute("results", eisresult);
        System.out.println(eisresult);
    }


}
