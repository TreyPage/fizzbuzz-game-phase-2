package edu.cnm.deepdive.fizzbuzz.controller;

import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import edu.cnm.deepdive.fizzbuzz.R;

public class StatusActivity extends AppCompatActivity {

  private ListView roundList;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_status);
    roundList = findViewById(R.id.round_list);
    //TODO Retrieve data to display, create adapter with data, attach adapter to view.
  }
}
