package sync;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import db.Database;

public class folderWatch {
	private String table;
	static ArrayList<String> watchedFolders = new ArrayList<String>();
	
	public folderWatch(String folder, String folderPath, String tableName){
		this.table = tableName;
		DirectoryWatchService watchService = null;
		try {
            watchService = new SimpleDirectoryWatchService(); // May throw
            watchService.register( // May throw
                    new DirectoryWatchService.OnFileChangeListener() {
                        @Override
                        public void onFileCreate(String filePath) {
                            // File created
                        	File file = new File(folderPath+"/"+filePath);
                        	if (file.isDirectory() && !watchedFolders.contains(folderPath+"/"+filePath)){
                        		new folderWatch(filePath, folderPath+"/"+filePath, table);
                        	} else if (file.isFile() && !filePath.equals(".DS_Store") && !filePath.contains(".temp")){
                        		if(folderPath.contains(table + "Encrypted".replace("_", " "))){
                        			runSql(Database.addFileToTableModified(table, filePath, folderPath+"/"+filePath, false, false));
                        		}else{
                        			runSql(Database.addFileToTable(table, filePath, folderPath+"/"+filePath, false, true));
                        		}
                        	}
                        }
                
                        @Override
                        public void onFileModify(String filePath) {
                            // File modified
                        	File file = new File(folderPath+"/"+filePath);
                        	if (file.isDirectory() && !watchedFolders.contains(folderPath+"/"+filePath)){
                        		new folderWatch(filePath, folderPath+"/"+filePath, table);
                        	} else if (file.isFile() && !filePath.equals(".DS_Store") && !filePath.contains(".temp") && LockUpEngine.getProcessing() == false){
                        		if(folderPath.contains(table + "Encrypted".replace("_", " "))){
                        			if (getUpdate(Database.getUpdateModified(table, filePath)) != true){
                        				runSql(Database.updateFileFromTable(table, filePath));
                        			}
                        		}else{
                        			if (getUpdate(Database.getUpdateOriginal(table, filePath)) != true){
                        				runSql(Database.updateFileFromTableModified(table, filePath));
                        			}
                        		}
                        	}
                        }
                        
                        @Override
                        public void onFileDelete(String filePath) {
                            // File deleted
                        	Pattern pattern = Pattern.compile("(\\.[^.]+)$");
                        	Matcher matcher = pattern.matcher(filePath);
                        	if (matcher.find()){
                        		if(folderPath.contains(table + "Encrypted".replace("_", " "))){
                        			runSql(Database.deleteFileFromTableModified(table, filePath));
                        		}else{
                        			runSql(Database.deleteFileFromTable(table, filePath));
                        		}
                        	}else{
                        		if(folderPath.contains(table + "Encrypted".replace("_", " "))){
                        			runSql(Database.deleteFolderFromTableModified(table, folderPath + "/" + filePath));
                        		}else{
                        			runSql(Database.deleteFolderFromTable(table, filePath));
                        		}
                        	}
                        }
                    },
                    folderPath
            );
            
            watchService.start();
            watchedFolders.add(folderPath);
            File directory = new File(folderPath);
    		File[] files = directory.listFiles();
    		for (File file : files){
    			if (file.isDirectory()){
    				new folderWatch(file.getName().toString(),  file.toString(), table);
    			}else if (file.isFile() && !file.getName().toString().equals(".DS_Store") && !file.getName().toString().contains(".temp")){
    				if(file.toString().contains(table + "Encrypted".replace("_", " "))){
    					runSql(Database.addFileToTableModified(table, file.getName().toString(), file.toString(), false, false));
    				} else {
    					runSql(Database.addFileToTable(table, file.getName().toString(), file.toString(), false, true));
    				}
    			}
    		}
        } catch (IOException e) {
        	Logger.getLogger(folderWatch.class.getName()).log(Level.SEVERE, "Unable to register file change listener", e);
        }
    }
	
	public void runSql(String sql){
		Database db = new Database();
		try {
			db.startDatabase();
			db.runStatement(sql);
		} catch (SQLException  | IOException  | BackingStoreException  | InvalidPreferencesFormatException e) {
			e.printStackTrace();
		}
	}
	
	public boolean getUpdate(String sql){
		Database db = new Database();
		Boolean update = null;
		try {
			db.startDatabase();
			update = db.runStatementBool(sql);
		} catch (SQLException  | IOException  | BackingStoreException  | InvalidPreferencesFormatException e) {
			e.printStackTrace();
		}
		return update;
	}
}
