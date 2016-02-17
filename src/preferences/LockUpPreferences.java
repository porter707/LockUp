package preferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

import csp.SecurePassword;
import io.IO;

public class LockUpPreferences {

	Preferences preferences;
	String parentFolder = IO.getUserDataDirectory() + File.separator + "LockUp" + File.separator;
	
	public LockUpPreferences() throws IOException, BackingStoreException, InvalidPreferencesFormatException{
		
	    InputStream inputStream = null;
	    // First try loading from the current directory
	    try {
	        File preferencesFile = new File(parentFolder + "preferences.xml");
	        inputStream = new FileInputStream(preferencesFile);
	    } catch (Exception e ) { 
	    	inputStream = null; 
	    }

        if (inputStream == null ) {
        	preferences = Preferences.userRoot().node("preferences");
        	setCredentials(SecurePassword.SecureRandomAlphaNumericString(), SecurePassword.SecureRandomAlphaNumericString());
        	setFirstLaunch(true);
        	savePreferences();
        } else {
        	Preferences.importPreferences(inputStream);
			preferences = Preferences.userRoot().node("preferences");
        }
	}

    public void setCredentials(String username, String password){
    	preferences.put("db_username", username);
    	preferences.put("db_password", password);
    }
    
    public void setFirstLaunch(boolean firstLaunch){
    	preferences.putBoolean("first_launch", firstLaunch);
    }
    
    public String getUsername() {
    	return preferences.get("db_username", null);
    }

    public String getPassword() {
    	return preferences.get("db_password", null);
    }
    
    public boolean getFirstLaunch(){
    	return preferences.getBoolean("first_launch", true);
    }
    
    public void savePreferences() throws IOException, BackingStoreException{
    	OutputStream outputStream = new FileOutputStream(parentFolder + "preferences.xml");
    	preferences.exportNode(outputStream);
    }
}