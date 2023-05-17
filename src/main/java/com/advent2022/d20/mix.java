package com.advent2022.d20;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;

/**
 * Support class to create Circular buffer
 **/
class Node{
  long val;
  Node next = null; 
  Node prev = null;

  Node(long val){
    this.val = val;
  }
}

public class mix{

  /**
   * Before mixing, calculate the number of positions 
   * as val % (to_process.size()-1). Then substitute 
   * in the correct position the node. To maintain the 
   * process order, the Nodes are saved in a list in the 
   * order in which they are given in input. The substitution
   * is done in a circular buffer.
   * Part 1 and Part 2 are choosen through the `big` flag. 
   **/
  static long sol(String filename, boolean big) {
    Node head = null;
    Node tail = null;
    LinkedList<Node> to_process = new LinkedList<>();

    long mul = big? 811589153 : 1;
    try{
        InputStream iStream = mix.class
          .getClassLoader()
          .getResourceAsStream(filename);
        BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
        String line;

        while((line = reader.readLine()) != null) {
          long val = Long.parseLong(line) * mul;
          Node node = new Node(val);
          to_process.add(node);
          if(tail != null) {
            tail.next = node;
            node.prev = tail;
            tail = node;
          } 
          if(head == null) {
            head = node;
            tail = node;
          }
        }
        reader.close();
      } catch (FileNotFoundException e) {
        System.out.println("File not found");
        e.printStackTrace();
      } catch (IOException e) {
        System.out.println("End of file");
    }
    tail.next = head;
    head.prev = tail;

    int turns = big ? 10 : 1;
    for(int turn = 0; turn < turns; turn++){
      for(Node n:to_process){
        Node act = n;
        Node to_substitute = act;
        if(act.val == 0){
          continue;
        }
        for(int i = 0; i<Math.abs(act.val) % (to_process.size()-1); i++) {
          if(act.val > 0) {
            to_substitute = to_substitute.next;
          } else {
            to_substitute = to_substitute.prev;
          }
        }
        if(to_substitute != n){
          Node prev = act.prev;
          Node next = act.next;
          prev.next = next;
          next.prev = prev;
          if(act.val > 0) {
            act.next = to_substitute.next;
            to_substitute.next = act;
            act.prev = to_substitute;
            act.next.prev = act;
          } else {
            act.prev = to_substitute.prev;
            to_substitute.prev = act;
            act.next = to_substitute;
            act.prev.next = act;
          }
        }
      }
    }

    Node act = head;
    boolean first = true;
    while(act != head || first) {
      first = false;
      if(act.val == 0){
        break;
      }
      act = act.next;
    }

    long res = 0;
    for(int i = 1; i<3001; i++){
      act = act.next;
      if(i == 1000 || i == 2000 || i == 3000){
        res += act.val;
      }
    }
    return res;
  }
  
  public static void execute(){
    String example = "inputs/d20/example.txt";
    System.out.println("(example) Solution 1:" + sol(example, false));
    System.out.println("(example) Solution 2:" + sol(example, true));
   
    String input = "inputs/d20/input.txt";
    System.out.println("Solution 1:" + sol(input, false));
    System.out.println("Solution 2:" + sol(input, true));
  }
}
