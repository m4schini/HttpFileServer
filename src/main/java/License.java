import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class License {
  private static List keys = new ArrayList();
  
  /**
   * Dirty method to load license keys from a txt file.
   * This method will be replaced.
   *
   * @return success of key loading
   */
  static boolean loadKeys() {
    try {
      Files.lines(FileSystems.getDefault().getPath("keys.txt"))
              .forEach(s -> keys.add(s));
    } catch (IOException e) {
      return false;
      //e.printStackTrace();
    }
    return true;
  }
  
  /**
   * @param key license key that needs to be verified
   * @return true = key is valid, false = key isn't valid
   */
  static boolean verify(String key ) {
    return key.contains(key);
  }
}
