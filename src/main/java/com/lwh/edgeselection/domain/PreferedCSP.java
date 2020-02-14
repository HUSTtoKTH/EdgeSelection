package com.lwh.edgeselection.domain;

import javax.persistence.*;

@Entity
@Table(name = "preferedCSP")
public class PreferedCSP {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private Long CSPid;
    private Long applicationid;

    public PreferedCSP() {
    }

    public PreferedCSP(long applicationid, long csp_id) {
        this.applicationid = applicationid;
        this.CSPid = csp_id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCSPid() {
        return CSPid;
    }

    public void setCSPid(Long CSP_id) {
        this.CSPid = CSP_id;
    }

    public Long getApplicationid() {
        return applicationid;
    }

    public void setApplicationid(Long application_id) {
        this.applicationid = application_id;
    }
}

