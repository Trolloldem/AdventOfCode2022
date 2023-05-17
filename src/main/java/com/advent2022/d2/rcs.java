package com.advent2022.d2;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Custom class to implement a pair
 **/
class MyPair {
  HashMap<String, String> map;
  Integer val;
  MyPair(HashMap<String, String> map, Integer val) {
    this.map = map;
    this.val = val;
  }
}

public class rcs {
 
  /**
   * Define 3 HashMap: 1 is the moves to do to lose,
   * 1 the moves to win and the final one is for points.
   * The first 2 HashMap are stored in another one used 
   * to map symbol to course of action (winning, losing).
   **/
  static int sol_2(String filename) {
    HashMap<String, String> to_lose = new HashMap<>();
    to_lose.put("A", "C");
    to_lose.put("B", "A");
    to_lose.put("C", "B");
    HashMap<String, String> to_win = new HashMap<>(); 
    to_win.put("A", "B");
    to_win.put("B", "C");
    to_win.put("C", "A");
    HashMap<String, Integer> points = new HashMap<>();
    points.put("A", 1);
    points.put("B", 2);
    points.put("C", 3);
    HashMap<String, MyPair> to_do = new HashMap<>();
    to_do.put("X", new MyPair(to_lose, 0));
    to_do.put("Y", null); 
    to_do.put("Z", new MyPair(to_win, 6));

    int score = 0;
    try {
      InputStream iStream = rcs.class. 
        getClassLoader(). 
        getResourceAsStream(filename);
      BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
      String line;
      while(((line = reader.readLine()) != null)) {
        String[] tokens = line.split(" ");
        MyPair pair = to_do.get(tokens[1]);

        if (pair == null) {
          score += points.get(tokens[0]) + 3;
        } else {
          HashMap<String, String> action = pair.map;
          score += points.get(action.get(tokens[0])) + pair.val;
        }
      }
      iStream.close();
    } catch (FileNotFoundException e) {
      System.out.println("File not found");
      e.printStackTrace();
    } catch (IOException e) {
      System.out.println("End of file");
    }

    return score;
  }

  /**
   * Define 2 HashMap: 1 for winning cases,
   * 1 to store the points of each move used.
   * In addition use a HashSet to store draws.
   **/
  static int sol_1(String filename) {
    HashMap<String, Integer> wins = new HashMap<>();
    wins.put("A Y", 2);
    wins.put("B Z", 3);
    wins.put("C X", 1);
    HashSet<String> draws = new HashSet<>(); 
    draws.add("A X");
    draws.add("B Y");
    draws.add("C Z");
    HashMap<String, Integer> points = new HashMap<>();
    points.put("X", 1);
    points.put("Y", 2);
    points.put("Z", 3);

    int score = 0;
    try {
      InputStream iStream = rcs.class. 
        getClassLoader(). 
        getResourceAsStream(filename);
      BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
      String line;
      while(((line = reader.readLine()) != null)) {
        String[] tokens = line.split(" ");
        if (wins.containsKey(line)) {
          score += wins.get(line) + 6;
        } else if (draws.contains(line)) {
          score += points.get(tokens[1]) + 3;
        } else {
          score += points.get(tokens[1]);
        }
      }
      iStream.close();
    } catch (FileNotFoundException e) {
      System.out.println("File not found");
      e.printStackTrace();
    } catch (IOException e) {
      System.out.println("End of file");
    }

    return score;
  }

  public static void execute(){
    String example = "inputs/d2/example.txt";
    System.out.println("(example) Solution 1:" + sol_1(example));
    System.out.println("(example) Solution 2:" + sol_2(example));

    String input = "inputs/d2/input.txt";
    System.out.println("Solution 1:" + sol_1(input));
    System.out.println("Solution 2:" + sol_2(input));
  }
 
}
