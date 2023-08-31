package lt.pageup.sma.objects;

import org.jetbrains.annotations.NotNull;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import lt.pageup.sma.utils.KeyUtils;

public class Contact {
    private String phoneNumber;
    private PublicKey publicKey;
    private String name;

    public Contact(@NotNull String name, @NotNull String phoneNumber, @NotNull String publicKeyBytesBase64) {
        this.name = name;

        PublicKey publicKey = KeyUtils.getPublicKeyFromBytesBase64(publicKeyBytesBase64);

        this.phoneNumber = phoneNumber;
        this.publicKey = publicKey;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public String getPublicKeyBytesBase64() {
        return android.util.Base64.encodeToString(publicKey.getEncoded(), android.util.Base64.DEFAULT);
    }

    public String encryptMessage(@NotNull String message) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, this.publicKey);
        byte[] encryptedBytes = cipher.doFinal(message.getBytes());

        // Convert encrypted bytes to Base64-encoded string
        return android.util.Base64.encodeToString(encryptedBytes, android.util.Base64.DEFAULT);
    }
}