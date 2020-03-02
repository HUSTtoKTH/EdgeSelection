package com.lwh.edgeselection.domain;

import java.util.*;

public class V2ServiceTable {
    private Map<EIS, Set<CSP>> map = new HashMap<>();
    HashSet<EIS> usedEIS = new HashSet<>();
    HashSet<CSP> usedCSP = new HashSet<>();
    private List<ServiceForm> list = new ArrayList<>();
}
