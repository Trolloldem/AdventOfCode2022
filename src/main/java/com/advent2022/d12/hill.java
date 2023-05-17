package com.advent2022.d12;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;

import com.advent2022.utils.Coord;

/**
 * Support class to take into account a Set of visited
 * the original value (kept to fast check instead of getting the value in matrix)
 **/
class hillCoord extends Coord {
  public Character val;
  public HashSet<hillCoord> visited;

  public hillCoord(int x, int y) {
    super(x, y);
  }
}

public class hill {
  
  /**
   * Set of Coord used to loop every possible direction
   **/
  static Coord[] dirs = {new Coord(0, 1), new Coord(0, -1), new Coord(1, 0), new Coord(-1, 0)};

  /**
   * Check if the provided coordinates can be visited (bounds check + check if visited)
   **/
  static boolean visitable(ArrayList<char[]> matrix, int row, int col, HashSet<hillCoord> visited) {
    return row >= 0 && col >= 0 && row < matrix.size() && col < matrix.get(0).length && !visited.contains(new hillCoord(row, col)); 
  }

  /**
   * Check if it is possible to move in the given coord
   **/
  static boolean can_move(ArrayList<char[]> matrix, int row, int col, char val) {
    return val + 1 >= matrix.get(row)[col];
  }

  /**
   * Given a node and a frontier, add neighboor Coord in the frontier after checking 
   * if Coord are visitable and are available for movements
   **/
  static void movements(ArrayList<char[]> matrix, HashSet<hillCoord> frontier, int row, int col, char val, HashSet<hillCoord> visited){
    for(Coord dir: dirs){
      int n_row = row + dir.x;
      int n_col = col + dir.y;
      if (visitable(matrix, n_row, n_col, visited) && can_move(matrix, n_row, n_col, val)) {
        hillCoord next_coord = new hillCoord(n_row, n_col);
        next_coord.val = matrix.get(n_row)[n_col];
        next_coord.visited = visited;
        frontier.add(next_coord);
      }
    }
  }

  /** 
   * Create the hills and store it as an ArrayList of char[]
   * For each S (or 'S' + 'a's if multiple is true) fix a starting point 
   * and a new Set of visited Coord. Use BFS to explore and find the first 
   * step in which the ending node is added to the frontier.
   **/
  static int sol(String filename, boolean multiple) {
    ArrayList<char[]> matrix = new ArrayList<>();
    hillCoord end = new hillCoord(0, 0);
    HashSet<hillCoord> frontier = new HashSet<>();
    try{
      InputStream iStream = hill.class
        .getClassLoader()
        .getResourceAsStream(filename);
      BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
      String line;
      while((line = reader.readLine()) != null) {
        matrix.add(line.toCharArray());
        int row = matrix.size() - 1;
        for(int col = 0; col < line.length(); col++){
          if ((multiple && line.charAt(col) == 'a') || (line.charAt(col) == 'S')) {
            hillCoord start = new hillCoord(row, col);
            start.val = matrix.get(row)[col];
            if(start.val.equals(Character.valueOf('S'))) {
              start.val = 'a';
              matrix.get(row)[col] = 'a';
            }
            start.visited = new HashSet<>();
            frontier.add(start);
          } else if (line.charAt(col) == 'E') {
            end.x  = row;
            end.y = col;
            matrix.get(row)[col] = 'z';
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

    int res = 0;
    while (frontier.size() > 0) {
      HashSet<hillCoord> new_frontier = new HashSet<>();
      for (hillCoord coord:frontier) {
        movements(matrix, new_frontier, coord.x, coord.y, coord.val, coord.visited);
        coord.visited.add(coord);
      }
      res += 1;
      if (new_frontier.contains(end)) {
        return res;
      }
      frontier = new_frontier;
    }
    return res;
  }
 
  public static void execute(){
    String example = "inputs/d12/example.txt";
    System.out.println("(example) Solution 1:" + sol(example, false));
    System.out.println("(example) Solution 2:" + sol(example, true));
    
    String input = "inputs/d12/input.txt";
    System.out.println("Solution 1:" + sol(input, false));
    System.out.println("Solution 2:" + sol(input, true));

  }

}
