/**
 * Any use of this code must get permit from the author Xing Han
 */

/**
 * Class to implement Assignment2 solution
 * findProgram method should be implemented.
 * Please do not include any main methods.
 */

import java.util.*;

public class Assignment2 {
	
	public static ArrayList<Airdrop> findProgram(int[][] map, int row, int column) {
		//Variable region_num is used to count the number of connected regions
		int region_num = 0;	
		//Construct an ArrayList of arrays to store the information of each drop
		ArrayList<int []> feed_point = new ArrayList<>();

		/* This is the main part of the function: go through each element of the 2D matrix,
		 * but not in a normal order. It follows the diagonal route so elements that have smaller
		 * row + column value will be discovered first.
		 * When we find the element that is not equal to 0, then this is a region contains lions.
		 * Conduct DFS search starting from this element to find all connected element.
		 */
		for (int sum = 0; sum < row + column - 1; sum++) {
			for (int i = (sum < column)? 0 : sum - column + 1, j = (sum < column)? sum : column - 1; 
						  i < row && j >= 0; i++, j--) {
				if (map[i][j] != 0) {					
					int[] position = {i, j, 0};					
					DFS_helper(map, i, j, position);
					feed_point.add(position);
					region_num++;
				}
			}
		}
		
		//Move the drop information from array to the object Airdrop, then return the result
		ArrayList<Airdrop> result = new ArrayList<>();
		for (int i = 0; i < region_num; i++) {
			Airdrop feed_plan = new Airdrop(feed_point.get(i)[0], feed_point.get(i)[1], feed_point.get(i)[2]);
			result.add(feed_plan);
		}
		return result;
	}
	
	private static void DFS_helper (int[][] map, int row, int column, int[] position) {
        //If the position of the element is out of the matrix, or we have reached the boundary of the region
		//Then the DFS will terminate
		if (row < 0 || column < 0 || row >= map.length || 
        		column >= map[0].length || map[row][column] == 0) {
            return;
        }
        
        //Keep adding the lions found in this region while conducting DFS
		position[2] += map[row][column];
        //Set the region that have been covered to 0 in case we go through that again
        map[row][column] = 0;
        
        //DFS to find the connected elements in all direction
        DFS_helper(map, row + 1, column, position);
        DFS_helper(map, row - 1, column, position);
        DFS_helper(map, row, column + 1, position);
        DFS_helper(map, row, column - 1, position);
	}
}