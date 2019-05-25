import com.github.m4schini.FancyLog.Log;

import net.freeutils.httpserver.HTTPServer;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class UpdateServer implements HTTPServer.ContextHandler {
  private License license = new License();
  
  @Override
  public int serve(HTTPServer.Request request, HTTPServer.Response response) throws IOException {
    Log.divide();
    Log.status("New Request:");
    
    Map<String, String> params = request.getParams();
    if (license.verify(params.get("key"))) {
      Log.status("used licensekey: " + params.get("key"));
    
      File file = new File(Config.PATH_UPDATES + params.get("file"));
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
  static void sendFile(int status, String contentType, File file, HTTPServer.Response response) throws IOException {
    
    response.sendHeaders(status, file.length(), -1,
            "W/\"" + Integer.toHexString(file.hashCode()) + "\"",
            contentType, null);
    OutputStream out = response.getBody();
    
    if (out != null)
      out.write(IOUtils.toByteArray(new FileInputStream(file)));
  }
}


