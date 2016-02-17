package db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;

import org.h2.jdbcx.JdbcDataSource;

import preferences.LockUpPreferences;

public class Database {
	
	Connection conn;
	
	public void startDatabase() throws SQLException, IOException, BackingStoreException, InvalidPreferencesFormatException{
		JdbcDataSource ds = new JdbcDataSource();
		LockUpPreferences pref = new LockUpPreferences();
		ds.setURL("jdbc:h2:~/LockUp/LockUpDB/LockUp.db");
		ds.setUser(pref.getUsername());
		ds.setPassword(pref.getPassword());
		conn = ds.getConnection();
	}
	
	public void createUserTable() throws SQLException{
		PreparedStatement stmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS LockUpUser (id INT(6) UNSIGNED PRIMARY KEY, secureKey VARCHAR(32) NOT NULL,)");
        stmt.execute();
        stmt.close();
	}
	
	public void addToUserTable(String key) throws SQLException{
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO LockUpUser VALUES(1, '" + key + "')");
		System.out.println(stmt);
        stmt.execute();
        stmt.close();
	}
	
	public void updateUserTable(String key) throws SQLException{
		PreparedStatement stmt = conn.prepareStatement("UPDATE LockUpUser SET secureKey = '" + key + "' WHERE id = '1'");
        stmt.execute();
        stmt.close();
	}
	
	public boolean getUserCount() throws SQLException{
		PreparedStatement stmt = conn.prepareStatement("SELECT * FROM LockUpUser");
		stmt.execute();
        ResultSet resultSet = stmt.getResultSet();
        boolean result = resultSet.next();
        stmt.close();
        return result;
	}
	
	public void createFolderTable() throws SQLException{
		PreparedStatement stmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS Folders (id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY, FolderName VARCHAR(255) NOT NULL UNIQUE,)");
        stmt.execute();
        stmt.close();
	}
	
	public boolean addFolderToTable(String folder){
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement("INSERT INTO Folders Values (NULL, '" + folder + "')");
	        stmt.execute();
	        stmt.close();
		} catch (SQLException e) {
			return false;
		}
		return true;

	}
	
	public void removeFolderFromTable(String folder) throws SQLException{
		PreparedStatement stmt = conn.prepareStatement("DELETE FROM Folders WHERE FolderName = '" + folder + "'");
        stmt.execute();
        stmt.close();
	}
	
	public void createFileTable(String table){
		
	}
	
	public void addFileToTable(String table, String file){
		
	}
	
	public void removeFileFromTable(String table, String file){
		
	}
	
	public void updateFileFromTable(String table, String file){
		
	}
	public void closeDatabase() throws SQLException{
		PreparedStatement stmt = conn.prepareStatement("DROP TABLE Folders");
		stmt.execute();
//        ResultSet resultSet = stmt.getResultSet();
//        ResultSetMetaData rsmd = resultSet.getMetaData();
//        int columnsNumber = rsmd.getColumnCount();
//        while (resultSet.next()) {
//            for (int i = 1; i <= columnsNumber; i++) {
//                if (i > 1) System.out.print(",  ");
//                String columnValue = resultSet.getString(i);
//                System.out.print(columnValue + " " + rsmd.getColumnName(i));
//            }
//            System.out.println("");
//        }
        stmt.close();
	}
	
	public static void main(String[] args) throws SQLException, IOException, BackingStoreException, InvalidPreferencesFormatException{
		Database db = new Database();
		db.startDatabase();
		db.closeDatabase();
	}
}
