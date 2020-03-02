package com.lwh.edgeselection.domain;

import java.util.*;

public class ServiceTable {
    private Map<EIS, Set<CSP>> map = new HashMap<>();
    private HashSet<EIS> usedEIS = new HashSet<>();
    private HashSet<CSP> usedCSP = new HashSet<>();
    private List<ServiceForm> list = new ArrayList<>();

    public void add(ServiceForm serviceForm){
        list.add(serviceForm);
        usedCSP.add(serviceForm.getCsp());
        if(usedEIS.add(serviceForm.getEis())){
            map.put(serviceForm.getEis(), new HashSet<CSP>(){{add(serviceForm.getCsp());}});
        }else {
            Set<CSP> csps = map.get(serviceForm.getEis());
            csps.add(serviceForm.getCsp());
        }
    }

    public void addAll(List<ServiceForm> serviceForms){
        for(ServiceForm serviceForm:serviceForms){
            add(serviceForm);
        }
    }


    public int numberOfEIS() {
        return usedEIS.size();
    }

    public boolean checkNumberOfEIS(int num){
        return numberOfEIS() >= num;
    }

    public boolean checkNumberOfCSP(int num) {
        for (Set<CSP> csps : map.values()) {
            if (csps.size() < num) {
                return false;
            }
        }
        return true;
    }

    public boolean checkReliability(int numOfEIS, int numOfCSP){
        return checkNumberOfEIS(numOfEIS) && checkNumberOfCSP(numOfCSP);
    }

    public boolean checkCSP(Iterable<CSP> likeCSP) {
        for(CSP csp:likeCSP){
            if(!usedCSP.contains(csp)){
                return false;
            }
        }
        return true;
    }

    public double calculateCost(){
        double cost = 0;
        for(EIS eis:usedEIS){
            cost+=eis.getCost_second();
        }
        for(CSP csp:usedCSP){
            cost+=csp.getCost_scond();
        }
        return cost;
    }

    public Map<EIS, Set<CSP>> getMap() {
        return map;
    }

    public void setMap(Map<EIS, Set<CSP>> map) {
        this.map = map;
    }

    public HashSet<EIS> getUsedEIS() {
        return usedEIS;
    }

    public void setUsedEIS(HashSet<EIS> usedEIS) {
        this.usedEIS = usedEIS;
    }

    public HashSet<CSP> getUsedCSP() {
        return usedCSP;
    }

    public void setUsedCSP(HashSet<CSP> usedCSP) {
        this.usedCSP = usedCSP;
    }

    public List<ServiceForm> getList() {
        return list;
    }

    public void setList(List<ServiceForm> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<EIS, Set<CSP>> entry : map.entrySet()) {
            sb.append("EIS = " + entry.getKey().getId() + ": ");
            for(CSP csp:entry.getValue()){
                sb.append("CSP"+csp.getId()+", ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
