package com.advent2022.d8;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class visible{
 
  /**
   * For each tree check every direction 
   * In one case just check if it is visible from outside 
   * In the other check how many tree it can see
   **/
  static int sol(String filename, boolean visible) {
    ArrayList<ArrayList<Integer>> matrix = new ArrayList<>();
    try{
      int idx = 0;
      InputStream iStream = visible.class. 
        getClassLoader(). 
        getResourceAsStream(filename);
      BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
      String line;
      while((line = reader.readLine()) != null) {
        matrix.add(new ArrayList<>(line.length()));
        for(Character c: line.toCharArray()) {
          matrix.get(idx).add(Integer.parseInt(c.toString()));
        }
        idx += 1;
      }
      reader.close();
    } catch (FileNotFoundException e) {
      System.out.println("File not found");
      e.printStackTrace();
    } catch (IOException e) {
      System.out.println("End of file");
    }

    int res = 0;
    for(int row = 0; row < matrix.size(); row++){
      for(int col = 0; col < matrix.get(0).size(); col++){
          if ((row == 0 || row == matrix.size() - 1) || (col == 0 || col == matrix.get(0).size()-1)){
            if (visible){
              res += 1;
            }
            continue;
          }
          
          int right = 0;
          int left = 0;
          int top = 0;
          int bottom = 0;
          int i = 0;
          boolean right_visible = true;
          boolean left_visible = true;
          boolean top_visible = true;
          boolean bottom_visible = true;

          for(i = col-1; i > -1; i--){
            if (matrix.get(row).get(i) >= matrix.get(row).get(col)){
              left_visible = false;
              break;
            }
          }
          if (i == -1) {
            i = 0;
          }
          left = col - i;
          for(i = col+1; i<matrix.get(0).size(); i++){
            if (matrix.get(row).get(i) >= matrix.get(row).get(col)){
              right_visible = false;
              break;
            }
          }
          if(i == matrix.get(0).size()) {
            i -= 1;
          }
          right = i - col;
          for(i = row-1; i > -1; i--){
            if (matrix.get(i).get(col) >= matrix.get(row).get(col)){
              top_visible = false;
              break;
            }
          }
          if (i == -1){
            i = 0;
          }
          top = row - i;
          for(i = row+1; i < matrix.size(); i++){
            if(matrix.get(i).get(col) >= matrix.get(row).get(col)){
              bottom_visible = false;
              break;
            }
          }
          if(i == matrix.size()) {
            i -= 1;
          }
          bottom = i - row;
          if (visible){
            if(right_visible || left_visible || top_visible || bottom_visible){
              res += 1;
            }
          } else {
            res = Math.max(res, right * left * top * bottom);
          }
      }
    }
    return res;
  } 

  public static void execute(){
    String example = "inputs/d8/example.txt";
    System.out.println("(example) Solution 1:" + sol(example, true));
    System.out.println("(example) Solution 2:" + sol(example, false));

    String input = "inputs/d8/input.txt";
    System.out.println("Solution 1:" + sol(input, true));
    System.out.println("Solution 2:" + sol(input, false));

  }
}
