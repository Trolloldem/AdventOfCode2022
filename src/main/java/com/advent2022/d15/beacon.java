package com.advent2022.d15;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.advent2022.utils.Coord;

/**
 * Comparator used to order Coord used to 
 * represent intervals (x=star, y=end)
 **/
class segment_cmp implements Comparator<Coord>{

  @Override
  public int compare(Coord p1, Coord p2) {
    if(p1.x != p2.x){
      return Integer.compare(p1.x, p2.x);
    }
    return Integer.compare(p1.y, p2.y);
  }

}

public class beacon {

  /**
   * Merge and ordered ArrayList of Coord representing intervals 
   * into non-overlapping intervals
   **/
  static LinkedList<Coord> merge(ArrayList<Coord> coords) {
    LinkedList<Coord> res = new LinkedList<>(); 
    if(coords.size() <= 1){
       for(Coord c:coords){
         res.add(c);
       }
       return res;
     }
    res.add(coords.get(0));
    int idx = 0;
    while(idx<coords.size()){
      Coord interval1 = coords.get(idx);
      Coord interval2 = res.getLast();
      if(interval2.y >= interval1.x) {
        Coord new_interval = new Coord(Math.min(interval1.x, interval2.x), Math.max(interval1.y, interval2.y));
        res.removeLast();
        res.addLast(new_interval);
      } else {
        res.addLast(interval1);
      }
      idx += 1;
    }
    return res;
  }

  /**
   * Calculate the Manhattan distance between two coords 
   **/
  static int get_distance(Coord p1, Coord p2) {
    return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
  }

  /**
   * Parse coord from a String in the form: 
   * x=2, y=18
   **/
  static Coord get_coord(String info){
    String[] tokens = info.split("=");
    StringBuilder x_sb = new StringBuilder();
    for(char c:tokens[1].toCharArray()){
      if(c == ',') {
        break;
      }
      x_sb.append(Character.valueOf(c));
    }
    StringBuilder y_sb = new StringBuilder();
    for(char c:tokens[2].toCharArray()){
      y_sb.append(Character.valueOf(c));
    }
    int x = Integer.parseInt(x_sb.toString());
    int y = Integer.parseInt(y_sb.toString());
    return new Coord(x, y);
  }

  /**
   * Parse the input and keep track of the positions of 
   * the sensors and the distance between the closest 
   * beacon. Then for each possible row between start and 
   * end check represent as intervals the coordinates covered 
   * by each sensor, then sort and merge the intervals. 
   * If start == end, for the row with index == start sum 
   * the coverage of all intervals. Otherwise, check the first 
   * column not part of an interval covering that row
   **/
  static long sol(String filename, int start, int end) {
    HashMap<Coord, Integer> sensors = new HashMap<>();
    long res = 0; // Only used for part 1
    try{

      InputStream iStream = beacon.class
        .getClassLoader()
        .getResourceAsStream(filename);
      BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
      String line;

      // Ger sensors
      while((line = reader.readLine()) != null) {
        String[] tokens = line.split(":");
        Coord sensor = get_coord(tokens[0]);
        Coord beacon = get_coord(tokens[1]);
        sensors.put(sensor, get_distance(sensor, beacon));
      }
      reader.close();
    } catch (FileNotFoundException e) {
      System.out.println("File not found");
      e.printStackTrace();
    } catch (IOException e) {
      System.out.println("End of file");
    }

    // Check each row between start and end
    for(int target=start; target<=end;target++) {
      HashSet<Coord> current = new HashSet<>();
      for(Entry<Coord, Integer>entry:sensors.entrySet()){
        Coord sensor = entry.getKey();
        Integer dist = entry.getValue();
        Integer vertical_dist = get_distance(sensor, new Coord(sensor.x, target));
        if(vertical_dist<=dist){ // The row can be reached vertically by the sensor
          // Add the interval of columns covered by the sensors
          current.add(new Coord(sensor.x-(dist-vertical_dist), sensor.x+(dist-vertical_dist)));
        }
      }

      // Sort and merge overlapping intervals
      ArrayList<Coord> sorted_curr = current.stream().sorted(new segment_cmp()).collect(Collectors.toCollection(ArrayList::new));
      LinkedList<Coord> merged = merge(sorted_curr);

      // Check in the final intervals for first free cell / sum of covered cells
      for(Coord c:merged){
        if(start != end) {
          if(c.x > start || c.y < end) { // interval leaves at least one cell free after start of before and
            if (c.x > start) {
              return ((long)(c.x-1))*4000000l+target;
            } else {
              return ((long)(c.y+1))*4000000l+target;
            }
          }
        } else {
          res += (Math.abs(c.y - c.x) + 1);
        }
      }

    }
    return res;
  }

  public static void execute(){
    String example = "inputs/d15/example.txt";
    System.out.println("(example) Solution 1:" + sol(example, 10, 10));
    System.out.println("(example) Solution 2:" + sol(example, 0, 20));
    
    String input = "inputs/d15/input.txt";
    System.out.println("Solution 1:" + sol(input, 2000000, 2000000));
    System.out.println("Solution 2:" + sol(input, 0, 4000000));
  }
}
