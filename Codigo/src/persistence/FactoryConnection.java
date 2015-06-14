package persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class FactoryConnection {

	static String statusConnection = "";
	
	private String local = "jdbc:mysql://localhost/sisres_db";
	private String user = "root";
	private String password = "";
	
	//Singleton
		private static FactoryConnection instance;
		private FactoryConnection() {
			
		}
		
		/**
	     * Instantiates an object for future connection to the database if there is no instance of it.
	     * @return FactoryConnection 
	     */
		public static FactoryConnection getInstance() {
			if ( instance == null ) {
				instance = new FactoryConnection();
			}else{
				//do nothing
			}
			return instance;
		}
	
		
	/**
	 * Opens the connection with the database.	
	 * @return Connection - Connection with the database
	 */
	public Connection getConnection() throws SQLException {
		Connection con = null;
		con = DriverManager.getConnection( local, user, password );
		return con;
	}

}
