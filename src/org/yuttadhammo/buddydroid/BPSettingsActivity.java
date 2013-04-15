package org.yuttadhammo.buddydroid;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.Preference.OnPreferenceChangeListener;
import android.text.InputType;
import com.actionbarsherlock.view.MenuItem;


public class BPSettingsActivity extends SherlockPreferenceActivity {
	
	private Context context;
	private static SharedPreferences prefs;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		this.context = getApplicationContext();
		addPreferencesFromResource(R.xml.preferences);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		
		
		final Preference helpPref = (Preference)findPreference("help");
		helpPref.setOnPreferenceClickListener(new OnPreferenceClickListener(){

			@Override
			public boolean onPreferenceClick(Preference arg0) {
				Intent intent = new Intent(context, BPHelpActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);

				return false;
			}
			
		});

		final EditTextPreference websitePref = (EditTextPreference)findPreference("website");
		final EditTextPreference userPref = (EditTextPreference)findPreference("username");
		final EditTextPreference passPref = (EditTextPreference)findPreference("password");

		final EditTextPreference streamPref = (EditTextPreference)findPreference("stream_max");
		final EditTextPreference contentPref = (EditTextPreference)findPreference("content_max");
		
		final EditTextPreference intervalPref = (EditTextPreference)findPreference("sync_interval");
		
		setupEditTextPreference(websitePref, getWebsite(), null);
		setupEditTextPreference(userPref,"", null);

		setupEditTextPreference(streamPref,"20", null);
		setupEditTextPreference(contentPref,null, getString(R.string.contentMaxDesc));

		setupEditTextPreference(intervalPref,"60", null);

		intervalPref.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
		streamPref.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
		contentPref.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
		websitePref.getEditText().setInputType(InputType.TYPE_TEXT_VARIATION_URI);
		
		final CheckBoxPreference syncPref = (CheckBoxPreference)findPreference("interval_sync");

		if(prefs.getBoolean("interval_sync", false))
			intervalPref.setEnabled(true);
		else
			intervalPref.setEnabled(false);

		syncPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				if(!syncPref.isChecked())
					intervalPref.setEnabled(true);
				else
					intervalPref.setEnabled(false);
					
				return true;
			}
			
		});
		
		int api = Build.VERSION.SDK_INT;	
		
		if (api >= 14) {
			getActionBar().setHomeButtonEnabled(true);
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		
		if(Buddypress.CUSTOM_WEBSITE != null)
			websitePref.setEnabled(false);
		
	}
    private ProgressDialog downloadProgressDialog;
	protected String TAG = "BPSettingsActivity";


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		super.onOptionsItemSelected(item);
		
		switch (item.getItemId()) {
	        case android.R.id.home:
	            // app icon in action bar clicked; go home
	            finish();
	            return true;

			default:
				return false;
	    }
	}	
	
	public void setupEditTextPreference(final EditTextPreference etp, final String def, final String desc) {
		if(def != null) {
			if(etp.getText() == null || etp.getText().equals(""))
				etp.setText(def);
			etp.setSummary(etp.getText());
		}
		else if(etp.getText() != null && !etp.getText().equals("")) {
			etp.setSummary(etp.getText());
		}
		
		
		etp.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			public boolean onPreferenceChange(Preference preference,
					final Object newValue) {
				if((newValue == null || ((String)newValue).length() == 0) && desc != null)
					etp.setSummary(desc);
				else
					etp.setSummary((String) newValue);

				return true;
			}
			
		});			
	}
	private static String getWebsite() {
		String website = Buddypress.CUSTOM_WEBSITE  != null ? Buddypress.CUSTOM_WEBSITE : prefs.getString("website", "");
		if(website.length() == 0)
			website = null;
		return website;
	}	
}
