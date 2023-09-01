package lt.pageup.sma.managers;

import android.content.Context;

import org.jetbrains.annotations.NotNull;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;


import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import lt.pageup.sma.MainActivity;
import lt.pageup.sma.database.MessageDataSource;
import lt.pageup.sma.http.RequestMaker;
import lt.pageup.sma.objects.Contact;
import lt.pageup.sma.objects.Message;
import lt.pageup.sma.utils.KeyUtils;

public class MessageManager {

    private List<Message> messages;
    private MessageDataSource dataSource;

    public MessageManager(@NotNull Context context) {
        dataSource = new MessageDataSource(context);
        dataSource.open();
    }

    public void sendMessage(String phoneNumber, String message) {
        if (message.isEmpty()) {
            return;
        }

        if (phoneNumber.isEmpty()) {
            return;
        }

        Contact contact = MainActivity.getInstance().getContactManager().getContact(phoneNumber);

        if (contact == null) {
            return;
        }

        String encryptedMessage;
        try {
            encryptedMessage = contact.encryptMessage(message);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException |
                 NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }

        if (200 == RequestMaker.sendMessage(phoneNumber, phoneNumber, MainActivity.getInstance().getKeyManager().getSecretString(), encryptedMessage))
            dataSource.insertMessage(new Message(message, phoneNumber, true));
    }

    public void receiveMessage(String phoneNumber, String message) {
        // decrypt message
        PrivateKey privateKey = MainActivity.getInstance().getKeyManager().getPrivateKey();
        String decryptedMessage;
        try {
            decryptedMessage = KeyUtils.decryptMessage(message, privateKey);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException |
                 NoSuchAlgorithmException | NoSuchPaddingException e) {
            decryptedMessage = "Could not decrypt message";
        }

        // if contact is not in database, add it
        Contact contact = MainActivity.getInstance().getContactManager().getContact(phoneNumber);
        if (contact == null) {
            MainActivity.getInstance().getContactManager().addContact(phoneNumber, phoneNumber);
        }

        dataSource.insertMessage(new Message(decryptedMessage, phoneNumber, false));
    }

    public List<Message> getMessagesForContact(String phoneNumber) {
        return dataSource.getContactMessages(phoneNumber);
    }
}