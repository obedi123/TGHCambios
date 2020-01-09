package com.tablonca.obedimc.tghcambios;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NotificationActivity extends AppCompatActivity {

    private static final String SP_PAISES = "sharedPreferencesPaises";

    private static final String SPS_PAISES = "showPreferencesPaises";

    @BindView(R.id.spPaises)
    Spinner spPaises;
    @BindView(R.id.tvPaises)
    TextView tvPaises;

    private Set<String> mPaisesSet;
    private Set<String> mPaisesShow;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences sSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ButterKnife.bind(this);

        configActionBar();

        configSharedPreferences();
        showSharedPreferences();

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnSuccessListener(instanceIdResult -> Log.d("TokenId", instanceIdResult.getToken()));
    }

    private void configActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar !=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void configSharedPreferences() {
        mSharedPreferences = getPreferences(Context.MODE_PRIVATE);

        mPaisesSet = mSharedPreferences.getStringSet(SP_PAISES, new HashSet<>());
    }

    private void showSharedPreferences() {
        sSharedPreferences = getPreferences(Context.MODE_PRIVATE);

        mPaisesShow = sSharedPreferences.getStringSet(SPS_PAISES, new HashSet<>());

        showTopics();
    }

    private void showTopics() {
        tvPaises.setText(mPaisesShow.toString());
    }

    @OnClick({R.id.btnSuscribir, R.id.btnDesuscribir})
    public void onViewClicked(View view) {

        String topic = getResources().getStringArray(R.array.paisesValues)[spPaises.getSelectedItemPosition()];
        String topicShow = getResources().getStringArray(R.array.paises)[spPaises.getSelectedItemPosition()];

        switch (view.getId()) {
            case R.id.btnSuscribir:
                if (!mPaisesSet.contains(topic)) {
                    FirebaseMessaging.getInstance().subscribeToTopic(topic);
                    mPaisesSet.add(topic);
                    mPaisesShow.add(topicShow);
                    saveSharedPreferences();
                    savetoshowSharedPreferences();
                }
                break;
            case R.id.btnDesuscribir:
                if (mPaisesSet.contains(topic)) {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
                    mPaisesSet.remove(topic);
                    mPaisesShow.remove(topicShow);
                    saveSharedPreferences();
                    savetoshowSharedPreferences();
                }
                break;
        }
    }

    private void savetoshowSharedPreferences() {
        SharedPreferences.Editor editor = sSharedPreferences.edit();
        editor.clear();
        editor.putStringSet(SPS_PAISES, mPaisesShow);
        editor.apply();

        showTopics();
    }

    private void saveSharedPreferences() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.clear();
        editor.putStringSet(SP_PAISES, mPaisesSet);
        editor.apply();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
