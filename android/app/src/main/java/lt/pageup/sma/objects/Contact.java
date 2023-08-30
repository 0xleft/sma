package lt.pageup.sma.objects;

import org.jetbrains.annotations.NotNull;

import java.security.PublicKey;

import lt.pageup.sma.utils.KeyUtils;

public class Contact {
    private String phoneNumber;
    private PublicKey publicKey;

    public Contact(@NotNull String phoneNumber, @NotNull String publicKeyBytesBase64) {

        PublicKey publicKey = KeyUtils.getPublicKeyFromBytesBase64(publicKeyBytesBase64);
        if (publicKey == null) {
            throw new IllegalArgumentException("Invalid public key");
        }

        this.phoneNumber = phoneNumber;
        this.publicKey = publicKey;
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
}