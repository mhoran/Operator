package com.matthoran.operator;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;

public class ServerRegistrar extends AsyncTask<String, Void, Boolean> {
	@Override
	protected Boolean doInBackground(String... params) {
		String myPhoneNumber = params[0];
		String deviceID = params[1];
		registerWithServer(myPhoneNumber,deviceID);
		return true;
	}

	private void registerWithServer(String phoneNumber, String deviceID) {
		final AndroidHttpClient client = AndroidHttpClient.newInstance("Operator");
		final HttpPost postRequest = new HttpPost(registrationURL());

		List<NameValuePair> params = new ArrayList<NameValuePair>(2);
		params.add(new BasicNameValuePair("phone_number",phoneNumber));
		params.add(new BasicNameValuePair("registration_id", deviceID));
		try {
			postRequest.setEntity(new UrlEncodedFormEntity(params));
			client.execute(postRequest);
		} catch (Exception e) {
			postRequest.abort();
		} finally {
			if (client != null) {
				client.close();
			}
		}
		return;
	}

	private String registrationURL() {
		return "http://operator.mike-burns.com/registrations";
	}
}