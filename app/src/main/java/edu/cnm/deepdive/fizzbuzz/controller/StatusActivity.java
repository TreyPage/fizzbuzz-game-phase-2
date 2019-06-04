package edu.cnm.deepdive.fizzbuzz.controller;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import edu.cnm.deepdive.fizzbuzz.R;
import edu.cnm.deepdive.fizzbuzz.model.Game;
import edu.cnm.deepdive.fizzbuzz.model.Round;
import edu.cnm.deepdive.fizzbuzz.view.RoundAdapter;
import java.util.List;

public class StatusActivity extends AppCompatActivity {

  private ListView roundList;

  @SuppressLint("SetTextI18n")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_status);
    roundList = findViewById(R.id.round_list);
    Game game = (Game) getIntent().getSerializableExtra(getString(R.string.game_data_key));
    List<Round> rounds = game.getRounds();
    int totalRounds = rounds.size();
    int totalCorrect = 0;
    for (Round round : rounds) {
      if (round.isCorrect()) {
        totalCorrect++;
      }
    }
    TextView totalRoundsDisplay = findViewById(R.id.total_rounds);
    TextView totalCorrectDisplay = findViewById(R.id.total_correct);
    totalRoundsDisplay.setText(Integer.toString(totalRounds));
    totalCorrectDisplay.setText(Integer.toString(totalCorrect));
    RoundAdapter adapter = new RoundAdapter(this, game.getRounds());
    roundList.setAdapter(adapter);
  }
}
