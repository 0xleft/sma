package lt.pageup.sma.http;

import java.io.DataOutputStream;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import lt.pageup.sma.objects.Message;

public class RequestMaker {

    //private static final String BASE_URL = "https://pageup.lt/api/sma";
    private static final String BASE_URL = "http://localhost:5000";

    public static boolean register(String phoneNumber, String secretString, String publicKey) {
        return postRequest("/auth/register", "{\"phoneNumber\":\"" + phoneNumber + "\",\"secretString\":\"" + secretString + "\",\"publicKey\":\"" + publicKey + "\"}");
    }

    public static boolean sendMessage(String phoneNumber, String toPhoneNumber, String secretString, String encryptedMessage) {
        return postRequest("/messages/send", "{\"phoneNumber\":\"" + phoneNumber + "\",\"secretString\":\"" + secretString + "\",\"to\":\"" + toPhoneNumber + "\",\"message\":\"" + encryptedMessage + "\"}");
    }

    public static List<Message> getMessages(String phoneNumber, String secretString) {
    }

    public static String getPublicKey(String phoneNumber) {

    }

    public static boolean postRequest(String location, String data) {
        URL url = new URL(BASE_URL + location);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, null, new java.security.SecureRandom());
        connection.setSSLSocketFactory(sslContext.getSocketFactory());


        connection.setRequestMethod("POST");
        connection.setConnectTimeout(5000);
        connection.setRequestProperty("User-Agent", "sma user agent");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        // Write JSON data to the request
        try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
            wr.writeBytes(data);
            wr.flush();
        }

        int responseCode = connection.getResponseCode();

        return responseCode == 200;
    }
}
