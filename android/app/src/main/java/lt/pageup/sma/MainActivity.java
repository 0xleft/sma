package lt.pageup.sma;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.security.Key;
import java.security.PrivateKey;

import lt.pageup.sma.managers.ContactManager;
import lt.pageup.sma.managers.KeyManager;
import lt.pageup.sma.managers.MessageManager;

public class MainActivity extends AppCompatActivity {

    private PrivateKey privateKey;
    private ContactManager contactManager;
    private KeyManager keyManager;
    private MessageManager messageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.keyManager = new KeyManager(getSharedPreferences("sma", MODE_PRIVATE));
        this.contactManager = new ContactManager(getBaseContext());
        this.messageManager = new MessageManager(getBaseContext());

        setContentView(R.layout.contact_list);
    }
}