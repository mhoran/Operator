package com.matthoran.operator;

import android.app.Activity;
import android.os.Bundle;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView;

public class MyContacts extends Activity
{

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
            startActivity(new Intent(MyContacts.this, TheirContacts.class));
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
        ContactsContract.Contacts.DISPLAY_NAME
      };
      String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '1'";
      String[] selectionArgs = null;
      String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";

      return managedQuery(uri, projection, selection, selectionArgs, sortOrder);
    }
}
