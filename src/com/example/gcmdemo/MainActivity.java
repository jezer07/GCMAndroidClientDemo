package com.example.gcmdemo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Profile;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends Activity {
	static final String SENDER_ID = "737133065087";

	SharedPreferences sp;
	Spinner mSentTo;
	EditText mMessage;
	String mName;
	ListView mMessageList;
	private BroadcastReceiver receiver;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_main);
		sp = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
		

		if (!sp.contains(Consts.NAME)) {

			newName();
		}

		mName = sp.getString(Consts.NAME, null);

		GCMRegistrarCompat.checkDevice(this);
		if (BuildConfig.DEBUG) {
			GCMRegistrarCompat.checkManifest(this);
		}

		final String regId = GCMRegistrarCompat.getRegistrationId(this);
		if (regId.length() == 0) {
			new RegisterTask(this).execute(SENDER_ID);
		} else {
			Log.d(getClass().getSimpleName(), "Existing registration: " + regId);
			Toast.makeText(this, regId, Toast.LENGTH_LONG).show();
		}

		mSentTo = (Spinner) findViewById(R.id.spinner1);
		mMessage = (EditText) findViewById(R.id.message);

	}
	@Override
	protected void onResume() {
		super.onResume();
		
		
	}
	public String [] getContacts(){
		new AsyncTask<Void,Void,Void>(){

			@Override
			protected Void doInBackground(Void... arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			
			
		};
		
		
		
		
		return null;
	}
	public void onSend(View v) {

		new AsyncTask<Void, Void, Void>() {
			String sendTo;
			String message;

			protected void onPreExecute() {

				sendTo = mSentTo.getSelectedItem().toString();
				message = mMessage.getText().toString();
				Toast.makeText(MainActivity.this, "Sending", Toast.LENGTH_SHORT)
						.show();

				Log.d("values", "name =  " + sendTo);
				Log.d("values", "message =  " + message);
			};

			@Override
			protected Void doInBackground(Void... params) {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(
						"http://192.168.63.175:3000/message/send_push/");

				try {
					// Add your data
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
							3);
					nameValuePairs.add(new BasicNameValuePair("user", sendTo));
					nameValuePairs.add(new BasicNameValuePair("message",
							message));
					nameValuePairs.add(new BasicNameValuePair("sender",
							GCMRegistrarCompat
									.getRegistrationId(MainActivity.this)));
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

					// Execute HTTP Post Request
					HttpResponse response = httpclient.execute(httppost);

				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
				} catch (IOException e) {
					// TODO Auto-generated catch block
				}
				return null;
			}

		}.execute();

		// Create a new HttpClient and Post Header

	}
	

	private void newName() {
		final String[] SELF_PROJECTION = new String[] { Phone._ID,
				Phone.DISPLAY_NAME, };
		Cursor cursor = this.getContentResolver().query(Profile.CONTENT_URI,
				SELF_PROJECTION, null, null, null);
		cursor.moveToFirst();
		String name = cursor.getString(1);
		sp.edit().putString(Consts.NAME, name).commit();
		Log.d("NAME",name);
		
		
	}

	private static class RegisterTask extends
			GCMRegistrarCompat.BaseRegisterTask {

		SharedPreferences sp;

		RegisterTask(Context context) {
			super(context);

			sp = context.getSharedPreferences(context.getPackageName(),
					Context.MODE_PRIVATE);
		}

		@Override
		protected void onPreExecute() {

		}

		@Override
		public void onPostExecute(String regid) {
			Log.d(getClass().getSimpleName(), "registered as: " + regid);
		}

		@Override
		protected void sendRegistrationIdToServer(String regid) {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(
					"http://192.168.63.175:3000/users/");
			String name = sp.getString(Consts.NAME, null);

			try {
				// Add your data
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						2);
				nameValuePairs.add(new BasicNameValuePair("user[username]",
						name));
				nameValuePairs
						.add(new BasicNameValuePair("user[regid]", regid));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httppost);
				
				Log.d("status", "" + response.getStatusLine().getStatusCode());

			} catch (ClientProtocolException e) {
			} catch (IOException e) {
			}

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
