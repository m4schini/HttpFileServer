import com.github.m4schini.FancyLog.Log;

import net.freeutils.httpserver.HTTPServer;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Scanner;

public class Main {
  
  private static License license = new License();
  public static final String UPDATES_PATH = "updateFiles/";
  
  public static void main(String[] args) throws IOException {
    if (License.loadKeys()) {
    } else {
      Log.critical("Unable to load license Keys");
      System.exit(-1);
    }
  
    HTTPServer server = new HTTPServer(4200);
    HTTPServer.VirtualHost host = server.getVirtualHost(null);
    host.addContext("/update", new getUpdater());
    host.addContext("/v2/update", new serveUpdate());
    server.start();
  
    Scanner scanner = new Scanner(System.in);
    Log.success("Server started");
    while (true) try {
      Cli.handler(scanner.nextLine());
    } catch (Exception e) {
      Log.exception(e);
    }
  }
  
  private static class getUpdater implements HTTPServer.ContextHandler {
    @Override
    public int serve(HTTPServer.Request request, HTTPServer.Response response) throws IOException {
      Log.divide();
      Log.warning("new update request");
      Map<String, String> params = request.getParams();
      
      //Obsolete version: License.verify_fromTXT
      if (license.verify(params.get("key"))) {
        Log.status("used licensekey: " + params.get("key"));
  
        File file = new File(UPDATES_PATH + params.get("file"));
        if (file.exists()) {
          try {
            response.getHeaders().add("Content-Disposition", "filename=" + params.get("file"));
            sendFile(200, "application/zip", file, response);
            Log.success("File " + params.get("file") + " served");
          } catch (IOException e) {
            Log.error("Something went wrong with the file");
            //e.printStackTrace();
          }
        } else {
          Log.error("File does not exist");
          response.send(404, "File wasn't found");
        }
      } else {
        Log.error("request used invalid key");
        response.send(401, "You're not allowed to access this file");
      }
      return 0;
    }
    
    /**
     * Modified net.freeutils.httpserver.HTTPServer.Response#send(int, java.lang.String)
     * makes it possible to serve FileHandler instead of an HTML Page
     *
     * @param status http status code
     * @param contentType MIME Content type
     * @param file File you want to serve
     * @param response HTTPServer.Response, needed to answer to request
     * @throws IOException HTTPServer.Response, FileInputStream
     */
    void sendFile(int status, String contentType, File file, HTTPServer.Response response) throws IOException {
      
      response.sendHeaders(status, file.length(), -1,
              "W/\"" + Integer.toHexString(file.hashCode()) + "\"",
              contentType, null);
      OutputStream out = response.getBody();
      
      if (out != null)
        out.write(IOUtils.toByteArray(new FileInputStream(file)));
    }
  }
}
