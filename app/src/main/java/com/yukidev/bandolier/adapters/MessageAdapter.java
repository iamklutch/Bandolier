package com.yukidev.bandolier.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;
import com.yukidev.bandolier.R;
import com.yukidev.bandolier.utils.ParseConstants;

import java.util.Date;
import java.util.List;

/**
 * Created by James on 5/14/2015.
 */
public class MessageAdapter extends ArrayAdapter<ParseObject> implements Filterable {

    protected Context mContext;
    protected List<ParseObject> mMessages;

    public MessageAdapter(Context context, List<ParseObject> messages) {
        super(context, R.layout.message_item,messages );

        mContext = context;
        mMessages = messages;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        // the if statement recycles the view (like in recyclerview)
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.message_item, null);
            holder = new ViewHolder();
            holder.iconImageView = (ImageView) convertView.findViewById(R.id.messageIcon);
            holder.nameLabel = (TextView) convertView.findViewById(R.id.senderLabel);
            holder.timeLabel = (TextView) convertView.findViewById(R.id.timeLabel);
            holder.bulletTitleLabel = (TextView) convertView.findViewById(R.id.bulletTitleLabel);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder)convertView.getTag();
        }
        ParseObject message = mMessages.get(position);

        Date createdAt = message.getCreatedAt();
        long now = new Date().getTime();
        String convertedDate = DateUtils.getRelativeTimeSpanString(createdAt.
                getTime(), now, DateUtils.SECOND_IN_MILLIS).toString();

        holder.timeLabel.setText(convertedDate);
        if (message.get(ParseConstants.KEY_MESSAGE_TYPE).equals(ParseConstants.MESSAGE_TYPE_BULLET)){
            holder.iconImageView.setImageResource(R.drawable.bullets);
        } else {
            holder.iconImageView.setImageResource(R.drawable.ic_action_add_group);
        }

        holder.nameLabel.setText(message.getString(ParseConstants.KEY_SENDER_NAME));
        holder.bulletTitleLabel.setText(message.getString(ParseConstants.KEY_BULLET_TITLE));
        return convertView;
    }
    public static class ViewHolder {
        ImageView iconImageView;
        TextView nameLabel;
        TextView timeLabel;
        TextView bulletTitleLabel;
    }

    public void refill(List<ParseObject> messages) {
        mMessages.clear();
        mMessages.addAll(messages);
        notifyDataSetChanged();
    }
}
