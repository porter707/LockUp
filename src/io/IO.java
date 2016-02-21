package io;

import java.io.File;
import java.util.ArrayList;

public class IO {
    public static void main(String args[]){
    	System.out.println(getLockUpDirectory());
    }
    
	public void initialSetup(){
		File newFolder = new File(getUserDataDirectory() + File.separator + "LockUp");
		newFolder.mkdir();
	}
	
	public static String getUserDataDirectory() {
        return System.getProperty("user.home") + File.separator;
    }
	
	public static String getLockUpDirectory(){
		return System.getProperty("user.home") + File.separator + "LockUp" + File.separator;
	}
	
	public static void newFolder(String folder, String path){
		if (path == null){
			path = getLockUpDirectory();
		}
		File newFolder = new File(path + File.separator + folder);
		boolean success = newFolder.mkdir();
		System.out.println(success);
		if (!success){
			System.err.println("Failed to create " + folder + " folder");
		}
	}
	
	public void removeFolder(String folder){
		
	}
	
	public static void getAllFiles(String folder, ArrayList<File> fileList, int i){
		File directory;
		if (i == 0){
			directory = new File(getLockUpDirectory() + folder);
		}else{
			directory = new File(folder);
		}
		//get all the files from the directory
		File[] files = directory.listFiles();
		for (File file : files){
			if (file.isFile()){
				fileList.add(file);
			}else if (file.isDirectory()){
				getAllFiles(file.getAbsolutePath(), fileList, 1);
			}
		}
	}
	
	public void getFile(String file){
		
	}
	
	public void getMacTime(String file){
		
	}

}
