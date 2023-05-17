package com.advent2022.d4;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class overlap {
  /**
   *  Check for complete overlap between two intervals
   **/
  static boolean complete_overlap(Integer[] i1, Integer[] i2) {
    return i1[0] <= i2[0] && i1[1] >= i2[1];
  }

  /**
   *  Check for partial overlap between two intervals
   **/
  static boolean partial_overlap(Integer[] i1, Integer[] i2) {
    return (i1[0] <= i2[0] && i2[0] <= i1[1]) || (i1[0] <= i2[1] && i2[1] <= i1[1]);
  }

  /**
   * Apply one of the two helper function to check for overlapping intervals
   **/
  static int sol(String filename, boolean check_partial) {
    int res = 0;
    try{
      InputStream iStream = overlap.class 
        .getClassLoader() 
        .getResourceAsStream(filename);
      BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
      String line;
      while((line = reader.readLine()) != null) {
        String[] intervals = line.split(",");
        Integer[] interval1 = { Integer.parseInt(intervals[0].split("-")[0]), Integer.parseInt(intervals[0].split("-")[1]) };
        Integer[] interval2 = { Integer.parseInt(intervals[1].split("-")[0]), Integer.parseInt(intervals[1].split("-")[1]) };
        if (check_partial) {
          if (complete_overlap(interval1, interval2)   || complete_overlap(interval2, interval1) || partial_overlap(interval1, interval2) || partial_overlap(interval1, interval2)) {
            res += 1;
          } 
        } else {
          if (complete_overlap(interval1, interval2)   || complete_overlap(interval2, interval1)) {
            res += 1;
          }
        }
      }
      reader.close();
    } catch (FileNotFoundException e) {
      System.out.println("File not found");
      e.printStackTrace();
    } catch (IOException e) {
      System.out.println("End of file");
    }
    return res;
  }
  
  public static void execute(){
    String example = "inputs/d4/example.txt";
    System.out.println("(example) Solution 1:" + sol(example, false));
    System.out.println("(example) Solution 2:" + sol(example, true));

    String input = "inputs/d4/input.txt";
    System.out.println("Solution 1:" + sol(input, false));
    System.out.println("Solution 2:" + sol(input, true));

  }

}
