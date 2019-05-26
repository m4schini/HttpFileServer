import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

class Config {
  static final String PATH_CONFIG = "server.conf";
  static final String PATH_UPDATES = "updateFiles/";
  static Properties mimetypes;
  
  static Properties load(String path) throws IOException {
    Properties propertiesFile = new Properties();
    FileInputStream inputStream = new FileInputStream(path);
    propertiesFile.load(inputStream);
    inputStream.close();
    return propertiesFile;
  }
  
  static void save(String path, Properties properties) throws IOException {
    FileOutputStream outputStream = new FileOutputStream(path);
    properties.store(outputStream, "This is a config file for updateServer");
    outputStream.close();
  }
  
  static void loadMIMEs() {
    try {
      mimetypes = load("filetypes");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}