package lt.pageup.sma.managers;

import android.content.Context;

import org.jetbrains.annotations.NotNull;

import java.util.List;


import lt.pageup.sma.database.MessageDataSource;
import lt.pageup.sma.objects.Message;

public class MessageManager {

    private List<Message> messages;
    private MessageDataSource dataSource;

    public MessageManager(@NotNull Context context) {
        dataSource = new MessageDataSource(context);
        dataSource.open();
    }

    public void sendMessage(String phoneNumber, String message) {
        // make request to api to get public key
    }

    public List<Message> getMessagesForContact(String phoneNumber) {
        // get messages from database
        return null;
    }
}