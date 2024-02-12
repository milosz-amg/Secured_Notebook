package com.example.notebook_fingerprint;

import android.content.SharedPreferences;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import androidx.annotation.RequiresApi;

import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;


public class Cryptography {
    public final static String KEY_ALIAS="note_key";
    public static final String SHARED_PREFS = "sharedPrefs";

    public static void debug(){
        System.out.println("debug");
    }

    public static byte[] generateiv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    public static void genNewKey(){
        try {
            //KeyGenParameterSpec
            KeyGenParameterSpec spec = new KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT
            )
                    .setKeySize(256)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .setRandomizedEncryptionRequired(false)
                    .setUserAuthenticationRequired(true) //zwroc klucz tylko gdy user przeszedl autoryzacje
                    .build();

            //KeyGenerator
            KeyGenerator keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES,"AndroidKeyStore");

            //GENERATOR+SPEC
            keyGenerator.init(spec);

            //generuje klucz
            keyGenerator.generateKey();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String encryptNote(String note){
        try{
            byte[] iv = generateiv();
            SecretKey secretKey = loadFromKeystore();

            Cipher cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
            byte[] encryptedBytes = cipher.doFinal(note.getBytes());

            byte[] combined = new byte[iv.length + encryptedBytes.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encryptedBytes, 0, combined, iv.length, encryptedBytes.length);

            //zakoduj do Stringa i zwróć
            System.out.println(Base64.getEncoder().encodeToString(combined));
            return Base64.getEncoder().encodeToString(combined);
        }

        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String decryptNote(String encryptedNote){
        try{
            //instancja cipher
            Cipher cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);

            //pobierz iv+notatake do tablicy
            byte[] combined = Base64.getDecoder().decode(encryptedNote);

            // Wyodrębnij IV
            byte[] iv = new byte[16];
            System.arraycopy(combined, 0, iv, 0, iv.length);

            // Wyodrębnij zaszyfrowaną notatkę
            byte[] encryptedBytes = new byte[combined.length - iv.length];
            System.arraycopy(combined, iv.length, encryptedBytes, 0, encryptedBytes.length);

            //załaduj secretKey
            SecretKey secretKey = loadFromKeystore();

            //poskladaj ciphera
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));

            // Odszyfruj notatkę
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            System.out.println(decryptedBytes);
            return new String(decryptedBytes);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private static SecretKey loadFromKeystore() {
        try {
            // Load the Android Keystore
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);

            // Load the SecretKey from the Keystore
            return (SecretKey) keyStore.getKey(KEY_ALIAS, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean verifyPassword(String userInput, String hashedPassword, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // hashuj haslo podane
        String hashedUserInput = hashPassword(userInput, salt);

        // porownaj z hashem z sharedpreferecnes
        return hashedPassword.equals(hashedUserInput);
    }

    public static byte[] generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return salt;
    }
    public static String hashPassword(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        int iterations = 10000;
        int keyLength = 256;

        //co bedziemy szyfrowac
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        //Hmac hash-based message authentication code
        //PBKDF2 Password-Based Key Derivation Function
        // popularne dla systemów mobilnych (IOS używa):
        //-potrzebuje mało mocy obliczeniowej - mało energii
        //-podatny na brute force
        //-trudno zaprogramować atak bruteforce na PBKDF2 na karcie graficznej
        byte[] hash = skf.generateSecret(spec).getEncoded();

        return Base64.getEncoder().encodeToString(hash);
    }
}
