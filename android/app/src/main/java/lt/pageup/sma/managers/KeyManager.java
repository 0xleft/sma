package lt.pageup.sma.managers;

import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import lt.pageup.sma.utils.KeyUtils;

public class KeyManager {

    private PrivateKey privateKey;
    private String secretString;
    private SharedPreferences sharedPreferences;

    public KeyManager(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;

        this.secretString = generateSecretString();

        PrivateKey privateKey = getPrivateKeyKeyStore();
        if (privateKey == null) {
            try {
                privateKey = addPrivateKey();
            } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException | KeyStoreException e) {
                e.printStackTrace();
            }
        }

        this.privateKey = privateKey;

        // get the secret string from the local storage
    }

    public @Nullable String generateSecretString() {

        String secretString = sharedPreferences.getString("secretString", "");
        if (secretString.isEmpty()) {
            secretString = KeyUtils.generateRandomString(10000);
            sharedPreferences.edit().putString("secretString", secretString).apply();
        }

        return secretString;
    }

    public @Nullable PrivateKey getPrivateKeyKeyStore() {
            KeyStore keyStore;
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (KeyStoreException e) {return null;}

        // init keystore
        try {
            keyStore.load(null);
        } catch (CertificateException | NoSuchAlgorithmException | IOException e) {return null;}

        try {
            return (PrivateKey) keyStore.getKey("smalocal", "8423u86v543u629uu!%$#V(%$^!YU95t1u".toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {return null;}
    }

    public @Nullable PrivateKey addPrivateKey() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, KeyStoreException {
        KeyStore keyStore;
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (KeyStoreException e) {return null;}

        // init keystore
        try {
            keyStore.load(null);
        } catch (CertificateException | NoSuchAlgorithmException | IOException e) {return null;}

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore");
        keyPairGenerator.initialize(new KeyGenParameterSpec.Builder(
                "smalocal",
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                .build());
        KeyPair newKeyPair = keyPairGenerator.generateKeyPair();

        PrivateKey newPrivateKey = newKeyPair.getPrivate();

        // save private key
        keyStore.setKeyEntry("smalocal", newPrivateKey, "8423u86v543u629uu!%$#V(%$^!YU95t1u".toCharArray(), new Certificate[0]);

        // TODO
        // send the public key to the server with the current phone number and secretString to register the user

        return newPrivateKey;
    }

    public String getSecretString() {
        return secretString;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public String decryptMessage(@NotNull String message) throws IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        // Decrypt the encrypted bytes using the private key
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = cipher.doFinal(message.getBytes());

        return new String(decryptedBytes);
    }
}
