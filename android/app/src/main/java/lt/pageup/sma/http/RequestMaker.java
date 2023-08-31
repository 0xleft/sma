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
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
            return asyncPostRequest("/auth/register", jsonStringer.toString());
        } catch (IOException | NoSuchAlgorithmException | KeyManagementException | JSONException e) {
            e.printStackTrace();
            return 400;
        }
    }

    public static int sendMessage(String phoneNumber, String toPhoneNumber, String secretString, String encryptedMessage) {
        try {
            JSONStringer jsonStringer = new JSONStringer();
            jsonStringer.object();
            jsonStringer.key("phoneNumber").value(phoneNumber);
            jsonStringer.key("to").value(toPhoneNumber);
            jsonStringer.key("secretString").value(secretString);
            jsonStringer.key("message").value(encryptedMessage);
            jsonStringer.endObject();
            return asyncPostRequest("/messages/send", jsonStringer.toString());
        } catch (IOException | NoSuchAlgorithmException | KeyManagementException | JSONException e) {
            e.printStackTrace();
            return 400;
        }
    }

    public static @Nullable JSONObject getMessages(String phoneNumber, String secretString) {
        try {
            return asyncGetRequest("/messages", "phoneNumber=" + phoneNumber + "&secretString=" + secretString);
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
            return asyncGetRequest("/auth/get_public_key", "phoneNumber=" + phoneNumber).getString("publicKey");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int asyncPostRequest(String location, String data) throws IOException, NoSuchAlgorithmException, KeyManagementException, JSONException {
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

    public static @Nullable JSONObject asyncGetRequest(String location, String query) throws IOException, JSONException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable<JSONObject> callable = () -> getRequest(location, query);
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

    public static @NotNull JSONObject getRequest(String location, String query) throws IOException, JSONException {
        URL url = new URL(BASE_URL + location + "?" + query);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setRequestProperty("Content-Type", "application/json");

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
