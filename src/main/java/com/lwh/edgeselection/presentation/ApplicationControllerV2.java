package com.lwh.edgeselection.presentation;


import com.lwh.edgeselection.Application.AreaService;
import com.lwh.edgeselection.Application.CSPService;
import com.lwh.edgeselection.Application.EISService;
import com.lwh.edgeselection.Application.ServiceFunction;
import com.lwh.edgeselection.Functions.Functions;
import com.lwh.edgeselection.domain.*;
import com.lwh.edgeselection.repository.ApplicationRepository;
import com.lwh.edgeselection.repository.EISRepository;
import com.lwh.edgeselection.repository.LatencyRepository;
import com.lwh.edgeselection.repository.TableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Set;

@Controller
public class ApplicationControllerV2 {
    @Autowired
    private CSPService cspService;
    @Autowired
    private EISService eisService;
    @Autowired
    private AreaService areaService;
    @Autowired
    private LatencyRepository latencyRepository;
    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private TableRepository tableRepository;
    @Autowired
    private EISRepository eisRepository;
    @Autowired
    private ServiceFunction serviceFunction;

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
        System.out.println("Prefered CSP: ");
        for(CSP csp: likeCSP){
            System.out.print(csp.getId()+" ");
        }
        System.out.println();
        System.out.print("UnPrefered CSP: ");
        Set<CSP> unlikeCSP = application.getUnpreferedCSPs();
        for(CSP csp: unlikeCSP){
            System.out.print(csp.getId()+" ");
        }
        System.out.println();
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
        applicationRepository.save(application);
        ServiceTable result = serviceFunction.bruteForceForWeb(application);
        model.addAttribute("result",result);
        return "selectionoutput";
//        return result.toString();
    }
}
