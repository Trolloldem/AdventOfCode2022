package com.advent2022.d17;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import com.advent2022.utils.Coord;

class Piece{
  
  Coord[] cells;
  static int size = 5;
  static Character stop = Character.valueOf('#');

  /**
   * Initialize Horizontal line
   **/
  void create_hline(int mh) {
    Coord[] cells = {new Coord(mh-4, 2), new Coord(mh-4, 3), new Coord(mh-4, 4), new Coord(mh-4, 5)};
    this.cells = cells;
  }

  /**
   * Initialize Vertical line
   **/
  void create_vline(int mh) {
    Coord[] cells = {new Coord(mh-4, 2), new Coord(mh-5, 2), new Coord(mh-6, 2), new Coord(mh-7, 2)};
    this.cells = cells;
  }

  /**
   * Initialize Square
   **/
  void create_square(int mh){
    Coord[] cells = {new Coord(mh-4, 2), new Coord(mh-5, 2), new Coord(mh-4, 3), new Coord(mh-5, 3)};
    this.cells = cells;
  }
   
  /**
   * Initialize L shape
   **/
  void create_L(int mh){
    Coord[] cells = {new Coord(mh-4, 2), new Coord(mh-4, 3), new Coord(mh-4, 4), new Coord(mh-5, 4), new Coord(mh-6, 4)};
    this.cells = cells;
  }

  /**
   * Initialize Plus shape
   **/
  void create_plus(int mh){
    Coord[] cells = {new Coord(mh-5,2), new Coord(mh-4, 3), new Coord(mh-5, 3), new Coord(mh-6, 3), new Coord(mh-5, 4)};
    this.cells = cells;
  }

  /**
   * Check if the Piece can be pushed to the left and update its cells
   **/
  void push_left(ArrayList<ArrayList<Character>> matrix) {
    for(Coord cell:this.cells){
      int r = cell.x;
      int c = cell.y;
      if(c-1 < 0 || matrix.get(r).get(c-1).equals(stop)) {
        return;
      }
    }
    for(int i = 0; i<this.cells.length;i++){
      cells[i].y -= 1;
    }
  }

  /**
   * Check if the Piece can be pushed to the right and update its cells
   **/
  void push_right(ArrayList<ArrayList<Character>> matrix) {
    for(Coord cell:this.cells){
      int r = cell.x;
      int c = cell.y;
      if(c+1 >= matrix.get(0).size() || matrix.get(r).get(c+1).equals(stop)) {
        return;
      }
    }
    for(int i = 0; i<this.cells.length;i++){
      cells[i].y += 1;
    }
  }

  /**
   * Check if the Piece is falling and update its cells
   **/
  boolean fall(ArrayList<ArrayList<Character>> matrix) {
    for(Coord cell:this.cells){
      int r = cell.x;
      int c = cell.y;
      if(r+1 >= matrix.size() || matrix.get(r+1).get(c).equals(stop)) {
        return false;
      }
    }
    for(int i = 0; i<this.cells.length;i++){
      cells[i].x += 1;
    }
    return true;
  }
  
  /**
   * Update the matrix with the Coord of each cell of the Piece
   **/
  void stop(ArrayList<ArrayList<Character>> matrix) {
    for(Coord cell:this.cells){
      int r = cell.x;
      int c = cell.y;
      matrix.get(r).set(c, stop);
    }
  }

  /**
   * Get higher row of the shape
   **/
  int get_row(){
    int min = Integer.MAX_VALUE;
    for(Coord cell:this.cells){
      min = Math.min(min, cell.x);
    }
    return min;
  }

  /**
   * The constructor is responsible of the rock shape 
   * and initial location
   **/
  Piece(int i, int mh){
    switch(i) {
    case 0:
      create_hline(mh);
    break;
    case 1:
      create_plus(mh);
    break;
    case 2:
      create_L(mh);
    break;
    case 3:
      create_vline(mh);
    break;
    case 4:
      create_square(mh);
    break;
    }
  }
}

public class rocks {
  /**
   * Simply simulate in a large buffer. For part 2 first 
   * keep track of when a repeating sequence start. Then 
   * stop simulating, calculate the height after multiple 
   * repetition and simulate the remainder.
   **/
  public static long sol(String filename, boolean repeat) {
    int height = repeat? 4*10000 :4 * 2030; // buffer to let block fall
    HashMap<Coord, Integer> counter = new HashMap<>();
    int width = 7; // width
    int mh = height;
    String pattern = "";
    try {
      InputStream iStream = rocks.class.
        getClassLoader()
        .getResourceAsStream(filename);
      BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
      pattern = reader.readLine();
      reader.close();
    } catch (FileNotFoundException e) {
      System.out.println("File not found");
      e.printStackTrace();
    } catch (IOException e) {
      System.out.println("End of file");
    }
    boolean is_push = true;
    int idx_push = 0;
    int idx_piece = 0;
    ArrayList<ArrayList<Character>> matrix = new ArrayList<>(height);
    for(int i = 0; i<height; i++){
      ArrayList<Character> act = new ArrayList<>(width);
      for(int j = 0; j < width; j++) {
        act.add(Character.valueOf('.'));
      }
      matrix.add(act);
    }
    long blocks = repeat ? 1000000000000l: 2022;
    boolean break_ext = false;
    long i;
    long prev = 0;
    long prev_mh = 0;
    Coord to_repeat = null;
    for(i =0; i<blocks && !break_ext;i++){
      Piece piece = new Piece(idx_piece, mh);
      idx_piece = (idx_piece + 1) % Piece.size;
      while(true){
        if(is_push) { // Push step
          is_push = false;
          char direction = pattern.charAt(idx_push);
          idx_push = (idx_push + 1) % pattern.length();
          if(direction == '<') {
            piece.push_left(matrix); 
          } else {
            piece.push_right(matrix);
          }
        } else { // Fall step
          is_push = true;
          if(!piece.fall(matrix)) {
            piece.stop(matrix);
            mh = Math.min(mh, piece.get_row());
            if(repeat) {
              Coord couple = new Coord(idx_push, idx_piece);
              if(!counter.containsKey(couple)) {
                counter.put(couple, 0);
              }
              counter.put(couple, counter.get(couple)+1);
              // Sequence start to repeat from the third time they appear
              if(to_repeat == null && counter.get(couple) == 3) {
                to_repeat = couple;
                prev = i; // Keep track of size in block
                prev_mh = mh; // Keep tack of size in height
                break;
              }
              if(to_repeat != null && couple.equals(to_repeat)){
                break_ext = true; // Fourth time, first repetition reached
              }
            }
            break;
          }
        }
      }
    }

    // Keep falling but count only repeating sequence
    if(repeat){
      i-=1; // To handle break, we have already incremented i without need
      long repeated_mh = prev_mh -mh; // Height of the repeating sequence
      long size = i - prev; // Size of the repeating sequence in blocks
      long mul = (blocks - i -1) / size; // Blocks count from 0
      long remain = blocks -i - mul * size - 1; // Blocks count from 0
      long act_height = height - mh + mul * repeated_mh; // Current height
      prev_mh = mh; // Keep track of starting point
      while(remain > 0){ // Keep falling
        remain -= 1;
        Piece piece = new Piece(idx_piece, mh);
        idx_piece = (idx_piece + 1) % Piece.size;
        while(true){
          if(is_push) {
            is_push = false;
            char direction = pattern.charAt(idx_push);
            idx_push = (idx_push + 1) % pattern.length();
            if(direction == '<') {
              piece.push_left(matrix); 
            } else {
              piece.push_right(matrix);
            }
          } else {
            is_push = true;
            if(!piece.fall(matrix)) {
              piece.stop(matrix);
              mh = Math.min(mh, piece.get_row());
              break;
            }
          }
        }
      }
      act_height = act_height + (prev_mh - mh); // Calculate additional height
      return act_height;
    }
    return height-mh;
  }



  public static void execute(){
    String example = "inputs/d17/example.txt";
    System.out.println("(example) Solution 1:" + sol(example, false));
    System.out.println("(example) Solution 2:" + sol(example, true));

    String input = "inputs/d17/input.txt";
    System.out.println("Solution 1:" + sol(input, false));
    System.out.println("Solution 2:" + sol(input, true));
    
  }
}
