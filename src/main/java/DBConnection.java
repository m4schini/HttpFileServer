
import java.io.Closeable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Collin Alpert
 * @see <a href="https://github.com/CollinAlpert/APIs/blob/master/de/collin/DBConnection.java">GitHub</a>
 *
 * modified by Malte Schink
 */
public class DBConnection implements Closeable {

	static String HOSTNAME;		//Specifies the hostname/ip address of the database.
	static String DATABASE;		//Specifies the name of the database to connect to.
	static String USERNAME;		//Specifies the username to log in on the database with.
	static String PASSWORD;		//Specifies the password to log in on the database with.

	/**
	 * Specifies the port to connect to the database on.
	 * This property is optional. If not specified, it will be set to 3306, the default port of MySQL.
	 */
	public static int PORT = 3306;


	static {
		DriverManager.setLoginTimeout(5);
	}

	private Connection connection;
	private boolean isConnectionValid;

	public DBConnection() {
		try {
			//var connectionString = String.format("jdbc:mysql://%s:%d/%s?serverTimezone=UTC", HOST, PORT, DATABASE);
			//Class.forName("com.mysql.cj.jdbc.Driver");
			//System.setProperty("user", USERNAME);
			//System.setProperty("password", PASSWORD);
			//connection = DriverManager.getConnection(connectionString, System.getProperties());
			
			DriverManager.setLoginTimeout(5);
			connection = DriverManager.getConnection(
							"jdbc:mariadb://" + HOSTNAME + ":" + PORT + "/" + DATABASE + "?autoReconnect=true",
							USERNAME,
							PASSWORD);
			
			isConnectionValid = true;
		} catch ( SQLException e) {
			e.printStackTrace();
			isConnectionValid = false;
		} catch (Exception e) {
			isConnectionValid = false;
		}
	}

	/**
	 * Checks if the connection is valid/successful.
	 *
	 * @return True if connection was successful, false if not.
	 */
	public boolean isValid() {
		return this.isConnectionValid;
	}


	/**
	 * Executes a DQL statement on the database without Java parameters.
	 *
	 * @param query The query to be executed.
	 * @return The {@link ResultSet} containing the result from the DQL statement.
	 * @throws SQLException if the query is malformed or cannot be executed.
	 */
	public ResultSet execute(String query) throws SQLException {
		Statement statement = connection.createStatement();
		Log.status(query);
		var set = statement.executeQuery(query);
		statement.closeOnCompletion();
		return set;
	}

	/**
	 * Executes a DQL statement on the database with Java parameters.
	 *
	 * @param query  The query to be executed.
	 * @param params The Java parameters to be inserted into the query.
	 * @return The {@link ResultSet} containing the result from the DQL statement.
	 * @throws SQLException if the query is malformed or cannot be executed.
	 */
	public ResultSet execute(String query, Object... params) throws SQLException {
		var statement = connection.prepareStatement(query);
		for (int i = 0; i < params.length; i++) {
			statement.setObject(i + 1, params[i]);
		}

		Log.status(query);
		var set = statement.executeQuery();
		statement.closeOnCompletion();
		return set;
	}

	/**
	 * This command is used for any DDL/DML queries.
	 *
	 * @param query The query to be executed.
	 * @return the last generated ID. This return value should only be used with INSERT statements.
	 * @throws SQLException if the query is malformed or cannot be executed.
	 */
	public long update(String query) throws SQLException {
		var statement = connection.createStatement();
		Log.status(query);
		statement.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
		return updateHelper(statement);
	}

	/**
	 * This command is used for any DDL/DML queries with Java parameters.
	 *
	 * @param query  The query to be executed.
	 * @param params The Java parameters to be inserted into the query.
	 * @return the last generated ID. This return value should only be used with INSERT statements.
	 * @throws SQLException if the query is malformed or cannot be executed.
	 */
	public long update(String query, Object... params) throws SQLException {
		var statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		for (int i = 0; i < params.length; i++) {
			statement.setObject(i + 1, params[i]);
		}

		Log.status(query);
		statement.executeUpdate();
		return updateHelper(statement);
	}

	private long updateHelper(Statement statement) throws SQLException {
		statement.closeOnCompletion();
		var set = statement.getGeneratedKeys();
		if (set.next()) {
			return set.getLong(1);
		}

		return -1;
	}

	/**
	 * Determines if a connection to the database still exists or not.
	 *
	 * @return {@code True} if a connection exists, {@code false} if not.
	 * This method will return {@code false} if an exception occurs.
	 */
	public boolean isOpen() {
		try {
			return !connection.isClosed();
		} catch (SQLException e) {
			System.err.println("Could not determine connection status");
			return isConnectionValid = false;
		}
	}

	/**
	 * Closes the connection to the database.
	 */
	@Override
	public void close() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			System.err.println("Could not close database connection");
			e.printStackTrace();
		} finally {
			isConnectionValid = false;
		}
	}
}
