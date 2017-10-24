package com.example.jason.multichatapp.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jason.multichatapp.R;
import com.example.jason.multichatapp.Utils.DateTimeUtils;
import com.example.jason.multichatapp.Utils.Utils;
import com.example.jason.multichatapp.models.ChatMessage;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

    private List<ChatMessage> mChatMessages;

    private Context mContext;
    private ViewHolder mViewHolder;

    private ItemClickListener mClickListener;

    public interface ItemClickListener {
        void onItemClicked(View v, ChatMessage message);
    }

    public MessagesAdapter(Context context, List<ChatMessage> messages, ItemClickListener listener) {
        mChatMessages = messages;
        mContext = context;
        mClickListener = listener;
    }

    private Context getContext() {
        return mContext;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvMessage;
        public TextView tvUserName;
        public TextView tvTimeAgo;
        public TextView tvMyMessage;
        public TextView tvMyUserName;
        public TextView tvMyTimeAgo;
        public RelativeLayout rlMessage;
        public RelativeLayout rlMyMessage;
        public TextView tvOriginalMessage;
        public TextView tvMyOriginalMessage;

        public ViewHolder(View itemView) {
            super(itemView);

            tvMessage = (TextView) itemView.findViewById(R.id.tvMessage);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvTimeAgo = (TextView) itemView.findViewById(R.id.tvTimeAgo);
            tvMyMessage = (TextView) itemView.findViewById(R.id.tvMyMessage);
            tvMyUserName = (TextView) itemView.findViewById(R.id.tvMyUserName);
            tvMyTimeAgo = (TextView) itemView.findViewById(R.id.tvMyTimeAgo);
            rlMessage = (RelativeLayout) itemView.findViewById(R.id.rlMessage);
            rlMyMessage = (RelativeLayout) itemView.findViewById(R.id.rlMyMessage);
            tvOriginalMessage = (TextView) itemView.findViewById(R.id.tvOriginalMessage);
            tvMyOriginalMessage = (TextView) itemView.findViewById(R.id.tvMyOriginalMessage);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            ChatMessage message = mChatMessages.get(position);
            tvOriginalMessage.setVisibility(View.VISIBLE);
            tvMyOriginalMessage.setVisibility(View.VISIBLE);
            mClickListener.onItemClicked(v, message);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View messageView = inflater.inflate(R.layout.item_message, parent, false);

        // Return a new holder instance
        mViewHolder = new ViewHolder(messageView);
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get the data model based on position
        ChatMessage message = mChatMessages.get(position);
        String userId = message.getName();

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("user information", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", null);
        String uid = sharedPreferences.getString("uid", null);
        String language = sharedPreferences.getString("language", "English");
        String messageToDisplay = message.getTextByLanguage(Utils.getLanguageCode(language));
        if (messageToDisplay == null) {
            messageToDisplay = message.getText();
        }
        if (uid != null && uid.equals(message.getName())) {
            userId = email;
            holder.rlMessage.setVisibility(View.INVISIBLE);
            holder.rlMyMessage.setVisibility(View.VISIBLE);
            holder.rlMessage.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT); // TODO set gravity to RIGHT
            // Set item views based on your views and data model
            TextView myTextView = holder.tvMyMessage;
            myTextView.setText(messageToDisplay);
            TextView myUserName = holder.tvMyUserName;
            myUserName.setText(userId);
            TextView myTime = holder.tvMyTimeAgo;
            myTime.setText(new DateTimeUtils().getRelativeTimeAgo(message.getTimestamp()));
            TextView myOriginalMessage =  holder.tvMyOriginalMessage;
            myOriginalMessage.setText(message.getText());


        } else {
            holder.rlMessage.setVisibility(View.VISIBLE);
            holder.rlMyMessage.setVisibility(View.INVISIBLE);
            holder.rlMessage.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            // Set item views based on your views and data model
            TextView textView = holder.tvMessage;
            textView.setText(messageToDisplay);
            TextView userName = holder.tvUserName;
            userName.setText(userId);
            TextView time = holder.tvTimeAgo;
            time.setText(new DateTimeUtils().getRelativeTimeAgo(message.getTimestamp()));
            TextView originalMessage =  holder.tvOriginalMessage;
            originalMessage.setText(message.getText());
        }
    }

    @Override
    public int getItemCount() {
        return mChatMessages.size();
    }
}
