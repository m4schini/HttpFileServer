import net.freeutils.httpserver.HTTPServer;

import java.io.IOException;

public class serveUpdate implements HTTPServer.ContextHandler {
  
  @Override
  public int serve(HTTPServer.Request req, HTTPServer.Response resp) throws IOException {
    
    return 0;
  }
}
