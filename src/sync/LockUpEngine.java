package sync;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;

import db.Database;

public class LockUpEngine implements Runnable{
	
	private Thread t;
	private Database db = new Database();
	private ArrayList<String> lockUpFolders = new ArrayList<String>();
	private ArrayList<String> userFolders = new ArrayList<String>();
	private ArrayList<String> files = new ArrayList<String>();
	
	public void start() throws SQLException, IOException, BackingStoreException, InvalidPreferencesFormatException{
		if (t == null){
			db.startDatabase();
			t = new Thread (this, "LockUp");
	        t.start ();
		}
	}
	
	@Override
	public void run() {
		while (true){
			try {
				getFolders();
				for (int i = 0; i < lockUpFolders.size(); i++){
					getFiles(lockUpFolders.get(i));
					for (int j = 0; j < files.size(); j ++){
						String[] parts = files.get(j).split(",");
						if (parts[2].equals("FALSE")){
							String[] path = parts[0].split("/LockUp/Vault/" + lockUpFolders.get(i));
							transform(parts[0], userFolders.get(i) + path[1], true);
						}else if (parts[3].equals("FALSE")){
							transform(parts[1], parts[0], false);
						}
					}
				}

			} catch (SQLException | IOException | BackingStoreException | InvalidPreferencesFormatException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void getFolders() throws SQLException, IOException, BackingStoreException, InvalidPreferencesFormatException{
		List<String> resultSet = db.selectFolders();
        for(int i = 0; i < resultSet.size(); i++){
        	String[] parts = resultSet.get(i).split(",");
        	lockUpFolders.add(parts[1].replace(" ", "_"));
        	userFolders.add(parts[0]);
        }
	}
	
	public void getFiles(String folder){
		List<String> resultSet;
		try {
			resultSet = db.selectFiles(folder);
	        for(int i = 0; i < resultSet.size(); i++){
	        	files.add(resultSet.get(i));
	        }
		} catch (SQLException e) {
			lockUpFolders.remove(folder);
		}
	}
	public void transform(String file, String fileDestination, boolean encrypt){
		if (encrypt == true){
			compression();
			encryption();
		}else{
			decryption();
			decompression();
		}
		//System.out.println(file);
		//System.out.println(fileDestination);
	}
	public void compression(){
		
	}
	public void decompression(){
		
	}
	public void encryption(){
		
	}
	public void decryption(){
		
	}
}
