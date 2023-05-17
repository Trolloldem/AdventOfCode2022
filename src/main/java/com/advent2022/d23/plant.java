package com.advent2022.d23;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import com.advent2022.utils.Coord;

/**
 * Support enum to clearly identify directions
 **/
enum Cardinals {
  NORTH(0),
  EAST(1),
  SOUTH(2),
  WEST(3),
  NORTHEAST(4),
  NORTHWEST(5),
  SOUTHEAST(6),
  SOUTHWEST(7);

  int idx;

  Cardinals(int idx){
    this.idx = idx;
  }

}

/**
 * Support class to store Coordinates + flag of Elf presence + Neighbors nodes
 **/
class Node{

  boolean elf;
  int x;
  int y;
  ArrayList<Node> neigh;

  /**
   * 1 possible Neighbor per direction. Initially, all Neighbors are null
   **/
  Node(boolean elf, int x, int y){
    this.elf = elf;
    this.x = x;
    this.y = y;

    neigh = new ArrayList<>(Cardinals.values().length);
    for(int i = 0; i<Cardinals.values().length;i++){
      neigh.add(null);
    }
  }

  /**
   * Check if there are no elves in Neighbors
   **/
  boolean no_one_near(){
    return neigh.stream().allMatch(x -> x == null || !x.elf);
  }

  /**
   * Check if the north area is free
   **/
  boolean propose_north(){
    Cardinals[] north = {Cardinals.NORTH, Cardinals.NORTHEAST, Cardinals.NORTHWEST};
    return Arrays.stream(north).allMatch(x -> neigh.get(x.idx) == null 
        || !neigh.get(x.idx).elf);
  }

  /**
   * Check if the south area is free
   **/
  boolean propose_south(){
    Cardinals[] south = {Cardinals.SOUTH, Cardinals.SOUTHEAST, Cardinals.SOUTHWEST};
    return Arrays.stream(south).allMatch(x -> neigh.get(x.idx) == null 
        || !neigh.get(x.idx).elf);
  }
 
  /**
   * Check if the east area is free
   **/
  boolean propose_east(){
    Cardinals[] east = {Cardinals.EAST, Cardinals.NORTHEAST, Cardinals.SOUTHEAST};
    return Arrays.stream(east).allMatch(x -> neigh.get(x.idx) == null 
        || !neigh.get(x.idx).elf);
  }

  /**
   * Check if the west area is free
   **/
  boolean propose_west(){
    Cardinals[] west = {Cardinals.WEST, Cardinals.NORTHWEST, Cardinals.SOUTHWEST};
    return Arrays.stream(west).allMatch(x -> neigh.get(x.idx) == null 
        || !neigh.get(x.idx).elf);
  }

  /**
   * Check if the an area is free given its direction
   **/
  boolean check_proposal(Cardinals cardinal) {
    switch(cardinal){
    case NORTH: 
      return this.propose_north();
    case SOUTH:
      return this.propose_south();
    case EAST:
      return this.propose_east();
    case WEST:
      return this.propose_west();
    default: // This case is never verified
      return false;
    }
  }

  /**
   * Set the Neighbors in the corresponding direction
   **/
  void set(Cardinals cardinal, Node node) {
    this.neigh.set(cardinal.idx, node);
  }

  /**
   * Get the Neighbor in the given direction
   **/
  Node get(Cardinals cardinal){
    return this.neigh.get(cardinal.idx);
  }
}

public class plant {

  /**
   * Updates the network given a new set of coordinates containing an elf
   **/
  static void update(Node move, HashSet<Node> elves, HashMap<Coord, Node> map, int x, int y){

    // CASE: WEST
    if(move.y - y == 1) { // The node already exists
      if(move.get(Cardinals.WEST) != null) {
        move.get(Cardinals.WEST).elf = true;
        elves.add(move.get(Cardinals.WEST));
      } else{
        // Create the node an populate the immediate neighbors using the 
        // elf previous position
        Node to_add = new Node(true, x, y);
        to_add.set(Cardinals.EAST, move);
        to_add.set(Cardinals.NORTHEAST, move.get(Cardinals.NORTH));
        to_add.set(Cardinals.SOUTHEAST, move.get(Cardinals.SOUTH));
        to_add.set(Cardinals.NORTH, move.get(Cardinals.NORTHWEST));
        to_add.set(Cardinals.SOUTH, move.get(Cardinals.SOUTHWEST));

        // Update the node containing the elf previous position with the new 
        // node
        move.set(Cardinals.WEST, to_add);
        if(move.get(Cardinals.NORTH) != null){
          move.get(Cardinals.NORTH).set(Cardinals.SOUTHWEST, to_add);
        } 
        if(move.get(Cardinals.SOUTH) != null){
          move.get(Cardinals.SOUTH).set(Cardinals.NORTHWEST, to_add);
        }
        if(move.get(Cardinals.NORTHWEST) != null) {
          move.get(Cardinals.NORTHWEST).set(Cardinals.SOUTH, to_add);
        }
        if(move.get(Cardinals.SOUTHWEST) != null) {
          move.get(Cardinals.SOUTHWEST).set(Cardinals.NORTH, to_add);
        }

        // Check if the remaining neighbors already exists and update them
        Coord west = new Coord(x, y-1);
        Coord north_west = new Coord(x-1, y-1);
        Coord south_west = new Coord(x+1, y-1);
        if(map.containsKey(west)){
          map.get(west).set(Cardinals.EAST, to_add);
          to_add.set(Cardinals.WEST, map.get(west));
        }
        if(map.containsKey(north_west)){
          map.get(north_west).set(Cardinals.SOUTHEAST, to_add);
          to_add.set(Cardinals.NORTHWEST, map.get(north_west));
        }
        if(map.containsKey(south_west)){
          map.get(south_west).set(Cardinals.NORTHEAST, to_add);
          to_add.set(Cardinals.SOUTHWEST, map.get(south_west));
        }
        // Add the new node to the network and to the elves set
        map.put(new Coord(x, y), to_add);
        elves.add(to_add);
      }
    }

    // CASE: EAST -> See WEST
    if(move.y - y == -1) {
      if(move.get(Cardinals.EAST) != null) {
        move.get(Cardinals.EAST).elf = true;
        elves.add(move.get(Cardinals.EAST));
      } else{
        Node to_add = new Node(true, x, y);
        to_add.set(Cardinals.WEST, move);
        to_add.set(Cardinals.NORTHWEST, move.get(Cardinals.NORTH));
        to_add.set(Cardinals.SOUTHWEST, move.get(Cardinals.SOUTH));
        to_add.set(Cardinals.NORTH, move.get(Cardinals.NORTHEAST));
        to_add.set(Cardinals.SOUTH, move.get(Cardinals.SOUTHEAST));

        move.set(Cardinals.EAST, to_add);
        if(move.get(Cardinals.NORTH) != null){
          move.get(Cardinals.NORTH).set(Cardinals.SOUTHEAST, to_add);
        } 
        if(move.get(Cardinals.SOUTH) != null){
          move.get(Cardinals.SOUTH).set(Cardinals.NORTHEAST, to_add);
        }
        if(move.get(Cardinals.NORTHEAST) != null) {
          move.get(Cardinals.NORTHEAST).set(Cardinals.SOUTH, to_add);
        }
        if(move.get(Cardinals.SOUTHEAST) != null) {
          move.get(Cardinals.SOUTHEAST).set(Cardinals.NORTH, to_add);
        }

        Coord east = new Coord(x, y+1);
        Coord north_east = new Coord(x-1, y+1);
        Coord south_east = new Coord(x+1, y+1);
        if(map.containsKey(east)){
          map.get(east).set(Cardinals.WEST, to_add);
          to_add.set(Cardinals.EAST, map.get(east));
        }
        if(map.containsKey(north_east)){
          map.get(north_east).set(Cardinals.SOUTHWEST, to_add);
          to_add.set(Cardinals.NORTHEAST, map.get(north_east));
        }
        if(map.containsKey(south_east)){
          map.get(south_east).set(Cardinals.NORTHWEST, to_add);
          to_add.set(Cardinals.SOUTHEAST, map.get(south_east));
        }
        map.put(new Coord(x, y), to_add);
        elves.add(to_add);
      }
    }
    
    // CASE NORTH -> See WEST
    if(move.x - x == 1) {
      if(move.get(Cardinals.NORTH) != null) {
        move.get(Cardinals.NORTH).elf = true;
        elves.add(move.get(Cardinals.NORTH));
      } else{
        Node to_add = new Node(true, x, y);
        to_add.set(Cardinals.SOUTH, move);
        to_add.set(Cardinals.SOUTHEAST, move.get(Cardinals.EAST));
        to_add.set(Cardinals.SOUTHWEST, move.get(Cardinals.WEST));
        to_add.set(Cardinals.EAST, move.get(Cardinals.NORTHEAST));
        to_add.set(Cardinals.WEST, move.get(Cardinals.NORTHWEST));

        move.set(Cardinals.NORTH, to_add);
        if(move.get(Cardinals.WEST) != null){
          move.get(Cardinals.WEST).set(Cardinals.NORTHEAST, to_add);
        } 
        if(move.get(Cardinals.EAST) != null){
          move.get(Cardinals.EAST).set(Cardinals.NORTHWEST, to_add);
        }
        if(move.get(Cardinals.NORTHWEST) != null) {
          move.get(Cardinals.NORTHWEST).set(Cardinals.EAST, to_add);
        }
        if(move.get(Cardinals.NORTHEAST) != null) {
          move.get(Cardinals.NORTHEAST).set(Cardinals.WEST, to_add);
        }

        Coord north = new Coord(x-1, y);
        Coord north_west = new Coord(x-1, y-1);
        Coord north_east = new Coord(x-1, y+1);
        if(map.containsKey(north)){
          map.get(north).set(Cardinals.SOUTH, to_add);
          to_add.set(Cardinals.NORTH, map.get(north));
        }
        if(map.containsKey(north_east)){
          map.get(north_east).set(Cardinals.SOUTHWEST, to_add);
          to_add.set(Cardinals.NORTHEAST, map.get(north_east));
        }
        if(map.containsKey(north_west)){
          map.get(north_west).set(Cardinals.SOUTHEAST, to_add);
          to_add.set(Cardinals.NORTHWEST, map.get(north_west));
        }
        map.put(new Coord(x, y), to_add);
        elves.add(to_add);
      }
    }

    // CASE SOUTH -> See WEST
    if(move.x - x == -1) {
      if(move.get(Cardinals.SOUTH) != null) {
        move.get(Cardinals.SOUTH).elf = true;
        elves.add(move.get(Cardinals.SOUTH));
      } else{
        Node to_add = new Node(true, x, y);
        to_add.set(Cardinals.NORTH, move);
        to_add.set(Cardinals.NORTHEAST, move.get(Cardinals.EAST));
        to_add.set(Cardinals.NORTHWEST, move.get(Cardinals.WEST));
        to_add.set(Cardinals.EAST, move.get(Cardinals.SOUTHEAST));
        to_add.set(Cardinals.WEST, move.get(Cardinals.SOUTHWEST));

        move.set(Cardinals.SOUTH, to_add);
        if(move.get(Cardinals.WEST) != null){
          move.get(Cardinals.WEST).set(Cardinals.SOUTHEAST, to_add);
        } 
        if(move.get(Cardinals.EAST) != null){
          move.get(Cardinals.EAST).set(Cardinals.SOUTHWEST, to_add);
        }
        if(move.get(Cardinals.SOUTHWEST) != null) {
          move.get(Cardinals.SOUTHWEST).set(Cardinals.EAST, to_add);
        }
        if(move.get(Cardinals.SOUTHEAST) != null) {
          move.get(Cardinals.SOUTHEAST).set(Cardinals.WEST, to_add);
        }

        Coord south = new Coord(x+1, y);
        Coord south_west = new Coord(x+1, y-1);
        Coord south_east = new Coord(x+1, y+1);
        if(map.containsKey(south)){
          map.get(south).set(Cardinals.NORTH, to_add);
          to_add.set(Cardinals.SOUTH, map.get(south));
        }
        if(map.containsKey(south_west)){
          map.get(south_west).set(Cardinals.NORTHEAST, to_add);
          to_add.set(Cardinals.SOUTHWEST, map.get(south_west));
        }
        if(map.containsKey(south_east)){
          map.get(south_east).set(Cardinals.NORTHWEST, to_add);
          to_add.set(Cardinals.SOUTHEAST, map.get(south_east));
        }
        map.put(new Coord(x, y), to_add);
        elves.add(to_add);
      }
    }
  }

  /**
   * In both parts, the input is parsed as a graph containing only the minimum 
   * number of nodes necessary to represent the elves.
   * In both part, this graph is updated when elves move to node that previously 
   * were not part of the graph. Information about neighbors are update as well.
   *
   * In part 1, once 10 turns have been reached, the maximum and minimum X and Y 
   * coordinates are retrieved and used to calculate the number of empty tiles. 
   *
   * In part 2, the procedure continues until a flag checks the fact that no 
   * proposal has been finished, thus 0 elves has moved.
   **/
  static int sol(String filename, boolean limited) {
    Node node_prev_line = null;
    HashMap<Coord, Node> map = new HashMap<>();
    HashSet<Node> elves = new HashSet<>();
    try {
      InputStream iStream = plant.class. 
        getClassLoader(). 
        getResourceAsStream(filename);
      BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
      String line;
      int x = 0;
      while((line = reader.readLine()) != null) {
        int y = 0;
        Node node_act_line = null;
        Node prev = null;
        for(char c:line.toCharArray()) {
          boolean elf = c == '#';
          Node node = new Node(elf, x, y);
          map.put(new Coord(x, y), node);
          if(elf){
            elves.add(node);
          }
          if(node_act_line == null) {
            node_act_line = node;
          } 
          if(prev != null) {
            node.set(Cardinals.WEST, prev);
            prev.set(Cardinals.EAST, node);
          }
          if(node_prev_line != null) {
            node.set(Cardinals.NORTH, node_prev_line);
            node_prev_line.set(Cardinals.SOUTH, node);
            if(node_prev_line.get(Cardinals.WEST) != null) {
              node_prev_line.get(Cardinals.WEST).set(Cardinals.SOUTHEAST, node);
              node.set(Cardinals.NORTHWEST, node_prev_line.get(Cardinals.WEST));
            }
            if(node_prev_line.get(Cardinals.EAST) != null) {
              node_prev_line.get(Cardinals.EAST).set(Cardinals.SOUTHWEST, node);
              node.set(Cardinals.NORTHEAST, node_prev_line.get(Cardinals.EAST));
              node_prev_line = node_prev_line.get(Cardinals.EAST);
            }
          }
          prev = node;
          y += 1;
        }
        x += 1;
        node_prev_line = node_act_line;
      }
      reader.close();
    } catch (FileNotFoundException e) {
      System.out.println("File not found");
      e.printStackTrace();
    } catch (IOException e) {
      System.out.println("End of file");
    }

    Cardinals[] moves = {Cardinals.NORTH, Cardinals.SOUTH, Cardinals.WEST, 
      Cardinals.EAST};
    int move_idx = 0;
    int turn = 0;
    while(true) {
      HashMap<Coord, HashSet<Node>> proposals = new HashMap<>();
      for(Node elf:elves){
        if(elf.no_one_near()){
          continue;
        }
        for(int i = 0; i<4; i++){
          int idx = (move_idx + i) % moves.length;
          if(elf.check_proposal(moves[idx])) {
            int ax = 0;
            int ay = 0;
            switch(moves[idx]){
            case NORTH:
              ax = -1;
            break;
            case SOUTH:
              ax = 1;
            break;
            case EAST:
              ay = 1;
            break;
            case WEST:
              ay = -1;
            break; 
            default:
              ax = 0;
              ay = 0;
            }
            Coord dest = new Coord(elf.x+ax, elf.y+ay);
            if(!proposals.containsKey(dest)){
              proposals.put(dest, new HashSet<>());
            }
            proposals.get(dest).add(elf);
            break;
          }
        }
      }
      move_idx = (move_idx + 1) % 4;

      boolean at_least_one = false;
      for(Entry<Coord, HashSet<Node>> entry:proposals.entrySet()) {
        Coord dest = entry.getKey();
        HashSet<Node> moved = entry.getValue();
        if(moved.size() == 1){
          at_least_one = true;
          int x = dest.x;
          int y = dest.y;
          Node mov = moved.iterator().next();
          elves.remove(mov);
          mov.elf = false;
          update(mov, elves, map, x, y);
        }
      }
      turn += 1;
      if(limited && turn == 10){
        break;
      }
      if(!at_least_one){
        return turn;
      }
    }
    int min_x = Integer.MAX_VALUE;
    int max_x = Integer.MIN_VALUE;
    int min_y = Integer.MAX_VALUE;
    int max_y = Integer.MIN_VALUE;
    for(Node elf:elves){
      min_x = Math.min(min_x, elf.x);
      min_y = Math.min(min_y, elf.y);
      max_x = Math.max(max_x, elf.x);
      max_y = Math.max(max_y, elf.y);
    }
    return (1+Math.abs(max_x-min_x))*(1+Math.abs(max_y-min_y)) - elves.size();
  }

  public static void execute(){
    String example = "inputs/d23/example.txt";
    System.out.println("(example) Solution 1:" + sol(example, true));
    System.out.println("(example) Solution 2:" + sol(example, false));

    String input = "inputs/d23/input.txt";
    System.out.println("Solution 1:" + sol(input, true));
    System.out.println("Solution 2:" + sol(input, false));
  }

}
