package sync;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;

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
                        	} else if (file.isFile()){
                        		if(folderPath.contains(table + "Encrypted".replace("_", " "))){
                        			runSql(Database.addFileToTableModified(table, filePath, folderPath+"/"+filePath, false, false));
                        		}else{
                        			runSql(Database.addFileToTable(table, filePath, folderPath+"/"+filePath, false, true));
                        		}
                        	}
                        	System.out.println("file added " + folderPath + "/" + filePath);
                        }
                
                        @Override
                        public void onFileModify(String filePath) {
                            // File modified
                        	File file = new File(folderPath+"/"+filePath);
                        	if (file.isDirectory() && !watchedFolders.contains(folderPath+"/"+filePath)){
                        		new folderWatch(filePath, folderPath+"/"+filePath, table);
                        	} else if (file.isFile()){
                        		if(folderPath.contains(table + "Encrypted".replace("_", " "))){
                        			runSql(Database.updateFileFromTableModified(table, filePath));
                        		}else{
                        			runSql(Database.updateFileFromTable(table, filePath));
                        		}
                        	}
                        	System.out.println("file modded " + folderPath + "/" + filePath);
                        }
                        
                        @Override
                        public void onFileDelete(String filePath) {
                    		if(folderPath.contains(table + "Encrypted".replace("_", " "))){
                    			runSql(Database.deleteFileFromTableModified(table, filePath));
                    		}else{
                    			runSql(Database.deleteFileFromTable(table, filePath));
                    		}
                        	System.out.println("deleted " + folderPath + "/" + filePath);
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
}