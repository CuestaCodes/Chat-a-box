package com.renelcuesta.chat_a_box;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager.widget.ViewPager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.renelcuesta.chat_a_box.adapters.FragmentAdapter;
import com.renelcuesta.chat_a_box.adapters.HistoryRecyclerViewAdapter;
import com.renelcuesta.chat_a_box.chat.ChatMessage;
import com.renelcuesta.chat_a_box.fragments.ChatMessageFragment;
import com.renelcuesta.chat_a_box.fragments.HistoryFragment;
import com.renelcuesta.chat_a_box.fragments.MembersFragment;
import com.renelcuesta.chat_a_box.signin.Signin;
import fr.tkeunebr.gravatar.Gravatar;


public class MainActivity extends AppCompatActivity implements
        ChatMessageFragment.OnFragmentInteractionListener,
        HistoryFragment.OnListFragmentInteractionListener,
        MembersFragment.OnListFragmentInteractionListener, RewardedVideoAdListener {

    String TAG = "Chat-a-boxMainActivity";

    FirebaseApp mApp;

    FirebaseDatabase mDatabase;

    FirebaseAuth mAuth;

    String mDisplayName;

    FirebaseAuth.AuthStateListener mAuthListener;

    ViewPager mViewPager;

    FragmentAdapter mFragmentAdapter;

    TabLayout mTabLayout;

    AdView mBannerAdView;

    AdRequest mBannerAdRequest;

    InterstitialAd mInterstitialAd;

    RewardedVideoAd mRewardAd;

    private Menu menu;

    int mAdvertCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e(TAG, "Chat-a-box project started...");

        initViewPager();
        initAdverts();
        initFirebase();
        initDatabaseChat();
    }

    private void initFirebase() {
        mApp = FirebaseApp.getInstance();
        mDatabase = FirebaseDatabase.getInstance(mApp);
        mAuth = FirebaseAuth.getInstance(mApp);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@androidx.annotation.NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();

                if (user != null) {
                    Log.e(TAG, "Status Update : Valid current user logged on : email [" +
                            user.getEmail() + "]");

                    loadDisplayName();

                } else {
                    Log.e(TAG, "Status Update : No valid current user logged on");
                    mDisplayName = "Null";

                    mAuth.removeAuthStateListener(mAuthListener);
                    Intent signInIntent = new Intent(getApplicationContext(), Signin.class);
                    startActivityForResult(signInIntent, 101);
                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 101) && (resultCode == RESULT_OK)) {
            mDisplayName = data.getStringExtra("display name");

            Log.e(TAG, "Returned activity display name [" + mDisplayName + "]");
            mAuth.addAuthStateListener(mAuthListener);

            initGravatars();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.options_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.option_logout) {
            Log.e(TAG, "Log Out button pressed");

            mAuth.signOut();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void initViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mFragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mFragmentAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        int tabSelectedColor = ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark,
                null);
        int tabNotSelectedColor = ResourcesCompat.getColor(getResources(), R.color.colorAccent,
                null);
        int tabColors = ResourcesCompat.getColor(getResources(), R.color.colorPrimary,
                null);

        mTabLayout.setTabTextColors(tabSelectedColor, tabNotSelectedColor);
        mTabLayout.setBackground(new ColorDrawable(tabColors));

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mAdvertCounter++;

                if (mAdvertCounter >= 10) {
                    if (mRewardAd.isLoaded()) {
                        mRewardAd.show();
                    }

                    mAdvertCounter = 0;
                } else if (mAdvertCounter == 5) {
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    }
                }

                InputMethodManager imm =
                        (InputMethodManager) getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);

                if (position == 0) {
                    imm.showSoftInput(getCurrentFocus(), 0);
                } else {
                    imm.hideSoftInputFromWindow(mBannerAdView.getWindowToken(), 0);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public void onFragmentInteraction(Uri uri) {
        Log.e(TAG, "Chat Fragment");
    }

    public void onHistoryListFragmentInteraction(ChatMessage item) {
        Log.e(TAG, "History Fragment");
    }

    public void onMembersListFragmentInteraction(String item) {
        Log.e(TAG, "History Fragment");
    }

    public void initAdverts() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mBannerAdView = findViewById(R.id.bottomBanner);
        mBannerAdRequest = new AdRequest.Builder().build();
        mBannerAdView.loadAd(mBannerAdRequest);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();

                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });

        mRewardAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardAd.setRewardedVideoAdListener(this);
        mRewardAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                new AdRequest.Builder().build());
    }

    @Override
    public void onRewardedVideoAdLoaded() {
    }

    @Override
    public void onRewardedVideoAdOpened() {
    }

    @Override
    public void onRewardedVideoStarted() {
    }

    @Override
    public void onRewardedVideoAdClosed() {
        mRewardAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                new AdRequest.Builder().build());
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
    }

    @Override
    public void onRewardedVideoCompleted() {
    }

    private void loadDisplayName() {
        mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = mDatabase.getReference("Users");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                ChatMessageFragment chatMessageFragment = (ChatMessageFragment)
                        mFragmentAdapter.getItem(0);

                Log.e(TAG, "User ID is :" + userID);

                mDisplayName = dataSnapshot.child(userID).getValue().toString();

                Log.e(TAG, mDisplayName + " from inside ValueEventListener");

                MenuItem item = menu.findItem(R.id.displayName);
                item.setTitle(mDisplayName);

                Log.e(TAG, "Inside onCreateOptionsMenu displayname :" + mDisplayName);

                chatMessageFragment.routeDisplayName(mDisplayName);
                HistoryRecyclerViewAdapter.routeDisplayName(mDisplayName);

                initGravatars();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "The read failed: " + databaseError.getCode());
            }
        };

        myRef.addValueEventListener(eventListener);
    }

    public void initDatabaseChat() {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = rootRef.child("chatMessages");

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HistoryFragment historyFragment = (HistoryFragment)
                        mFragmentAdapter.getItem(1);
                MembersFragment membersFragment = (MembersFragment)
                        mFragmentAdapter.getItem(2);

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    ChatMessage chat = child.getValue(ChatMessage.class);

                    Log.e(TAG, "Child " + chat.toString());

                    historyFragment.routeChatMessage(chat);
                    membersFragment.routeChatMessage(chat.chatSender);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };

        ref.addValueEventListener(listener);
    }

    public void initGravatars() {
        String myEmail = mAuth.getCurrentUser().getEmail();

        Log.e(TAG, "Fetching Gravatar for [" + myEmail + "] Display Name [" + mDisplayName +
                "]");

        if (myEmail != null) {
            String gravatarURL = Gravatar.init().with(myEmail).size(100).build();

            if (!gravatarURL.contains("https")) {
                gravatarURL = gravatarURL.replace("http", "https");
            }

            if (mDatabase != null) {
                DatabaseReference ref =
                        mDatabase.getReference("userGravatars").child(mDisplayName);
                ref.setValue(gravatarURL);
            } else {
                Log.e(TAG, "Invalid displayname");
            }
        } else {
            Log.e(TAG, "invalid email");
        }
    }
}
