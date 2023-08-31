package lt.pageup.sma.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.telephony.TelephonyManager;
import android.util.Log;

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
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import lt.pageup.sma.MainActivity;
import lt.pageup.sma.http.RequestMaker;
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
            Log.e("BINGBONG", "KeyManager: Private key is null");
            try {
                privateKey = addPrivateKey();
            } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException | KeyStoreException e) {
                e.printStackTrace();
            }
        }

        if (privateKey == null) {
            throw new IllegalStateException("Private key is null");
        }

        this.privateKey = privateKey;
    }

    public @Nullable String generateSecretString() {

        String secretString = sharedPreferences.getString("secretString", "");
        if (secretString.isEmpty()) {
            Log.e("BINGBONG", "generateSecretString: secretString is empty");
            secretString = KeyUtils.generateRandomString(10000);
            sharedPreferences.edit().putString("secretString", secretString).apply();
        }

        return secretString;
    }

    public @Nullable PrivateKey getPrivateKeyKeyStore() {
        String encodedPrivateKey = sharedPreferences.getString("privateKey", "");
        if (encodedPrivateKey.isEmpty()) {
            return null;
        }

        try {
            return KeyUtils.getPrivateKeyFromBytesBase64(encodedPrivateKey);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }

    public @Nullable PrivateKey addPrivateKey() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, KeyStoreException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(4096);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        PrivateKey newPrivateKey = keyPair.getPrivate();

        String newPrivateKeyBase64 = android.util.Base64.encodeToString(newPrivateKey.getEncoded(), android.util.Base64.DEFAULT);

        sharedPreferences.edit().putString("privateKey", newPrivateKeyBase64).apply();

        PublicKey publicKey = keyPair.getPublic();
        String publicKeyBase64 = android.util.Base64.encodeToString(publicKey.getEncoded(), android.util.Base64.DEFAULT);
        Log.w("BINGBONG",String.valueOf(RequestMaker.register("BOGIJ", secretString, publicKeyBase64)));

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
