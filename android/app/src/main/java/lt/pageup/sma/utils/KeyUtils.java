package lt.pageup.sma.utils;

import android.os.Build;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class KeyUtils {

    public static String generateRandomString(int length) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[length];
        secureRandom.nextBytes(randomBytes);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        } else {
            return android.util.Base64.encodeToString(randomBytes, android.util.Base64.URL_SAFE | android.util.Base64.NO_PADDING | android.util.Base64.NO_WRAP);
        }
    }

    public static String decryptMessage(@NotNull String message, @NotNull PrivateKey privateKey) throws NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        byte[] encryptedBytes = android.util.Base64.decode(message, android.util.Base64.DEFAULT);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        return new String(decryptedBytes);
    }

    public static @Nullable PublicKey getPublicKeyFromBytesBase64(@NotNull String publicKeyBytesBase64) {
        byte[] publicKeyBytes;

        try {
            publicKeyBytes = android.util.Base64.decode(publicKeyBytesBase64, android.util.Base64.DEFAULT);
        } catch (IllegalArgumentException e) {
            return null;
        }


        // Convert bytes to public key
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);

        KeyFactory keyFactory;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException ignored) {return null; /* this can only happen if RSA is not supported*/}

        try {
            return keyFactory.generatePublic(keySpec);
        } catch (Exception ignored) {return null; /* this can only happen if keySpec is invalid*/}
    }

    public static @NotNull PrivateKey getPrivateKeyFromBytesBase64(@NotNull String privateKeyBytesBase64) throws InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] publicKeyBytes = android.util.Base64.decode(privateKeyBytesBase64, android.util.Base64.DEFAULT);

        // Convert bytes to public key
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(publicKeyBytes);

        KeyFactory keyFactory;
        keyFactory = KeyFactory.getInstance("RSA");

        return keyFactory.generatePrivate(keySpec);
    }
}