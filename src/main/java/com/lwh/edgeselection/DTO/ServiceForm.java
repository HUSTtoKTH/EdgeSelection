package com.lwh.edgeselection.DTO;

import com.lwh.edgeselection.domain.Area;
import com.lwh.edgeselection.domain.CSP;
import com.lwh.edgeselection.domain.EIS;
import com.lwh.edgeselection.domain.Latency;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceForm {
    private EIS eis;
    private CSP csp;
    private Area area;
    private Latency latency;
    double cost;

    public ServiceForm() {
    }

    public ServiceForm(EIS eis, CSP csp) {
        this.eis = eis;
        this.csp = csp;
        this.latency = null;
        this.cost = eis.getCost_second() + csp.getCost_scond();
    }

    public ServiceForm(EIS eis, CSP csp, Latency latency) {
        this.eis = eis;
        this.csp = csp;
        this.latency = latency;
        this.cost = eis.getCost_second() + csp.getCost_scond();
    }

    public ServiceForm(EIS eis, CSP csp, Area area, Latency latency) {
        this.eis = eis;
        this.csp = csp;
        this.area = area;
        this.latency = latency;
        this.cost = eis.getCost_second() + csp.getCost_scond();
    }



}
