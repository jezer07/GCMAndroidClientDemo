package com.example.gcmdemo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Profile;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.example.gcmdemo.model.PushMessage;
import com.example.gcmdemo.model.User;

public class MainActivity extends Activity implements Callback<List<User>> {
	static final String SENDER_ID = "737133065087";

	SharedPreferences sp;
	Spinner mSentTo;
	EditText mMessage;
	String mName;
	ListView mMessageListView;
	
	
	List<PushMessage> mPushList;
	
	
	// an action to listen
	public static final String MESSAGE_SENT_ACTION = "com.example.MESSAGE_RECEIVED_ACTION";
	// key to extract a text from bundle
	public static final String MESSAGE_EXTRA = "com.example.MESSAGE_EXTRA";

	
	
	private BroadcastReceiver mPushReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		sp = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
		mPushList = new ArrayList<PushMessage>();
		
		mMessageListView = (ListView)findViewById(R.id.listView1);
		PushMessage dummy = new PushMessage("Hello world", "Barrack Obama", null);
		mPushList.add(dummy);
		PushAdapter pa = new PushAdapter(this, mPushList);
		
		mMessageListView.setAdapter(pa);
		
		
		RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(
				"http://192.168.63.175:3000").build();
		ContactsInterface contacts = restAdapter.create(ContactsInterface.class);
		contacts.contacts(this);

		//Check if there's no entry of owner's name on the SharedPreferences
		//if non, get it from contacts
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
		}

		mSentTo = (Spinner) findViewById(R.id.spinner1);
		mMessage = (EditText) findViewById(R.id.message);
		mMessage.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
                   onSend();
                    return true;
                }
				
				return false;
			}
		});

	}
	
	
	
	@Override
	protected void onResume() {
		super.onResume();
		mPushReceiver = new BroadcastReceiver(){
			
			@Override
			public void onReceive(Context c, Intent i) {
				Bundle bundle = i.getExtras();
				String message = bundle.getString("msg");
				String from = bundle.getString("sender");
				
				PushMessage p = new PushMessage(message,from,null);				
				mPushList.add(0,p);
				
				//Collections.reverse(mPushList);
				((BaseAdapter) mMessageListView.getAdapter()).notifyDataSetChanged(); 
				
				
			}
			
		};
		
		registerReceiver(mPushReceiver, new IntentFilter(MESSAGE_SENT_ACTION));
	}
	// Overridden from CallBack<T>
	@Override
	public void failure(RetrofitError exception) {
		Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show();
	}
	
	// Overridden from CallBack<T>
	@Override
	public void success(List<User> c, Response r) {
		List<String> names = new ArrayList<String>();
		for (int i = 0; i < c.size(); i++) {
			String name = c.get(i).toString();
			Log.d("Names", "" + name);
			names.add(name);
		}
		names.add("All");

		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, names);
		mSentTo.setAdapter(spinnerAdapter);

	}
	
	public void onSend() {

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
							4);
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
		Log.d("NAME", name);

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

	private static class RegisterTask extends
			GCMRegistrarCompat.BaseRegisterTask {

		SharedPreferences sp;

		RegisterTask(Context context) {
			super(context);

			sp = context.getSharedPreferences(context.getPackageName(),
					Context.MODE_PRIVATE);
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
	
	static class PushAdapter extends ArrayAdapter<PushMessage>{

		List<PushMessage> _pushList;
		Context c;
		public PushAdapter(Context context, List<PushMessage> values) {
			super(context, R.layout.chat_row,values);
			
			_pushList = values;
			c = context;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
		        convertView = LayoutInflater.from(c)
		          .inflate(R.layout.chat_row, parent, false);
		    }
			TextView from = (TextView)convertView.findViewById(R.id.fromTV);
			TextView message = (TextView)convertView.findViewById(R.id.messageTV);
			
			
			
			from.setText(_pushList.get(position).getFrom());
			message.setText(_pushList.get(position).getMessage());
			
			
			
			return convertView;
		}
		
	}



}
