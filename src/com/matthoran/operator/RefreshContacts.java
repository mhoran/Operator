package com.matthoran.operator;

import android.os.AsyncTask;
import android.net.Uri;
import android.net.http.AndroidHttpClient;

class RefreshContacts extends AsyncTask<String, Void, String> {
  @Override
  protected String doInBackground(String... params) {
    return "Hello, world!";
  }

  @Override
  protected void onPostExecute(String result) {
  }

  private String refreshContacts(String url) {
    final AndroidHttpClient client = AndroidHttpClient.newInstance("Operator");
    final HttpGet getRequest = new HttpGet(url);

    try {
      HttpResponse response = client.execute(getRequest);
      final int statusCode = response.getStatusLine().getStatusCode(0);
      if (statusCode != HttpStatus.SC_OK) {
        Log.w("OSHI~", "Error" + statusCode + " while blah.");
      }

      final HttpEntity entity = response.getEntity();
    } finally {
      if (client != null) {
        client.close();
      }
    }
    return null;
  }

}
