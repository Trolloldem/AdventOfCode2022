package com.advent2022.d1;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.PriorityQueue;

public class calories {
  
      /**
   * Get the most calories among elves
   **/
  public static int sol_1(String filename) {
    int most_cal = 0;
    int curr_elf = 0;
    try {
      InputStream iStream = calories.class
        .getClassLoader()
        .getResourceAsStream(filename);
      BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
      String line;
      while((line = reader.readLine()) != null) {
        try {
          curr_elf += Integer.parseInt(line);
        } catch (NumberFormatException e) {
          most_cal = Math.max(most_cal, curr_elf);
          curr_elf = 0;
        }
      }
      iStream.close();
    } catch (FileNotFoundException e) {
      System.out.println("File not found");
      e.printStackTrace();
    } catch (IOException e) {
      System.out.println("End of file");
    } 
    return most_cal;
  }
  /**
   * PriorityQueue used to store the 3
   * elves with the most calories
   **/
  public static int sol_2(String filename) {
    PriorityQueue<Integer> top_three = new PriorityQueue<>();
    int curr_elf = 0;
    try {
      InputStream iStream = calories.class.
        getClassLoader()
        .getResourceAsStream(filename);
      BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
      String line;
      while((line = reader.readLine()) != null) {
        try {
          curr_elf += Integer.parseInt(line);
        } catch (NumberFormatException e) {
          top_three.offer(curr_elf);
          if (top_three.size() > 3) {
            top_three.poll();
          }
          curr_elf = 0;
        }
      }
      iStream.close();
    } catch (FileNotFoundException e) {
      System.out.println("File not found");
      e.printStackTrace();
    } catch (IOException e) {
      System.out.println("End of file");
    }

    top_three.offer(curr_elf);
    if (top_three.size() > 3) {
      top_three.poll();
    }

    int res = 0;
    while (top_three.size() > 0) {
      res += top_three.poll();
    }
    return res;
  }



  public static void execute(){
    String example = "inputs/d1/example.txt";
    System.out.println("(example) Solution 1:" + sol_1(example));
    System.out.println("(example) Solution 2:" + sol_2(example));

    String input = "inputs/d1/input.txt";
    System.out.println("Solution 1:" + sol_1(input));
    System.out.println("Solution 2:" + sol_2(input));


  }

}
