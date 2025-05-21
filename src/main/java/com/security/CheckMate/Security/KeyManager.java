package com.security.CheckMate.Security;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;

import java.security.PrivateKey;

public class KeyManager {
	private KeyPair keyPair;
	private PublicKey publicKey;
	private PrivateKey privateKey;
	
	public boolean generate() throws NoSuchAlgorithmException {
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
		keyPairGen.initialize(1024);
		KeyPair keyPair = keyPairGen.generateKeyPair();
		this.setKeyPair(keyPair);

		privateKey = keyPair.getPrivate();
		publicKey = keyPair.getPublic();
		
		return true;
	}
	
	public boolean savePublicKey(String fname) {
		try (FileOutputStream fos = new FileOutputStream(fname)) {
			try (ObjectOutputStream ostream = new ObjectOutputStream(fos)) {
				ostream.writeObject(publicKey);
				ostream.flush();
				return true;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public PublicKey getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(PublicKey publicKey) {
		this.publicKey = publicKey;
	}

	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(PrivateKey privateKey) {
		this.privateKey = privateKey;
	}

	public boolean savePrivateKey(String fname) {
		try (FileOutputStream fos = new FileOutputStream(fname)) {
			try (ObjectOutputStream ostream = new ObjectOutputStream(fos)) {
				ostream.writeObject(privateKey);
				ostream.flush();
				return true;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public PublicKey loadPublicKey(String fname) {
		try (FileInputStream fis = new FileInputStream(fname)) {
			try (ObjectInputStream ostream = new ObjectInputStream(fis)) {
				Object obj = ostream.readObject();
				this.publicKey = (PublicKey)obj;
				return (PublicKey)obj;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public PrivateKey loadPrivateKey(String fname) {
		try (FileInputStream fis = new FileInputStream(fname)) {
			try (ObjectInputStream ostream = new ObjectInputStream(fis)) {
				Object obj = ostream.readObject();
				this.privateKey = (PrivateKey)obj;
				return (PrivateKey)obj;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void printInfo() {
		System.out.println("암호화 알고리즘 : " + publicKey.getAlgorithm());
		System.out.println("공개키 정보:");
		System.out.println("키의 길이 (bytes): " + publicKey.getEncoded().length);
		printBytes(publicKey.getEncoded());
		System.out.println("\n개인키 정보:");
		System.out.println("키의 길이 (bytes): " + privateKey.getEncoded().length);
		printBytes(privateKey.getEncoded());
	}
	
	public void printBytes(byte[] b) {
		for (byte bytes : b) {
			System.out.print(String.format("%02x", bytes) + " ");
		}
	}
	
	public Key getKey(String fname) {
		if (fname.contains("public")) {
			return loadPublicKey(fname);
		}
		else {
			return loadPrivateKey(fname);
		}
	}
	public boolean encrypt(String kFname, String pFname, String eFname) throws FileNotFoundException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
		Cipher cipher = Cipher.getInstance("RSA");
		Key key = getKey(kFname);
		cipher.init(Cipher.ENCRYPT_MODE, key);
		String data = "";
		try (FileInputStream fis = new FileInputStream(pFname)) {
			int c;
			while ((c = fis.read()) != -1) {
				data += (char)c;
			}
		}
		
		try (FileOutputStream fos = new FileOutputStream(eFname);
				CipherOutputStream cos = new CipherOutputStream(fos, cipher)) {
			cos.write(data.getBytes());
			cos.flush();
		}
		return true;
	}
	
	public boolean decrypt(String kFname, String eFname) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, FileNotFoundException, IOException {
		Cipher cipher = Cipher.getInstance("RSA");
		Key key = getKey(kFname);
		cipher.init(Cipher.DECRYPT_MODE, key);
		
		try (FileInputStream bis = new FileInputStream(eFname);
				CipherInputStream cis = new CipherInputStream(bis, cipher);
				Scanner sc = new Scanner(cis)) {
			String decrypted = new String();
			while (sc.hasNext()) {
				decrypted += sc.nextLine();
				decrypted += "\n";
			}
			System.out.println("result : \n" + decrypted);
		}
		return true;
	}

	public KeyPair getKeyPair() {
		return keyPair;
	}

	public void setKeyPair(KeyPair keyPair) {
		this.keyPair = keyPair;
	}
}
