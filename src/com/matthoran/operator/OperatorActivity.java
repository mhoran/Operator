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

public class OperatorActivity extends Activity
{

    private ListView mContactList;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_contacts);

        mContactList = (ListView) findViewById(R.id.contactList);
        populateContactList();
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
