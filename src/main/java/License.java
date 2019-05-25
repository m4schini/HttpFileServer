import com.github.m4schini.FancyLog.Log;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * For now this has some quick and dirty methods. Planned is a license key verification connected to a database.
 */
@Deprecated
public class License {
  private static List keys = new ArrayList();
  private static DBConnection dbconnection = null;
  
  public License() {
    dbconnection = new DBConnection();
  }
  
  boolean verify(String key) {
    try {
      ResultSet resultSet = dbconnection.execute("SELECT * FROM licenseKeys WHERE licenseKey=?", key);
      resultSet.next();
      
      if (new Date().before(resultSet.getDate("validUntil"))) {
        Log.success("Licensekey is valid");
        return true;
      }
    } catch (SQLException e) {
      //e.printStackTrace();
      Log.error("Licensekey does not exist or some other SQL related error");
      return false;
    }
    Log.warning("Licensekey is not valid anymore");
    return false;
  }
  
  boolean activity(String key) {
    try {
      dbconnection.update("insert into activity values(?,?)",
              key,
              new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                      .format(Calendar.getInstance().getTime())
      );
      return true;
      
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }
  
  void close() {
    dbconnection.close();
  }
  
  /**
   * Dirty method to load license keys from a txt file.
   * This method will be replaced.
   *
   * @return success of key loading
   */
  @Deprecated
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
  @Deprecated
  static boolean verify_fromTXT(String key ) {
    return key.contains(key);
  }
}


