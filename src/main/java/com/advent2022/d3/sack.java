package com.advent2022.d3;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class sack {
  /**
   * Support function to create the map between letters and scores
   **/
  static HashMap<Character, Integer> get_scores() {
    HashMap<Character, Integer> scores = new HashMap<>();
    int score = 1;
    for(int letter = 'a'; letter <= 'z'; letter++){
      Character c = (char) letter;
      scores.put(c, score);
      score+=1;
    }
    for(int letter = 'A'; letter <= 'Z'; letter++){
      Character c = (char) letter;
      scores.put(c, score);
      score+=1;
    }
    return scores;
  }

  /**
   * Create an hashmap with the score of each letter.
   * Scan the input line dividing into two parts the sack.
   * If the second part contains a character in the first,
   * break the cicle and add the score.
   **/
  static int sol_1(String filename) {
    HashMap<Character, Integer> scores = get_scores();
    int res = 0;
    try {
      InputStream iStream = sack.class. 
        getClassLoader(). 
        getResourceAsStream(filename);
      BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
      String line;
      while(((line = reader.readLine()) != null)) {
        Integer size = line.length() / 2;
        HashSet<Character> compartment1 = new HashSet<>();
        for(int i = 0; i < line.length(); i++){
          if (i < size) {
            compartment1.add(line.charAt(i));
          } else {
            Character check = line.charAt(i);
            if (compartment1.contains(check)){
              res += scores.get(check);
              break;
            }
          }
        }
      }
      iStream.close();
    } catch (FileNotFoundException e) {
      System.out.println("File not found");
      e.printStackTrace();
    } catch (IOException e) {
      System.out.println("End of file");
    }
    return res;
  }
 
  /**
   * Read lines in groups of 3 lines. Create 
   * 2 Set of Characters for the first 2 lines.
   * During the third scan check if a character 
   * is also in the other 2 Sets.
   **/
  static int sol_2(String filename) {
    HashMap<Character, Integer> scores = get_scores();
    int res = 0;
    try {
      InputStream iStream = sack.class. 
        getClassLoader(). 
        getResourceAsStream(filename);
      BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
      String line;
      while((line = reader.readLine()) != null) {
        ArrayList<HashSet<Character>> sets = new ArrayList<>(3);
        for(int i = 0; i < 3; i++){
          sets.add(new HashSet<>());
          for(Character c:line.toCharArray()){
            if (i == 2 && sets.get(0).contains(c) && sets.get(1).contains(c)){
               res += scores.get(c);
               break;
            } else {
              sets.get(i).add(c);
            }
          }
          if(i!=2){
            line = reader.readLine();
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
    return res;
  }

  public static void execute(){
    String example = "inputs/d3/example.txt";
    System.out.println("(example) Solution 1:" + sol_1(example));
    System.out.println("(example) Solution 2:" + sol_2(example));

    String input = "inputs/d3/input.txt";
    System.out.println("Solution 1:" + sol_1(input));
    System.out.println("Solution 2:" + sol_2(input));


  }

}
