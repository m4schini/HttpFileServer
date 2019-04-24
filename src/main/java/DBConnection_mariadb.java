import java.sql.*;

/**
 * @author Collin Alpert
 * @see <a href="https://github.com/CollinAlpert/APIs/blob/master/de/collin/DBConnection.java">GitHub</a>
 *
 * Edited and stripped down by Malte Schink
 * @see <a href="https://github.com/m4schini/APIs/tree/master/com/github/collinalpert/apis/database">GitHub</a>
 */
public class DBConnection_mariadb implements AutoCloseable {
	private Connection connection;
	private boolean isConnectionValid;
	
	final static String HOSTNAME = Keys.DB.HOSTNAME;
	final static String DATABASE = Keys.DB.DATABASE;
	final static String USERNAME = Keys.DB.USERNAME;
	final static String PASSWORD = Keys.DB.PASSWORD;
	
	public DBConnection_mariadb() {
		try {
			DriverManager.setLoginTimeout(5);
			connection = DriverManager.getConnection(
							"jdbc:mariadb://" + HOSTNAME + ":3306/" + DATABASE + "?autoReconnect=true",
							USERNAME,
							PASSWORD);
			isConnectionValid = true;
			
		} catch (SQLException e) {
			e.printStackTrace();
			isConnectionValid = false;
		} catch (Exception e) {
			System.err.println(
							"The connection to the database failed. Please check if the MySQL " +
											"server is reachable and if you have an internet connection.");
			
			isConnectionValid = false;
			System.exit(1);
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
	 * @return The {@link ResultSet} containing the result from the SELECT query.
	 * @throws SQLException if the query is malformed or cannot be executed.
	 */
	public ResultSet execute(String query) throws SQLException {
		Statement statement = connection.createStatement();
		var set = statement.executeQuery(query);
		statement.closeOnCompletion();
		return set;
	}
	
	/**
	 * Executes a DQL statement on the database with Java parameters.
	 *
	 * @param query  The query to be executed.
	 * @param params The Java parameters to be inserted into the query.
	 * @return The {@link ResultSet} containing the result from the SELECT query.
	 * @throws SQLException if the query is malformed or cannot be executed.
	 */
	public ResultSet execute(String query, Object... params) throws SQLException {
		var statement = connection.prepareStatement(query);
		for (int i = 0; i < params.length; i++) {
			statement.setObject(i + 1, params[i]);
		}
		var set = statement.executeQuery();
		statement.closeOnCompletion();
		return set;
	}
	
	/**
	 * This command is used for any DDL/DML queries.
	 *
	 * @param query The query to be executed.
	 * @throws SQLException if the query is malformed or cannot be executed.
	 */
	public void update(String query) throws SQLException {
		var statement = connection.createStatement();
		statement.executeUpdate(query);
		statement.closeOnCompletion();
	}
	
	/**
	 * This command is used for any DDL/DML queries with Java parameters.
	 *
	 * @param query  The query to be executed.
	 * @param params The Java parameters to be inserted into the query.
	 * @throws SQLException if the query is malformed or cannot be executed.
	 */
	public void update(String query, Object... params) throws SQLException {
		var statement = connection.prepareStatement(query);
		for (int i = 0; i < params.length; i++) {
			statement.setObject(i + 1, params[i]);
		}
		statement.executeUpdate();
		statement.closeOnCompletion();
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
			isConnectionValid = false;
			return false;
		}
	}
	
	/**
	 * Closes the connection to the database.
	 */
	@Override
	public void close() {
		try {
			connection.close();
		} catch (SQLException e) {
			System.err.println("Could not close database connection");
		} finally {
			isConnectionValid = false;
		}
	}
}
