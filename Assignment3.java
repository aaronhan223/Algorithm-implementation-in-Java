import java.util.ArrayList;

/**
 * Any use of this code must get permit from the author Xing Han
 */

public class Assignment3 {

	private int totalClasses;
	private int maxGrade;
	private static int [][]gradearray;
	public Assignment3() {
		this.totalClasses = 0;
		this.maxGrade = 0;
		this.gradearray = null;
	}

	public void initialize(int totalClasses, int maxGrade,  int[][] gradearray) {//These varaibles are set from TestAssignment3.java
		this.totalClasses = totalClasses;
		this.maxGrade = maxGrade;
		this.gradearray = gradearray;
	}

	public result[] compute(int totalHours, result[] studentoutput) {
		
			int[][] Optimal = new int[totalClasses + 1][totalHours + 1];
			int[][] decision_trace = new int[totalClasses + 1][totalHours + 1];
			int course_time = totalHours;
		//Initialize Opt[0][i], i.e. the first horizontal line, O(H) time
			for (int i = 0; i < totalHours + 1; i++) {
				Optimal[0][i] = 0;
			}
		//Initialize Opt[i][0], i.e. the first vertical line, O(I) time
			for (int i = 1; i < totalClasses + 1; i++) {
				Optimal[i][0] = Optimal[i - 1][0] + gradearray[i - 1][0];
			}
		
		/* Implement the dynamic programming algorithm, for each step, we find the maximum possible grade
		 * among the sum of the first n-1 course and the nth course. In the meantime, we keep track of the
		 * study time for each course (decision_trace) that yield the maximum result.
		 * O(I*H*Math.min(9, H)) time
		 */
			for (int i = 1; i < totalClasses + 1; i++) {
				for (int h = 1; h < totalHours + 1; h++) {
					int[] res_tuple = find_max(Optimal[i - 1], i, h);
					Optimal[i][h] = res_tuple[0];
					decision_trace[i][h] = res_tuple[1];
				}
			}
		
		//Add the result to the output, O(I) time
			for (int i = totalClasses; i >= 1; i--) {
				studentoutput[i - 1].setHour(decision_trace[i][course_time]);
				studentoutput[i - 1].setGrade(gradearray[i - 1][decision_trace[i][course_time]]);
				course_time -= decision_trace[i][course_time];
			}
			return studentoutput;
	}
		/* This function can be used to find the maximum possible grade among each combination 
		 * of the first n-1 courses and the nth course. It returns both the maximum grade and 
		 * the study time for the nth course that yield the maximum grade.
		 */
	private int[] find_max(int[] prev_class, int class_num, int hours) {
		int result = 0;
		int position = 0;
		ArrayList<int[]> maps = new ArrayList<>();
		for (int k = 0; k <= Math.min(9, hours); k++) {
		//result stores the maximum value we find in each step
			result = Math.max(result, gradearray[class_num - 1][k] + prev_class[hours - k]);
			int[] pairs = {result, k};
			maps.add(pairs);
		}
		/* We find the optimal grade using shortest study time for course with larger course number.
		 * So the algorithm will resolve the ties by choosing classes with lower IDs.
		 */
		for (int i = 0; i < maps.size(); i++) {
			if (maps.get(i)[0] == result) {
				position = maps.get(i)[1];
				break;
			}
		}
		
		int[] res_tuple = {result, position};
		return res_tuple;
	}
}


/*  WRITE YOUR REPORT INSIDE THIS SECTION AS COMMENTED CODE
 * 
 * 1. Recursive definition of optimal solution.
 * Opt(i, h) = max  gradearray[i][k] + Opt(i - 1, h - k)
 * 			    0<=k<=h
 * 
 * 2. Algorithm for iteratively computing the optimal solution.
 * First, we initialize the horizontal line Opt[0][i] (the total grade that will get in study
 * i hours for 0 courses), and vertical line Opt[i][0] (the total grade that will get in study
 * 0 hours for i courses). We calculate the optimal grades for all possible i (course number) and 
 * h (study time) by incrementally filling in an I * H size table. The current optimal grade depend
 * on the previous optimal grade. For each step in calculating Opt(i, h), there are Math.min(9, h) different 
 * combinations based on study time of current course and optimal grade in previous i - 1 courses.
 * We take the maximum grade among Math.min(9, h) possible solutions as the new optimal value, this will take
 * O(Math.min(9, H)) time complexity. The total time complexity would be: O(I*H) * O(Math.min(9, H)) = O(I*H*Math.min(9, H)) = O(I*H).
 * 
 * 3. Algorithm for recreating the optimal solution
 * When iterating through the I*H table, we record the number of hours studied in each course, k, that 
 * yield the optimal grade in each step. After we finish iteration, start at the last point Opt(I, H)
 * Find the value k that produced this result in previous iteration, and k is the number of hour studied
 * for the last course. Then find Opt(I - 1, H - k), find the time spent on this course and repeat.
 * 
 * 4. Prove for optimal substructure
 * Optimum means get the best grade in same or fewer amount of time. 
 * We can prove by exchange argument. Let a[i][m] denote the grade for studying m hours in the ith course.
 * Suppose the substructure a[1][m1], a[2][m2], ..., a[i - 1][m(i-1)] for Opt(i, h) is not optimal, 
 * then we must have a better solution a[1][k1], a[2][k2], ..., a[i - 1][k(i-1)], that
 * a[1][k1] + a[2][k2] +...+ a[i - 1][k(i-1)] > a[1][m1] + a[2][m2] +...+ a[i - 1][m(i-1)],
 * where k1 + k2 +...+ k(i-1) = K, m1 + m2 +...+ m(i-1) = M, and K <= M.
 * Suppose we have same amount of time to study the ith course: a[i][h - M].
 * Therefore, a[1][k1] + a[2][k2] +...+ a[i - 1][k(i-1)] + a[i][h - M] > a[1][m1] + a[2][m2] +...+ a[i - 1][m(i-1)] + a[i][h - M] = Opt(i, h),
 * and K + h - M <= M + h - M = h.
 * Contradicts the fact that Opt(i, h) is an optimal solution.
 * Then the problem has optimal substructure.
 */
