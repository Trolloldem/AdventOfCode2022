package com.advent2022.d22;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.advent2022.utils.Coord;

/**
 * Helper class to Store a Warp on a Cube
 **/
class CubeWarp {
  Function<Coord, Coord> transformation;
  Coord dir;

  CubeWarp(Function<Coord, Coord> transformation, Coord dir){
    this.transformation = transformation;
    this.dir = dir;
  }

}

/**
 * Helper enum to take track of directions
 **/
enum Directions {
  RIGHT(new Coord(0, 1), 0),
  LEFT(new Coord(0, -1), 2),
  UP(new Coord(-1, 0), 3),
  DOWN(new Coord(1, 0), 1);

  Coord dir;
  int val;

  Directions(Coord dir, int val){
    this.dir = dir;
    this.val = val;
  }
  
  /**
   * Warp is done through a Mapping between the various 'colored' faces of the 
   * cube. The warp destination depends on:
   * 1) starting_row  and starting_col 
   * 2) current direction
   * 4) current cube face
   * The previous factor are the keys to the values of the mappings:
   * 1) Direction after the warp
   * 2) Lambda to transform the coordinates to the correct one on the new face
   *
   * The starting_row and starting_col are adapted with multiples of the 
   * dimension of one side of the cube to place the cursor in the right Coord.
   **/
  HashMap<Character, CubeWarp> init_map(int starting_row, int starting_col){
    HashMap<Character, CubeWarp> res = new HashMap<>();
    Function<Coord, Coord> lambda;
    switch(this){
    case UP:
      lambda = (coord) -> new Coord(starting_row+50+coord.y -starting_col+50,starting_col);
      res.put('4', new CubeWarp(lambda, Directions.RIGHT.dir));
      lambda = (coord) -> new Coord(starting_row+199, starting_col -50 + coord.y - starting_col -50);
      res.put('2', new CubeWarp(lambda, Directions.UP.dir));
      lambda = (coord) -> new Coord(starting_row+150+coord.y-starting_col, starting_col-50);
      res.put('1', new CubeWarp(lambda, Directions.RIGHT.dir));
    break;
    case DOWN:
      lambda = (coord) -> new Coord(starting_row+50+coord.y-starting_col-50, starting_col+49);
      res.put('2', new CubeWarp(lambda, Directions.LEFT.dir));
      lambda = (coord) -> new Coord(starting_row+150+coord.y-starting_col, starting_col-1);
      res.put('5', new CubeWarp(lambda, Directions.LEFT.dir));
      lambda = (coord) -> new Coord(starting_row ,starting_col+50+coord.y-starting_col+50);
      res.put('6', new CubeWarp(lambda, Directions.DOWN.dir));
    break;
    case RIGHT:
      lambda = (coord) -> new Coord(starting_row+149-coord.x+starting_row, starting_col+49);
      res.put('2', new CubeWarp(lambda, Directions.LEFT.dir));
      lambda = (coord) -> new Coord(starting_row+49, starting_col+50+coord.x-starting_row-50);
      res.put('3', new CubeWarp(lambda, Directions.UP.dir));
      lambda = (coord) -> new Coord(starting_row+49-coord.x+starting_row+100, starting_col+99);
      res.put('5', new CubeWarp(lambda, Directions.LEFT.dir));
      lambda = (coord) -> new Coord(starting_row+149, starting_col+coord.x-starting_row-150);
      res.put('6', new CubeWarp(lambda, Directions.UP.dir));
    break;
    case LEFT:
      lambda = (coord) -> new Coord(starting_row+149-coord.x+starting_row, starting_col-50);
      res.put('1', new CubeWarp(lambda, Directions.RIGHT.dir));
      lambda = (coord) -> new Coord(starting_row+100, starting_col-50+coord.x-starting_row-50);
      res.put('3', new CubeWarp(lambda, Directions.DOWN.dir));
      lambda = (coord) -> new Coord(starting_row+49-coord.x+starting_row+100, starting_col);
      res.put('4', new CubeWarp(lambda, Directions.RIGHT.dir));
      lambda = (coord) -> new Coord(starting_row, starting_col + coord.x-starting_row-150);
      res.put('6', new CubeWarp(lambda, Directions.DOWN.dir));
    break;
    }
    return res;
  }

  static Directions from_Coord(Coord dir) {
    for(Directions d:Directions.values()){
      if(dir.equals(d.dir)){
        return d;
      }
    }
    return null;
  }
}

/**
 * Support class to expose the result of a normal warp
 **/
class Warp{
  int row;
  int col;
  boolean stop;

  Warp(int row, int col, boolean stop){
    this.row = row;
    this.col = col;
    this.stop = stop;
  }
}

public class warp{

  static HashMap<Coord, Coord> dirs_clockwise = null;
  static HashMap<Coord, Coord> dirs_anticlockwise = null;

  /**
   * Generate and use the map to track the rotation of the cursor clockwise
   **/
  static Coord get_clockwise(Coord coord) {
    if(dirs_clockwise == null){
      dirs_clockwise = new HashMap<>();
      dirs_clockwise.put(Directions.RIGHT.dir, Directions.DOWN.dir);
      dirs_clockwise.put(Directions.DOWN.dir, Directions.LEFT.dir);
      dirs_clockwise.put(Directions.LEFT.dir, Directions.UP.dir);
      dirs_clockwise.put(Directions.UP.dir, Directions.RIGHT.dir);
    }
    return dirs_clockwise.get(coord);
  }

  /**
   * Generate and use the map to track the rotation of the cursor anticlockwise
   **/
  static Coord get_anticlockwise(Coord coord) {
    if(dirs_anticlockwise == null){
      dirs_anticlockwise = new HashMap<>();
      dirs_anticlockwise.put(Directions.RIGHT.dir, Directions.UP.dir);
      dirs_anticlockwise.put(Directions.UP.dir, Directions.LEFT.dir);
      dirs_anticlockwise.put(Directions.LEFT.dir, Directions.DOWN.dir);
      dirs_anticlockwise.put(Directions.DOWN.dir, Directions.RIGHT.dir);
    }
    return dirs_anticlockwise.get(coord);
  }

  /**
   * Check if the cursor coordinates will be on the board 
   **/
  static boolean in_board(int tr, int tc, ArrayList<ArrayList<Character>> matrix){
    return tr >= 0 && tr < matrix.size() && tc >= 0 && 
      tc < matrix.get(tr).size() && !matrix.get(tr).get(tc).equals(' ') && 
      !matrix.get(tr).get(tc).equals('#');
  }

  /**
   * Check if the cursor coordinates will be on a wall
   **/
  static boolean to_stop(int tr, int tc, ArrayList<ArrayList<Character>> matrix) {
    return tr >= 0 && tr < matrix.size() && tc >= 0 && 
    tc < matrix.get(tr).size() && matrix.get(tr).get(tc).equals('#');
  }

  /**
   * Perform a normal warp and do nothing if the warp places the cursor on a 
   * wall
   **/
  static Warp do_warp(Coord dir, ArrayList<ArrayList<Character>> matrix, int row, int col){
    int end = 0;
    int start = 0;
    int step = 0;
    Directions d = Directions.from_Coord(dir);
    boolean vertical = d.equals(Directions.UP) || d.equals(Directions.DOWN);
    switch(d){
    case RIGHT:
      start = 0;
      step = 1;
      end = matrix.get(row).size();
    break;
    case LEFT:
      start = matrix.get(row).size()-1;
      end = -1;
      step = -1;
    break;
    case UP:
      start = matrix.size() - 1;
      end = -1;
      step = -1;
    break;
    case DOWN:
      start = 0;
      end = matrix.size();
      step = 1;
    break;
    }

    boolean stop = false;
    for(int i = start; (step > 0 && i < end || step < 0 && i > end); i += step){
      char check = vertical ? matrix.get(i).get(col) : matrix.get(row).get(i);
      if(check != ' ') {
        if(check == '#') {
          stop = true;
          break;
        } else {
          if(vertical){
           row = i;
          } else{
           col = i;
          }
          break;
        }
      }
    }
    return new Warp(row, col, stop);
  }

  /**
   * Color sides of the cube to quickly check in which face it is placed the 
   * cursor. An alternative would be to identify the limits of each face as 
   * intervals, but this alternative also serves to debug
   **/
  static void color_side(char color, ArrayList<ArrayList<Character>> matrix, int row, int col){
    for(int idx_row = 0; idx_row < 50; idx_row++){
      for(int idx_col = 0; idx_col < 50; idx_col++){
        int tr = row + idx_row;
        int tc = col + idx_col;
        if(tr >= 0 && tr < matrix.size() && tc >= 0 && tc < matrix.get(tr).size() && matrix.get(tr).get(tc).equals('.')) {
          matrix.get(tr).set(tc, color);
        }
      }
    }
  }

  /**
   * Parse the walls in the cube and move the cursors accord to the warp method 
   * that has to be used. When 'cube' is true, CubeWarp is used, otherwise 
   * warp().
   **/
  static long sol(String filename, boolean cube) {

    ArrayList<ArrayList<Character>> matrix = new ArrayList<>();
    String instr = "";
    try{
        InputStream iStream = warp.class
          .getClassLoader()
          .getResourceAsStream(filename);
        BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
        String line;
        boolean lines = true;
        int size = 0;
        while((line = reader.readLine()) != null) {
          if(line.equals("")){
            lines = false;
            continue;
          }
          if(lines){
            if(size == 0){
              size = line.length();
            }
            ArrayList<Character> row = new ArrayList<>(size);
            for(int i = 0; i< size; i++){
              if(i<line.length()){
                row.add(line.charAt(i));
              } else {
                row.add(' ');
              }
            }
            matrix.add(row);
          } else {
            instr = line;
          }
        }
        reader.close();
      } catch (FileNotFoundException e) {
        System.out.println("File not found");
        e.printStackTrace();
      } catch (IOException e) {
        System.out.println("End of file");
    }
    int row = 0;
    int col = 0;
    Coord dir = new Coord(0, 1);
    Character point = Character.valueOf('.');
    for(int c = 0; c<matrix.get(row).size();c++){
      if(matrix.get(row).get(c).equals(point)){
        col = c;
        break;
      }
    }

    int starting_row = row;
    int starting_col = col;
    HashMap<Coord, HashMap<Character, CubeWarp>> mappings = null;
    if(cube) {
      color_side('1', matrix, row, col);
      color_side('2',matrix, row, col+50);
      color_side('3', matrix, row+50, col);
      color_side('4', matrix, row+100, col-50);
      color_side('5', matrix, row+100, col);
      color_side('6', matrix, row+150, col-50);
      mappings = new HashMap<>();
      for(Directions d:Directions.values()){
        mappings.put(d.dir, d.init_map(starting_row, starting_col));
      }
    }

    int idx = 0;
    while(idx < instr.length()){
      LinkedList<Character> to_conv = new LinkedList<>();
      char ct_char = instr.charAt(idx);;
      while(idx < instr.length() && ct_char != 'R' && ct_char != 'L') {
        to_conv.add(ct_char);
        idx += 1;
        if(idx < instr.length()){
          ct_char = instr.charAt(idx);
        }
      }
      int steps = Integer.parseInt(to_conv.stream().map(x-> x.toString()).collect(Collectors.joining("")));
      for(int step=0; step<steps;step++){
        int tr = row + dir.x;
        int tc = col + dir.y;
        if(in_board(tr, tc, matrix)) {
          row = tr;
          col = tc;
        } else {
          if(to_stop(tr, tc, matrix)){
            break;
          } else { 
            if(!cube){
              Warp warp_res = do_warp(dir, matrix, row, col);
              row = warp_res.row;
              col = warp_res.col;
              if(warp_res.stop){
                break;
              }
            } else {
              CubeWarp cw = mappings.get(dir).get(matrix.get(row).get(col));
              Coord new_dir = cw.dir;
              Coord new_coord = cw.transformation.apply(new Coord(row, col));
              if(matrix.get(new_coord.x).get(new_coord.y).equals('#')) {
                break;
              }
              row = new_coord.x;
              col = new_coord.y;
              dir = new_dir;
            }
          }
        }
      }
      if(idx == instr.length()){
        return 1000*(row+1)+4*(col+1)+Directions.from_Coord(dir).val;
      }
      if(instr.charAt(idx) == 'R') {
        dir = get_clockwise(dir);
      } else {
        dir = get_anticlockwise(dir);
      }
      idx += 1;
    }
    return 0;
  }
  
  public static void execute(){
    String example = "inputs/d22/example.txt";
    System.out.println("(example) Solution 1:" + sol(example, false));
   
    String input = "inputs/d22/input.txt";
    System.out.println("Solution 1:" + sol(input, false));
    System.out.println("Solution 2:" + sol(input, true));
  }
}
