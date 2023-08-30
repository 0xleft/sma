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

import lt.pageup.sma.objects.Message;

public class MessageDataSource {
    private SQLiteDatabase database;
    private MessageDatabaseHelper dbHelper;

    public MessageDataSource(Context context) {
        dbHelper = new MessageDatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void insertMessage(@NotNull Message message) {
        ContentValues values = new ContentValues();
        values.put(MessageDatabaseHelper.COLUMN_PHONE, message.getPhoneNumber());
        values.put(MessageDatabaseHelper.COLUMN_MESSAGE, message.getMessage());
        database.insert(MessageDatabaseHelper.TABLE_MESSAGES, null, values);
    }

    public void dropMessages() {
        database.delete(MessageDatabaseHelper.TABLE_MESSAGES, null, null);
    }

    public List<Message> getAllMessages() {
        List<Message> messages = new ArrayList<>();
        Cursor cursor = database.query(MessageDatabaseHelper.TABLE_MESSAGES,
                null, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Message message = cursorToMessage(cursor);
            messages.add(message);
            cursor.moveToNext();
        }
        cursor.close();
        return messages;
    }

    private @NotNull Message cursorToMessage(Cursor cursor) {
        @SuppressLint("Range") String phoneNumber = cursor.getString(cursor.getColumnIndex(MessageDatabaseHelper.COLUMN_PHONE));
        @SuppressLint("Range") String message = cursor.getString(cursor.getColumnIndex(MessageDatabaseHelper.COLUMN_MESSAGE));

        return new Message(phoneNumber, message);
    }
}