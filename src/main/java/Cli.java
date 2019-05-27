import com.github.m4schini.FancyLog.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;

class Cli {
  static void handler(String consoleInput) throws Exception {
    String[] command = consoleInput.split(" ");
    switch (command[0]) {
      case "exit":
        Log.status("bye.");
        UpdateServer.closeConnection();
        System.exit(0);
      break;
      case "ls":
        String arg1;
        try {
          arg1 = command[1];
        } catch (ArrayIndexOutOfBoundsException e) {
          ls(Config.PATH_UPDATES);
          break;
        }
        switch (arg1) {
          case "active":
          
          break;
          default:
            ls(Config.PATH_UPDATES + arg1);
          break;
        }
        break;
    }
  }
  
  /**
   * @param path to folder with requested content
   * @throws IOException readAtrributes
   */
  private static void ls(String path) throws IOException {
    Log.status("All available files in " + path + ":");
    File[] files = new File(path).listFiles();
    final String PATTERN = "%-40s%-15s%-30s%s%n";
    
    
    System.out.printf(PATTERN, "Name", "Size", "Created at", "Last accessed at");
    assert files != null;
    for (File file : files) {
      BasicFileAttributes f_attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
      String f_Name;
      if (file.isFile()) {
        f_Name = file.getName();
      } else {
        f_Name = file.getName() + "/";
      }
    
      System.out.printf(PATTERN,
              f_Name,
              (double) f_attrs.size() / 1000 + "kB",
              f_attrs.creationTime().toString().replace("T", " "),
              f_attrs.lastAccessTime().toString().replace("T", " "));
    }
  }
}