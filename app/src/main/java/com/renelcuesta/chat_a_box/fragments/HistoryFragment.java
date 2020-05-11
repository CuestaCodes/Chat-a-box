package com.renelcuesta.chat_a_box.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.renelcuesta.chat_a_box.chat.ChatComparator;
import com.renelcuesta.chat_a_box.chat.ChatMessage;
import com.renelcuesta.chat_a_box.adapters.HistoryRecyclerViewAdapter;
import com.renelcuesta.chat_a_box.R;
import java.util.ArrayList;
import java.util.Collections;


public class HistoryFragment extends Fragment {

    public String TAG = "Chat-a-boxHistoryFragment";

    private static final String ARG_COLUMN_COUNT = "column-count";

    private int mColumnCount = 3;

    private OnListFragmentInteractionListener mListener;

    static ArrayList<ChatMessage> mHistoryArray;

    public HistoryFragment() {
        mHistoryArray = new ArrayList<ChatMessage>();
    }

    public static HistoryFragment newInstance(int columnCount) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_list, container, false);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new HistoryRecyclerViewAdapter(mHistoryArray, mListener));
        }

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {

        void onHistoryListFragmentInteraction(ChatMessage item);
    }

    public void routeChatMessage(ChatMessage chat) {
        Log.e(TAG, chat + "Received");

        // don't want to duplicate data, so check before adding
        if (!HistoryFragment.mHistoryArray.contains(chat)) {
            HistoryFragment.mHistoryArray.add(chat);
        }

        Collections.sort(mHistoryArray, new ChatComparator());
    }
}
