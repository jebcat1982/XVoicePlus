package io.behindthemath.xvoiceplus.ui;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import io.behindthemath.xvoiceplus.gv.GoogleVoiceManager;

public class AccountListPreferences extends ListPreference {

    private static final String TAG = AccountListPreferences.class.getSimpleName();

    public AccountListPreferences(Context context, AttributeSet attrs) {
        super(context, attrs);

        AccountManager accountManager = AccountManager.get(context);
        if (accountManager != null) {
            final Account[] accounts = accountManager.getAccountsByType("com.google");
            String[] entries = new String[accounts.length];
            for (int i = 0; i < accounts.length; i++) {
                entries[i] = accounts[i].name;
            }
            setEntries(entries);
            setEntryValues(entries);

            setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String newAccountString = (String) newValue;
                    Log.d(TAG, "Account changed to " + newValue);
                    for (Account account : accounts) {
                        if (account.name.equals(newAccountString)) {
                            SharedPreferences prefs = getSharedPreferences();
                            if (prefs != null) {
                                final String previousAccount = prefs.getString("account", null);
                                GoogleVoiceManager.invalidateToken(getContext(), previousAccount);

                                GoogleVoiceManager.getToken(getContext(), account);

                                return true;
                            }
                        }
                    }
                    return false;
                }
            });
        }
    }
}