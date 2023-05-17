package com.advent2022.d7;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;
import java.util.stream.Collectors;

public class sh{
  
  /**
   * Explore and populate the Map that links each folder to its size
   * using known dirs size and file sizes
   **/
  static int explore(String to_explore, HashMap<String, HashSet<String>> dirs, HashMap<String, Integer> file_sizes, HashMap<String, Integer> final_sizes) {
    if (final_sizes.containsKey(to_explore)){
      return final_sizes.get(to_explore);
    }
    HashSet<String> children = dirs.get(to_explore);
    int size = 0;
    for(String child: children){
      if(final_sizes.containsKey(child)){
        size += final_sizes.get(child);
      } else if (file_sizes.containsKey(child)){
        size += file_sizes.get(child);
      } else {
        size += explore(child, dirs, file_sizes, final_sizes);
      }
    }
    final_sizes.put(to_explore, size);
    return size;
  }

  /**
   * Use switch statements to check the current command. 
   * Use a stack to check the current pwd and give correct names. 
   * Create the directory structure and then create the actual size. 
   * If get_small is true, simply sum the sizes under fixed size. 
   * Else check the first folder that can free the needed space
   **/
  static int sol(String filename, boolean get_small) {
    Stack<String> c_path = new Stack<>();
    HashMap<String, HashSet<String>> dirs = new HashMap<>();
    HashMap<String, Integer> file_sizes = new HashMap<>();
    dirs.put("/", new HashSet<>());
    c_path.add("/");

    try{
      InputStream iStream = sh.class. 
        getClassLoader(). 
        getResourceAsStream(filename);
      BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
      String line;
      while((line = reader.readLine()) != null) {
        char command = line.charAt(0);
        String[] tokens = line.split(" ");
        switch(command) {
          case '$':
            String cmd = tokens[1];
            switch(cmd) {
              case "cd":
                switch(tokens[2]){
                  case "/":
                    c_path = new Stack<>();
                    c_path.add("/");
                  break; 
                  case "..":
                    if(!c_path.peek().equals("/")){
                      c_path.pop();
                    }
                  break; 
                  default:
                    String new_pwd = c_path.peek() +"/"+ tokens[2];
                    if (!dirs.containsKey(new_pwd)){
                      dirs.put(new_pwd, new HashSet<>());
                    }
                    dirs.get(c_path.peek()).add(new_pwd);
                    c_path.add(new_pwd);
                  break;
                }
              break;
            }
          break;
          case 'd':
            String new_dir = c_path.peek() +"/"+ tokens[1];
            if (!dirs.containsKey(new_dir)){
              dirs.put(new_dir, new HashSet<>());
            }
            dirs.get(c_path.peek()).add(new_dir);
          break;
          default:
            int size = Integer.parseInt(tokens[0]);
            String f_name = c_path.peek() +"/"+ tokens[1];
            file_sizes.put(f_name, size);
            dirs.get(c_path.peek()).add(f_name);
          break;
        }
      }
      reader.close();
    } catch (FileNotFoundException e) {
      System.out.println("File not found");
      e.printStackTrace();
    } catch (IOException e) {
      System.out.println("End of file");
    }

    HashMap<String, Integer> final_sizes = new HashMap<>();
    explore("/", dirs, file_sizes, final_sizes);
    if (get_small) {
      int max_size = 100000;
      int res = 0;
      for(int size:final_sizes.values()){
        if (size <= max_size){
          res += size;
        }
      }
      return res;
    } else {
      int total_space = 70000000;
      int needed = 30000000;
      int free_now = total_space - final_sizes.get("/");
      for(int size :final_sizes.values().stream().sorted().collect(Collectors.toList())){
        if (size + free_now >= needed) {
          return size;
        }
      }
    }
    return -1;
  }

  public static void execute(){
    String example = "inputs/d7/example.txt";
    System.out.println("(example) Solution 1:" + sol(example, true));
    System.out.println("(example) Solution 2:" + sol(example, false));

    String input = "inputs/d7/input.txt";
    System.out.println("Solution 1:" + sol(input, true));
    System.out.println("Solution 2:" + sol(input, false));

  }
}
