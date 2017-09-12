package com;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class StringEncryption {

	public static void main(String[] args) {

		try {
			
			String msg = "This is a message.";

			String e = encryptAes128( msg );
			System.out.println("encrypted: " + e );
			System.out.println("decrypted: " + decryptAes128( e ) );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public final static String genKeyAes128() throws NoSuchAlgorithmException {
		KeyGenerator keyG = KeyGenerator.getInstance("AES");
		keyG.init(128);
		SecretKey secuK = keyG.generateKey();
		byte[] key = secuK.getEncoded();
		String strKey = Base64.getEncoder().encodeToString(key);
		return strKey;
	}
	
	public final static String getKeyAes128() {
		return "AiZXSuLwN0eM2rMhI4Ogng==";
	}
	
	
	public final static String encryptAes128(String s) throws NoSuchAlgorithmException, 
		BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, InvalidKeyException {

		if(s==null) {
			return null;
		}
		byte[] key = Base64.getDecoder().decode( getKeyAes128() );
		SecretKeySpec spec = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance("AES");

		cipher.init(Cipher.ENCRYPT_MODE, spec);

		byte[] encryptData = cipher.doFinal(s.getBytes());
		String strEncrypted = Base64.getEncoder().encodeToString(encryptData);
		return strEncrypted;
	}
	
	public final static String decryptAes128(String s) throws NoSuchAlgorithmException, 
		BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, InvalidKeyException {
		if(s==null) {
			return null;
		}
		byte[] key = Base64.getDecoder().decode( getKeyAes128() );
		SecretKeySpec spec = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance("AES");

		cipher.init(Cipher.DECRYPT_MODE, spec);
		byte[] original = cipher.doFinal(Base64.getDecoder().decode(s) );
		return new String( original );
	}
}
