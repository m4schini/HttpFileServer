import com.github.m4schini.FancyLog.Log;

import net.freeutils.httpserver.HTTPServer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class Main {
  
  public static void main(String[] args) throws IOException {
    HTTPServer server = new HTTPServer(4200);
    HTTPServer.VirtualHost host = server.getVirtualHost(null);
    host.addContext("/update", new UpdateServer());
    server.start();
    
    Log.success("Server started");
    
    
    try {
      Config.load(Config.PATH_CONFIG);
    } catch (FileNotFoundException e) {
      Cli.init();
    }
    
    
    //console input -> CLI Handler
    Scanner scanner = new Scanner(System.in);
    while (true) try {
      Cli.handler(scanner.nextLine());
    } catch (Exception e) {
      Log.exception(e);
    }
  }
}
