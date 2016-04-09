package db;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;

import org.h2.jdbcx.JdbcDataSource;

import io.IO;
import preferences.LockUpPreferences;

public class Database {
	
	Connection conn;
	
	public void startDatabase() throws SQLException, IOException, BackingStoreException, InvalidPreferencesFormatException{
		JdbcDataSource ds = new JdbcDataSource();
		LockUpPreferences pref = new LockUpPreferences();
		File file = new File(IO.getUserDataDirectory() + File.separator + "LockUp" + File.separator + "LockUpDB" + File.separator + "LockUp.db.mv.db");
		if(!file.exists()){
			File file2 = new File(IO.getUserDataDirectory() + File.separator + ".LockUpBackup" + File.separator + "LockUp.db.mv.db");
			if (file2.exists()){
				Path backup = Paths.get(IO.getUserDataDirectory() + File.separator + ".LockUpBackup" + File.separator + "LockUp.db.mv.db");
				Path dest = Paths.get(IO.getUserDataDirectory() + File.separator + "LockUp" + File.separator + "LockUpDB" + File.separator + "LockUp.db.mv.db");
				Files.copy(backup, dest);
			}
		}
		ds.setURL("jdbc:h2:~/LockUp/LockUpDB/LockUp.db;mode=MySQL");
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
	
	public String getKey() throws SQLException{
		String key ="";
		PreparedStatement stmt = conn.prepareStatement("SELECT secureKey FROM LockUpUser");
        stmt.execute();
        ResultSet resultSet = stmt.getResultSet();
        while (resultSet.next()){
        	key = resultSet.getString(1);
        }
        stmt.close();
		return key;
	}
	public void createFolderTable() throws SQLException{
		PreparedStatement stmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS Folders "
				+ "(id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY, "
				+ "FolderName VARCHAR(255) NOT NULL UNIQUE, "
				+ "FolderPath VARCHAR(255) NOT NULL UNIQUE, "
				+ "FolderNameLockUp VARCHAR(255) NOT NULL UNIQUE, "
				+ "FolderPathLockUp VARCHAR(255) NOT NULL UNIQUE)");
        stmt.execute();
        stmt.close();
	}
	
	public List<String> selectAllFolders() throws SQLException{
		List<String> folders = new ArrayList<String>();
		PreparedStatement stmt = conn.prepareStatement("SELECT FolderNameLockUp FROM Folders");
        stmt.execute();
	    ResultSet resultSet = stmt.getResultSet();
	    ResultSetMetaData rsmd = resultSet.getMetaData();
	    int columnsNumber = rsmd.getColumnCount();
	    while (resultSet.next()) {
	        for (int i = 1; i <= columnsNumber; i++) {
	        	String columnValue = resultSet.getString(i);
                folders.add(columnValue);
	        }
	    }
	    stmt.close();
		return folders;
	}
	
	public List<String> selectFolders() throws SQLException{
		List<String> folders = new ArrayList<String>();
		PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Folders");
        stmt.execute();
	    ResultSet resultSet = stmt.getResultSet();
	    while (resultSet.next()) {
	    	folders.add(resultSet.getString(3) + "," + resultSet.getString(4));
	    }
	    stmt.close();
		return folders;
	}
	
	public List<String> selectFoldersToWatch() throws SQLException{
		List<String> folders = new ArrayList<String>();
		PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Folders");
        stmt.execute();
	    ResultSet resultSet = stmt.getResultSet();
	    while (resultSet.next()) {
	    	folders.add(resultSet.getString(2) + "," + resultSet.getString(3) + "," + resultSet.getString(4) + "," + resultSet.getString(5));
	    }
	    stmt.close();
		return folders;
	}
	
	public boolean addFolderToTable(String folderName, String folderPath, String folderNameLockUp, String folderPathLockUp){		 
		try {
			PreparedStatement stmt = conn.prepareStatement("INSERT INTO Folders Values (NULL, '" + folderName + "','"+ folderPath +"', '" + folderNameLockUp + "','" + folderPathLockUp + "')");
	        stmt.execute();
	        stmt.close();
	        createFileTable(folderNameLockUp.replace(" ", "_"));
		} catch (SQLException e) {
			return false;
		}
		return true;

	}
	
	public void removeFolderFromTable(String folder) throws SQLException{
		PreparedStatement stmt = conn.prepareStatement("DELETE FROM Folders WHERE FolderNameLockUp = '" + folder + "'");
        stmt.execute();
        stmt.close();
		stmt = conn.prepareStatement("DROP TABLE " + folder.replace(" ", "_") + "Folder");
        stmt.execute();
        stmt.close();
	}
	
	public void createFileTable(String table) throws SQLException{
		PreparedStatement stmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS " + table + 
				"Folder (id int(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,"
				+ "FileNameOriginal VARCHAR(255) NOT NULL UNIQUE,"
				+ "FilePathOriginal VARCHAR(255) NOT NULL UNIQUE,"
				+ "FileNameModified VARCHAR(255),"
				+ "FilePathModified VARCHAR(255),"
				+ "UpdateOriginal BOOLEAN,"
				+ "UpdateModified BOOLEAN,"
				+ "IV VARCHAR(16) UNIQUE)"
				);
        stmt.execute();
        stmt.close();
	}
	
	public void keyChangeUpdateFiles(String folder) throws SQLException{
		PreparedStatement stmt = conn.prepareStatement("UPDATE " + folder + "Folder SET UpdateModified = TRUE");
		stmt.execute();
		stmt.close();
	}
	
	public String getIV(String folder, String filePath, boolean original) throws SQLException{
		String IV = null;
		PreparedStatement stmt = null;
		if (original == true){
			stmt = conn.prepareStatement("SELECT IV FROM " + folder + "Folder WHERE FilePathOriginal = '"+filePath+"'");
		} else {
			stmt = conn.prepareStatement("SELECT IV FROM " + folder + "Folder WHERE FilePathModified = '"+filePath+"'");
		}
		stmt.execute();
		ResultSet resultSet = stmt.getResultSet();
	    while (resultSet.next()) {
	    	IV = resultSet.getString(1);
	    }
        stmt.close();
        return IV;
	}
	
	public void setIV(String folder, String filePath, String IV, boolean original) throws SQLException{
		PreparedStatement stmt = conn.prepareStatement("UPDATE " + folder + "Folder SET IV = '"+IV+"' WHERE FilePathOriginal = '"+filePath+"'");
		stmt.execute();
        stmt.close();
	}
	
	public List<String> selectAllFiles(String folder) throws SQLException{
		List<String> folders = new ArrayList<String>();
		PreparedStatement stmt = conn.prepareStatement("SELECT FileNameOriginal FROM " + folder + "Folder");
        stmt.execute();
	    ResultSet resultSet = stmt.getResultSet();
	    ResultSetMetaData rsmd = resultSet.getMetaData();
	    int columnsNumber = rsmd.getColumnCount();
	    while (resultSet.next()) {
	        for (int i = 1; i <= columnsNumber; i++) {
	        	String columnValue = resultSet.getString(i);
                folders.add(columnValue);
	        }
	    }
	    stmt.close();
		return folders;
	}
	
	public List<String> selectFiles(String folder) throws SQLException{
		List<String> files = new ArrayList<String>();
		PreparedStatement stmt = conn.prepareStatement("SELECT FilePathOriginal, FilePathModified, UpdateOriginal, UpdateModified FROM " + folder + "Folder");
        stmt.execute();
	    ResultSet resultSet = stmt.getResultSet();
	    while (resultSet.next()) {
	    	if (!resultSet.getString(3).equals("FALSE") || !resultSet.getString(4).equals("FALSE")){
	    		files.add(resultSet.getString(1) + "," + resultSet.getString(2) + "," + resultSet.getString(3) + "," + resultSet.getString(4));
	    	}
	    }
	    stmt.close();
		return files;
	}
	
	public List<String> selectFilesUpdateOriginal(String folder) throws SQLException{
		List<String> files = new ArrayList<String>();
		PreparedStatement stmt = conn.prepareStatement("SELECT FilePathOriginal, FilePathModified, UpdateOriginal, UpdateModified FROM " + folder + "Folder WHERE UpdateOriginal = 'TRUE'");
        stmt.execute();
	    ResultSet resultSet = stmt.getResultSet();
	    while (resultSet.next()) {
	    	if (!resultSet.getString(3).equals("FALSE") || !resultSet.getString(4).equals("FALSE")){
	    		files.add(resultSet.getString(1) + "," + resultSet.getString(2) + "," + resultSet.getString(3) + "," + resultSet.getString(4));
	    	}
	    }
	    stmt.close();
		return files;
	}
	
	public static String addFileToTable(String table, String fileNameOriginal, String filePathOriginal, Boolean UpdateOriginal, Boolean UpdateModified){
		String sql = "INSERT INTO " + table + "Folder"
			+ " (id, FileNameOriginal, FilePathOriginal, UpdateOriginal, UpdateModified) "
			+ "VALUES (NULL, '"+fileNameOriginal+"', '"+filePathOriginal+"', '"+UpdateOriginal+"', '"+UpdateModified+"') "
			+ "ON DUPLICATE KEY UPDATE "
			+ "FileNameOriginal = '"+fileNameOriginal+"', FilePathOriginal = '"+filePathOriginal+"'";	
		return sql;
	}
	
	public static String addFileToTableModified(String table, String fileNameModified, String filePathModified, Boolean UpdateOriginal, Boolean UpdateModified){
		String sql = "UPDATE " + table + "Folder"
			+ " SET FileNameModified = '"+fileNameModified+"', FilePathModified = '"+filePathModified+"', UpdateOriginal = '"+UpdateOriginal+"', UpdateModified = '"+UpdateModified+"'"
			+ " WHERE FileNameOriginal ='"+fileNameModified+"'";
		return sql;
	}
	
	public static String updateFileFromTable(String table, String file){
		String sql = "UPDATE " + table + "Folder SET UpdateOriginal = 'true' WHERE FileNameOriginal = '"+ file +"'";
		return sql;
	}
	
	public static String updateFileFromTableModified(String table, String file){
		String sql = "UPDATE " + table + "Folder SET UpdateModified = 'true' WHERE FileNameModified = '"+ file +"'";
		return sql;
	}
	
	public void setFalseOriginal(String table, String file) throws SQLException{
		PreparedStatement stmt = conn.prepareStatement("UPDATE " + table + "Folder SET UpdateOriginal = 'false' WHERE FilePathModified  = '"+ file +"'");
		stmt.execute();
		stmt.close();
	}
	
	public void setFalseModified(String table, String file) throws SQLException{
		PreparedStatement stmt = conn.prepareStatement("UPDATE " + table + "Folder SET UpdateModified = 'false' WHERE FilePathOriginal = '"+ file +"'");
		stmt.execute();
		stmt.close();
	}
	
	public static String deleteFileFromTable(String table, String file){
		String sql = "DELETE FROM " + table + "Folder WHERE FileNameOriginal = '" + file + "'";
		return sql;
	}
	
	public static String deleteFileFromTableModified(String table, String file){
		String sql = "DELETE FROM " + table + "Folder WHERE FileNameModified = '" + file + "'";
		return sql;
	}
	
	public static String deleteFolderFromTable(String table, String file){
		String sql = "DELETE FROM " + table + "Folder WHERE filePathOriginal LIKE '%" + file + "%'";
		return sql;
	}
	
	public static String deleteFolderFromTableModified(String table, String file){
		String sql = "DELETE FROM " + table + "Folder WHERE filePathModified LIKE '%" + file + "%'";
		return sql;
	}
	
	public static String getUpdateOriginal(String table, String file){
		String sql ="SELECT UpdateOriginal FROM " + table + "Folder WHERE fileNameOriginal = '" + file + "'";
		return sql;
	}
	
	public static String getUpdateModified(String table, String file){
		String sql ="SELECT UpdateModified FROM " + table + "Folder WHERE fileNameModified = '" + file + "'";
		return sql;
	}
	
	public void runStatement(String sql) throws SQLException{
		PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.execute();
        stmt.close();
	}
	
	public boolean runStatementBool(String sql) throws SQLException{
		Boolean update = false;
		PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.execute();
	    ResultSet resultSet = stmt.getResultSet();
	    while (resultSet.next()) {
	    	update = resultSet.getBoolean(1);
	    }
        stmt.close();
        return update;
	}
	public void closeDatabase() throws IOException{
		Path dest = Paths.get(IO.getUserDataDirectory() + File.separator + ".LockUpBackup" + File.separator + "LockUp.db.mv.db");
		Path backup = Paths.get(IO.getUserDataDirectory() + File.separator + "LockUp" + File.separator + "LockUpDB" + File.separator + "LockUp.db.mv.db");
		Files.copy(backup, dest, StandardCopyOption.REPLACE_EXISTING);
	}
}

