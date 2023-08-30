package lt.pageup.sma.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import lt.pageup.sma.objects.Contact;

public class ContactDataSource {
    private SQLiteDatabase database;
    private ContactDatabaseHelper dbHelper;

    public ContactDataSource(Context context) {
        dbHelper = new ContactDatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void insertContact(@NotNull Contact contact) {
        ContentValues values = new ContentValues();
        values.put(ContactDatabaseHelper.COLUMN_PHONE, contact.getPhoneNumber());
        values.put(ContactDatabaseHelper.COLUMN_PUBLIC_KEY, contact.getPublicKeyBytesBase64());
        database.insert(ContactDatabaseHelper.TABLE_CONTACTS, null, values);
    }

    public void removeContact(@NotNull Contact contact) {
        database.delete(ContactDatabaseHelper.TABLE_CONTACTS, ContactDatabaseHelper.COLUMN_PHONE + " = " + contact.getPhoneNumber(), null);
    }

    public List<Contact> getAllContacts() {
        List<Contact> contacts = new ArrayList<>();
        Cursor cursor = database.query(ContactDatabaseHelper.TABLE_CONTACTS,
                null, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Contact contact = cursorToContact(cursor);
            contacts.add(contact);
            cursor.moveToNext();
        }
        cursor.close();
        return contacts;
    }

    private @NotNull Contact cursorToContact(Cursor cursor) {
        @SuppressLint("Range") String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactDatabaseHelper.COLUMN_PHONE));
        @SuppressLint("Range") String publicKeyBytesBase64 = cursor.getString(cursor.getColumnIndex(ContactDatabaseHelper.COLUMN_PUBLIC_KEY));

        return new Contact(phoneNumber, publicKeyBytesBase64);
    }
}