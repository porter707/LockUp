/**
 * Implementation of AES
 * Bouncy Castle API installed as a library
 * CBC mode for encryption and decryption
 * With PKCS7 Padding
 * @author Ashley Porter
 */
package cipher;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;

import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class AES {
	// Declare the ciphers
    PaddedBufferedBlockCipher encryptCipher = null;
    PaddedBufferedBlockCipher decryptCipher = null;

    // Buffer used to transport the bytes from one stream to another
    byte[] inputBuffer = new byte[16];              //input buffer
    byte[] outputBuffer = new byte[512];            //output buffer
    // The key
    byte[] key = null;
    // The initialization vector needed by the CBC mode
    byte[] IV =  null;

    // The default block size
    public static int blockSize = 16;
    
    public AES(String key){
        //256 bit key should be passed in declared by the user
        this.key = key.getBytes();
        //default IV vector with all bytes set to 0
        IV = new byte[blockSize];
    }

    public AES(String key, byte[] iv){
        //256 bit key should be passed in declared by the user
        this.key = key.getBytes();

        //get the IV passed in by the user
        IV = new byte[blockSize];
        System.arraycopy(iv, 0 , IV, 0, iv.length);
    }
    
    public void InitCiphers(){
        //create the ciphers
        // AES block cipher in CBC mode with padding
        encryptCipher = new PaddedBufferedBlockCipher(
                new CBCBlockCipher(new AESEngine()));

        decryptCipher =  new PaddedBufferedBlockCipher(
                new CBCBlockCipher(new AESEngine()));

        //create the IV parameter
        ParametersWithIV parameterIV =
                new ParametersWithIV(new KeyParameter(key),IV);

        encryptCipher.init(true, parameterIV);
        decryptCipher.init(false, parameterIV);
    }

    public void ResetCiphers() {
        if(encryptCipher!=null)
            encryptCipher.reset();
        if(decryptCipher!=null)
            decryptCipher.reset();
    }
    
    public void CBCEncrypt(InputStream in, OutputStream out) throws ShortBufferException, 
    	IllegalBlockSizeException,
    	BadPaddingException,
    	DataLengthException,
    	IllegalStateException,
        InvalidCipherTextException,
        IOException
    	{
		    // Bytes written to out will be encrypted
		    // Read in the cleartext bytes from in InputStream and
		    //      write them encrypted to out OutputStream
	
		    int noBytesRead = 0;        //number of bytes read from input
		    int noBytesProcessed = 0;   //number of bytes processed
	
		    while ((noBytesRead = in.read(inputBuffer)) >= 0) {
		        noBytesProcessed = encryptCipher.processBytes(inputBuffer, 0, noBytesRead, outputBuffer, 0);
		        out.write(outputBuffer, 0, noBytesProcessed);
		    }
		    noBytesProcessed = encryptCipher.doFinal(outputBuffer, 0);
	
		    out.write(outputBuffer, 0, noBytesProcessed);
	
		    out.flush();
	
		    in.close();
		    out.close();
	}
    		    
    public void CBCDecrypt(InputStream in, OutputStream out) throws ShortBufferException, 
    	IllegalBlockSizeException,
        BadPaddingException,
        DataLengthException,
        IllegalStateException,
        InvalidCipherTextException,
        IOException
	    {
	        // Bytes read from in will be decrypted
	        // Read in the decrypted bytes from in InputStream and and
	        //      write them in cleartext to out OutputStream
	
	        int noBytesRead = 0;        //number of bytes read from input
	        int noBytesProcessed = 0;   //number of bytes processed
	
	        while ((noBytesRead = in.read(inputBuffer)) >= 0) {
	            noBytesProcessed = decryptCipher.processBytes(inputBuffer, 0, noBytesRead, outputBuffer, 0);
	            out.write(outputBuffer, 0, noBytesProcessed);
	        }
	        noBytesProcessed = decryptCipher.doFinal(outputBuffer, 0);
	        
	        out.write(outputBuffer, 0, noBytesProcessed);
	
	        out.flush();
	
	        in.close();
	        out.close();
    }
}
