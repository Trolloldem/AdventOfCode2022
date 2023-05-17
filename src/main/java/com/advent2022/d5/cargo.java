package com.advent2022.d5;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;

public class cargo {
  static int BLOCK_SIZE = 4; // Size of a crate + the space between crates
                            
  /**
   * Converts the input concerning crates from rows to columns
   **/
  static LinkedList<String> get_columns(BufferedReader reader){
    ArrayList<StringBuilder> cols = new ArrayList<>();
    LinkedList<String> res = new LinkedList<>();
    int size = 0;
    String line;
    try {
      while((line = reader.readLine()) != null) {
        if (line.contains("1")) {
          continue;
        }
        if (line.equals("")) {
          break;
        }
        if (size == 0) {
          size = (line.length() + 1) / BLOCK_SIZE;
          for(int i = 0; i<size;i++){
            cols.add(new StringBuilder());
          }
        }
        for(int i = 1, col = 0; i<line.length(); i+=BLOCK_SIZE, col++){
          if(line.charAt(i) == ' '){
            continue;
          }
          cols.get(col).append(line.charAt(i));
        }
      }
    } catch (IOException e) {
      System.out.println("End of file");
    }
    for(StringBuilder sb: cols){
      res.add(sb.toString());
    }
    return res;
  }

  /**
   * Uses stack to keep track of columns.
   * The reorder parameters check if the stack must be 
   * reversed or not.
   **/
  static String sol(String filename, boolean reorder) {
    StringBuilder res = new StringBuilder();
    try{
      InputStream iStream = cargo.class 
        .getClassLoader() 
        .getResourceAsStream(filename);
      BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
      ArrayList<LinkedList<Character>> stacks = new ArrayList<>();
      LinkedList<String> columns = get_columns(reader);
      
      for(String column:columns){
          LinkedList<Character> act = new LinkedList<>();
          for(int i = column.length()-1; i >= 0; i--){
            act.addLast(column.charAt(i));
          }
          stacks.add(act);
      }

      String line;
      while((line = reader.readLine()) != null) {
        String[] tokens = line.split(" ");
        int size = Integer.parseInt(tokens[1]); 
        LinkedList<Character> to_move = stacks.get(Integer.parseInt(tokens[3])-1);
        LinkedList<Character> to_reorder = stacks.get(Integer.parseInt(tokens[5])-1);
        LinkedList<Character> tmp = new LinkedList<>();
        for (int idx = 0; idx < size; idx++){
          if (to_move.size() > 0 && reorder) {
            to_reorder.addLast(to_move.removeLast());
          }
          else if (to_move.size() > 0) {
            tmp.addLast(to_move.removeLast());
            if (idx == size - 1) {
              while (tmp.size() > 0) {
                to_reorder.addLast(tmp.removeLast());
              }
            }
          }
        }
      }
      iStream.close();
      for(LinkedList<Character> stack:stacks){
        if (stack.size()>0){
          res.append(stack.getLast().toString());
        }
      }
    } catch (FileNotFoundException e) {
      System.out.println("File not found");
      e.printStackTrace();
    } catch (IOException e) {
      System.out.println("End of file");
    } 
    return res.toString();
  }

   public static void execute(){
    String example = "inputs/d5/example.txt";
    System.out.println("(example) Solution 1:" + sol(example, true));
    System.out.println("(example) Solution 2:" + sol(example, false));

    String input = "inputs/d5/input.txt";
    System.out.println("Solution 1:" + sol(input, true));
    System.out.println("Solution 2:" + sol(input, false));

  }

}
