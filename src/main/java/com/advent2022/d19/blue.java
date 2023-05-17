package com.advent2022.d19;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;


/**
 * Support class to store the blueprints for all 
 * the robots as HashMaps
 **/
class Blueprints {
  HashMap<String, Integer> ore;
  HashMap<String, Integer> clay;
  HashMap<String, Integer> obsidian;
  HashMap<String, Integer> geode;


  void toString_helper(StringBuilder sb, HashMap<String, Integer> plan){
    if(plan.size() == 0){
      return;
    }
     for(Entry<String, Integer> entry:plan.entrySet()){
      sb.append(entry.getKey());
      sb.append(": ");
      sb.append(entry.getValue());
      sb.append("\n");
    }

  }

  /**
   * To debug parsing
   **/
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("To create ore:\n");
    toString_helper(sb, this.ore);
    sb.append("To create clay:\n");
    toString_helper(sb, this.clay);
    sb.append("To create obsidian:\n");
    toString_helper(sb, this.obsidian);
    sb.append("To create geode:\n");
    toString_helper(sb, this.geode);

    return sb.toString();
  }
}

public class blue{

  /**
   * Global max to prune search procedure
   **/
  static int c_max = 0;

  /**
   * Count occurrences of substring in String
   **/
  static int count_sub(String token, String sub){
    int index = 0;
    int count = 0;
    while(index != -1 && index < token.length()){
      token = token.substring(index);
      int tmp = token.indexOf(sub);
      if(tmp!=-1){
        count += 1;
      } else {
        break;
      }
      index = tmp + sub.length() + 1;
    }
    return count;
  }

  /**
   * Parse the input into a list of Blueprints
   **/
  static LinkedList<Blueprints> parse(BufferedReader reader) {
    String line;
    LinkedList<Blueprints> blueprints = new LinkedList<>();
    blueprints.add(new Blueprints());
    try{
      while((line = reader.readLine()) != null) {
        String[] tokens = line.split("\\.");
        for(String token:tokens){
          if(count_sub(token, "ore") == 2) {
            String search = token.split("costs ")[1];
            StringBuilder sb = new StringBuilder();
            int i = 0;
            while(search.charAt(i) != ' '){
              sb.append(search.charAt(i));
              i += 1;
            }
            int n = Integer.parseInt(sb.toString());
            blueprints.getLast().ore = new HashMap<>();
            blueprints.getLast().ore.put("ore", n);
          } else if(count_sub(token, "ore") == 1 && count_sub(token, "clay") == 1 && count_sub(token, "obsidian") == 0) {
            String search = token.split("costs ")[1];
            StringBuilder sb = new StringBuilder();
            int i = 0;
            while(search.charAt(i) != ' '){
              sb.append(search.charAt(i));
              i += 1;
            }
            int n = Integer.parseInt(sb.toString());
            blueprints.getLast().clay = new HashMap<>();
            blueprints.getLast().clay.put("ore", n);

          } else if(count_sub(token, "obsidian") == 1 && count_sub(token, "geode") == 0){
            String search = token.split("costs ")[1];
            StringBuilder sb = new StringBuilder();
            int i = 0;
            while(search.charAt(i) != ' '){
              sb.append(search.charAt(i));
              i += 1;
            }
            int n = Integer.parseInt(sb.toString());
            blueprints.getLast().obsidian = new HashMap<>();
            blueprints.getLast().obsidian.put("ore", n); 

            search = token.split("and ")[1];
            sb = new StringBuilder();
            i = 0;
            while(search.charAt(i) != ' '){
              sb.append(search.charAt(i));
              i += 1;
            }
            n = Integer.parseInt(sb.toString());
            blueprints.getLast().obsidian.put("clay", n); 
          } else if(count_sub(token, "geode") == 1){
            String search = token.split("costs ")[1];
            StringBuilder sb = new StringBuilder();
            int i = 0;
            while(search.charAt(i) != ' '){
              sb.append(search.charAt(i));
              i += 1;
            }
            int n = Integer.parseInt(sb.toString());
            blueprints.getLast().geode = new HashMap<>();
            blueprints.getLast().geode.put("ore", n); 

            search = token.split("and ")[1];
            sb = new StringBuilder();
            i = 0;
            while(search.charAt(i) != ' '){
              sb.append(search.charAt(i));
              i += 1;
            }
            n = Integer.parseInt(sb.toString());
            blueprints.getLast().geode.put("obsidian", n);
            blueprints.add(new Blueprints());
          } 
        }
      }
      reader.close();
    } catch (FileNotFoundException e) {
      System.out.println("File not found");
      e.printStackTrace();
    } catch (IOException e){
      System.out.println("End of file");
    }
    blueprints.removeLast();
    return blueprints;
  }

  /**
   * Use DP to get the maximum number of Geodes. At each steps, try to build 
   * every possible robot and store the maximum. Use the number of robot + 
   * resources + time as a key for recursion. Since the key is very large, prune 
   * when it is not possible even in the best possible scenario (1 geode robot per 
   * turn) to reach the current best score. 
   **/
  static int get_max(int time, int ro, int rc, int rob, int rg, int o, int c, int ob, int g, Blueprints blueprint, HashMap<String, Integer> dp){
    if(time == 0) {
      return g;
    }

    String key = time + "|" + ro + "|" + rc + "|" + rob + "|" + rg + "|" + o + "|" + c + "|" + ob + "|" + g;
    int acc = 0;
    for(int i = 1; i<time;i++){
      acc += i;
    }
    if((g + rg*time + acc <= c_max)) { // Pruning
      return 0;
    }

    int res = 0;
    if(dp.containsKey(key)){
      return dp.get(key);
    }

    // Try to build a robot when possible
    if(o >= blueprint.geode.get("ore") && ob >= blueprint.geode.get("obsidian")){
      res = Math.max(res, get_max(time-1, ro, rc, rob, rg+1, o-blueprint.geode.get("ore")+ro, c+rc, ob-blueprint.geode.get("obsidian")+rob, g+rg, blueprint, dp));
    }
    if(o >= blueprint.obsidian.get("ore") && c >= blueprint.obsidian.get("clay")){
      res = Math.max(res, get_max(time-1, ro, rc, rob+1, rg, o-blueprint.obsidian.get("ore")+ro, c-blueprint.obsidian.get("clay")+rc, ob+rob, g+rg, blueprint, dp));
    }
    if(o >= blueprint.clay.get("ore")){
      res = Math.max(res, get_max(time-1, ro, rc+1, rob, rg, o-blueprint.clay.get("ore")+ro, c+rc, ob+rob, g+rg, blueprint, dp));
    }
    if(o >= blueprint.ore.get("ore")){
      res = Math.max(res, get_max(time-1, ro+1, rc, rob, rg, o-blueprint.ore.get("ore")+ro, c+rc, ob+rob, g+rg, blueprint, dp));
    }

    res = Math.max(res, get_max(time-1, ro, rc, rob, rg, o+ro, c+rc, ob+rob, g+rg, blueprint, dp));
    dp.put(key, res);
    c_max = Math.max(c_max, res);
    return res;
  }
  static int sol(String filename, boolean larger) {
    InputStream iStream = blue.class. 
      getClassLoader(). 
      getResourceAsStream(filename);
    BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
    LinkedList<Blueprints> blueprints = parse(reader);
    int res = larger ? 1 : 0;
    int time = larger ? 32 : 24;
    int to_use = larger ? Math.min(3, blueprints.size()) : blueprints.size();
    for(int i =0; i<to_use; i++){
      Blueprints b = blueprints.get(i);
      c_max = 0;
      int tmp = get_max(time, 1, 0, 0, 0, 0, 0, 0, 0, b, new HashMap<>());
      if(larger){
        res *= tmp;
      }
      else{
        res += (i+1) * tmp;
      }
    }
    return res;
  }

  public static void execute(){
    String example = "inputs/d19/example.txt";
    System.out.println("(example 1) Solution 1:" + sol(example, false));
    System.out.println("(example 1) Solution 2:" + sol(example, true));
    
    String input = "inputs/d19/input.txt";
    System.out.println("Solution 1:" + sol(input, false));
    System.out.println("Solution 2:" + sol(input, true));
  }
}
