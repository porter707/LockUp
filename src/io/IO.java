package io;

import java.io.File;
import java.util.ArrayList;

public class IO {
    
	public static String getUserDataDirectory() {
        return System.getProperty("user.home") + File.separator;
    }
	
	public void initialSetup(){
		File newFolder = new File(getUserDataDirectory() + File.separator + "LockUp");
		boolean success = newFolder.mkdir();
		if (!success){
			System.err.println("Failed to create LockUp user folder");
		}
	}
	
	public void newFolder(String folder){
		File newFolder = new File(getUserDataDirectory() + File.separator + "LockUp" + File.separator + folder);
		boolean success = newFolder.mkdir();
		System.out.println(success);
		if (!success){
			System.err.println("Failed to create " + folder + " folder");
		}
	}
	
	public void removeFolder(String folder){
		
	}
	
	public void getAllFiles(String folder, ArrayList<File> fileList){
		File directory = new File(getUserDataDirectory() + File.separator + folder);
		//get all the files from the directory
		File[] files = directory.listFiles();
		for (File file : files){
			if (file.isFile()){
				fileList.add(file);
			}else if (file.isDirectory()){
				getAllFiles(file.getAbsolutePath(), fileList);
			}
		}
	}
	
	public void getFile(String file){
		
	}
	
	public void getMacTime(String file){
		
	}

}
