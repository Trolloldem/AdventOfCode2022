package com.advent2022.d10;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class cycle {

  /**
   * Iterate over the operations and update the x accordingly.
   * Pay attention when it is updated because two cicle are used.
   **/
  static int sol_1(String filename){
    int x = 1;
    int cycle = 1;
    Integer[] supp_arr = {20, 60, 100, 140, 180, 220 };
    HashSet<Integer> to_check = new HashSet<>(Arrays.asList(supp_arr));
    int res = 0;
    try{
      InputStream iStream = cycle.class. 
        getClassLoader(). 
        getResourceAsStream(filename);
      BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
      String line;
      while((line = reader.readLine()) != null) {
        String[] tokens = line.split(" ");
        cycle += 1;
        if(tokens.length == 1) {
        } else {
            if(to_check.contains(cycle)) {
              res += x * cycle;
            }
            cycle += 1;
            x += Integer.parseInt(tokens[1]);
        }
        if (to_check.contains(cycle)){
          res += x * cycle;
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

  /**
   * At each cycle control the positions near the current x for the cursor 
   * Pay attention when the screen of 40 character is filled
   * Pay attention when there are no more lines but the reader asks for new lines
   **/
  static String sol_2(String filename) {
    int x = 1;
    LinkedList<LinkedList<Character>> res = new LinkedList<>();
    try{
      InputStream iStream = cycle.class. 
        getClassLoader(). 
        getResourceAsStream(filename);
      BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
      String line;
      LinkedList<Character> act = new LinkedList<>();
      boolean to_break = false;
      while(!to_break) {
        while(act.size() < 40){
          line = reader.readLine();
          if (line == null) {
            to_break = true;
            break;
          }
          String[] tokens = line.split(" ");
          if (tokens.length == 1) {
            if(Math.abs(act.size() - x) <= 1) {
              act.add('#');
            } else {
              act.add('.');
            } 
          } else {
            if(Math.abs(act.size() - x) <= 1){
              act.add('#');
            } else {
              act.add('.');
            }
            if (act.size() == 40) {
              res.add(act);
              act = new LinkedList<>();
            }
            if(Math.abs(act.size() - x) <= 1){
              act.add('#');
            } else {
              act.add('.');
            }
            x += Integer.parseInt(tokens[1]); 
          }
          if (act.size() == 40) {
            res.add(act);
            act = new LinkedList<>();
          }
        }  
      }
      reader.close();
    } catch (FileNotFoundException e) {
      System.out.println("File not found");
      e.printStackTrace();
    } catch (IOException e){} // Requested newLine even if the input finished
   
    // Fancy way to create List<String> from List<List<Character>
    List<String> tmp = res.stream()
      .map(elem -> String.join(" ", 
            elem.stream().map(character -> character.toString()).collect(Collectors.toList())
            ))
      .collect(Collectors.toList());
    return String.join("\n", tmp);

  }
  public static void execute(){
    String example = "inputs/d10/example.txt";
    System.out.println("(example 1) Solution 1:" + sol_1(example));
    System.out.println("(example 1) Solution 2:\n" + sol_2(example));
    
    String input = "inputs/d10/input.txt";
    System.out.println("Solution 1:" + sol_1(input));
    System.out.println("Solution 2:\n" + sol_2(input));

  }
}
