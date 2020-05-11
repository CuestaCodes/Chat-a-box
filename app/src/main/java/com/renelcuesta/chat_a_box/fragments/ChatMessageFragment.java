package com.renelcuesta.chat_a_box.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.fragment.app.Fragment;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.renelcuesta.chat_a_box.chat.ChatMessage;
import com.renelcuesta.chat_a_box.R;
import java.util.Date;


public class ChatMessageFragment extends Fragment {

    public String TAG = "Chat-a-boxChatMessageFragment";

    private static final String ARG_PARAM1 = "param1";

    private static final String ARG_PARAM2 = "param2";

    private static String mDisplayName;

    FirebaseDatabase mFirebaseDatabase;

    FirebaseApp mApp;

    private String mParam1;

    private String mParam2;

    private OnFragmentInteractionListener mListener;

    static EditText mChatEditMessage;

    ImageButton mSendButton;

    private Context context;

    public ChatMessageFragment() {
        // Required empty public constructor
    }

    public static ChatMessageFragment newInstance(String param1, String param2) {
        ChatMessageFragment fragment = new ChatMessageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_chat_message, container, false);

        mApp = FirebaseApp.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance(mApp);

        mChatEditMessage = (EditText)
                view.findViewById(R.id.chatEditMessage);
        mSendButton = (ImageButton) view.findViewById(R.id.chatSendButton);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "Send Button Clicked");

                ChatMessage chat = new ChatMessage();

                chat.chatMessage = mChatEditMessage.getText().toString();

                DateFormat df = new DateFormat();
                chat.chatSendTime = df.format("dd-MM-yyyy HH:mm:ss",
                        new Date()).toString();
                chat.chatSender = getDisplayName();

                Log.e(TAG, "displayname in onlick listener is: " + chat.chatSender);

                String nodeKey = chat.chatSendTime + " " + chat.chatSender;

                if (!mChatEditMessage.getText().toString().equals("")) {
                    DatabaseReference ref = mFirebaseDatabase.getReference("chatMessages").child(nodeKey);
                    ref.setValue(chat);
                }

                mChatEditMessage.setText("");
            }
        });

        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }

    public void routeDisplayName(String displayName) {
        Log.e(TAG, "DisplayName : " + displayName + " received");

        ChatMessageFragment.mDisplayName = displayName;
    }

    public String getDisplayName() {
        Log.e(TAG, "Display name in getDisplayName() :" + mDisplayName);

        return mDisplayName;
    }
}
