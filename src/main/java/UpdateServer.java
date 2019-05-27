import com.github.m4schini.FancyLog.Log;

import net.freeutils.httpserver.HTTPServer;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class UpdateServer implements HTTPServer.ContextHandler {
  private static License license = new License();
  
  @Override
  public int serve(HTTPServer.Request request, HTTPServer.Response response) throws IOException {
    Log.divide();
    Log.status("New Request");
    
    Map<String, String> params = request.getParams();
    if (license.verify(params.get("key"))) {
      Log.status("Valid key");
      DataFile dataFile = getFile(Config.PATH_UPDATES + params.get("file"));
      if (dataFile.getFile().exists()) {
        try {
          response.getHeaders().add("Content-Disposition", "filename=" + params.get("file"));
          sendFile(200,
                  dataFile.getMime(),
                  dataFile.getFile(),
                  response);
          
          Log.success(dataFile.getFile().getName() + " served");
        } catch (IOException e) {
          Log.error("Something went wrong with the file");
          Log.exception(e);
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
   * Prepares File to serve. Checks if File exists and if it is a supported filetype.
   *
   * @param path to File
   * @return {@link DataFile} containing Mime and File
   */
  private static DataFile getFile(String path) {
    DataFile data = null;
  
    File file = new File(path);
    if (file.exists()) {
      String filetype = file.getName().split("\\.")[1];
      if (Config.getMIMEs().containsKey(filetype)) {
        data = new DataFile();
        data.setMime((String) Config.getMIMEs().get(filetype));
        data.setFile(file);
      }
    }
    return data;
  }
  
  /**
   * Modified net.freeutils.httpserver.HTTPServer.Response#send(int, java.lang.String)
   * makes it possible to serve File instead of an HTML Page
   *
   * @param status http status code
   * @param contentType mime content type
   * @param file File you want to serve
   * @param response HTTPServer.Response, needed to answer to request
   * @throws IOException HTTPServer.Response, FileInputStream
   */
  private static void sendFile(int status, String contentType, File file, HTTPServer.Response response)
          throws IOException {
    
    response.sendHeaders(status, file.length(), -1,
            "W/\"" + Integer.toHexString(file.hashCode()) + "\"",
            contentType, null);
    OutputStream out = response.getBody();
    
    if (out != null)
      out.write(IOUtils.toByteArray(new FileInputStream(file)));
  }
  
  static void closeConnection() {
    license.close();
  }
}

class DataFile {
  private static String mime = null;
  private static File file = null;
  
  String getMime() {
    return mime;
  }
  
  File getFile() {
    return file;
  }
  
  void setMime(String mime) {
    DataFile.mime = mime;
  }
  
  void setFile(File file) {
    DataFile.file = file;
  }
}
