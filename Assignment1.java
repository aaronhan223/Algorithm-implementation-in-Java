/**
 * Any use of this code must get permit from the author Xing Han
 */

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Assignment1 {

    // Part1: Implement a Brute Force Solution
  public static ArrayList<Integer> stableMatchBruteForce(Preferences preferences) {
      // get the length of the array
      int l = preferences.getNumberOfProfessors();
      ArrayList<Integer> result = new ArrayList<>();
      
      // get all possible permutations
      int[][] permutations = allPermutation(preferences);
      
      //construct 2-D arrays to store students' and professors' preference
      int[][] stu_preference = new int[l][l];   
      int[][] prof_preference = new int[l][l];
      
      //change normal preference order to inverse order so as to be used for stable judgments
      stu_preference = generateInversePref(preferences.getStudents_preference());    
      prof_preference = generateInversePref(preferences.getProfessors_preference());
      
      // test all possible permutations if they are stable
      for (int i = 0; i < permutations.length; i++) {
          // if stable, break the loop and return
          if (isStable(permutations[i], stu_preference, prof_preference)) {
              for (int j = 0; j < l; j++) {
                  result.add(permutations[i][j]);
              }
              break;
          }
      }
      return result;
  }
  
  //In the inverse preference list, the index is student, and the content is the student's rank (from professor's perspective)
  private static int[][] generateInversePref(ArrayList<ArrayList<Integer>> preferences) {
      int l = preferences.size();
      int[][] studPreferenceArray = new int[l][l];
      for (int i = 0; i < l; i++) {
          ArrayList<Integer> list = preferences.get(i);
          for (int j = 0; j < l; j++) {
        	  	//key part of inverse
              studPreferenceArray[i][list.get(j) - 1] = l - j;
          }
      }
      return studPreferenceArray;
  }
  
  //Function used to generate all possible permutations of students/professors list, and stored in a 2-D array
  private static int[][] allPermutation(Preferences preferences) {
      // get the length of the array
      int l = preferences.getNumberOfProfessors();
      
      // the permutation_array is a 2-D array, which is:
      // total number of permutations * length of each outcome
      int permutation_length = 1;
      for (int i = 1; i < l + 1; i++) {
          permutation_length = permutation_length * i;
      }
      int[][] permutation_array = new int[permutation_length][l];
      
      // initiate each combination
      int[] lists = new int[l];
      for (int i = 0; i < lists.length; i++) {
          lists[i] = i;
      }
      DFS(lists, 0, permutation_array);
      return permutation_array;
  }
  
  //global variable used in DFS to count the permutation number
  private static int count = 0;
  
  //Depth First Search used to find all permutations
  public static void DFS(int[] lists, int d, int[][] permutation_array) {
      //generate each possible permutation by swapping the order, and store each outcome in list array
      for(int i = d; i < lists.length; i++){
          swapper(lists, i, d);
          //increase the start point to generate new permutations
          DFS(lists, d + 1, permutation_array);
          //trace back by swapping back
          swapper(lists, i, d);
      }

      //when one permutation is successfully generated, add them to the permutation array
      if (d == lists.length - 1){
          for (int i = 0; i < lists.length; i++) {
              permutation_array[count][i] = lists[i];
          }
          count++;
      }
  }
  
  // Swap two integers in an array
  private static void swapper(int[] lists, int i, int d) {
      int temp = lists[i];
      lists[i] = lists[d];
      lists[d] = temp;
  }

  // Function used to judge if a connection is stable or not
  private static boolean isStable(int[] prof_match, int[][] stu_preference, int[][] prof_preference) {
      int l = prof_match.length;
      // according to professor's perspective, construct student's match in student's perspective
      int[] stu_match = new int[l];
      for (int i = 0; i < l; i++) {
          stu_match[prof_match[i]] = i;
      }
      // check each student of each professor
      for (int i = 0; i < l; i++) {
          for (int j = 0; j < l; j++) {
              int stu_Rank = prof_preference[i][j];
              int prof_Rank = stu_preference[j][i];
              int curr_profRank = prof_preference[i][prof_match[i]];
              int curr_stuRank = stu_preference[j][stu_match[j]];
              // check if they are unstable, i.e. they both prefer each other than current partners
              if (stu_Rank > curr_profRank && prof_Rank > curr_stuRank) {
                  return false;
              }
          }
      }
      return true;
  }
            

    // Part2: Implement Gale-Shapley Algorithm
  public static ArrayList<Integer> stableMatchGaleShapley(Preferences preferences) {
      // get the length of the array
      int l = preferences.getNumberOfProfessors();
      int[][] prof_Preference = ArrayListToArray(preferences);
      int[][] stu_Preference = new int[l][l];   
      stu_Preference = generateInversePref(preferences.getStudents_preference());
      ArrayList<Integer> result = new ArrayList<>();

      int[] stu_status = new int[l];
      int[] prof_status = new int[l];
      Queue<Integer> waitlist = new LinkedList<>();
      int[] count = new int[l];
      
      for (int i = 0; i < l; i++) {
          //all students are unmatched initially
          stu_status[i] = -1;
          //all professors are added to waitlist
          waitlist.add(i);
          //the number of proposal of all the professors are all 0 initiallly
          count[i] = 0;
      }
      
      // Implement G-S algorithms
      while (waitlist.size() != 0) {
          // get a free professor
          int curr_Prof = waitlist.remove();
          // select the professor's favorite student that he haven't proposed yet
          int curr_Stu = prof_Preference[curr_Prof][count[curr_Prof]];
          // if the student is free
          if (stu_status[curr_Stu] == -1) {
              //the student is matched with this professor
              stu_status[curr_Stu] = curr_Prof; 
              prof_status[curr_Prof] = curr_Stu; 
          } 
          else {
              // if the student has already matched with someone, get student's current matching
        	  	  /* note that currentProf is the professor that the student currently matching with 
        	  	   and curr_Prof is the professor that we are trying to find a match
        	  	   */
              int currentProf = stu_status[curr_Stu];
              // if student prefers the professor he is currently matching with (constant time operation)
              if (stu_Preference[curr_Stu][currentProf] > stu_Preference[curr_Stu][curr_Prof]) {
                  //professor remains free
                  waitlist.add(curr_Prof);
              } 
              else {
                  // if student prefer this professor, his current professor becomes free
                  waitlist.add(currentProf);
                  // change both student's and professor's status
                  stu_status[curr_Stu] = curr_Prof;
                  prof_status[curr_Prof] = curr_Stu;
              }
          }
          // increase the number of propose of professor
          count[curr_Prof]++;
      }

      //add all the professor's status to the final result
      for (int i = 0; i < l; i++) {
          result.add(prof_status[i]);
      }     
      return result;
  }
    
    // Change data structure from ArrayList to Array
    private static int[][] ArrayListToArray(Preferences preferences) {
        int l = preferences.getNumberOfProfessors();
        int[][] pref_array = new int[l][l];
        for (int i = 0; i < l; i++) {
            ArrayList<Integer> list = preferences.getProfessors_preference().get(i);
            for (int j = 0; j < l; j++) {
            	pref_array[i][j] = list.get(j) - 1;
            }
        }
        return pref_array;
    }
    
 
    // Part3 - a: Matching with Costs (Professor Optimal)
    public static ArrayList<Cost> stableMatchCosts(Preferences preferences) {
        // get the length of the array
        int l = preferences.getNumberOfProfessors();
        ArrayList<Cost> result = new ArrayList<>();
        //implement G-S algorithms in part 2 to get professor optimal result
        ArrayList<Integer> prof_optimal = stableMatchGaleShapley(preferences);
        
        // use the inverse preference to calculate the cost to each other in an easier way
        int[][] profInversePref = generateInversePref(preferences.getProfessors_preference());
        int[][] stuInversePref = generateInversePref(preferences.getStudents_preference());
        
        //loop through each professor and add to result
        for (int i = 0; i < l; i++) {
            Cost cost = new Cost(i, prof_optimal.get(i), l - profInversePref[i][prof_optimal.get(i)], 
                    l - stuInversePref[prof_optimal.get(i)][i]);
            result.add(cost);
        }        
        return result;
    }
    
    // Part3 - b: Matching with Costs (Student Optimal)
    public static ArrayList<Cost> stableMatchCostsStudent(Preferences preferences) {
        // get the length of the array
        int l = preferences.getNumberOfProfessors();
        ArrayList<Cost> result = new ArrayList<>();
        
        // swap the preference lists of student and professor, which will generate a student optimal result
  	    ArrayList<ArrayList<Integer>> profPrerenceList = preferences.getProfessors_preference();
  	    preferences.setProfessors_preference(preferences.getStudents_preference()); 
  	    preferences.setStudents_preference(profPrerenceList);
  	    ArrayList<Integer> stu_optimal = stableMatchGaleShapley(preferences);
  	    
  	    // use the inverse preference to calculate the cost to each other in an easier way
  	    int[][] profInversePref = generateInversePref(preferences.getProfessors_preference());
  	    int[][] stuInversePref = generateInversePref(preferences.getStudents_preference());
  	    
  	    //loop through each professor and add to result
  	    for (int i = 0; i < l; i++) {
  	        Cost cost = new Cost(i, stu_optimal.get(i), l - profInversePref[i][stu_optimal.get(i)], 
  	                l - stuInversePref[stu_optimal.get(i)][i]);
  	        result.add(cost);
  	    }        
  	    return result;
  	}
}