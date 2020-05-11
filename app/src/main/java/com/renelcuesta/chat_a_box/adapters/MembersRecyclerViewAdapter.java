package com.renelcuesta.chat_a_box.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.renelcuesta.chat_a_box.R;
import com.renelcuesta.chat_a_box.fragments.MembersFragment.OnListFragmentInteractionListener;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MembersRecyclerViewAdapter extends RecyclerView.Adapter<MembersRecyclerViewAdapter.ViewHolder> {

    public String TAG = "Chat-a-boxMembersRecyclerViewAdapter";

    private final List<String> mValues;

    private final OnListFragmentInteractionListener mListener;

    HashMap<String, String> mUserGravatars;

    private Context mContext;

    FirebaseDatabase mDatabase;

    public MembersRecyclerViewAdapter(ArrayList<String> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;

        initGravatars();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_members, parent, false);

        ViewHolder vh = new ViewHolder(view);

        mContext = parent.getContext();

        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mChatMember.setText(holder.mItem);

        String mGravatarURL = "https://www.gravatar.com/avatar";
        String memberName = mValues.get(position);

        if (mUserGravatars.containsKey(memberName))
        {
            mGravatarURL = mUserGravatars.get(memberName);
        }

        Picasso.with(mContext).load(mGravatarURL).into(holder.mChatMemberIcon);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onMembersListFragmentInteraction(holder.mItem);
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

        public final TextView mChatMember;

        public ImageView mChatMemberIcon;

        public String mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mChatMember = (TextView) view.findViewById(R.id.chat_sender_member);
            mChatMemberIcon = (ImageView) view.findViewById(R.id.chat_member_icon);
        }

        @Override
        public String toString() {
            return super.toString() + " '";
        }
    }

    public void initGravatars() {
        mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = mDatabase.getReference("userGravatars");

        mUserGravatars = new HashMap<String, String>();

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUserGravatars.clear();

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String childKey = child.getKey();
                    String childValue = (String) child.getValue();

                    Log.e(TAG, "Child: Key [" + childKey + "] Value [" + childValue + "]");

                    mUserGravatars.put(childKey, childValue);
                }

                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        myRef.addValueEventListener(eventListener);
    }
}
