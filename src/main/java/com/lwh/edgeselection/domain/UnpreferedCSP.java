package com.lwh.edgeselection.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "UnpreferedCSP")
@Getter
@Setter
public class UnpreferedCSP {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private Long CSPid;
    private Long applicationid;

    public UnpreferedCSP() {
    }

    public UnpreferedCSP(long applicationid, long csp_id) {
        this.applicationid = applicationid;
        this.CSPid = csp_id;
    }

}

