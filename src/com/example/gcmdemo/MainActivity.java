package com.example.gcmdemo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends Activity {
	static final String SENDER_ID = "737133065087";
	
	
	Spinner mName;
	EditText mMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_main);
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
	
			mName = (Spinner)findViewById(R.id.spinner1);
			mMessage = (EditText)findViewById(R.id.message);
		
		
		
	}

	public void onSend(View v) {
		/*final String regId = GCMRegistrarCompat.getRegistrationId(this);
		if (regId.length() == 0) {
			new RegisterTask(this).execute(SENDER_ID);
		} else {
			Log.d(getClass().getSimpleName(), "Existing registration: " + regId);
			Toast.makeText(this, regId, Toast.LENGTH_LONG).show();
		}*/
		new AsyncTask<Void, Void, Void>() {
			String name;
			String message;
			
			protected void onPreExecute() {
				
				name =  mName.getSelectedItem().toString();
				message = mMessage.getText().toString();
				Toast.makeText(MainActivity.this, "Sending", Toast.LENGTH_SHORT).show();
				
				Log.d("values","name =  "+name);
				Log.d("values","message =  "+message);
			};
			
		
			
			@Override
			protected Void doInBackground(Void... params) {
				HttpClient httpclient = new DefaultHttpClient();
			    HttpPost httppost = new HttpPost("http://192.168.63.175:3000/message/send_push/");
			    
			    

			    try {
			        // Add your data
			        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
			        nameValuePairs.add(new BasicNameValuePair("user", name));
			        nameValuePairs.add(new BasicNameValuePair("message", message));
			        nameValuePairs.add(new BasicNameValuePair("sender", GCMRegistrarCompat.getRegistrationId(MainActivity.this)));
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

	private static class RegisterTask extends
			GCMRegistrarCompat.BaseRegisterTask {
		RegisterTask(Context context) {
			super(context);
		}

		@Override
		public void onPostExecute(String regid) {
			Log.d(getClass().getSimpleName(), "registered as: " + regid);
			Toast.makeText(context, regid, Toast.LENGTH_LONG).show();
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
