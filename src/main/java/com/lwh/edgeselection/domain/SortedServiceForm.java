package com.lwh.edgeselection.domain;

import java.util.*;

public class SortedServiceForm {
    private EIS eis;
    private CSP csp;
    private Area area;
    private Latency latency;

    private Map<EIS, Set<CSP>> map = new HashMap<>();
    HashSet<EIS> usedEIS = new HashSet<>();
    HashSet<CSP> usedCSP = new HashSet<>();
    private List<ServiceForm> list = new ArrayList<>();
}
