package lt.pageup.sma.managers;

import android.content.Context;

import org.jetbrains.annotations.NotNull;

import java.util.List;


import lt.pageup.sma.database.MessageDataSource;
import lt.pageup.sma.objects.Message;

public class MessageManager {
    // for every contact there is a name and public key which we use to encrypt messages
    // secret key is only one and is used to decrypt messages
    // we store public keys in sql database with all contacts and for private key we    // use shared preferences

    private List<Message> messages;

    public MessageManager(@NotNull Context context) {
        MessageDataSource dataSource = new MessageDataSource(context);
        dataSource.open();
        messages = dataSource.getAllMessages();
        dataSource.close();
    }

    public void sendMessage(String phoneNumber, String message) {
        // make request to api to get public key
    }

    public List<Message> getMessagesForContact(String phoneNumber) {
        // get messages from database
        return null;
    }
}