package lt.pageup.sma.managers;

import android.content.Context;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.List;


import lt.pageup.sma.database.ContactDataSource;
import lt.pageup.sma.http.RequestMaker;
import lt.pageup.sma.objects.Contact;
import lt.pageup.sma.utils.KeyUtils;

public class ContactManager {
    // for every contact there is a name and public key which we use to encrypt messages
    // secret key is only one and is used to decrypt messages
    // we store public keys in sql database with all contacts and for private key we    // use shared preferences

    private List<Contact> contacts;
    private ContactDataSource dataSource;

    public ContactManager(@NotNull Context context) {
        dataSource = new ContactDataSource(context);
        dataSource.open();
        contacts = dataSource.getAllContacts();
        dataSource.close();
    }

    public void addContact(String name, String phoneNumber) {

        String publicKey = RequestMaker.getPublicKey(phoneNumber);

        if (publicKey == null) {
            return;
        }

        Log.e("BINGBONG", "addContact: " + publicKey);

        PublicKey key = KeyUtils.getPublicKeyFromBytesBase64(publicKey);

        if (key == null) {
            return;
        }

        Log.e("BINGBONG", "addContact: " + key.toString());

        dataSource.open();
        dataSource.insertContact(new Contact(name, phoneNumber, publicKey));
        contacts = dataSource.getAllContacts();
        dataSource.close();
    }

    public PublicKey getPublicKey(String phoneNumber) {
        dataSource.open();
        PublicKey publicKey = dataSource.getContactPublicKey(phoneNumber);
        dataSource.close();
        return publicKey;
    }

    public Contact getContact(String phoneNumber) {
        dataSource.open();
        Contact contact = dataSource.getContact(phoneNumber);
        dataSource.close();
        return contact;
    }

    public void removeContact(String phoneNumber) {
        // remove contact from database
    }

    public String getContactName(String phoneNumber) {
        dataSource.open();
        String name = dataSource.getContactName(phoneNumber);
        dataSource.close();
        return name;
    }

    public List<Contact> getContacts() {
        return contacts;
    }
}