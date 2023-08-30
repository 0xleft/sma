package lt.pageup.sma.managers;

import android.content.Context;

import org.jetbrains.annotations.NotNull;

import java.util.List;


import lt.pageup.sma.database.ContactDataSource;
import lt.pageup.sma.objects.Contact;

public class ContactManager {
    // for every contact there is a name and public key which we use to encrypt messages
    // secret key is only one and is used to decrypt messages
    // we store public keys in sql database with all contacts and for private key we    // use shared preferences

    private List<Contact> contacts;

    public ContactManager(@NotNull Context context) {
        ContactDataSource dataSource = new ContactDataSource(context);
        dataSource.open();
        contacts = dataSource.getAllContacts();
        dataSource.close();
    }

    public void addContact(String phoneNumber) {
        // make request to api to get public key
    }

    public void removeContact(String phoneNumber) {
        // remove contact from database
    }

    public Contact getContact(String phoneNumber) {
        // get contact from database
        return null;
    }

    public void updateContact(String phoneNumber) {
        // make request to api to get public key
    }
}