package com.advent2022.d6;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class code {
 
  /**
   * Sliding window approach with the requested size 
   * with an HashMap to store counters of the window
   **/
  static int sol(String filename, int size) {
    try{
      HashMap<Character, Integer> ct = new HashMap<>();
      InputStream iStream = code.class. 
        getClassLoader(). 
        getResourceAsStream(filename);
      BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
      String line;
      while((line = reader.readLine()) != null) {
        for(int i = 0; i<size; i++){
          Character c = line.charAt(i);
          if (!ct.containsKey(c)) {
            ct.put(c, 0);
          }
          ct.put(c, ct.get(c) + 1);
        }
        int start = 0;
        int end = size;
        while (end < line.length()) {
          if (ct.keySet().size() == size) {
            reader.close();
            return end;
          }
          Character first = line.charAt(start);
          ct.put(first, ct.get(first) - 1);
          if (ct.get(first) == 0) {
            ct.remove(first);
          }
          Character new_char = line.charAt(end);
          if (!ct.containsKey(new_char)) {
            ct.put(new_char, 0);
          }
          ct.put(new_char, ct.get(new_char)+1);
          start += 1;
          end += 1;
        }
      }
      iStream.close();
    } catch (FileNotFoundException e) {
      System.out.println("File not found");
      e.printStackTrace();
    } catch (IOException e) {
      System.out.println("End of file");
    }
    return -1;
  }

  public static void execute(){
    String example = "inputs/d6/example.txt";
    System.out.println("(example) Solution 1:" + sol(example, 4));
    System.out.println("(example) Solution 2:" + sol(example, 14));

    String input = "inputs/d6/input.txt";
    System.out.println("Solution 1:" + sol(input, 4));
    System.out.println("Solution 2:" + sol(input, 14));

  }
}
