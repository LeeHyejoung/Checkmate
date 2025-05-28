package com.security.CheckMate.Security;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Scanner;

public class Verification {

	public static void main(String[] args) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, IOException {
		Scanner sc = new Scanner(System.in);
		
		//System.out.print("데이터 파일 이름 : ");
		String plainName = sc.next();
		FileInputStream fis1 = new FileInputStream(plainName);
		byte[] data = fis1.readAllBytes();
		fis1.close();
		
		//System.out.print("공개키 파일 이름 : ");
		String pubKeyName = sc.next();
		
		AsymmetricKeyManager keyMan = new AsymmetricKeyManager();
		keyMan.loadPublicKey(pubKeyName);
		
		//System.out.print("전자서명 파일 이름 : ");
		String signName = sc.next();
		FileInputStream fis = new FileInputStream(signName);
		byte[] signature = fis.readAllBytes();
		for (byte bytes : signature) {
			System.out.print(String.format("%02x", bytes) + " ");
		}
		
		Signature sig = Signature.getInstance("SHA256withRSA");
		sig.initVerify(keyMan.getPublicKey());
		sig.update(data);
		boolean rslt = sig.verify(signature);
		
		System.out.println("\n서명 검증 결과:" + rslt);
		
		fis.close();
		sc.close();
	}

}
