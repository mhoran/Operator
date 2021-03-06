package com.matthoran.operator;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MyContacts extends ListActivity
{
  private static final String TAG = "MyContacts";
 
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    /* 1. Unless already registered, register.
     *  registration means to store our registration_id
     * 2. Download any new mappings.
     *  we keep a DB of phone number -> registration_id for each contact
     * 3. Update the UI to reflect possible contacts.
     */
    super.onCreate(savedInstanceState);

    registerWithServer();
    
    setUpContactsList();
  }
  
  @Override
  protected void onListItemClick(ListView parent, View view, int position, long id) {
	  super.onListItemClick(parent, view, position, id);
	  if (position >= 0) {
		  final Cursor cursor = (Cursor) parent.getItemAtPosition(position);

		  final long contactId = cursor.getInt(cursor.getColumnIndex(ContactsContract.Data._ID));
		  final String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.LOOKUP_KEY));
		  Uri uri = ContactsContract.Contacts.getLookupUri(contactId, lookupKey);

		  Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		  intent.setClass(MyContacts.this, TheirContacts.class);
		  startActivity(intent);
	  }
  }
  
  protected void setUpContactsList() {
	  Cursor contactsCursor = getContacts();
	  ListAdapter adapter = new SimpleCursorAdapter(
			  this,
			  android.R.layout.simple_list_item_1,
			  contactsCursor,
			  new String[] { ContactsContract.Data.DISPLAY_NAME },
			  new int[] { android.R.id.text1 });
	  setListAdapter(adapter);
  }

  private void populateContactList() {
	  setUpContactsList();
  }

  private Cursor getContacts()
  {
    Uri uri = ContactsContract.Contacts.CONTENT_URI;
    String[] projection = new String[] {
      ContactsContract.Contacts._ID,
        ContactsContract.Contacts.DISPLAY_NAME,
        ContactsContract.Contacts.LOOKUP_KEY
    };
    String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1";
    String[] selectionArgs = null;
    String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";

    return managedQuery(uri, projection, selection, selectionArgs, sortOrder);
  }

  private String myPhoneNumber() {
	  return "8572778808";
  }
  
  private String myDeviceId() {
	  return "abcde";
  }

  private void refreshContacts() {
    ContactRefresher contactRefresher = new ContactRefresher();
    contactRefresher.execute();
  }

  private class ContactRefresher extends AsyncTask<Void, Void, String> {
    private String myPhoneNumber() {
      return MyContacts.this.myPhoneNumber();
    }

    @Override
    protected String doInBackground(Void... params) {
      return refreshContacts();
    }

    @Override
    protected void onPostExecute(String xml) {
      populateContactList();
    }

    private String refreshContacts() {
      final AndroidHttpClient client = AndroidHttpClient.newInstance("Operator");
      final HttpGet getRequest = new HttpGet(contactListUrl(myPhoneNumber()));

      try {
        HttpResponse response = client.execute(getRequest);
        final int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != HttpStatus.SC_OK) {
          Log.w(TAG, "Error " + statusCode + " while retrieving contacts.");
          return null;
        }

        final HttpEntity entity = response.getEntity();
        
        if (entity != null) {
            InputStream inputStream = null;
            try {
              inputStream = entity.getContent(); 
              // this is XML; parse it
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

    private String contactListUrl(String phoneNumber) {
      return "http://operator.mike-burns.com/contact_lists/" + phoneNumber;
    }
  }
  
  private void registerWithServer() {
    ServerRegistrar registrar = new ServerRegistrar();
    registrar.execute();
  }
  
  private class ServerRegistrar extends AsyncTask<Void, Void, Boolean> {
    @Override
    protected Boolean doInBackground(Void... params) {
      return registerWithServer();
    }

    private String myPhoneNumber() {
      return MyContacts.this.myPhoneNumber();
    }

    private String myDeviceId() {
      return MyContacts.this.myDeviceId();
    }

    private Boolean registerWithServer() {
      final AndroidHttpClient client = AndroidHttpClient.newInstance("Operator");
      final HttpPost postRequest = new HttpPost(registrationUrl());

      List<NameValuePair> params = new ArrayList<NameValuePair>(2);
      params.add(new BasicNameValuePair("phone_number",myPhoneNumber()));
      params.add(new BasicNameValuePair("registration_id", myDeviceId()));
      try {
        postRequest.setEntity(new UrlEncodedFormEntity(params));
        HttpResponse response = client.execute(postRequest);
        final int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != HttpStatus.SC_OK) {
          Log.w(TAG, "Error " + statusCode + " while registering with the server.");
          return false;
        }
        Log.i(TAG, "Successfully registered with the server.");
        return true;
      } catch (Exception e) {
        postRequest.abort();
      } finally {
        if (client != null) {
          client.close();
        }
      }
      return false;
    }

    private String registrationUrl() {
      return "http://operator.mike-burns.com/registrations";
    }

    protected void onPostExecute(Boolean registrationResult) {
      if (registrationResult) {
        MyContacts.this.refreshContacts();
      } else {
        Log.w(TAG, "Nothing to do; registration failed.");
      }
    }
  }
}
