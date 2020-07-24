package com.lwh.edgeselection.DTO;

import com.lwh.edgeselection.domain.Application;
import com.lwh.edgeselection.domain.CSP;
import com.lwh.edgeselection.domain.EIS;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class ServiceToMatrix {
    double[] x_cost;
    ArrayList<double[]> y_cost = new ArrayList<>();
    ArrayList<double[]> y_latency = new ArrayList<>();
    Map<CSP, List<XY>> mandatory_csp = new HashMap<>();
    private Map<EIS, Map<CSP, Double>> EISmap = new HashMap<>();
    private HashSet<EIS> usedEIS = new HashSet<>();
    private HashSet<ServiceForm> list = new HashSet<>();
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
        if(usedEIS.add(serviceForm.getEis())){
            Map<CSP, Double> map = new HashMap<>();
            map.put(serviceForm.getCsp(),serviceForm.getLatency().getDelay());
            EISmap.put(serviceForm.getEis(), map);
        }else {
            Map<CSP, Double> map = EISmap.get(serviceForm.getEis());
            map.put(serviceForm.getCsp(),serviceForm.getLatency().getDelay());
        }
    }

    public void addAll(Iterable<ServiceForm> serviceForms){
        for(ServiceForm serviceForm:serviceForms){
            add(serviceForm);
        }
    }

    public void transfer(Set<CSP> like_csp){
        x_cost = new double[usedEIS.size()];
        int i = 0;
        for (Map.Entry<EIS, Map<CSP, Double>> entry : getEISmap().entrySet()) {
            x_cost[i] += entry.getKey().getCost_second();
            y_cost.add(new double[entry.getValue().size()]);
            y_latency.add(new double[entry.getValue().size()]);
            int j = 0;
            for(Map.Entry<CSP, Double> subentry:entry.getValue().entrySet()){
                CSP csp = subentry.getKey();
                y_cost.get(i)[j] = csp.getCost_scond();
                y_latency.get(i)[j] = subentry.getValue();
                if(like_csp.contains(csp)){
                    if(!mandatory_csp.containsKey(csp)){
                        List<XY> results = new LinkedList<>();
                        XY result = new XY(i,j);
                        results.add(result);
                        mandatory_csp.put(csp,results);
                    }else {
                        XY result = new XY(i,j);
                        mandatory_csp.get(csp).add(result);
                    }
                }
                j++;
            }
            i++;
        }
    }


    public Iterable<ServiceForm> transferBack(double[] x, double[][] y){
        List<ServiceForm> ans = new LinkedList<>();
        int i = 0;
        for (Map.Entry<EIS, Map<CSP, Double>> entry : getEISmap().entrySet()) {
            int j = 0;
            for(Map.Entry<CSP, Double> subentry:entry.getValue().entrySet()){
                if(y[i][j] == 1){
                    ServiceForm serviceForm = findByEISandCSP(entry.getKey(), subentry.getKey());
                    if(serviceForm != null){
                        ans.add(serviceForm);
                    }
                }
                j++;
            }
            i++;
        }
        return ans;
    }

    public ServiceForm findByEISandCSP(EIS eis, CSP csp){
        for(ServiceForm serviceForm:list){
            if(serviceForm.getEis().equals(eis) && serviceForm.getCsp().equals(csp)){
                return serviceForm;
            }
        }
        return null;
    }

    public double averageLatency(){
        return latencySum/list.size();
    }

    @Setter
    @Getter
    public class XY {
        int x;
        int y;

        public XY(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

}
