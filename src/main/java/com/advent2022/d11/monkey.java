package com.advent2022.d11;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Interface to simulate a lambda function 
 **/
interface Operation {
  BigInteger op(BigInteger a, BigInteger b);
}

public class monkey implements Comparable<monkey>{

 LinkedList<BigInteger> items; // Current items inspected by the monkey
 Operation operation; // Mathematical operation on the inspected item
 BigInteger div; // Modulo to check
 Integer TTrue; // Index to throw when divisible by div
 Integer TFalse; // Index to throw when not divisible by div
 BigInteger attention; // Number of times a monkey has inspected an item
 /**
  * Support method to instantiate the interface simulating the lambda function
  **/
 static Operation create_operation(String[] tokens) {
    if(tokens[1].equals("*")) {
      if (tokens[0].equals(tokens[2]) && tokens[0].equals("old")){
        return (a, b) -> a.multiply(b);
      } else {
        return (a, b) -> a.multiply(BigInteger.valueOf(Long.parseUnsignedLong(tokens[2])));
      }
    } else {
      if (tokens[0].equals(tokens[2]) && tokens[0].equals("old")){
        return (a, b) -> a.add(b);
      } else {
        return (a, b) -> a.add(BigInteger.valueOf(Long.parseUnsignedLong(tokens[2])));
      }
    }
 }

 /**
  * An ArrayList of monkey objects is used to represent the monkeys
  * The number of turns is decided by the problem part solved (20, 10000)
  * Turns are done according to the rule of the specific solved part
  * For the second part, a modulo is calculated as the multiplication of 
  * all the monkey.div fields to avoid having slow BigIntegers. 
  **/
 static BigInteger sol(String filename, int turns) {
   BigInteger modulo = BigInteger.valueOf(1);
   ArrayList<monkey> inventory = new ArrayList<>();
    try{
      
      InputStream iStream = monkey.class
        .getClassLoader()
        .getResourceAsStream(filename);
      BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
      String line;
      boolean to_break = false;
      while(!to_break) {
        monkey act = new monkey();
        act.attention = BigInteger.valueOf(0);
        inventory.add(act);
        for(int i = 0; i<=6; i++){
          line = reader.readLine();
          if(line == null){
            to_break = true;
            break;
          }
          line = line.strip();
          String[] tokens = line.split(" ");
          switch(tokens[0]){
            case "Starting":
              LinkedList<BigInteger> items = new LinkedList<>();
              for(int idx = 2; idx < tokens.length; idx++){
                BigInteger item;
                if(tokens[idx].charAt(tokens[idx].length()-1) == ','){
                  item = BigInteger.valueOf(Long.parseUnsignedLong(tokens[idx].substring(0, tokens[idx].length()-1)));
                } else {
                  item = BigInteger.valueOf(Long.parseUnsignedLong(tokens[idx]));
                }
                items.add(item);
              }
              act.items = items;
              break;
            case "Operation:":
              String[] slice = new String[tokens.length - 3];
              for(int idx = 0; idx < slice.length; idx++){
                slice[idx] = tokens[idx+3];
              }
              act.operation = create_operation(slice);
              break;
            case "Test:":
              act.div = BigInteger.valueOf(Long.parseUnsignedLong(tokens[tokens.length-1]));
              modulo = modulo.multiply(act.div);
              break;
            case "If":
                if(tokens[1].equals("true:")){
                  act.TTrue = Integer.parseInt(tokens[tokens.length-1]);
                } else {
                  act.TFalse = Integer.parseInt(tokens[tokens.length-1]);
                }
              break;
          }
        }
      }
    } catch (FileNotFoundException e) {
      System.out.println("File not found");
      e.printStackTrace();
    } catch (IOException e) {}

    BigInteger zero = BigInteger.valueOf(0);
    BigInteger three = BigInteger.valueOf(3);
    for(int turn = 0; turn < turns; turn++){
      for(int idx = 0; idx < inventory.size(); idx++){
        monkey act = inventory.get(idx);
        while(act.items.size() > 0) {
          BigInteger item = act.items.pop();
          act.attention = act.attention.add(BigInteger.valueOf(1));
          BigInteger new_item = act.operation.op(item, item);
          if(turns == 20) {
            new_item = new_item.divide(three);
          }
          int throw_idx;
          if (new_item.mod(act.div).equals(zero)) {
            throw_idx = act.TTrue;
          } else {
            throw_idx = act.TFalse;
          }
          new_item = new_item.mod(modulo);
          inventory.get(throw_idx).items.add(new_item);
        }
      } 
    }
    Collections.sort(inventory);
    return inventory.get(inventory.size()-1).attention.multiply(inventory.get(inventory.size()-2).attention);
  }

  public static void execute(){
    String example = "inputs/d11/example.txt";
    System.out.println("(example 1) Solution 1:" + sol(example, 20));
    System.out.println("(example 1) Solution 2:" + sol(example, 10000));
    
    String input = "inputs/d11/input.txt";
    System.out.println("Solution 1:" + sol(input, 20));
    System.out.println("Solution 2:" + sol(input, 10000));
  }

  @Override
  public int compareTo(monkey other) {
    return this.attention.compareTo(other.attention);
  }

}
