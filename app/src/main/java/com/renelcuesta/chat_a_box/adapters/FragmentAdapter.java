package com.renelcuesta.chat_a_box.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.renelcuesta.chat_a_box.fragments.ChatMessageFragment;
import com.renelcuesta.chat_a_box.fragments.HistoryFragment;
import com.renelcuesta.chat_a_box.fragments.MembersFragment;

public class FragmentAdapter extends FragmentPagerAdapter {

    public String TAG = "Chat-a-boxFragmentAdapter";

    private static int NUM_ITEMS = 3;

    HistoryFragment mHistory;

    MembersFragment mMembers;

    public FragmentAdapter(FragmentManager manager) {
        super(manager,
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    {
        mHistory = HistoryFragment.newInstance(1);
        mMembers = MembersFragment.newInstance(1);
    }

    public int getCount() {
        return NUM_ITEMS;
    }

    public Fragment getItem(int position) {
        Fragment page = null;
        switch (position) {
            case 0:
                page = ChatMessageFragment.newInstance("One", "Two");
                break;
            case 1:
                page = mHistory;
                break;
            case 2:
                page = mMembers;
                break;

            default:
                page = ChatMessageFragment.newInstance("One", "Two");
                break;
        }

        return page;
    }

    public CharSequence getPageTitle(int position) {
        CharSequence result = "";

        switch (position) {
            case 0:
                result = "Chat";
                break;
            case 1:
                result = "History";
                break;
            case 2:
                result = "Members";
                break;

            default:
                result = "Chat";
                break;
        }

        return result;
    }
}

