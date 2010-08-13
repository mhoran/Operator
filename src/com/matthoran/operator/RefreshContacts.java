package com.matthoran.operator;

import android.os.AsyncTask;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.HttpStatus;
import org.apache.http.HttpResponse;
import org.apache.http.HttpEntity;
import java.io.InputStream;
import android.util.Log;

class RefreshContacts extends AsyncTask<String, Void, String> {
  @Override
  protected String doInBackground(String... params) {
    return refreshContacts("http://operator.mike-burns.com/contacts_list/" + params[0]);
  }

  @Override
  protected void onPostExecute(String result) {
  }

  private String refreshContacts(String url) {
    final AndroidHttpClient client = AndroidHttpClient.newInstance("Operator");
    final HttpGet getRequest = new HttpGet(url);

    try {
      HttpResponse response = client.execute(getRequest);
      final int statusCode = response.getStatusLine().getStatusCode();
      if (statusCode != HttpStatus.SC_OK) {
        Log.w("OSHI~", "Error" + statusCode + " while blah.");
        return null;
      }

      final HttpEntity entity = response.getEntity();
      
      if (entity != null) {
    	  InputStream inputStream = null;
    	  try {
    	    inputStream = entity.getContent(); 
    	    return inputStream.toString();
    	  } finally {
            if (inputStream != null) {
              inputStream.close();
            }
    	    entity.consumeContent();
    	  }
    	}
      
      
    } catch (Exception e) {
      getRequest.abort();
    } finally {
      if (client != null) {
        client.close();
      }
    }
    return null;
  }

}
