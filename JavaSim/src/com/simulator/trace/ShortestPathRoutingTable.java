package com.simulator.trace;

/**/
public class ShortestPathRoutingTable {
	
	public int shortestPathTable [][] = null;
	
	/* 
	 * Clumsy way of making this table. It will be better to make it from the Brite topology
	 * */
	
	ShortestPathRoutingTable (){
		
		shortestPathTable = new int [8][8];
		
		shortestPathTable [0][0] = 0;
		shortestPathTable [0][1] = 2;
		shortestPathTable [0][2] = 1;
		shortestPathTable [0][3] = 2;
		shortestPathTable [0][4] = 2;
		shortestPathTable [0][5] = 3;
		shortestPathTable [0][6] = 3;
		shortestPathTable [0][7] = 3;
		
		shortestPathTable [1][0] = 2;
		shortestPathTable [1][1] = 0;
		shortestPathTable [1][2] = 1;
		shortestPathTable [1][3] = 2;
		shortestPathTable [1][4] = 2;
		shortestPathTable [1][5] = 3;
		shortestPathTable [1][6] = 3;
		shortestPathTable [1][7] = 3;
		
		shortestPathTable [2][0] = 1;
		shortestPathTable [2][1] = 1;
		shortestPathTable [2][2] = 0;
		shortestPathTable [2][3] = 1;
		shortestPathTable [2][4] = 1;
		shortestPathTable [2][5] = 2;
		shortestPathTable [2][6] = 2;
		shortestPathTable [2][7] = 2;
		
		shortestPathTable [3][0] = 2;
		shortestPathTable [3][1] = 2;
		shortestPathTable [3][2] = 1;
		shortestPathTable [3][3] = 0;
		shortestPathTable [3][4] = 1;
		shortestPathTable [3][5] = 1;
		shortestPathTable [3][6] = 2;
		shortestPathTable [3][7] = 2;
		
		shortestPathTable [4][0] = 2;
		shortestPathTable [4][1] = 2;
		shortestPathTable [4][2] = 1;
		shortestPathTable [4][3] = 1;
		shortestPathTable [4][4] = 0;
		shortestPathTable [4][5] = 2;
		shortestPathTable [4][6] = 1;
		shortestPathTable [4][7] = 1;
		
		shortestPathTable [5][0] = 3;
		shortestPathTable [5][1] = 3;
		shortestPathTable [5][2] = 2;
		shortestPathTable [5][3] = 1;
		shortestPathTable [5][4] = 2;
		shortestPathTable [5][5] = 0;
		shortestPathTable [5][6] = 3;
		shortestPathTable [5][7] = 1;
		
		shortestPathTable [6][0] = 3;
		shortestPathTable [6][1] = 3;
		shortestPathTable [6][2] = 2;
		shortestPathTable [6][3] = 2;
		shortestPathTable [6][4] = 1;
		shortestPathTable [6][5] = 3;
		shortestPathTable [6][6] = 0;
		shortestPathTable [6][7] = 2;
		
		shortestPathTable [7][0] = 3;
		shortestPathTable [7][1] = 3;
		shortestPathTable [7][2] = 2;
		shortestPathTable [7][3] = 2;
		shortestPathTable [7][4] = 1;
		shortestPathTable [7][5] = 1;
		shortestPathTable [7][6] = 2;
		shortestPathTable [7][7] = 0;		
	}

}
