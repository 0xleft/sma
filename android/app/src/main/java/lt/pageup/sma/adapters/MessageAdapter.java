package lt.pageup.sma.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import lt.pageup.sma.R;
import lt.pageup.sma.objects.Message;

public class MessageAdapter extends ArrayAdapter<Message> {

    public MessageAdapter(Context context, List<Message> messages) {
        super(context, 0, messages);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message message = getItem(position);

        if (convertView == null) {

            if (message.isSelf()) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_out, parent, false);
            } else {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_inc, parent, false);
            }
        }

        ((TextView) convertView.findViewById(R.id.message_content)).setText(message.getPhoneNumber());

        return convertView;
    }
}
