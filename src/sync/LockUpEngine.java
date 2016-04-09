package sync;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;

import org.bouncycastle.crypto.DataLengthException;

import cipher.AES;
import compression.Zip;
import csp.SecurePassword;
import db.Database;

public class LockUpEngine implements Runnable{
	
	private Thread t;
	private Database db = new Database();
	private ArrayList<String> lockUpFolders = new ArrayList<String>();
	private ArrayList<String> userFolders = new ArrayList<String>();
	private ArrayList<String> files = new ArrayList<String>();
	private String key;
	byte[] IV;
	private static int file = 0;
	private static int total = 0;
	private static boolean processing = false;
	private static boolean updateKey = false;
	
	public void start() throws SQLException, IOException, BackingStoreException, InvalidPreferencesFormatException{
		if (t == null){
			db.startDatabase();
			getKey();
			t = new Thread (this, "LockUp");
	        t.start ();
		}
	}

	@Override
	public void run() {
		while (true){
			try {
				if(updateKey == true){
					getKey();
					keyChanged();
					LockUpEngine.updateKey = false;
				}
				setProcessing(true);
				getFolders();
				for (int i = 0; i < lockUpFolders.size(); i++){
					getFiles(lockUpFolders.get(i));
					for (int j = 0; j < files.size(); j ++){
						System.out.println(files.get(j));
						String[] parts = files.get(j).split(",");
						setFile(j +1);
						setTotal(files.size());
						if (parts[2].equals("FALSE") && updateKey == false){
							String[] path = parts[0].split("/LockUp/Vault/" + lockUpFolders.get(i));
							String stringIV = db.getIV(lockUpFolders.get(i), parts[0], true);
							System.out.println(lockUpFolders.get(i));
							System.out.println(parts[0]);
							if (stringIV == null){
								db.setIV(lockUpFolders.get(i), parts[0], SecurePassword.SecureRandomAlphaNumericString().substring(0, 16), true);
								stringIV = db.getIV(lockUpFolders.get(i), parts[0], true);
								System.out.println(stringIV);
								IV = stringIV.getBytes();
							}else{
								IV = stringIV.getBytes();
							}
							transform(parts[0], userFolders.get(i) + path[1], true);
							db.setFalseModified(lockUpFolders.get(i), parts[0]);
						}else if (parts[3].equals("FALSE")){
							transform(parts[1], parts[0], false);
							String stringIV = db.getIV(lockUpFolders.get(i), parts[1], false);
							IV = stringIV.getBytes();
							db.setFalseOriginal(lockUpFolders.get(i), parts[1]);
						}
					}
					setFile(0);
					setTotal(0);
					files.clear();
				}
				lockUpFolders.clear();
				userFolders.clear();
				Thread.sleep(5000);
				setProcessing(false);

			} catch (InterruptedException | SQLException | IOException | BackingStoreException | InvalidPreferencesFormatException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private void keyChanged() throws SQLException, IOException, BackingStoreException, InvalidPreferencesFormatException {
		getFolders();
		for (int i = 0; i < lockUpFolders.size(); i++){
			db.keyChangeUpdateFiles(lockUpFolders.get(i));
		}
		lockUpFolders.clear();
		userFolders.clear();
	}

	private void getKey() throws SQLException {
		key = db.getKey();
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
			if (updateKey == true){
				resultSet = db.selectFilesUpdateOriginal(folder);
			}else{
				resultSet = db.selectFiles(folder);
			}
	        for(int i = 0; i < resultSet.size(); i++){
	        	files.add(resultSet.get(i));
	        }
		} catch (SQLException e) {
			lockUpFolders.remove(folder);
		}
	}
	public void transform(String file, String fileDestination, boolean encrypt){
		if (encrypt == true){
			compression(file, file + ".temp");
			encryption(file + ".temp", fileDestination);
			File temp = new File(file + ".temp");
			temp.delete();
		}else{
			decryption(file, file + ".temp");
			decompression(file + ".temp", fileDestination);
			File temp = new File(file + ".temp");
			temp.delete();
		}
	}
	public void compression(String file, String fileDestination){
		Zip.zipper(file, fileDestination);
	}
	public void decompression(String file, String fileDestination){
		Zip.unZipper(file, fileDestination);
	}
	public void encryption(String file, String fileDestination){
        try {
            FileInputStream fis =
                    new FileInputStream(new File(file));
            FileOutputStream fos =
                    new FileOutputStream(new File(fileDestination));
 
            AES AESCipher = new AES(key, IV);

            AESCipher.InitCiphers();
 
            AESCipher.CBCEncrypt(fis, fos);
 
        } catch (ShortBufferException ex) {
            Logger.getLogger(LockUpEngine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(LockUpEngine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(LockUpEngine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DataLengthException ex) {
            Logger.getLogger(LockUpEngine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalStateException ex) {
            Logger.getLogger(LockUpEngine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(LockUpEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
	}
	public void decryption(String file, String fileDestination){
        try {
            FileInputStream fis =
                    new FileInputStream(new File(file));
            FileOutputStream fos =
                    new FileOutputStream(new File(fileDestination));
 
            AES AESCipher = new AES(key, IV);

            AESCipher.InitCiphers();
 
            AESCipher.CBCDecrypt(fis, fos);
 
        } catch (ShortBufferException ex) {
            Logger.getLogger(LockUpEngine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(LockUpEngine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(LockUpEngine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DataLengthException ex) {
            Logger.getLogger(LockUpEngine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalStateException ex) {
            Logger.getLogger(LockUpEngine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(LockUpEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
	}

	public static int getFile() {
		return file;
	}

	public static void setFile(int file) {
		LockUpEngine.file = file;
	}

	public static int getTotal() {
		return total;
	}

	public static void setTotal(int total) {
		LockUpEngine.total = total;
	}

	public static boolean getProcessing() {
		return processing;
	}

	public static void setProcessing(boolean processing) {
		LockUpEngine.processing = processing;
	}

	public static void setUpdateKey(boolean updateKey) {
		LockUpEngine.updateKey = updateKey;
	}
}
