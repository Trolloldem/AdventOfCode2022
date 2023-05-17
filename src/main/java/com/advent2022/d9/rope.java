package com.advent2022.d9;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.advent2022.utils.Coord;

public class rope{
 
  /**
   * Updates the "following node" (tail) when the row or the column is the same
   **/
  static void check_dist(Coord to_update, int eq1, int eq2, int diff1, int diff2, boolean row) {
    if (eq1 == eq2 && Math.abs(diff1-diff2) > 1) {
      if (diff1 > diff2) {
        if (row) {
          to_update.x += 1;
        } else {
          to_update.y += 1;
        }
      } else {
        if (row) {
          to_update.x -= 1;
        } else {
          to_update.y -= 1;
        }

      }
    }
  }

  /**
   * Ropes are stored in an ArrayList with the capacity
   * fixed on the requested part (2 for first, 10 for second)
   **/
  static int sol(String filename, boolean multiple) {
    Coord tail = null;
    Coord head = null;
    int iterations = multiple? 10 : 2;
    ArrayList<Coord> nodes = new ArrayList<>(iterations);
    for(int i=0; i<iterations; i++){
      nodes.add(new Coord(0, 0));
    }
    HashSet<Coord> positions = new HashSet<>();
    positions.add(new Coord(0, 0));
    HashMap<String, Coord> directions = new HashMap<>();
    directions.put("R", new Coord(0, 1));
    directions.put("U", new Coord(-1, 0));
    directions.put("D", new Coord(1, 0));
    directions.put("L", new Coord(0, -1));
    try{
      InputStream iStream = rope.class. 
        getClassLoader(). 
        getResourceAsStream(filename);
      BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
      String line;
      while((line = reader.readLine()) != null) {
        String[] tokens = line.split(" ");
        Coord direction = directions.get(tokens[0]);
        int steps = Integer.parseInt(tokens[1]);
        for(int i = 0; i<steps; i++){
          for(int j = 0; j < iterations-1; j++){
            tail = nodes.get(j+1);
            head = nodes.get(j);
            if(j == 0){
              head.x += direction.x;
              head.y += direction.y;
            }
            // Same row 
            check_dist(tail, head.x, tail.x, head.y, tail.y, false);
            // Same col
            check_dist(tail, head.y, tail.y, head.x, tail.x, true);
            // Handle diagonal movements
            if (head.x != tail.x && head.y != tail.y && Math.abs(head.x - tail.x) + Math.abs(head.y - tail.y) > 2) {
              if(head.x > tail.x && head.y > tail.y) {
                tail.x += 1;
                tail.y += 1;
              } else if(head.x > tail.x && head.y < tail.y){
                tail.x += 1;
                tail.y -= 1;
              } else if(head.x < tail.x && head.y < tail.y) {
                tail.x -= 1;
                tail.y -= 1;
              } else {
                tail.x -= 1;
                tail.y += 1;
              }
            }
          }
          // Only final node positions are requested
          positions.add(new Coord(tail.x, tail.y));
        }
      }
      reader.close();
    } catch (FileNotFoundException e) {
      System.out.println("File not found");
      e.printStackTrace();
    } catch (IOException e) {
      System.out.println("End of file");
    }
   
    return positions.size();
  }
  
  public static void execute(){
    String example1 = "inputs/d9/example.txt";
    System.out.println("(example 1) Solution 1:" + sol(example1, false));
    System.out.println("(example 1) Solution 2:" + sol(example1, true));
    
    String example2 = "inputs/d9/example2.txt";
    System.out.println("(example 2) Solution 1:" + sol(example2, false));
    System.out.println("(example 2) Solution 2:" + sol(example2, true));


    String input = "inputs/d9/input.txt";
    System.out.println("Solution 1:" + sol(input, false));
    System.out.println("Solution 2:" + sol(input, true));

  }
}
