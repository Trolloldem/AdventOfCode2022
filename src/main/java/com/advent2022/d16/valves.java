package com.advent2022.d16;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * Class to represent a Valve
 **/
class Valve {
  int rate;
  HashMap<String, Integer> dest;

  Valve(int rate, HashMap<String, Integer> dest){
    this.rate = rate;
    this.dest = dest;
  }
}

/**
 * Key of DP
 **/
class Key {
  String node;
  int turns;
  LinkedList<String> can_open;
 
  Key(String node, int turns, LinkedList<String> can_open) {
    this.node = node;
    this.turns = turns;
    this.can_open = can_open;
  }
  
  public boolean equals(Object obj) {
    if(!(obj instanceof Key)) {
      return false;
    }
    Key other = (Key)obj;
    return this.node.equals(other.node) && this.turns == other.turns && this.can_open.equals(other.can_open);
  }

  public int hashCode(){
    String to_hash = this.node + "|" + this.turns + "|" + this.can_open.stream().collect(Collectors.joining(",", "", ""));
    return to_hash.hashCode();
  }

}

/**
 * Comparator to sort Entries of DP from larger to smaller
 **/
class entry_cmp implements Comparator<Entry<String, Valve>>{

  @Override
  public int compare(Entry<String, Valve> e1, Entry<String, Valve> e2) {
    return -Integer.compare(e1.getValue().rate, e2.getValue().rate);
  }

}

public class valves {
 
  /**
   * Pruning: calculates the max flow than can be achieved in the 
   * best scenario in which the best valves are distant 1 step
   **/
  static int potential(HashSet<String> can_open, HashMap<String, Valve> valves, int turns){
    int res = 0;
    int spent = 1;
    for(Entry<String, Valve> e:valves.entrySet().stream().sorted(new entry_cmp()).collect(Collectors.toList())){
      if(can_open.contains(e.getKey())){
        res += e.getValue().rate*(turns-spent);
        spent += 2;
      }
    }
    return res;
  }

  /**
   * DP Approach: open when possible, explore the other always and store the best decision.
   * Keep current state to prune from one run to another
   **/
  static int search(String node, HashMap<String, Valve> valves, int turns,HashSet<String> can_open, HashMap<Key, Integer> dp, int c_best, int base, int curr){
    
    if(turns == 0 || can_open.size()==0) {
      return 0;
    }
    
    //prune
    if(base!=0 && base + curr + potential(can_open, valves, turns) <= c_best) {
      return 0;
    }

    Key key = new Key(node, turns, can_open.stream().sorted().collect(Collectors.toCollection(LinkedList::new)));
    int res = 0;
    if(dp.containsKey(key)) {
      return dp.get(key);
    }

    // Wait one turn  to open
    if(can_open.contains(node)) {
      can_open.remove(node);
      res = Math.max(res, (turns - 1)*valves.get(node).rate + search(node, valves, turns-1, can_open, dp, c_best, base, curr+(turns-1)*valves.get(node).rate));
      can_open.add(node);
    }

    for(Entry<String, Integer> entry:valves.get(node).dest.entrySet()){
      String visit = entry.getKey();
      Integer cost = entry.getValue();
      // Pay attention to negative costs and avoid cycle
      if(!can_open.contains(visit) || turns - cost < 0){
        continue;
      }
      res = Math.max(res, search(visit, valves, turns-cost, can_open, dp, c_best, base, curr));
    }
    if(res != 0) {
        dp.put(key, res);
    }
    return res;
  }

  /**
   * Get the distances between one origin and all useful valves + "AA"
   **/
  static HashMap<String, Integer> get_dists(String origin, HashSet<String> can_open, HashMap<String, Valve> valves) {
    HashSet<String> frontier = new HashSet<>();
    frontier.add(origin);
    int steps = 0;
    HashMap<String, Integer> res = new HashMap<>();
    HashSet<String> visited = new HashSet<>();
    while(frontier.size()>0) {
      HashSet<String> n_frontier = new HashSet<>();
      Iterator<String> it = frontier.iterator();
      while(it.hasNext()) {
        String act = it.next();
        if(!visited.contains(act) && !act.equals(origin) && (act.equals("AA") || can_open.contains(act))) {
          if(!res.containsKey(act)) {
            res.put(act, steps);
            if(res.size()==can_open.size()+1){
              return res;
            }
          }
        }
        visited.add(act);
        for(String to_visit:valves.get(act).dest.keySet()) {
          if(!visited.contains(to_visit)){
            n_frontier.add(to_visit);
          }
        }
      }
      steps+=1;
      frontier = n_frontier;
    }
    return res;
  }

  /**
   * Read the data and search through search().
   * The number of turns is dependant on the part solved.
   * The second part iterates over the various.
   **/
  static int sol(String filename, boolean elephant) {
    int turns = elephant ? 26 :30;
    HashMap<String, Valve> streams = new HashMap<>();
    HashSet<String> can_open = new HashSet<>();
    try{
        InputStream iStream = valves.class
          .getClassLoader()
          .getResourceAsStream(filename);
        BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
        String line;
        while((line = reader.readLine()) != null) {
          String[] tokens = line.split(";");
          int rate = Integer.parseInt(tokens[0].split("=")[1]);
          String source = tokens[0].split(" ")[1];

          HashMap<String, Integer> dest = new HashMap<>();
          if(tokens[1].contains("valves")) {
            String valves = tokens[1].split("valves ")[1];
            for(String valve:valves.split(",")) {
              dest.put(valve.strip(), 1);
            }
          } else {
            dest.put(tokens[1].split("valve ")[1], 1);
          }
          streams.put(source, new Valve(rate, dest));
          if(streams.get(source).rate != 0){
            can_open.add(source);
          }
        }
        reader.close();
      } catch (FileNotFoundException e) {
        System.out.println("File not found");
        e.printStackTrace();
      } catch (IOException e) {
        System.out.println("End of file");
    }

    // Explore the nodes to find the distance between what can 
    // be opened + "AA"
    HashMap<String, Valve> f_streams = new HashMap<>();
    HashMap<String, Integer> costs = get_dists("AA", can_open, streams);
    f_streams.put("AA", new Valve(streams.get("AA").rate, costs));
    for(String c:can_open){
      costs = get_dists(c, can_open, streams);
      f_streams.put(c, new Valve(streams.get(c).rate, costs));
    }
    streams = f_streams;


    HashMap<Key, Integer> dp = new HashMap<>();
    HashSet<Long> visited = new HashSet<>();
    if(elephant){
      ArrayList<String> to_get = can_open.stream().collect(Collectors.toCollection(ArrayList::new));
      long mask = 1;
      for(int i =0; i<=to_get.size(); i++){
        mask = mask*2;
      }
      mask -= 1;
      int res = 0;
      for(long i = mask/2 -1; i <= mask; i++) { // Better bitmask?
        HashSet<String> ele_open = new HashSet<>();
        HashSet<String> hum_open = new HashSet<>();
        long masked = (mask & i);
        long power = 1;
        for(int e = 0; e<to_get.size(); e++){
          if((masked & power) != 0) {
            hum_open.add(to_get.get(e));
          } else {
            ele_open.add(to_get.get(e));
          }
          power = power * 2;
        }
        if(!visited.contains(masked)){
          int r1 = search("AA", streams, turns, hum_open, dp, res, 0, 0);
          int r2 = search("AA", streams, turns, ele_open, dp, res, r1, 0);
          res = Math.max(res, r1+r2);
          visited.add(masked);
          visited.add(~masked);
        }
      }
      return res;
    }
    return search("AA", streams, turns, can_open, dp, 0, 0, 0);
  }
  
  public static void execute(){
    String example = "inputs/d16/example.txt";
    System.out.println("(example) Solution 1:" + sol(example, false));
    System.out.println("(example) Solution 2:" + sol(example, true));
    
    String input = "inputs/d16/input.txt";
    System.out.println("Solution 1:" + sol(input, false));
    System.out.println("Solution 2:" + sol(input, true));
  }
}
