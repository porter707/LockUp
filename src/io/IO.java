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
		System.out.println(success);
		if (!success){
			System.err.println("Failed to create LockUp user folder folder");
		}
	}
	
	public void newFolder(String folder){
		
	}
	
	public void removeFolder(String folder){
		
	}
	
	public void getAllFiles(String folder, ArrayList<File> fileList){
		
	}
	
	public void getFile(String file){
		
	}
	
	public void getMacTime(String file){
		
	}

}
