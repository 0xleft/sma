package lt.pageup.sma.http;

import android.annotation.SuppressLint;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class RequestMaker {

    //private static final String BASE_URL = "https://pageup.lt/api/sma";
    private static final String BASE_URL = "http://127.0.0.1:5000";

    public static @Nullable JSONObject register(String phoneNumber, String secretString, String publicKey) {
        try {
            return postRequest("/auth/register", "{\"phoneNumber\":\"" + phoneNumber + "\",\"secretString\":\"" + secretString + "\",\"publicKey\":\"" + publicKey + "\"}");
        } catch (IOException | NoSuchAlgorithmException | KeyManagementException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static @Nullable JSONObject sendMessage(String phoneNumber, String toPhoneNumber, String secretString, String encryptedMessage) {
        try {
            return postRequest("/messages/send", "{\"phoneNumber\":\"" + phoneNumber + "\",\"secretString\":\"" + secretString + "\",\"to\":\"" + toPhoneNumber + "\",\"message\":\"" + encryptedMessage + "\"}");
        } catch (IOException | NoSuchAlgorithmException | KeyManagementException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static @Nullable JSONObject getMessages(String phoneNumber, String secretString) {
        try {
            return postRequest("/messages", "{\"phoneNumber\":\"" + phoneNumber + "\",\"secretString\":\"" + secretString + "\"}");
        } catch (IOException | NoSuchAlgorithmException | KeyManagementException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * @return public key in base64
     */
    public static String getPublicKey(String phoneNumber) {
        try {
            return postRequest("/auth/get_public_key", "{\"phoneNumber\":\"" + phoneNumber + "\"}").getString("publicKey");
        } catch (IOException | NoSuchAlgorithmException | KeyManagementException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static @NotNull JSONObject postRequest(String location, String data) throws IOException, NoSuchAlgorithmException, KeyManagementException, JSONException {
        @SuppressLint("CustomX509TrustManager") TrustManager[] trustAllCertificates = new TrustManager[] {
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
        };

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCertificates, new java.security.SecureRandom());

        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

        URL url = new URL(BASE_URL + location);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setConnectTimeout(5000);
        connection.setRequestProperty("User-Agent", "sma user agent");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
            wr.writeBytes(data);
            wr.flush();
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
