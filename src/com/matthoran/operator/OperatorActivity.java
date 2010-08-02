package com.matthoran.operator;

import android.app.Activity;
import android.os.Bundle;

public class OperatorActivity extends Activity
{
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
    }
}