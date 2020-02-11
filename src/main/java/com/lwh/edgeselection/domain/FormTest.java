package com.lwh.edgeselection.domain;

public class FormTest {
    private EIS eis;
    private CSP csp;
    private Area area;
    private Double latency = 0.0;

    public FormTest(EIS eis, CSP csp, Area area) {
        this.eis = eis;
        this.csp = csp;
        this.area = area;
    }

    public FormTest(EIS eis, CSP csp) {
        this.eis = eis;
        this.csp = csp;
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

    public Double getLatency() {
        return latency;
    }

    public void setLatency(Double latency) {
        this.latency = latency;
    }
}
