package com.lwh.edgeselection.DTO;

import com.lwh.edgeselection.domain.Application;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BinaryRepresent {
    int[] binaryRepresent;
    double latencySum;
    int validCount;

    public BinaryRepresent(int n, int validCount) {
        this.binaryRepresent = new int[n];
        latencySum = Double.MAX_VALUE;
        this.validCount = validCount;
    }


    public BinaryRepresent(int[] binaryRepresent, int validCount) {
        this.binaryRepresent = binaryRepresent;
        latencySum = Double.MAX_VALUE;
        this.validCount = validCount;
    }


    public ServiceTable transfer(ServiceTable serviceTable){
        ServiceTable cur = new ServiceTable();
        for(int i = 0; i < validCount; i++){
            if(binaryRepresent[i] == 1){
                cur.add(serviceTable.getList().get(i));
            }
        }
        return cur;
    }
}
