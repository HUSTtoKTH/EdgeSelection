package com.lwh.edgeselection.Application;

import com.lwh.edgeselection.DTO.ServiceTable;
import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

public class CplexSolver {

    IloCplex cplex;
    ServiceTable serviceTable;

    public CplexSolver() throws IloException {
        IloCplex cplex = new IloCplex();
    }

    static double[] x_cost = {6,4};
    static double[] y1_cost = {2,3,1};
    static double[] y1_latency = {25,40,45};
    static double[] y2_cost = {2,3,1};
    static double[] y2_latency = {30,35,50};
    static double budget = 10;
    static int NumOfEIS = 1;
    static int NumOfCSP = 2;

    public static void main(String[] args) {
        try {

            IloCplex cplex = new IloCplex(); // creat a model
            IloNumVar[] x = cplex.boolVarArray(2);
            IloNumVar[][] y = new IloNumVar[2][];
            for (int c = 0; c < 2; c++) {
                y[c] = cplex.boolVarArray(3);
            }




            cplex.addLe(cplex.sum(
                    cplex.scalProd(x, x_cost),
                    cplex.scalProd(y[0], y1_cost),
                    cplex.scalProd(y[1], y2_cost)
            ),budget);

            for(int j = 0; j<3;j++){
                // Constraint: only produce product on machine 'i' if it is 'used'
                //             (to capture fixed cost of using machine 'i')
                cplex.addLe(y[0][j], cplex.prod(10000, x[0]));
                cplex.addLe(y[1][j], cplex.prod(10000, x[1]));
            }
            IloNumExpr y1sum = y[0][0];
            IloNumExpr y2sum = y[1][0];
            for(int j = 1; j<3;j++){
                y1sum = cplex.sum(y1sum,y[0][j]);
                y2sum = cplex.sum(y2sum,y[1][j]);
            }
            cplex.addGe(cplex.sum(x[0],x[1]), NumOfEIS);
            cplex.addGe(y1sum, cplex.prod(NumOfCSP, x[0]));
            cplex.addGe(y2sum, cplex.prod(NumOfCSP, x[1]));

            cplex.addMinimize(
                    cplex.diff(
                            cplex.sum(cplex.scalProd(y1_latency,y[0]), cplex.scalProd(y2_latency,y[1])),
                            cplex.prod(cplex.sum(y1sum,y2sum),37.5)
                    )
            );
//            cplex.addMinimize(
//                    cplex.sum(cplex.scalProd(y1_latency,y[0]), cplex.scalProd(y2_latency,y[1]))
//            );
//            cplex.addMaximize(
//                    cplex.sum(y1sum,y2sum)
//            );

            if (cplex.solve()) {
                cplex.output().println("Solution status = " + cplex.getStatus());
                cplex.output().println("Solution value = " + cplex.getObjValue());
                double[] valx = cplex.getValues(x);
                for (int j = 0; j < valx.length; j++)
                    cplex.output().println("x" + (j+1) + "  = " + valx[j]);
                for (int i = 0; i < y.length; i++)
                    for (int j = 0; j < y[0].length; j++)
                        cplex.output().println("y" + i + ""+ j + "  = " + cplex.getValue(y[i][j]));
            }
            cplex.end();

        } catch (IloException e) {
            System.err.println("Concert exception caught: " + e);
        }
    }
}
