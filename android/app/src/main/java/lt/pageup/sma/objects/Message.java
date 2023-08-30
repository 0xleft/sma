package lt.pageup.sma.objects;

public class Message {

    private String message;
    private String phoneNumber;
    private boolean self;

    public Message(String message, String phoneNumber, boolean self) {
        this.message = message;
        this.phoneNumber = phoneNumber;
        this.self = self;
    }

    public boolean isSelf() {
        return self;
    }

    public String getMessage() {
        return message;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}