import com.github.m4schini.FancyLog.Log;

import net.freeutils.httpserver.HTTPServer;

import java.io.IOException;
import java.util.Map;

public class UpdateServer implements HTTPServer.ContextHandler {
  
  @Override
  public int serve(HTTPServer.Request req, HTTPServer.Response resp) throws IOException {
    Log.divide();
    Log.status("New Request:");
    
    Map<String, String> params = req.getParams();
    
    return 0;
  }
}
