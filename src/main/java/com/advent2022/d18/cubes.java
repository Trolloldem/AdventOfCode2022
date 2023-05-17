package com.advent2022.d18;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import com.advent2022.utils.Coord;

/**
 * Enum used to check automate the iteration 
 * over the 6 sides of a cube
 **/
enum Side {
  X_LEFT(0),
  X_RIGHT(1),
  Y_UP(2),
  Y_DOWN(3),
  Z_FRONT(4),
  Z_REAR(5); 

  int idx;

  Side(int idx){
    this.idx = idx;
  }
}

/**
 * Support class to store exposed sides
 **/
class Cube {
  int x;
  int y;
  int z;
  int free_sides = 6;
  boolean[] sides = new boolean[6];

  Cube(int x, int y, int z){
    this.x = x;
    this.y = y;
    this.z = z;
    for(int i=0;i<sides.length;i++){
      sides[i] = true;
    }
  }

  /**
   * Get a Coord representing the values 
   * of the sides not touched
   **/
  Coord other_sides(Side side){
    switch(side){
      case X_RIGHT: 
      case X_LEFT:
        return new Coord(y, z);
      case Y_UP:
      case Y_DOWN:
        return new Coord(x, z);
      case Z_REAR:
      case Z_FRONT:
        return new Coord(x, y);
      default:
        return null;
    }
  }

  /**
   * Get the neighbor Cube near the 
   * specified Side
   **/
  Cube near_cube(Side side){
    switch(side){
      case X_RIGHT:
        return new Cube(x+1, y, z);
      case X_LEFT:
        return new Cube(x-1, y, z);
      case Y_UP:
        return new Cube(x, y-1, z);
      case Y_DOWN:
        return new Cube(x, y+1, z);
      case Z_REAR:
        return new Cube(x, y, z+1);
      case Z_FRONT:
        return new Cube(x, y, z-1);
      default:
        return null;
    }

  }

  /**
   * Update number of Sides that are free
   **/
  void set_occupation(Side side){
    boolean free = sides[side.idx];
    if(free) {
      sides[side.idx] = false;
      free_sides -= 1;
    }
  }

  /**
   * Check if the specified Side is free
   **/
  boolean is_free(Side side){
    return sides[side.idx]; 
  }

  public boolean equals(Object obj) {
    if(!(obj instanceof Cube)) {
      return false;
    }

    Cube other = (Cube)obj;
    return this.x == other.x && this.y == other.y && this.z == other.z;
  }

  public int hashCode(){
    String to_hash = ((Integer)this.x).toString() + "|" + ((Integer)this.y).toString() + "|" + ((Integer)this.z).toString();
    return to_hash.hashCode();
  }

}

public class cubes {

  /**
   * Check if Cube is outside of the boundaries explored
   **/
  static boolean is_outside(Cube neighbor, int max_x, int min_x, int max_y, int min_y, int max_z, int min_z){
    return neighbor.x < min_x || neighbor.x > max_x || neighbor.y < min_y || neighbor.y > max_y || neighbor.z < min_z || neighbor.z > max_z;
  }
  
  /**
   * Exploration Algorithm
   **/
  static void bfs(HashSet<Cube> cubes, HashSet<Cube> air, HashSet<Cube> water, Cube start, int max_x, int min_x, int max_y, int min_y, int max_z, int min_z){
    HashSet<Cube> frontier = new HashSet<>();
    frontier.add(start);
    HashSet<Cube> visited = new HashSet<>();
    boolean is_water = false;
    Cube[] dirs = {new Cube(-1, 0, 0), new Cube(1,0,0), new Cube(0, -1, 0), new Cube(0, 1, 0), new Cube(0, 0, -1), new Cube(0, 0, 1)};
    while(frontier.size()>0){
      HashSet<Cube> n_frontier = new HashSet<>();
      for(Cube act:frontier) {
        visited.add(act);
        for(Cube dir:dirs){
          Cube neighbor = new Cube(act.x+dir.x, act.y+dir.y, act.z+dir.z);
          if(water.contains(neighbor)){
            is_water = true;
          }
          boolean out = is_outside(neighbor, max_x, min_x, max_y, min_y, max_z, min_z);
          if(out) {
            is_water = true;
          }
          if(!out && !cubes.contains(neighbor) && !visited.contains(neighbor)){
            n_frontier.add(neighbor);
          }
        }
      }
      frontier = n_frontier;
    }
    if(is_water){
      water.addAll(visited);
    } else {
      air.addAll(visited);
    }
  }

  /**
   * Check if the Side of a Cube is occupied given 
   * the Cubes with the same coordinates over one dimension
   **/
  static void update_side(Cube cube, HashMap<Integer, LinkedList<Cube>> index,Side check, Side opposite, int key){
    if(cube.is_free(check)) {
      if(index.containsKey(key)) {
        for(Cube other:index.get(key)){
          if(cube.other_sides(check).equals(other.other_sides(opposite))){
            other.set_occupation(opposite);
            cube.set_occupation(check);
            break;
          }
        }
      }
    }
  }

  /**
   * Search air cubes starting from a Side of a cube
   **/
  static int search_side(Cube cube, Side side, HashMap<Integer, LinkedList<Cube>> index, HashSet<Cube> air_cubes, HashSet<Cube> water_cubes, HashSet<Cube> cubes, int max_x, int min_x, int max_y, int min_y, int max_z, int min_z){
    if(cube.is_free(side)){
      Cube to_find = cube.near_cube(side);
      if(air_cubes.contains(to_find)) {
        cube.set_occupation(side);
        return -1;
      }
      bfs(cubes, air_cubes, water_cubes, to_find, max_x, min_x, max_y, min_y, max_z, min_z);
      if(air_cubes.contains(to_find)) {
        cube.set_occupation(side);
        return -1;
      }
    }
    return 0;
  }

  /**
   * Update the HashMap representing the Cubes 
   * with a given value of 1 coordinate
   **/
  static void update_index(HashMap<Integer, LinkedList<Cube>> index, Integer key, Cube cube) {
    if(!index.containsKey(key)){
      index.put(key, new LinkedList<>());
    }
    index.get(key).add(cube);
  }

  /**
   * Parse the Cube coordinates.
   * Part 1: For each cube check each side if it is free or not
   * Part 2: For each side explore if the neighbor cubes are 
   * air or water.
   **/
  static int sol(String filename, boolean check_outside){

    int max_x = Integer.MIN_VALUE;
    int min_x = Integer.MAX_VALUE;
    int max_y = Integer.MIN_VALUE;
    int min_y = Integer.MAX_VALUE;
    int max_z = Integer.MIN_VALUE;
    int min_z = Integer.MAX_VALUE;

    HashSet<Cube> cube_set = new HashSet<>();
    HashMap<Integer, LinkedList<Cube>> x_index = new HashMap<>();
    HashMap<Integer, LinkedList<Cube>> y_index = new HashMap<>();
    HashMap<Integer, LinkedList<Cube>> z_index = new HashMap<>();

    try {
      InputStream iStream = cubes.class.
        getClassLoader()
        .getResourceAsStream(filename);
      BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
      String line;
      while((line=reader.readLine()) != null){
        String[] tokens = line.split(",");
        int x = Integer.parseInt(tokens[0]);
        int y = Integer.parseInt(tokens[1]);
        int z = Integer.parseInt(tokens[2]);
        max_x = Math.max(max_x, x);
        min_x = Math.min(min_x, x);
        max_y = Math.max(max_y, y);
        min_y = Math.min(min_y, y);
        max_z = Math.max(max_z, z);
        min_z = Math.min(min_z, z);
        Cube cube = new Cube(x, y, z);
        cube_set.add(cube);
        update_index(x_index, x, cube);
        update_index(y_index, y, cube);
        update_index(z_index, z, cube);
      }
      reader.close();
    } catch (FileNotFoundException e) {
      System.out.println("File not found");
      e.printStackTrace();
    } catch (IOException e) {
      System.out.println("End of file");
    }
  
    int res = 0;
    for(Cube cube:cube_set){
      int x = cube.x - 1;
      update_side(cube, x_index, Side.X_LEFT, Side.X_RIGHT, x);
      x = cube.x + 1;
      update_side(cube, x_index, Side.X_RIGHT, Side.X_LEFT, x);
      int y = cube.y - 1;
      update_side(cube, y_index, Side.Y_UP, Side.Y_DOWN, y);
      y = cube.y + 1;
      update_side(cube, y_index, Side.Y_DOWN, Side.Y_UP, y);
      int z = cube.z - 1;
      update_side(cube, z_index, Side.Z_FRONT, Side.Z_REAR, z);
      z = cube.z + 1;
      update_side(cube, z_index, Side.Z_REAR, Side.Z_FRONT, z);
      res += cube.free_sides;
    }

    if(check_outside){
      HashSet<Cube> air_cubes = new HashSet<>();
      HashSet<Cube> water_cubes = new HashSet<>();
      for(Cube cube:cube_set){
        if(cube.free_sides > 0) {
          for(Side side:Side.values()){
            HashMap<Integer, LinkedList<Cube>> index = null;
            switch(side){
              case X_RIGHT: 
              case X_LEFT:
                index = x_index;
              case Y_UP:
              case Y_DOWN:
                index = y_index;
              case Z_REAR:
              case Z_FRONT:
                index = z_index;
            }
            res += search_side(cube, side, index, air_cubes, water_cubes, cube_set, max_x, min_x, max_y, min_y, max_z, min_z);
          }
        }
      }
    }
    return res;
  }


  public static void execute(){
    String example = "inputs/d18/example.txt";
    System.out.println("(example) Solution 1:" + sol(example, false));
    System.out.println("(example) Solution 2:" + sol(example, true));

    String input = "inputs/d18/input.txt";
    System.out.println("Solution 1:" + sol(input, false));
    System.out.println("Solution 2:" + sol(input, true));
  }

}
