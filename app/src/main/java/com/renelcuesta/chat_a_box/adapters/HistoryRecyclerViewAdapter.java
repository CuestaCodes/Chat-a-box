package com.renelcuesta.chat_a_box.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.renelcuesta.chat_a_box.chat.ChatMessage;
import com.renelcuesta.chat_a_box.R;
import com.renelcuesta.chat_a_box.fragments.HistoryFragment.OnListFragmentInteractionListener;
import java.util.ArrayList;
import java.util.List;


public class HistoryRecyclerViewAdapter extends RecyclerView.Adapter<HistoryRecyclerViewAdapter.ViewHolder> {

    public String TAG = "Chat-a-boxHistoryRecyclerViewAdapter";

    private final List<ChatMessage> mValues;

    private final OnListFragmentInteractionListener mListener;

    private static String mDisplayName;

    public HistoryRecyclerViewAdapter(ArrayList<ChatMessage> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mChatSendTime.setText("(" + holder.mItem.getChatSendTime() + ")");
        holder.mChatSender.setText(holder.mItem.getChatSender());
        holder.mChatMessage.setText(holder.mItem.getChatMessage());

        if (holder.mChatSender.getText().equals(mDisplayName)) {
            holder.mChatIcon.setImageResource(R.drawable.sendarrow);
        } else {
            holder.mChatIcon.setImageResource(R.drawable.receivearrow);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onHistoryListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;

        public final TextView mChatSendTime;

        public final TextView mChatSender;

        public final TextView mChatMessage;

        public ChatMessage mItem;

        public final ImageView mChatIcon;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mChatSendTime = (TextView) view.findViewById(R.id.chat_send_time);
            mChatSender = (TextView) view.findViewById(R.id.chat_sender);
            mChatMessage = (TextView) view.findViewById(R.id.chat_message);
            mChatIcon = (ImageView) view.findViewById(R.id.arrow);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mChatSender.getText() + "'"
                    + mChatMessage.getText() + "'";
        }
    }

    public static void routeDisplayName(String displayName) {
        HistoryRecyclerViewAdapter.mDisplayName = displayName;
    }
}
