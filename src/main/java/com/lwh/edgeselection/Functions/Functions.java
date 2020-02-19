package com.lwh.edgeselection.Functions;

import com.lwh.edgeselection.domain.*;
import com.lwh.edgeselection.repository.*;
import org.apache.commons.lang3.RandomUtils;

import java.math.BigInteger;
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
       containsNearbyDuplicate(new int[]{1,0,1,1},1);
    }

    public static boolean containsNearbyDuplicate(int[] nums, int k) {
        Map<Integer, Integer> map =  new HashMap<>();
        for(int i = 0; i < nums.length; i++){
            Integer pre = map.get(nums[i]);
            if(pre != null){
                if(i - pre <= k){
                    return true;
                }
            }else{
                map.put(nums[i], i);
            }
        }
        return false;
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
        List<int[]> results = new LinkedList<>();
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

    public static void CombinationResult(List<ServiceForm> table) {
        //number of elements
        int n = table.size();
        //number of possible combination ：2^n
        int nbit = 1<<n;
        System.out.println("number of possible combination："+nbit);
        for(int i=0 ; i<nbit ; i++) {                        //结果有nbit个。输出结果从数字小到大输出：即输出0,1,2,3,....2^n。
            ServiceTable result = new ServiceTable();
            for(int j=0; j<n ; j++) {                        //每个数二进制最多可以左移n次，即遍历完所有可能的变化新二进制数值了
                int tmp = 1<<j ;
                if((tmp & i)!=0) {                            //& 表示与。两个位都为1时，结果才为1
                    ServiceForm serviceForm = table.get(j);
                    result.add(serviceForm);
                }
            }

        }
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


    public static void generateEIS(EISRepository eisRepository, AreaRepository areaRepository,
                                LatencyRepository latencyRepository, CSPRepository cspRepository) {
//            generate EISs
        Area testArea = new Area("test");
        areaRepository.save(testArea);
        List<CSP> csps = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            CSP csp = new CSP(Math.random() * 10);
            cspRepository.save(csp);
            csp.getAreas().add(testArea);
            cspRepository.save(csp);
            csps.add(csp);
        }
        for(int i = 0; i < 30; i++) {
            EIS eis = new EIS(Math.random(), Math.random() * 10);
            //                bandwidth[100MB, 10GB]
            eis.setBandwidth(RandomUtils.nextDouble(100, 10000));
            //                cpu cores [1, 8]
            eis.setNum_cpus(RandomUtils.nextInt(1, 8));
            //                cpu frequency [1.8, 4]
            eis.setCpu_frequency(RandomUtils.nextDouble(1.8, 4));
            //                disk size [100GB, 10000GB]
            eis.setDisk_size(RandomUtils.nextInt(100, 10000));
            //                memory size [10MB, 10GB]
            eis.setMem_size(RandomUtils.nextInt(10, 10000));
            eisRepository.save(eis);
            for (CSP csp : csps) {
                if (Math.random() > 0.5) {
                    eis.getCsps().add(csp);
                    Latency latency = new Latency(testArea.getId(), csp.getId(), eis.getId(), Math.random() * 30 + 20);
                    latencyRepository.save(latency);
                }
            }
            eisRepository.save(eis);
        }
    }


    public static void generateApp(CSPRepository cspRepository, ApplicationRepository applicationRepository,
                                PreferedCSPRepository preferedCSPRepository, UnpreferedCSPRepository unpreferedCSPRepository) {
        //generate Applications
        List<CSP> csps = cspRepository.findAll();
        for(int i = 0; i < 20; i++){
            Application application = new Application(1+(int)(Math.random()*3),1+(int)(Math.random()*3), Math.random(),Math.random()*30 + 20);
//            setCPUIntensive(application);
            if(Math.random() < 0.33){
                setCPUIntensive(application);
            }else {
                if(Math.random() < 0.5){
                    setDataIntensive(application);
                }else {
                    setCommunicationIntensive(application);
                }
            }
            applicationRepository.save(application);
            for(CSP csp:csps){
                if(Math.random() < 0.33){
                    preferedCSPRepository.save(new PreferedCSP(application.getId(),csp.getId()));
                }else {
                    if(Math.random() < 0.5){
                        unpreferedCSPRepository.save(new UnpreferedCSP(application.getId(), csp.getId()));
                    }
                }
            }
        }
    }

    private static void setAppType(Application application, double bandwidth, int cpu_num, double cpu_frequency, int disk_size, int memory){
//                bandwidth[100MB, 10GB]
        application.setBandwidth(bandwidth);
//                cpu cores [1, 8]
        application.setNum_cpus(cpu_num);
//                cpu frequency [1.8, 4]
        application.setCpu_frequency(cpu_frequency);
//                disk size [100GB, 10000GB]
        application.setDisk_size(disk_size);
//                memory size [10MB, 10GB]
        application.setMem_size(memory);
    }

    private static void setCPUIntensive(Application application){
        setAppType(application,
                //                bandwidth[100MB, 1GB]
                RandomUtils.nextDouble(100, 1000),
                //                cpu cores [4, 8]
                RandomUtils.nextInt(4, 8),
                //                cpu frequency [1.8, 4]
                RandomUtils.nextDouble(1.8, 4),
                //                disk size [2GB, 100GB]
                RandomUtils.nextInt(2, 100),
                //                memory size [10MB, 5GB]
                RandomUtils.nextInt(10, 5000)
                );
    }

    private static void setDataIntensive(Application application){
        setAppType(application,
                //                bandwidth[100MB, 2GB]
                RandomUtils.nextDouble(100, 2000),
                //                cpu cores [1, 4]
                RandomUtils.nextInt(1, 4),
                //                cpu frequency [1.8, 2.2]
                RandomUtils.nextDouble(1.8, 2.2),
                //                disk size [100GB, 10000GB]
                RandomUtils.nextInt(100, 10000),
                //                memory size [100MB, 10GB]
                RandomUtils.nextInt(100, 10000)
        );
    }

    private static void setCommunicationIntensive(Application application){
        setAppType(application,
                //                bandwidth[100MB, 10 GB]
                RandomUtils.nextDouble(100, 10000),
                //                cpu cores [1, 4]
                RandomUtils.nextInt(1, 4),
                //                cpu frequency [1.8, 2.2]
                RandomUtils.nextDouble(1.8, 2.2),
                //                disk size [2GB, 20GB]
                RandomUtils.nextInt(2, 20),
                //                memory size [100MB, 1GB]
                RandomUtils.nextInt(100, 1000)
        );
    }


}
