package lt.pageup.sma.http;

import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import lt.pageup.sma.MainActivity;
import lt.pageup.sma.database.MessageDataSource;
import lt.pageup.sma.objects.Contact;
import lt.pageup.sma.objects.Message;

public class RequestMaker {

    private static final String BASE_URL = "https://pageup.lt/api/sma";

    public static int register(String phoneNumber, String secretString, String publicKey) {
        try {
            JSONStringer jsonStringer = new JSONStringer();
            jsonStringer.object();
            jsonStringer.key("phoneNumber").value(phoneNumber);
            jsonStringer.key("secretString").value(secretString);
            jsonStringer.key("publicKey").value(publicKey);
            jsonStringer.endObject();
            return syncPostRequest("/auth/register", jsonStringer.toString());
        } catch (IOException | NoSuchAlgorithmException | KeyManagementException | JSONException e) {
            e.printStackTrace();
            return 400;
        }
    }

    public static void sendMessage(String phoneNumber, String toPhoneNumber, String secretString, String message, MessageDataSource datasource) {
        Contact contact = MainActivity.getInstance().getContactManager().getContact(toPhoneNumber);

        if (contact == null) {
            return;
        }

        String encryptedMessage;
        try {
            encryptedMessage = contact.encryptMessage(message);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | NoSuchPaddingException e) {
            return;
        }

        JSONStringer jsonStringer = new JSONStringer();
        try {
            jsonStringer.object();
            jsonStringer.key("phoneNumber").value(phoneNumber);
            jsonStringer.key("to").value(toPhoneNumber);
            Log.w("BINGBONG", "sendMessage: " + secretString);
            jsonStringer.key("secretString").value(secretString);
            jsonStringer.key("message").value(encryptedMessage);
            jsonStringer.endObject();
        } catch (JSONException e) {
            return;
        }

        new Thread() {
            @Override
            public void run() {
                try {
                    int responseCode = syncPostRequest("/messages/send", jsonStringer.toString());
                    if (responseCode == 200) {
                        datasource.insertMessage(new Message(message, toPhoneNumber, true));
                    } else {
                        datasource.insertMessage(new Message("failed to send", toPhoneNumber, true));
                    }
                    MainActivity.getInstance().runOnUiThread(() -> MainActivity.getInstance().changeActivityMessage());
                } catch (IOException | NoSuchAlgorithmException | KeyManagementException |
                         JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public static @Nullable JSONObject getMessages(String phoneNumber, String secretString) {
        try {
            HashMap<String, String> headers = new HashMap<>();
            headers.put("secretString", secretString);
            return syncGetRequest("/messages", "phoneNumber=" + phoneNumber, headers);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * @return public key in base64
     */
    public static String getPublicKey(String phoneNumber) {
        try {
            return syncGetRequest("/auth/get_public_key", "phoneNumber=" + phoneNumber, null).getString("publicKey");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int syncPostRequest(String location, String data) throws IOException, NoSuchAlgorithmException, KeyManagementException, JSONException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable<Integer> callable = () -> postRequest(location, data);
        Future<Integer> futureResult = executor.submit(callable);
        int result = 400;
        try {
            result = futureResult.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        executor.shutdown();
        Log.e("BINGBONG", "asyncPostRequest: " + result);
        return result;
    }

    public static @Nullable JSONObject syncGetRequest(String location, String query, @Nullable HashMap<String, String> headers) throws IOException, JSONException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable<JSONObject> callable = () -> getRequest(location, query, headers);
        Future<JSONObject> futureResult = executor.submit(callable);
        JSONObject result = null;
        try {
            result = futureResult.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        executor.shutdown();
        return result;
    }

    public static int postRequest(String location, String data) throws IOException {
        URL url = new URL(BASE_URL + location);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setConnectTimeout(5000);
        connection.setRequestProperty("Content-Type", "application/json");

        try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
            wr.writeBytes(data);
            wr.flush();
        }

        return connection.getResponseCode();
    }

    public static @NotNull JSONObject getRequest(String location, String query, @Nullable HashMap<String, String> headers) throws IOException, JSONException {
        URL url = new URL(BASE_URL + location + "?" + query);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setRequestProperty("Content-Type", "application/json");

        if (headers != null) {
            for (String key : headers.keySet()) {
                connection.setRequestProperty(key, headers.get(key));
            }
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return new JSONObject(response.toString());
    }
}
