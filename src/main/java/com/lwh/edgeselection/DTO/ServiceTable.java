package com.lwh.edgeselection.DTO;

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
    private List<ServiceForm> list = new ArrayList<>();

    /**
     * Add.
     *
     * @param serviceForm the service form
     */
    public void add(ServiceForm serviceForm){
        list.add(serviceForm);
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
    public boolean checkNumberOfCSP(int num) {
        for (Set<CSP> csps : EISmap.values()) {
            if (csps.size() < num) {
                return false;
            }
        }
        return true;
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
        return checkNumberOfEIS(numOfEIS) && checkNumberOfCSP(numOfCSP);
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
        getList().remove(ans);
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
        getList().remove(ans);
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
        getList().remove(ans);
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
        ServiceForm ans = new ServiceForm();
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
        getList().remove(ans);
        return ans;
    }

    /**
     * Retrieve cheapest row based on eis service form.
     *
     * @param eis the eis
     * @return the service form
     */
    public ServiceForm retrieveCheapestRowBasedOnEIS(EIS eis){
        ServiceForm ans = new ServiceForm();
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
        getList().remove(ans);
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
}
