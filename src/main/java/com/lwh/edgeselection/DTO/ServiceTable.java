package com.lwh.edgeselection.DTO;

import com.lwh.edgeselection.domain.Application;
import com.lwh.edgeselection.domain.CSP;
import com.lwh.edgeselection.domain.EIS;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * The type Service table.
 */
@Setter
@Getter
public class ServiceTable {
    private Map<EIS, TreeSet<CSP>> EISmap = new HashMap<>();
    private Map<CSP, TreeSet<EIS>> CSPmap = new HashMap<>();
    private HashSet<EIS> usedEIS = new HashSet<>();
    private HashSet<CSP> usedCSP = new HashSet<>();
    private LinkedList<ServiceForm> list = new LinkedList<>();
    private double latencySum = 0;

    /**
     * Add.
     *
     * @param serviceForm the service form
     */
    public void add(ServiceForm serviceForm){
        if(serviceForm == null)return;
        list.add(serviceForm);
        latencySum = latencySum + serviceForm.getLatency().getDelay();
        if(usedCSP.add(serviceForm.getCsp())){
            Comparator<EIS> compEIS = (EIS e1, EIS e2) ->{
                if(e1.getId() == e2.getId())
                    return 0;
                if(e1.getCost_second()<e2.getCost_second())
                    return -1;
                else if(e1.getCost_second()>e2.getCost_second())
                    return 1;
                return 0;
            };
            CSPmap.put(serviceForm.getCsp(), new TreeSet<EIS>(compEIS)
            {{add(serviceForm.getEis());}});
        }else {
            Set<EIS> eiss = CSPmap.get(serviceForm.getCsp());
            eiss.add(serviceForm.getEis());
        }
        if(usedEIS.add(serviceForm.getEis())){
            Comparator<CSP> compCSP = (c1, c2) -> {
                if(c1.getId() == c2.getId())
                    return 0;
                if(c1.getCost_scond()<c2.getCost_scond())
                    return -1;
                else if(c1.getCost_scond()>c2.getCost_scond())
                    return 1;
                return 0;
            };
            TreeSet<CSP> set = new TreeSet<>(compCSP);
            set.add(serviceForm.getCsp());
            EISmap.put(serviceForm.getEis(), set);
        }else {
            TreeSet<CSP> csps = EISmap.get(serviceForm.getEis());
            csps.add(serviceForm.getCsp());
        }
    }

    public void remove(ServiceForm serviceForm){
        if(serviceForm == null)return;
        list.remove(serviceForm);
        latencySum = latencySum - serviceForm.getLatency().getDelay();
        if(EISmap.get(serviceForm.getEis()).size() == 1){
            EISmap.remove(serviceForm.getEis());
            usedEIS.remove(serviceForm.getEis());
        }else {
            EISmap.get(serviceForm.getEis()).remove(serviceForm.getCsp());
        }
        if(CSPmap.get(serviceForm.getCsp()).size() == 1){
            CSPmap.remove(serviceForm.getCsp());
            usedCSP.remove(serviceForm.getCsp());
        }else {
            CSPmap.get(serviceForm.getCsp()).remove(serviceForm.getEis());
        }
    }

    /**
     * Add all.
     *
     * @param serviceForms the service forms
     */
    public void addAll(Iterable<ServiceForm> serviceForms){
        for(ServiceForm serviceForm:serviceForms){
            add(serviceForm);
        }
    }


    /**
     * Number of eis int.
     *
     * @return the int
     */
    public int numberOfEIS() {
        return usedEIS.size();
    }

    /**
     * Check number of eis boolean.
     *
     * @param num the num
     * @return the boolean
     */
    public boolean checkNumberOfEIS(int num){
        return numberOfEIS() >= num;
    }

    /**
     * Check number of csp boolean.
     *
     * @param num the num
     * @return the boolean
     */
    public boolean checkNumberOfCSPGreaterEqual(int num) {
        for (Set<CSP> csps : EISmap.values()) {
            if (csps.size() < num) {
                return false;
            }
        }
        return true;
    }

    public boolean checkNumberOfCSPGreater(int num) {
        for (Set<CSP> csps : EISmap.values()) {
            if (csps.size() > num) {
                return true;
            }
        }
        return false;
    }

    public boolean checkNumberOfCSPLess(int num) {
        for (Set<CSP> csps : EISmap.values()) {
            if (csps.size() < num) {
                return true;
            }
        }
        return false;
    }
    /**
     * Check single eis number of csp boolean.
     *
     * @param eis the eis
     * @param num the num
     * @return the boolean
     */
    public boolean checkSingleEISNumberOfCSP(EIS eis, int num) {
        return EISmap.get(eis).size() >= num;
    }

    /**
     * Check reliability boolean.
     *
     * @param numOfEIS the num of eis
     * @param numOfCSP the num of csp
     * @return the boolean
     */
    public boolean checkReliability(int numOfEIS, int numOfCSP){
        return checkNumberOfEIS(numOfEIS) && checkNumberOfCSPGreaterEqual(numOfCSP);
    }

    /**
     * Check csp boolean.
     *
     * @param likeCSP the like csp
     * @return the boolean
     */
    public boolean checkCSP(Iterable<CSP> likeCSP) {
        for(CSP csp:likeCSP){
            if(!usedCSP.contains(csp)){
                return false;
            }
        }
        return true;
    }

    /**
     * Calculate cost double.
     *
     * @return the double
     */
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

    /**
     * New calculate cost double.
     *
     * @return the double
     */
    public double newCalculateCost(){
        double cost = 0;
        for (Map.Entry<EIS, TreeSet<CSP>> entry : EISmap.entrySet()) {
            cost += entry.getKey().getCost_second();
            for(CSP csp:entry.getValue()){
                cost += csp.getCost_scond();
            }
        }
        return cost;
    }

//    private double metric(ServiceForm serviceForm, double factor){
//        return serviceForm.getCost() - factor*CSPmap.get(serviceForm.getCsp()).size();
//    }

    /**
     * Retrieve cheapest row based on csp service form.
     *
     * @param csp the csp
     * @return the service form
     */
    public ServiceForm retrieveCheapestRowBasedOnCSP(CSP csp) {
        ServiceForm ans = new ServiceForm();
        double cheapest = 0;
        for(ServiceForm serviceForm:list){
            if(serviceForm.getCsp().equals(csp))
            {
                double current = serviceForm.getCost();
                if(cheapest == 0 || cheapest > current){
                    cheapest = current;
                    ans = serviceForm;
                }
            }
        }
        remove(ans);
        return ans;
    }

    /**
     * Retrieve cheapest row based on csp service form.
     *
     * @param csp     the csp
     * @param num_CSP the num csp
     * @return the service form
     */
    public ServiceForm retrieveCheapestRowBasedOnCSP(CSP csp, int num_CSP) {
        ServiceForm ans = new ServiceForm();
        double cheapest = 0;
        for(ServiceForm serviceForm:list){
            if(serviceForm.getCsp().equals(csp))
            {
                double current = serviceForm.getCost();
                Iterator<CSP> it = EISmap.get(serviceForm.getEis()).iterator();
                for(int i = 1; i < num_CSP; i++){
                    CSP topCheapestCSP = it.next();
                    if(topCheapestCSP.equals(csp)){
                        i--;
                        continue;
                    }else {
                        current += topCheapestCSP.getCost_scond();
                    }
                }
                if(cheapest == 0 || cheapest > current){
                    cheapest = current;
                    ans = serviceForm;
                }
            }
        }
        remove(ans);
        return ans;
    }

    /**
     * Retrieve cheapest line with new eis service form.
     *
     * @param serviceTable the service table
     * @return the service form
     */
    public ServiceForm retrieveCheapestLineWithNewEIS(ServiceTable serviceTable) {
        ServiceForm ans = new ServiceForm();
        double cheapest = 0;
        for(ServiceForm serviceForm:list){
            if(serviceTable.getUsedEIS().contains(serviceForm.getEis())){
                continue;
            }
            double current = serviceForm.getCost();
            if(cheapest == 0 || cheapest > current){
                cheapest = current;
                ans = serviceForm;
            }
        }
        remove(ans);
        return ans;
    }

    /**
     * Retrieve cheapest line with new eis service form.
     *
     * @param serviceTable the service table
     * @param num_CSP      the num csp
     * @return the service form
     */
    public ServiceForm retrieveCheapestLineWithNewEIS(ServiceTable serviceTable, int num_CSP) {
        ServiceForm ans = null;
        double cheapest = 0;
        for(ServiceForm serviceForm:list) {
            if (serviceTable.getUsedEIS().contains(serviceForm.getEis())) {
                continue;
            }
            double current = serviceForm.getCost();
            Iterator<CSP> it = EISmap.get(serviceForm.getEis()).iterator();
            for (int i = 1; i < num_CSP; i++) {
                CSP topCheapestCSP = it.next();
                if (topCheapestCSP.equals(serviceForm.getCsp())) {
                    i--;
                    continue;
                } else {
                    current += topCheapestCSP.getCost_scond();
                }
            }
            if (cheapest == 0 || cheapest > current) {
                cheapest = current;
                ans = serviceForm;
            }
        }
        remove(ans);
        return ans;
    }

    /**
     * Retrieve cheapest row based on eis service form.
     *
     * @param eis the eis
     * @return the service form
     */
    public ServiceForm retrieveCheapestRowBasedOnEIS(EIS eis){
        ServiceForm ans = null;
        double cheapest = 0;
        for(ServiceForm serviceForm:list){
            if(serviceForm.getEis().equals(eis)){
                double current = serviceForm.getCost();
                if(cheapest == 0 || cheapest > current){
                    cheapest = current;
                    ans = serviceForm;
                }
            }
        }
        remove(ans);
        return ans;
    }


    /**
     * Find low reliability service eis.
     *
     * @param num the num
     * @return the eis
     */
    public EIS findLowReliabilityService(int num) {
        for (Map.Entry<EIS, TreeSet<CSP>> entry : EISmap.entrySet()) {
            if (entry.getValue().size() < num) {
                return entry.getKey();
            }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<EIS, TreeSet<CSP>> entry : EISmap.entrySet()) {
            sb.append("EIS = " + entry.getKey().getId() + ": ");
            for(CSP csp:entry.getValue()){
                sb.append("CSP"+csp.getId()+", ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public boolean checkBudget(double budget) {
        return newCalculateCost() < budget;
    }

    public double calculateLatency() {
        return latencySum/list.size();
    }

    public void removeExpensive() {
        Collections.sort(list, Comparator.comparingDouble(p -> p.getCost()));
        remove(list.getLast());
    }

    public ServiceForm retrieveFastestRowBasedOnCSP(CSP csp) {
        ServiceForm ans = null;
        double fastest = 0;
        for(ServiceForm serviceForm:list){
            if(serviceForm.getCsp().equals(csp))
            {
                double current = serviceForm.getLatency().getDelay();
                if(fastest == 0 || fastest > current){
                    fastest = current;
                    ans = serviceForm;
                }
            }
        }
        remove(ans);
        return ans;
    }

    public ServiceForm retrieveFastestLineWithNewEIS(HashSet<EIS> usedEIS) {
        ServiceForm ans = null;
        double fastest = 0;
        for(ServiceForm serviceForm:list){
            if(usedEIS.contains(serviceForm.getEis())){
                continue;
            }
            double current = serviceForm.getLatency().getDelay();
            if(fastest == 0 || fastest > current){
                fastest = current;
                ans = serviceForm;
            }
        }
        remove(ans);
        return ans;
    }

    public ServiceForm retrieveFastestRowBasedOnEIS(EIS unsatistfiedEIS) {
        ServiceForm ans = null;
        double fastest = 0;
        for(ServiceForm serviceForm:list){
            if(serviceForm.getEis().equals(unsatistfiedEIS)){
                double current = serviceForm.getLatency().getDelay();
                if(fastest == 0 || fastest > current){
                    fastest = current;
                    ans = serviceForm;
                }
            }
        }
        if(ans == null){

        }
        remove(ans);
        return ans;
    }

    public void removeByEIS(EIS unsatistfiedEIS){
        Iterator<ServiceForm> iterator = list.iterator();
        while (iterator.hasNext()) {
            ServiceForm serviceForm = iterator.next();
            if(serviceForm.getEis().equals(unsatistfiedEIS)){
                latencySum = latencySum - serviceForm.getLatency().getDelay();
                if(EISmap.get(serviceForm.getEis()).size() == 1){
                    EISmap.remove(serviceForm.getEis());
                    usedEIS.remove(serviceForm.getEis());
                }else {
                    EISmap.get(serviceForm.getEis()).remove(serviceForm.getCsp());
                }
                if(CSPmap.get(serviceForm.getCsp()).size() == 1){
                    CSPmap.remove(serviceForm.getCsp());
                    usedCSP.remove(serviceForm.getCsp());
                }else {
                    CSPmap.get(serviceForm.getCsp()).remove(serviceForm.getEis());
                }
                iterator.remove();
            }
        }
    }

    public void orderListByLatency(){
        Collections.sort(list,Comparator.comparingDouble(s -> s.getLatency().getDelay()));
    }

    public int validBnB(Application application){
        if(!checkBudget(application.getBudget())){
            return -1;
        }
//        if(numberOfEIS() > application.getNum_EIS_per_Country()){
//            return -1;
//        }
        if(numberOfEIS() < application.getNum_EIS_per_Country()){
            return 0;
        }
//        if(checkNumberOfCSPGreater(application.getNum_CSP_per_EIS())){
//            return -1;
//        }
        if(checkNumberOfCSPLess(application.getNum_CSP_per_EIS()) ){
            return 0;
        }
        if(!checkCSP(application.getPreferedCSPs())){
            return 0;
        }
        return 1;
    }
}
