package compression;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


public class Zip {	
	
	public static void zipper(String filename, String zipfile){
        byte[] buf = new byte[2048];
        try {
            String outFilename = zipfile;
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outFilename));
            FileInputStream in = new FileInputStream(filename);
            File file = new File(filename);
            out.putNextEntry(new ZipEntry(file.getName()));
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.closeEntry();
            in.close();
            out.close();
        } catch (IOException e) {
        	
        }
    }
	
	public static void unZipper(String zipfile, String filename){
        byte[] buf = new byte[2048];
        try {
            String outFilename = filename;
            ZipInputStream in = new ZipInputStream(new FileInputStream(zipfile));
            FileOutputStream out = new FileOutputStream(outFilename);
            ZipEntry entry = null;
            while ((entry = in.getNextEntry()) != null){
            	int len;
            	while((len = in.read(buf)) > 0){
            		out.write(buf, 0, len);
            	}
            }
            in.close();
            out.close();
        } catch (IOException e) {
        }
    }
}
