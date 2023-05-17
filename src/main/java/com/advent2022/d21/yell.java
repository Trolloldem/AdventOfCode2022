package com.advent2022.d21;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Support enum to support Operations over Operands
 **/
enum Sign {
  MULT("*"),
  PLUS("+"),
  MINUS("-"),
  MATCH("match"),
  DIV("/"); 

  // used for toString and to recognise during parsing
  String val;

  Sign(String val){
    this.val = val;
  }

  // Actual operation
  long operation(HashMap<String, Monkey> monkeys, String m1, String m2){
    switch(this){
    case MULT:
      return monkeys.get(m1).val * monkeys.get(m2).val;
    case PLUS:
      return monkeys.get(m1).val + monkeys.get(m2).val;
    case MINUS:
      return monkeys.get(m1).val - monkeys.get(m2).val;
    case DIV:
      return monkeys.get(m1).val / monkeys.get(m2).val;
    default: // Never used for Sign.MATCH and null
      return 0;
    }
  }

  // toString method
  public String toString(){
    return this.val;
  }
}

/**
 * Support class to represent an Operand, which can be a String(to recover) 
 * Val from map / A Monkey containing other Operands
 **/
class Operand {
  String name = null;
  Monkey monkey = null;
  
  boolean is_name = true;
  
  Operand(String name){
    this.name = name;
  }

  Operand(Monkey monkey) {
    this.monkey = monkey;
    this.is_name = false;
  }
  
  public String toString(){
    if(is_name) {
      return name;
    }
    return monkey.toString();
  }
}

/**
 * Support class that may contain a single Long or a Monkey with 2 Operands and 
 * an Operation
 **/
class Monkey {
  Operand first_op = null;
  Sign op = null;
  Operand second_op = null;
  Long val = null;

  boolean is_val = false;

  Monkey(String first_op, Sign op, String second_op){
    this.first_op = new Operand(first_op);
    this.op = op;
    this.second_op = new Operand(second_op);
  }

  Monkey(Monkey first_op, Sign op, Monkey second_op){
    this.first_op = new Operand(first_op);
    this.op = op;
    this.second_op = new Operand(second_op);
  }

  Monkey(Long val) {
    this.val = val;
    is_val = true;
  }

  /**
   * Check if one of the two Operands is a String and if that String equals to 
   * the name in input
   **/
  boolean has_op(String name){
    if(this.first_op.is_name && this.first_op.name.equals(name)){
      return true;
    }
    
    if(this.second_op.is_name && this.second_op.name.equals(name)){
      return true;
    }
    return false;
  }

  /**
   * Show the human readable operation equivalent of this Monkey
   **/
  public String toString() {
    if(this.is_val){
      return "(" + val + ")";
    }
    return "(" + first_op.toString() + " " + op + " " + second_op + ")";
  }
}

public class yell {

  /**
   * Search for the actual value of the Monkey with name equal to key. The match 
   * parameter indicates if the search must be stopped when the 'humn' key is 
   * reached. 
   *
   * The search operates as follows:
   * 1) If the searched monkey is already a Long value, return it 
   * 2) Search for the two Monkeys appearing as Operands(they cannot be Strings)
   *    execpt for 'humn' when match is true 
   * 3) After both Operands are solved, if both resolved to a Val, apply the 
   * operation corresponding to Sign using their values 
   * 4) If one of the two resolves to a Monkey(e.g. contains "humn" as String), 
   * resolve the current key as a new Monkey with two Operators 
   **/
  static Monkey search(HashMap<String, Monkey> monkeys, String key, boolean match){
    if(monkeys.get(key).is_val) {
      return monkeys.get(key);
    }

    if(match && monkeys.get(key).has_op("humn")){
      return monkeys.get(key);    
    }
    Monkey m1 = search(monkeys, monkeys.get(key).first_op.name, match);
    Monkey m2 = search(monkeys, monkeys.get(key).second_op.name, match);

    if(!m1.is_val || !m2.is_val) {
      Monkey res = new Monkey(m1, monkeys.get(key).op, m2);
      monkeys.put(key, res);
    } else {
      monkeys.put(key, new Monkey(monkeys.get(key).op.operation(monkeys, 
              monkeys.get(key).first_op.name, 
              monkeys.get(key).second_op.name)));
    }
    return monkeys.get(key);
  }

  /**
   * Given a Monkey that is a single Long as x, and a Monkey with several 
   * operands as to_solve, reach the 'humn' variable while reversing the 
   * Operation encountered from the outermost Monkey to the innermost.
   **/
  static Monkey solve(Monkey x, Monkey to_solve, HashMap<String, Monkey> monkeys){
    
    // You should check also the other, but we know the humn appears as first
    if(to_solve.has_op("humn")){
      if(to_solve.first_op.name.equals("humn") && to_solve.second_op.is_name){
        Monkey m2 = search(monkeys, to_solve.second_op.name, true);
        x.val = x.val + m2.val; // Should also check the operation, but 
                                // from toString is it possible to see that 
                                // the operation is a Sing.MINUS
        return x;
      }
    }
    
    // First operand can be resolved
    if(to_solve.first_op.monkey.is_val) {
      // If it is a Sign.MINUS || Sign.PLUS, both can be solved by substracting 
      // the first operand value and by changing the sign of x if Sing.MINUS
      if(to_solve.op.equals(Sign.PLUS) || to_solve.op.equals(Sign.MINUS)){
        x.val -= to_solve.first_op.monkey.val;
        if(to_solve.op.equals(Sign.MINUS)) {
          x.val = -x.val;
        }
      } else { // We should also check for Sign.DIV, but it is never the case
        x.val = x.val / to_solve.first_op.monkey.val;
      }
      // Continue the resolution of x
      return solve(x, to_solve.second_op.monkey, monkeys);
    } else { // Second operand
      switch(to_solve.op){ // Reverse each operation 
      case MULT:
        x.val = x.val / to_solve.second_op.monkey.val;
      break;
      case PLUS:
        x.val = x.val - to_solve.second_op.monkey.val;
      break;
      case MINUS:
        x.val = x.val + to_solve.second_op.monkey.val;
      break;
      case DIV:
        x.val = x.val * to_solve.second_op.monkey.val;
      break;
      default:
      break;
      }
      // Continue the resolution of x
      return solve(x, to_solve.first_op.monkey, monkeys);
    }
  }

  /**
   * When match is false, it is solved the value of each Name in input. 
   * When match is true, the operation for root is Sign.MATCH and 'humn'.
   * This allows the creation of a Monkey with the form 
   * (expression, Sign.MATCH, val_to_match)
   **/
  static long sol(String filename, boolean match) {

    HashMap<String, Monkey> monkeys = new HashMap<>(); 
    try{
        InputStream iStream = yell.class
          .getClassLoader()
          .getResourceAsStream(filename);
        BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
        String line;

        while((line = reader.readLine()) != null) {
          String[] tokens = line.split(":");
          boolean found = false;
          for(Sign sign:Sign.values()) {
            if(sign.equals(Sign.MATCH)){
              continue;
            }
            if(tokens[1].contains(sign.val)){
              String[] others = tokens[1].split("\\"+sign.val);
              Sign act_sign = match && tokens[0].strip().equals("root") ? 
                Sign.MATCH : sign;
              monkeys.put(tokens[0], new Monkey(others[0].strip(), act_sign, 
                    others[1].strip()));
              found = true;
              break;
            }
          }
          if(!found) {
            monkeys.put(tokens[0], new Monkey(Long.parseLong(tokens[1].strip())));
          }
          if(match && tokens[0].equals("humn")) {
            monkeys.put(tokens[0], new Monkey(tokens[0], null, null));
          }
        }
        reader.close();
      } catch (FileNotFoundException e) {
        System.out.println("File not found");
        e.printStackTrace();
      } catch (IOException e) {
        System.out.println("End of file");
    }
    if(!match){
      return search(monkeys, "root", match).val;
    }
    Monkey res = search(monkeys, "root", match);
     return solve(res.second_op.monkey, res.first_op.monkey, monkeys).val;
  }
  
  public static void execute(){
    String example = "inputs/d21/example.txt";
    System.out.println("(example) Solution 1:" + sol(example, false));
    System.out.println("(example) Solution 2:" + sol(example, true));
   
    String input = "inputs/d21/input.txt";
    System.out.println("Solution 1:" + sol(input, false));
    System.out.println("Solution 2:" + sol(input, true));
  }
}
