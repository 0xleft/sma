package lt.pageup.sma.managers;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

public class KeyManager {

    private PrivateKey privateKey;

    public KeyManager() {
        PrivateKey privateKey = getPrivateKeyKeyStore();
        if (privateKey == null) {
            try {
                privateKey = addPrivateKey();
            } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException | KeyStoreException e) {
                e.printStackTrace();
            }
        }

        this.privateKey = privateKey;
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

        return newPrivateKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }
}
