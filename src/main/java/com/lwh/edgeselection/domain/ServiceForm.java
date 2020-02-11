package com.lwh.edgeselection.domain;

public class ServiceForm {
    private EIS eis;
    private CSP csp;
    private Area area;
    private Latency latency;


    public ServiceForm(EIS eis, CSP csp) {
        this.eis = eis;
        this.csp = csp;
        this.latency = null;
    }

    public ServiceForm(EIS eis, CSP csp, Latency latency) {
        this.eis = eis;
        this.csp = csp;
        this.latency = latency;
    }

    public ServiceForm(EIS eis, CSP csp, Area area, Latency latency) {
        this.eis = eis;
        this.csp = csp;
        this.area = area;
        this.latency = latency;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public EIS getEis() {
        return eis;
    }

    public void setEis(EIS eis) {
        this.eis = eis;
    }

    public CSP getCsp() {
        return csp;
    }

    public void setCsp(CSP csp) {
        this.csp = csp;
    }

    public Latency getLatency() {
        return latency;
    }

    public void setLatency(Latency latency) {
        this.latency = latency;
    }
}
