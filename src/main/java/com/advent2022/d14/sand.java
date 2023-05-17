package com.advent2022.d14;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;

import com.advent2022.utils.Coord;

/**
 * Support class to keep a couple of Coords
 **/
class Line {
  Coord start; 
  Coord end;

  Line(Coord start, Coord end) {
    this.start = start;
    this.end = end;
  }

public boolean equals(Object obj) {
    if(!(obj instanceof Line)) {
      return false;
    }
    return ((Line)obj).start.equals(this.start) && ((Line)obj).end.equals(this.end);
  }

  public int hashCode(){
    String to_hash = ((Integer)this.start.x).toString() + "->" 
      + ((Integer)this.start.y).toString() + "|"
      + ((Integer)this.end.x).toString() + "->" + ((Integer)this.end.y).toString();
    return to_hash.hashCode();
  }

}

public class sand {
 /**
  * Check if Coordinates are inside the boundaries of our matrix
  **/
  static boolean is_valid(ArrayList<ArrayList<Character>> matrix, int x, int y) {
    return y < matrix.size() && x >= 0 && x < matrix.get(0).size();
  }


  /**
   * Fill of walls Character.valueOf('x') the given line 
   **/
  static void fill(ArrayList<ArrayList<Character>> matrix, Line l, int start_x){
    
    // Vertical
    if(l.start.x == l.end.x) {
      int s = Math.min(l.start.y, l.end.y);
      int e = Math.max(l.start.y, l.end.y);
      for(int i = s; i<e+1; i++){
        matrix.get(i).set(l.start.x - start_x, 'x');
      }
    } else { // Horizontal
      int s = Math.min(l.start.x, l.end.x) - start_x;
      int e = Math.max(l.start.x, l.end.x) - start_x;
      for(int i = s; i < e+1; i++){
        matrix.get(l.start.y).set(i, 'x');
      }
    }
  }

  /**
   * Parse the Coordinates and create the walls with the fill method.
   * After that simulate a single sand piece fall. When is_void is false, 
   * simply check when if falls outised of the boundaries.
   * If is_void is true, add an infinite floor and enlarge the matrix to 
   * account for Horizontal boundaries. 
   **/
  static int sol(String filename, boolean is_void) {
    int start_x = Integer.MAX_VALUE;
    int end_x = Integer.MIN_VALUE;
    int end_y = Integer.MIN_VALUE;
    HashSet<Line> coords = new HashSet<>();
    try{

      InputStream iStream = sand.class
        .getClassLoader()
        .getResourceAsStream(filename);
      BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
      String line;
      while((line = reader.readLine()) != null) {
        String[] tokens = line.split(" -> ");
        for(int i = 0; i < tokens.length-1; i++){
          String[] tt = tokens[i].split(",");
          start_x = Math.min(Integer.parseInt(tt[0]), start_x);
          end_x = Math.max(Integer.parseInt(tt[0]), end_x);
          end_y = Math.max(Integer.parseInt(tt[1]), end_y);
          Coord p1 = new Coord(Integer.parseInt(tt[0]), Integer.parseInt(tt[1]));
          tt = tokens[i+1].split(",");
          start_x = Math.min(Integer.parseInt(tt[0]), start_x);
          end_x = Math.max(Integer.parseInt(tt[0]), end_x);
          end_y = Math.max(Integer.parseInt(tt[1]), end_y);
          Coord p2 = new Coord(Integer.parseInt(tt[0]), Integer.parseInt(tt[1]));
          coords.add(new Line(p1, p2));
        }
      }
      reader.close();
    } catch (FileNotFoundException e) {
      System.out.println("File not found");
      e.printStackTrace();
    } catch (IOException e) {
      System.out.println("End of file");
    }

    // Larger matrix to treat what falls outside
    if (is_void) {
      end_y += 2; // max additional height needed with fake floor
      start_x -= 200;
      end_x += 200;
    }
    ArrayList<ArrayList<Character>> matrix = new ArrayList<>(end_y+1);
    for(int row = 0; row < end_y + 1; row++){
      ArrayList<Character> act = new ArrayList<>(end_x - start_x + 1);
      for(int col = 0; col < end_x - start_x + 1; col++){
        act.add('.');
      }
      matrix.add(act);
    }
    for(Line l:coords){
      fill(matrix, l, start_x);
    }

    // Fake floor 
    if(is_void){
      for(int i=0;  i< matrix.get(0).size(); i++){
        matrix.get(matrix.size()-1).set(i, 'x');
      }
    }

    for(ArrayList<Character> a:matrix){
      StringBuilder b = new StringBuilder();
      for(Character c:a){
        b.append(c);
      }
    }

    boolean fallen = false;
    int res = 0;
    Character x_char = Character.valueOf('x');
    // Update the exit condition according to the value of is_void
    while((!is_void && !fallen) || (is_void && !matrix.get(0).get(500-start_x).equals(x_char))) {
      int x = 500 - start_x;
      int y = 0;
      while(true) {
        if(is_valid(matrix, x, y+1) && !matrix.get(y+1).get(x).equals(x_char)) {
          y += 1;
        } else if (is_valid(matrix, x-1, y+1) && !matrix.get(y+1).get(x-1).equals(x_char)){
          y += 1;
          x -= 1;
        } else if (!is_void && x-1 == -1) {
          fallen = true; // Only valid if it is possible to fall 
          break;
        } else if (is_valid(matrix, x+1, y+1) && !matrix.get(y+1).get(x+1).equals(x_char)) {
          y += 1; 
          x += 1;
        } else if (!is_void && x+1  == matrix.get(0).size()) {
          fallen = true; // Only valid if it is possible to fall
          break;
        } else if (!is_void && y+1 == matrix.size()) {
          fallen = true; // Only valid if it is possible to fall
          break;
        } else {
          res += 1;
          matrix.get(y).set(x, 'x');
          break;
        }
      }
    }
    return res;
  }
  
  public static void execute(){
    String example = "inputs/d14/example.txt";
    System.out.println("(example) Solution 1:" + sol(example, false));
    System.out.println("(example) Solution 2:" + sol(example, true));
    
    String input = "inputs/d14/input.txt";
    System.out.println("Solution 1:" + sol(input, false));
    System.out.println("Solution 2:" + sol(input, true));

  }
}
