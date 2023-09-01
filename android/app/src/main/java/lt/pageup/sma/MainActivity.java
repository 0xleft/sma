package lt.pageup.sma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.security.Permission;

import lt.pageup.sma.adapters.ContactAdapter;
import lt.pageup.sma.adapters.MessageAdapter;
import lt.pageup.sma.managers.ContactManager;
import lt.pageup.sma.managers.KeyManager;
import lt.pageup.sma.managers.MessageManager;
import lt.pageup.sma.utils.MessageChecker;

public class MainActivity extends AppCompatActivity {

    private ContactManager contactManager;
    private KeyManager keyManager;
    private MessageManager messageManager;
    private String phoneNumber;
    private String myPhoneNumber;

    private static MainActivity instance;

    public static MainActivity getInstance() {
        return instance;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 1);
        }
        changeRegisterScreen();
    }

    public String getMyPhoneNumber() {
        return myPhoneNumber;
    }

    public ContactManager getContactManager() {
        return contactManager;
    }

    public KeyManager getKeyManager() {
        return keyManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public void changeRegisterScreen() {
        setContentView(R.layout.register_activity);

        if (!getSharedPreferences("sma", MODE_PRIVATE).getString("username", "").isEmpty()) {
            this.myPhoneNumber = getSharedPreferences("sma", MODE_PRIVATE).getString("username", "");
            this.keyManager = new KeyManager(getSharedPreferences("sma", MODE_PRIVATE));
            this.contactManager = new ContactManager(getBaseContext());
            this.messageManager = new MessageManager(getBaseContext());

            changeActivityContactList();
            new MessageChecker();
            return;
        }

        findViewById(R.id.register_button).setOnClickListener(v -> {
            String username = ((android.widget.EditText) findViewById(R.id.username)).getText().toString();

            if (username.isEmpty()) {
                return;
            }


            this.myPhoneNumber = username;
            this.keyManager = new KeyManager(getSharedPreferences("sma", MODE_PRIVATE));
            if (this.keyManager.submitPublicKey() == 200) {
                getSharedPreferences("sma", MODE_PRIVATE).edit().putString("username", username).apply();
            }
            changeRegisterScreen();
        });
    }

    public void changeActivityAddContact() {
        setContentView(R.layout.add_contact_activity);

        findViewById(R.id.cancel_button).setOnClickListener(v -> changeActivityContactList());
        findViewById(R.id.add_button).setOnClickListener(v -> {
            String name = ((android.widget.EditText) findViewById(R.id.name)).getText().toString();
            String phoneNumber = ((android.widget.EditText) findViewById(R.id.phone)).getText().toString();

            contactManager.addContact(name, phoneNumber, getBaseContext());
            changeActivityContactList();
        });
    }

    public void changeActivityContactList() {
        setContentView(R.layout.contact_list_activity);

        // my phone
        ((TextView) findViewById(R.id.my_phone)).setText(myPhoneNumber);
        findViewById(R.id.my_phone).setOnClickListener(v -> {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", myPhoneNumber);
            clipboard.setPrimaryClip(clip);

            android.widget.Toast.makeText(this, "Copied to clipboard", android.widget.Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.add_contact_button).setOnClickListener(v -> changeActivityAddContact());

        ContactAdapter contactViews = new ContactAdapter(this, contactManager.getContacts());
        ListView contactList = findViewById(R.id.contact_list);
        contactList.setAdapter(contactViews);
    }

    public void changeActivityMessage() {
        setContentView(R.layout.message_activity);
        EditText messageContent = findViewById(R.id.message_content);
        messageContent.setHint("Message");

        // send button
        findViewById(R.id.send_button).setOnClickListener(v -> {
            String message = messageContent.getText().toString();
            messageManager.sendMessage(phoneNumber, message);

            messageContent.setText("");
            messageContent.setHint("Message sent");

            changeActivityMessage();
        });

        // display name
        ((TextView) findViewById(R.id.display_name)).setText(contactManager.getContactName(phoneNumber));

        // display messages
        MessageAdapter messageViews = new MessageAdapter(this, messageManager.getMessagesForContact(phoneNumber));
        ListView messageList = findViewById(R.id.message_list);
        messageList.setAdapter(messageViews);

        // back button
        findViewById(R.id.back_button).setOnClickListener(v -> changeActivityContactList());
    }
}