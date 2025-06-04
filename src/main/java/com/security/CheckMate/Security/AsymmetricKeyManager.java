package com.security.CheckMate.Security;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;

public class AsymmetricKeyManager {
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

	public byte[] getPublicKeyBytes() {
		if (publicKey == null) return null;
		return publicKey.getEncoded();
	}

	public byte[] getPrivateKeyBytes() {
		if (privateKey == null) return null;
		return privateKey.getEncoded();
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

	public Key getKey(String fname) {
		if (fname.contains("public")) {
			return loadPublicKey(fname);
		}
		else {
			return loadPrivateKey(fname);
		}
	}

	public KeyPair getKeyPair() {
		return keyPair;
	}

	public void setKeyPair(KeyPair keyPair) {
		this.keyPair = keyPair;
	}
}
