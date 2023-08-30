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
        // encrypt message
        // send message
        // save message to database
        dataSource.insertMessage(new Message(message, phoneNumber, true));
    }

    public void receiveMessage(String phoneNumber, String message) {
        // decrypt message
        // save message to database
        dataSource.insertMessage(new Message(message, phoneNumber, false));
    }

    public List<Message> getMessagesForContact(String phoneNumber) {
        return dataSource.getContactMessages(phoneNumber);
    }
}