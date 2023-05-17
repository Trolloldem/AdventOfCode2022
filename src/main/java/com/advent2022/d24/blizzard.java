package com.advent2022.d24;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Map.Entry;

import com.advent2022.utils.Coord;

/**
 * Support class to store Coordinates along reach time and distance from end
 **/
class SearchPoint implements Comparable<SearchPoint>{
  Coord coord;
  Integer time;
  Integer dist;

  SearchPoint(Coord coord, int time, int dist){
    this.coord = coord;
    this.time = time;
    this.dist = dist;
  }

  /**
   * Comparable needed to use PriorityQueue
   **/
  @Override
  public int compareTo(SearchPoint other) {
    int dist_cmp = this.dist.compareTo(other.dist);
    if(dist_cmp == 0) {
      return this.time.compareTo(other.time);
    }
    return dist_cmp;
  }
}

/**
 * Store the Coordinates of already visited Coordinates at given times
 **/
class VisitedPoint {
  Coord coord;
  Integer time;
  
  VisitedPoint(Coord coord, int time) {
    this.coord = coord;
    this.time = time;
  }

  public boolean equals(Object obj) {
    if(!(obj instanceof VisitedPoint)) {
      return false;
    }
    return ((VisitedPoint)obj).coord.equals(this.coord) && 
      ((VisitedPoint)obj).time.equals(this.time);
  }

  public int hashCode(){
    String to_hash = this.coord.hashCode() + "|" + this.time;
    return to_hash.hashCode();
  }

}

public class blizzard {

  /**
   * Use A* to sort Coordinates to explore in such a way that avoids loops and 
   * that the first node that reaches the end is also the one using less steps. 
   * 
   * The frontier is a PriorityQueue taking into account distance + time.
   * The frontier is expanded only is at least one neighboor is in a space that 
   * is not occupied by a blizzard during the next turn.
   **/
  static int star(HashMap<Coord, Character> blizzards, HashSet<Coord> walls, 
      Coord pos, Coord ending_pos, int rows, int cols, 
      int starting_time) {
    PriorityQueue<SearchPoint> frontier = new PriorityQueue<>();
    HashSet<VisitedPoint> visited = new HashSet<>();

    Coord[] directions = {new Coord(0, 1), new Coord(0, -1), new Coord(1, 0), 
      new Coord(-1, 0), new Coord(0, 0)};

    int start_dist = Math.abs(pos.x - ending_pos.x) 
      + Math.abs(pos.y - ending_pos.y);
    frontier.add(new SearchPoint(pos, starting_time, start_dist));

    while(frontier.size() > 0) {
      SearchPoint act = frontier.poll();
      HashSet<Coord> candidates = new HashSet<>();
      for(Coord dir:directions) {
        Coord candidate = new Coord(act.coord.x + dir.x, act.coord.y + dir.y);

        // In the map and not in a wall
        if (!walls.contains(candidate) && candidate.x >= 0 
            && candidate.x < rows && candidate.y >= 0 && candidate.y < cols) {
          candidates.add(candidate);
        }
      }

      // A candidate is the ending => return its number of steps + 1
      if(candidates.contains(ending_pos)) {
        return act.time + 1;
      }

      // Check the position of each blizzard at the next turn
      for(Entry<Coord, Character> entry:blizzards.entrySet()){
        int x = 0;
        int y = 0;
        int movs = 0;
        Coord blizzard_pos = entry.getKey();
        char dir = entry.getValue();
        switch(dir){
          case '^':
            movs = (act.time + 1) % (rows-2);
            x = blizzard_pos.x - movs;
            if(x<=0){
              x = rows -2 - Math.abs(x);
            }
            y = blizzard_pos.y;
          break;
          case 'v':
            movs = (act.time + 1) % (rows-2);
            x = blizzard_pos.x + movs;
            if(x>=rows-1){
              x -= rows - 2;
            }
            y = blizzard_pos.y;
          break;
          case '>':
            movs = (act.time + 1) % (cols-2);
            y = blizzard_pos.y + movs;
            if(y>=cols-1){
              y -= cols - 2;
            }
            x = blizzard_pos.x;
          break;
          case '<':
            movs = (act.time + 1) % (cols-2);
            y = blizzard_pos.y - movs;
            if(y<=0){
              y = cols - 2 - Math.abs(y);
            }
            x = blizzard_pos.x;
          break;
          default:
          break;
        }
        // Check if the blizzard occupies the Coordinates of a neighboor
        Coord check = new Coord(x, y);
        if(candidates.contains(check)) {
          candidates.remove(check);
        }
        if(candidates.size() == 0) {
          break;
        }
      }

      for(Coord candidate:candidates){
        VisitedPoint check_visited = new VisitedPoint(candidate, act.time+1);
        if(!visited.contains(check_visited)) {
          visited.add(check_visited);
          int distance = act.time + 1 
            + Math.abs(candidate.x-ending_pos.x) 
            + Math.abs(candidate.y-ending_pos.y);
          frontier.add(new SearchPoint(candidate, act.time+1, distance));
        }
      }
    }
    return 0;
  }

  /**
   * Parse the input and store the Coordinates + directions of blizzard. 
   * Store also walls and starting + ending Coordinates. 
   * 
   * Use A* to explore. When triple is true, also return to start and finally 
   * go again towards the end. The starting timw of the last two is the time 
   * used to reach teh previous one.
   **/
  static int sol(String filename, boolean triple) {

    HashMap<Coord, Character> blizzards = new HashMap<>();
    HashSet<Coord> walls = new HashSet<>();
    boolean start = true;
    Coord starting_pos = null;
    Coord ending_pos = null;

    int x = 0;
    int y = 0;

    try {
      InputStream iStream = blizzard.class. 
        getClassLoader(). 
        getResourceAsStream(filename);
      BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
      String line;
      while((line = reader.readLine()) != null) {
        y = 0;
        for(char c:line.toCharArray()) {
          if(c == '#') {
            walls.add(new Coord(x, y));
          } else if(c == '.') {
            ending_pos = new Coord(x, y);
            if (start) {
              start = false;
              starting_pos = new Coord(x, y);
            }
          } else {
            blizzards.put(new Coord(x, y), c);
          }
          y += 1;
        }
        x += 1;
      }
      reader.close();
    } catch (FileNotFoundException e) {
      System.out.println("File not found");
      e.printStackTrace();
    } catch (IOException e) {
      System.out.println("End of file");
    }

    int result = star(blizzards, walls, starting_pos, ending_pos, x, y, 0);
    if(triple){
      result = star(blizzards, walls, ending_pos, starting_pos, x, y, result);
      result = star(blizzards, walls, starting_pos, ending_pos, x, y, result);
    }
    return result;
  }

  public static void execute(){
    String example = "inputs/d24/example.txt";
    System.out.println("(example) Solution 1:" + sol(example, false));
    System.out.println("(example) Solution 2:" + sol(example, true));

    String input = "inputs/d24/input.txt";
    System.out.println("Solution 1:" + sol(input, false));
    System.out.println("Solution 2:" + sol(input, true));
  }

}
