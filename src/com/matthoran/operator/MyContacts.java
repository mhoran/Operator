package com.matthoran.operator;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView;
import android.content.Context;
import android.util.Log;

public class MyContacts extends Activity
{
  private static final String TAG = "MyContacts";

  private ListView mContactList;

  /** Called when the activity is first created. */
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
    setContentView(R.layout.my_contacts);

    mContactList = (ListView) findViewById(R.id.contactList);
    populateContactList();

    mContactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position >= 0) {
          final Cursor cursor = (Cursor) parent.getItemAtPosition(position);

          final long contactId = cursor.getInt(cursor.getColumnIndex(ContactsContract.Data._ID));
          final String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.LOOKUP_KEY));
          Uri uri = ContactsContract.Contacts.getLookupUri(contactId, lookupKey);
          Log.d(TAG, uri.toString());

          Intent intent = new Intent(Intent.ACTION_VIEW, uri);
          intent.setClass(MyContacts.this, TheirContacts.class);
          startActivity(intent);
        }
      }
    });
  }

  private void populateContactList() {
    Cursor cursor = getContacts();
    String[] fields = new String[] {
      ContactsContract.Data.DISPLAY_NAME
    };
    SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
        R.layout.contact_entry, cursor, fields,
        new int[] {R.id.contactEntryText});
    mContactList.setAdapter(adapter);
  }

  private Cursor getContacts()
  {
    Uri uri = ContactsContract.Contacts.CONTENT_URI;
    String[] projection = new String[] {
      ContactsContract.Contacts._ID,
        ContactsContract.Contacts.DISPLAY_NAME,
        ContactsContract.Contacts.LOOKUP_KEY
    };
    String selection = null;
    String[] selectionArgs = null;
    String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";

    return managedQuery(uri, projection, selection, selectionArgs, sortOrder);
  }
}
