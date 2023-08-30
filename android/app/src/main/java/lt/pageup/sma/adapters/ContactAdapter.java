package lt.pageup.sma.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import lt.pageup.sma.MainActivity;
import lt.pageup.sma.R;
import lt.pageup.sma.objects.Contact;

public class ContactAdapter extends ArrayAdapter<Contact> {

    public ContactAdapter(Context context, List<Contact> contacts) {
        super(context, 0, contacts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.contact, parent, false);
        }

        Contact contact = getItem(position);

        TextView nameTextView = convertView.findViewById(R.id.contact_name);
        TextView phoneTextView = convertView.findViewById(R.id.contact_phone);

        nameTextView.setText(contact.getName());
        phoneTextView.setText(contact.getPhoneNumber());

        convertView.setOnClickListener(v -> {
            MainActivity.getInstance().setPhoneNumber(contact.getPhoneNumber());
            MainActivity.getInstance().changeActivityMessage();
        });

        return convertView;
    }
}
