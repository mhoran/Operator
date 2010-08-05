package com.matthoran.operator;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;

public class TheirContacts extends Activity {
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.their_contacts);

    Intent intent = getIntent();
    Log.d("TheirContacts", intent.getDataString());
  }
}
