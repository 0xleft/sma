package lt.pageup.sma.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import lt.pageup.sma.MainActivity;
import lt.pageup.sma.http.RequestMaker;
import lt.pageup.sma.objects.Message;

public class MessageChecker {

    public MessageChecker() {
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(5000);
                        Log.w("BINGBONG", "run: " + MainActivity.getInstance().getMyPhoneNumber());
                        JSONObject messages = RequestMaker.getMessages(MainActivity.getInstance().getMyPhoneNumber(), MainActivity.getInstance().getKeyManager().getSecretString());
                        if (messages == null) {
                            continue;
                        }
                        JSONArray messagesArray = messages.getJSONArray("messages");
                        Log.w("BINGBONG", "" + messagesArray.toString());

                        for (int i = 0; i < messagesArray.length(); i++) {
                            JSONObject message = messagesArray.getJSONObject(i);
                            String from = message.getString("from");
                            String messageText = message.getString("message");
                            MainActivity.getInstance().getMessageManager().receiveMessage(from, messageText);
                        }
                    } catch (InterruptedException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
}
