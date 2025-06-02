package com.security.CheckMate.Security;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Scanner;

public class Sign {

	public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException, IOException {
		Scanner sc = new Scanner(System.in);
		
		//System.out.print("데이터 파일 이름 : ");
		String plainName = sc.next();
		//System.out.print("개인키 파일 이름 : ");
		String priKeyName = sc.next();

		byte[] data;
		try (FileInputStream fis = new FileInputStream(plainName)) {
			data = fis.readAllBytes();
		}

		AsymmetricKeyManager keyMan = new AsymmetricKeyManager();
		keyMan.loadPrivateKey(priKeyName);
		
		Signature sig = Signature.getInstance("SHA256withRSA");
		sig.initSign(keyMan.getPrivateKey());
		sig.update(data);
		byte[] signature = sig.sign();
		
		for (byte bytes : signature) {
			System.out.print(String.format("%02x", bytes) + " ");
		}
		
		//System.out.print("\n서명을 저장할 파일 이름 : ");
		String signName = sc.next();
		
		FileOutputStream fos = new FileOutputStream(signName);
		fos.write(signature);
		
		fos.close();
		sc.close();
	}

}
