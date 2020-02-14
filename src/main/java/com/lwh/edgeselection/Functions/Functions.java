package com.lwh.edgeselection.Functions;

import com.lwh.edgeselection.domain.CSP;
import com.lwh.edgeselection.domain.EIS;
import com.lwh.edgeselection.domain.ServiceForm;
import com.lwh.edgeselection.domain.ServiceTable;

import java.util.*;

/**
 * The type Functions.
 */
public class Functions {
    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
//        List<int[]> x = Combination(10);
        System.out.println();
    }



    public static boolean checkReliability(ServiceTable serviceTable, int numOfEIS, int numOfCSP){
        return serviceTable.checkNumberOfEIS(numOfEIS) && serviceTable.checkNumberOfCSP(numOfCSP);
    }
    /**
     * Calculate cost double.
     *
     * @param fileredServices the filered services
     * @return the double
     */
    public static double calculateCost(List<ServiceForm> fileredServices){
        double cost = 0;
        HashSet<EIS> usedEIS = new HashSet<>();
        HashSet<CSP> usedCSP = new HashSet<>();
        for (ServiceForm service:fileredServices) {
            if(usedEIS.add(service.getEis())){
                cost += service.getEis().getCost_second();
            }
            if(usedCSP.add(service.getCsp())){
                cost += service.getCsp().getCost_scond();
            }
        }
        return cost;

    }

    /**
     * Filter table list.

     * @param original       the original
     * @param unpreferedCSPs the unprefered cs ps
     * @param statisfiedEIS  the statisfied eis
     * @param latency        the latency
     * @return the list
     */
    public static List<ServiceForm> filterTable(List<ServiceForm> original,
                                                List<CSP> unpreferedCSPs,
                                                List<EIS> statisfiedEIS,
                                                double latency){
        Iterator<ServiceForm> it= original.iterator();
        while(it.hasNext()){
            ServiceForm serviceForm = it.next();
            if(
                    serviceForm.getLatency().getDelay() > latency
                    ||    !statisfiedEIS.contains(serviceForm.getEis())
                    ||    unpreferedCSPs.contains(serviceForm.getCsp())
            ){
                it.remove();
            }
        }
        return original;
    }
//    public static List<Area> regionToCountry(List<DetailedArea> regions){
//        ArrayList<Area> areas = new ArrayList<>();
//        for(DetailedArea region:regions){
//
//        }
//    }

    /**
     * Combination list.
     *
     * @return the list of all possible combination with binary array representation
     */
    public static List<int[]> CombinationBinary(List<ServiceForm> table) {
        //number of elements
        int n = table.size();
        //number of possible combination ：2^n
        int nbit = 1<<n;
        System.out.println("number of possible combination："+nbit);
        List<int[]> results = new ArrayList<>();
        for(int i=0 ; i<nbit ; i++) {                        //结果有nbit个。输出结果从数字小到大输出：即输出0,1,2,3,....2^n。
            int[] result = new int[n];
            for(int j=0; j<n ; j++) {                        //每个数二进制最多可以左移n次，即遍历完所有可能的变化新二进制数值了
                int tmp = 1<<j ;
                if((tmp & i)!=0) {                            //& 表示与。两个位都为1时，结果才为1
                    result[j] = 1;
                }
            }
            results.add(result);
        }
        return results;
    }

    public static List<ServiceTable> CombinationResult(List<ServiceForm> table) {
        //number of elements
        int n = table.size();
        //number of possible combination ：2^n
        int nbit = 1<<n;
        System.out.println("number of possible combination："+nbit);
        List<ServiceTable> results = new ArrayList<>();
        for(int i=0 ; i<nbit ; i++) {                        //结果有nbit个。输出结果从数字小到大输出：即输出0,1,2,3,....2^n。
            ServiceTable result = new ServiceTable();
            for(int j=0; j<n ; j++) {                        //每个数二进制最多可以左移n次，即遍历完所有可能的变化新二进制数值了
                int tmp = 1<<j ;
                if((tmp & i)!=0) {                            //& 表示与。两个位都为1时，结果才为1
                    ServiceForm serviceForm = table.get(j);
                    result.add(serviceForm);
                }
            }
            results.add(result);
        }
        return results;
    }

    public static ServiceTable binaryToServiceTable(int[] binary, List<ServiceForm> serviceFormList) {
        ServiceTable serviceTable = new ServiceTable();
        for(int i = 0; i < binary.length; i++){
            if(binary[i] == 1){
                serviceTable.add(serviceFormList.get(i));
            }
        }
        return serviceTable;
    }


//    public static void generate() {
//
//    }
//    need to filter unsatisfied reliability
}
