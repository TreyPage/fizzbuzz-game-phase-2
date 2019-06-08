package edu.cnm.deepdive.fizzbuzz.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import edu.cnm.deepdive.fizzbuzz.R;
import edu.cnm.deepdive.fizzbuzz.controller.MainActivity;
import edu.cnm.deepdive.fizzbuzz.controller.SettingsActivity;

public class splash_screen extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash_screen);

    FloatingActionButton playButton = findViewById(R.id.splash_play);
    FloatingActionButton settingsButton = findViewById(R.id.splash_settings);
    FloatingActionButton helpButton = findViewById(R.id.splash_how_to);

    playButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(view.getContext(), MainActivity.class);
        startActivity(intent);
      }
    });
    settingsButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(view.getContext(), SettingsActivity.class);
        startActivity(intent);
      }
    });
    helpButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        View view = findViewById(R.id.splash_activity);
        String message = getString(R.string.how_to_play);
        int duration = Snackbar.LENGTH_INDEFINITE;

        showSnackbar(view, message, duration);

      }
    });
  }

  public void showSnackbar(View view, String message, int duration) {
    final Snackbar snackbar = Snackbar.make(view, message, duration);
    snackbar.setAction("DISMISS", new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        snackbar.dismiss();
      }
    });

    snackbar.show();
    }

  }
