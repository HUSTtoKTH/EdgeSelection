package com.lwh.edgeselection.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class FormForExcel {
    private double time;
    private String PreferedCSP;
    private String UnPreferedCSP;
    private int qualifiedEIS;
    private int numberOfEIS;
    private int numberOfCSP;
    private int numberOfallservice;
    private String possibleCombination;
    private String result;
    private double cost;
    private int num_EIS_per_Country;
    private int num_CSP_per_EIS;
    private double latency_avg;


}
