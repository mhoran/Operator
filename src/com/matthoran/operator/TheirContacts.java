package com.matthoran.operator;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.provider.ContactsContract;
import android.net.Uri;
import android.database.Cursor;
import android.widget.TextView;

public class TheirContacts extends Activity {
  private static final String TAG = "TheirContacts";

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.their_contacts);

    Intent intent = getIntent();
    Log.d(TAG, intent.getDataString());

    Cursor cursor = getContact(intent.getData());
    TextView tv = (TextView) findViewById(R.id.theirContactsText);
    if (cursor.moveToFirst()) {
      String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Data._ID));
      Cursor phones = getContentResolver().query( 
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
        null, 
        ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId, 
        null, null); 
      while (phones.moveToNext()) { 
        String phoneNumber = phones.getString( 
          phones.getColumnIndex( 
            ContactsContract.CommonDataKinds.Phone.NUMBER));                 
        tv.setText(phoneNumber);
      } 

    }
  }

  private Cursor getContact(Uri uri)
  { 
    String[] projection = new String[] {
      ContactsContract.Contacts._ID,
        ContactsContract.Contacts.DISPLAY_NAME,
        ContactsContract.Contacts.LOOKUP_KEY
    };

    return managedQuery(uri, projection, null, null, null);
  }

}
