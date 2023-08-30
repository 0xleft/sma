package lt.pageup.sma.http;

public class RequestMaker {

    public static boolean register(String phoneNumber, String secretString, String publicKey) {

        // /auth/register
        // TODO make request to api to register user
        return true;
    }

    public static boolean sendMessage(String phoneNumber, String toPhoneNumber, String secretString, String encryptedMessage) {
        // /messages/send
        // TODO make request to api to send message to user
        return true;
    }

    public static boolean getMessages(String phoneNumber, String secretString) {
        // /messages
        // TODO make request to api to get message of a user
        return true;
    }

    public static boolean getPublicKey(String phoneNumber) {
        // /auth/get_public_key
        // TODO make request to api to get public key of a user
        return true;
    }
}
