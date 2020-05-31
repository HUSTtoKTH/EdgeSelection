package com.lwh.edgeselection.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "preferedCSP")
@Setter
@Getter
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
}

