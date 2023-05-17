package com.advent2022.d13;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Stack;
import java.util.stream.Collectors;


/**
 * Support class to store Elements that can be 
 * both a List or a single Integer
 **/
class Element implements Comparable<Element>{
  LinkedList<Element> list;
  Integer single;
  boolean isSingle;

  /**
   * Constructor for an Element representing a List
   **/
  Element(LinkedList<Element> list) {
    this.list = list;
    isSingle = false;
  }

  /**
   * Constructor for an Element representing a single Integer
   **/
  Element(Integer single) {
    this.single = single;
    isSingle = true;
  }

  /**
   * toString method for debugging
   **/
  public String toString() {
    if(this.isSingle) {
      return this.single.toString();
    }
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    sb.append(String.join("," ,this.list.stream().map(x -> x.toString()).collect(Collectors.toList())));
    sb.append("]");
    return sb.toString();
  }

  /**
   * equals to check for equality between two Elements
   * Elements representing a single Integer uses Integer.equals 
   * List perform Element.equal on all the elements of the list
   **/
  public boolean equals(Object obj){
    if(!(obj instanceof Element)) {
      return false;
    }
    Element other = (Element)obj;
    if ((this.isSingle && !other.isSingle) || (!this.isSingle && other.isSingle)) {
      return false;
    }else if(this.isSingle && other.isSingle){
      return this.single.equals(other.single);
    } else {
      if(this.list.size() != other.list.size()) {
        return false;
      }
      for(int i = 0; i<this.list.size(); i++) {
        if(!this.list.get(i).equals(other.list.get(i))) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Check if a list is lesserThan another List
   **/
  public boolean listLesser(Element other) {
    if (this.list.size() != other.list.size()) {
      return true;
    } else {
      for(int i =0; i < this.list.size(); i++){
        Element te = this.list.get(i);
        Element oe = other.list.get(i);
        if(!te.equals(oe)){
          return true;
        }
      }
      return false;
    }
  }
  
  /**
   * compareTo method used to solve part2 quickly and part1 controlling that 
   * the result is -1. 
   * If two Element are a single Integer, Integer.compareTo is used 
   * If two Elements are a list, each element is checked with Element.compareTo 
   * If all the Elements are less or equal, it is checked what of the two cases 
   * it is by controlling the size and deep equality of the two List 
   * If only one of the two is a single Integer, it is inserted in a List 
   * with a single Element and checked with Element.compareTo against the other
   **/
  public int compareTo(Element other) {
    
    int i = 0;
    // Iterate over the size of the smaller list
    while(i < Math.min(this.list.size(), other.list.size())) {
      Element e1 = this.list.get(i);
      Element e2 = other.list.get(i);
      if (e1.isSingle && e2.isSingle) { // Two Integer
        if (!e1.single.equals(e2.single)) {
          return e1.single.compareTo(e2.single);
        }
      } else if(!e1.isSingle && !e2.isSingle) { // Two lists
        int cmp = e1.compareTo(e2);
        if (cmp >0) { // Greater than the other => 1
          return cmp;
        } else { // Lesser or Equal
          if (e1.list.size() != e2.list.size()) { // Lesser 
            return -1; // left run out of elements
          } else {
            if (e1.listLesser(e2)) { // Lesser
              return -1;
            }
          }
        }
      } else { // Conver single to List and Element.compareTo
        if (e1.isSingle) {
          LinkedList<Element> l = new LinkedList<>();
          l.add(e1);
          Element new_element = new Element(l);
          int cmp = new_element.compareTo(e2);
          if (cmp!=-1) {
            return cmp;
          } else {
            if (new_element.listLesser(e2)) {
              return -1;
            }
          }
        } else {
          LinkedList<Element> l = new LinkedList<>();
          l.add(e2);
          Element new_element = new Element(l);
          int cmp = e1.compareTo(new_element);
          if (cmp != -1) {
            return cmp;
          } else {
            if(e1.listLesser(new_element)) {
              return -1;
            }
          }
        }
      }
      i+=1;
    }
    // Check if this is the smaller list or the other
    int cmp = this.list.size() - other.list.size(); 
    cmp = cmp <= -1 ? -1 : cmp;
    return cmp;
  }
}

public class comp {
  static Element convert(String s) {
    Stack<Element> stack = new Stack<>();
    Element outer = new Element(new LinkedList<>());
    stack.add(outer);
    int i = 0;
    while(i < s.length() - 1){
      if (s.charAt(i) == ',') {
        i += 1;
        continue;
      }

      if (s.charAt(i) != '[' && s.charAt(i) != ']') {
        StringBuilder to_add_str = new StringBuilder();
        while(Character.isDigit(s.charAt(i))) {
          to_add_str.append(s.charAt(i));
          i += 1;
        }
        if(!stack.peek().isSingle) {
          stack.peek().list.add(new Element(Integer.parseInt(to_add_str.toString())));
        }
        continue;
      } else if (s.charAt(i) == '[') {
        stack.add(new Element(new LinkedList<>()));
      } else {
        Element to_add = stack.pop();
        if(!stack.peek().isSingle) {
          stack.peek().list.add(to_add);
        }
      }
      i+=1;
    }
    return stack.peek();
  }

  static int sol(String filename, boolean insert) {
    int idx = 1;
    int res = 0;
    ArrayList<Element> ordered = new ArrayList<>();

    try{

      InputStream iStream = comp.class
        .getClassLoader()
        .getResourceAsStream(filename);
      BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
      String line;
      while((line = reader.readLine()) != null) {
        if(line.length() == 0) {
          idx += 1;
          continue;
        }

        Element l1 = convert(line);
        line = reader.readLine();
        Element l2 = convert(line);
        if(insert) {
          ordered.add(l1);
          ordered.add(l2);
        } else {
          if(l1.compareTo(l2) == -1) {
            res += idx;
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
    Element two = convert("[[2]]");
    Element six = convert("[[6]]");
    if(insert){
      ordered.add(two);
      ordered.add(six);
      Collections.sort(ordered);
     
      int i1 = -1;
      for(int i = 0; i<ordered.size(); i++){
        if (i1 == -1 && ordered.get(i).equals(two)) {
          i1 = i + 1;
        } else if (i1 != -1 && ordered.get(i).equals(six)) {
          return i1 * (i+1);
        }
      }
    }
    return res;
  }
  
  public static void execute(){
    String example = "inputs/d13/example.txt";
    System.out.println("(example) Solution 1:" + sol(example, false));
    System.out.println("(example) Solution 2:" + sol(example, true));
    
    String input = "inputs/d13/input.txt";
    System.out.println("Solution 1:" + sol(input, false));
    System.out.println("Solution 2:" + sol(input, true));

  }


}
