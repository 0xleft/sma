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
    private ContactDataSource dataSource;

    public ContactManager(@NotNull Context context) {
        dataSource = new ContactDataSource(context);
        dataSource.open();
        contacts = dataSource.getAllContacts();
        dataSource.close();
    }

    public void addContact(String name, String phoneNumber) {
        // TODO make request to api to get public key
        dataSource.open();
        dataSource.insertContact(new Contact(name, phoneNumber, ""));
        contacts = dataSource.getAllContacts();
        dataSource.close();
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

    public void updateContact(String phoneNumber) {
        // make request to api to get public key
    }

    public List<Contact> getContacts() {
        return contacts;
    }
}