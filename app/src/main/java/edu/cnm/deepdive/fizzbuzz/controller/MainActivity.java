package edu.cnm.deepdive.fizzbuzz.controller;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;
import androidx.preference.PreferenceManager;
import edu.cnm.deepdive.fizzbuzz.R;
import edu.cnm.deepdive.fizzbuzz.model.Game;
import edu.cnm.deepdive.fizzbuzz.model.Round;
import edu.cnm.deepdive.fizzbuzz.model.Round.Category;
import edu.cnm.deepdive.fizzbuzz.view.SplashScreen;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Main Game play screen for FizzBuzz number classification Game.
 * <p>When the Game is running, this class displays a randomly selected number; the user then
 * flings the number in one of 4 directions, to indicate whether the number is a Fizz (divisible by
 * 3), Buzz (divisible by 5), FizzBuzz (divisible by both 3 & 5), or neither Fizz nor Buzz. A record
 * of numbers displayed, user actions, as well as an overall correct/incorrect tally are kept.</p>
 */
public class MainActivity extends AppCompatActivity
    implements View.OnTouchListener, SharedPreferences.OnSharedPreferenceChangeListener {

  private Random rng = new Random();
  private int value;
  private boolean running;
  private boolean complete;
  private TextView valueDisplay;
  private TextView clockDisplay;
  private ViewGroup valueContainer;
  private Rect displayRect = new Rect();
  private GestureDetectorCompat detector;
  private Timer valueTimer;
  private Timer gameTimer;
  private Timer clockTimer;
  private SharedPreferences preferences;
  private Game game;
  private int numDigits;
  private int timeLimit;
  private int gameDuration;
  private long gameTimerStart;
  private long gameTimeElapsed;
  private String gameDataKey;
  private String gameTimeElapsedKey;
  private String clockFormat;
  private int min;
  private double sec;
  private String valueString;

  /**
   * Initializes this activity when created, and when restored after {@link #onDestroy()} (for
   * example, after a change of orientation). In the latter case, the Game state is retrieved from
   * <code>savedInstanceState</code>.
   *
   * @param savedInstanceState saved Game state {@link Bundle}.
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    valueDisplay = findViewById(R.id.value_display);
    clockDisplay = findViewById(R.id.clock_display);
    valueContainer = (ViewGroup) valueDisplay.getParent();
    detector = new GestureDetectorCompat(this, new FlingListener());
    valueContainer.setOnTouchListener(this);
    preferences = PreferenceManager.getDefaultSharedPreferences(this);
    preferences.registerOnSharedPreferenceChangeListener(this);
    readSettings();
    gameDataKey = getString(R.string.game_data_key);
    gameTimeElapsedKey = getString(R.string.game_time_elapsed_key);
    clockFormat = getString(R.string.clock_format);
    if (savedInstanceState != null) {
      game = (Game) savedInstanceState.getSerializable(gameDataKey);
      gameTimeElapsed = savedInstanceState.getLong(gameTimeElapsedKey, 0);
      gameTimerStart = System.currentTimeMillis();
      updateClock();
    }
    if (game == null) {
      resumeGame();
    }
  }

  /**
   * Updates valueTimer(s) and UI to return display &amp; Game to the pre-{@link #onPause()} state.
   */
  @Override
  protected void onResume() {
    super.onResume();

  }

  /**
   * Captures current state of valueTimer(s) to fields, for possible saving by subsequent {@link
   * #onSaveInstanceState(Bundle)} invocation and/or restoration by {@link #onResume()} invocation.
   */
  @Override
  protected void onPause() {
    super.onPause();
    pauseGame();
  }

  /**
   * Writes critical Game state information to <code>outState</code>.
   *
   * @param outState Game state write target.
   */
  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putSerializable(gameDataKey, game);
    outState.putLong(gameTimeElapsedKey, gameTimeElapsed);
  }

  /**
   * Inflates menu options for control of Game, access to settings, and display of current Game
   * results.
   *
   * @param menu {@link Menu} to which inflated options are added.
   * @return flag indicating that a menu was inflated &amp; added (always <code>true</code> in this
   * case).
   */
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.options, menu);
    return true;
  }

  /**
   * Updates visible and enabled state of menu options, depending on Game state.
   *
   * @param menu options menu.
   * @return flag indicating that the options menu should be re-rendered (always <code>true</code>
   * in this case).
   */
  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    MenuItem play = menu.findItem(R.id.play);
    MenuItem pause = menu.findItem(R.id.pause);
    play.setEnabled(!running && !complete);
    play.setVisible(!running && !complete);
    pause.setEnabled(running && !complete);
    pause.setVisible(running && !complete);
    return true;
  }

  public void hideItems() {
    ImageView up = findViewById(R.id.up_arrow);
    up.setVisibility(View.INVISIBLE);
    ImageView down = findViewById(R.id.down_arrow);
    down.setVisibility(View.INVISIBLE);
    ImageView left = findViewById(R.id.left_arrow);
    left.setVisibility(View.INVISIBLE);
    ImageView right = findViewById(R.id.right_arrow);
    right.setVisibility(View.INVISIBLE);
    TextView upText = findViewById(R.id.up_text);
    upText.setVisibility(View.INVISIBLE);
    TextView downText = findViewById(R.id.neither_text);
    downText.setVisibility(View.INVISIBLE);
    TextView rightText = findViewById(R.id.right_text);
    rightText.setVisibility(View.INVISIBLE);
    TextView leftText = findViewById(R.id.left_text);
    leftText.setVisibility(View.INVISIBLE);
  }

  public void showItems() {
    ImageView up = findViewById(R.id.up_arrow);
    up.setVisibility(View.VISIBLE);
    ImageView down = findViewById(R.id.down_arrow);
    down.setVisibility(View.VISIBLE);
    ImageView left = findViewById(R.id.left_arrow);
    left.setVisibility(View.VISIBLE);
    ImageView right = findViewById(R.id.right_arrow);
    right.setVisibility(View.VISIBLE);
    TextView upText = findViewById(R.id.up_text);
    upText.setVisibility(View.VISIBLE);
    TextView downText = findViewById(R.id.neither_text);
    downText.setVisibility(View.VISIBLE);
    TextView rightText = findViewById(R.id.right_text);
    rightText.setVisibility(View.VISIBLE);
    TextView leftText = findViewById(R.id.left_text);
    leftText.setVisibility(View.VISIBLE);
  }

  /**
   * Handles user selection from the options menu.
   *
   * @param item option selected.
   * @return flag indicating that the selection was handled (or not).
   */
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    boolean handled = true;
    Intent intent;
    switch (item.getItemId()) {
      case R.id.play:
        resumeGame();
        break;
      case R.id.pause:
        pauseGame();
        break;
      case R.id.settings:
        intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        break;
      case R.id.replay:
        initgame();
        intent = new Intent(this, SplashScreen.class);
        pauseGame();
        startActivity(intent);
        break;
      case R.id.status:
        showStats();
        break;
      default:
        handled = super.onOptionsItemSelected(item);
        break;
    }
    return handled;
  }

  private void showStats() {
    Intent intent = new Intent(this, StatusActivity.class);
    intent.putExtra(getString(R.string.game_data_key), game);
    intent.putExtra(getString(R.string.minutes_key), min);
    intent.putExtra(getString(R.string.seconds_key), sec);
    startActivity(intent);
  }

  private void initgame() {
    game = new Game(timeLimit, numDigits, gameDuration);
    gameTimeElapsed = 0;
    complete = false;
    gameTimerStart = System.currentTimeMillis();
    updateClock();
  }

  /**
   * Handles user touch events on the screen. In general, these are handled simply by delegating to
   * an instance of a {@link android.view.GestureDetector.SimpleOnGestureListener} subclass.
   *
   * @param view target of touch event.
   * @param event details of event (location, type, time, etc.).
   * @return flag indicating that the user touch event was handled (or not).
   */
  @Override
  public boolean onTouch(View view, MotionEvent event) {
    boolean handled = false;
    if (running) {
      handled = detector.onTouchEvent(event);
      if (!handled && event.getActionMasked() == MotionEvent.ACTION_UP) {
        valueDisplay.setTranslationX(0);
        valueDisplay.setTranslationY(0);
        handled = true;
      }
    }
    return handled;
  }

  /**
   * Detects and handles changes in any {@link SharedPreferences} values in which this app's
   * configuration settings are stored. Most such changes are handled by re-starting the Game in
   * progress (if any).
   *
   * @param sharedPreferences configuration settings.
   * @param key specific setting changed.
   */
  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    readSettings();
    pauseGame();
    game = new Game(timeLimit, numDigits, gameDuration);
  }

  private void readSettings() {
    numDigits = preferences.getInt(getString(R.string.num_digits_key),
        getResources().getInteger(R.integer.num_digits_default));
    timeLimit = preferences.getInt(getString(R.string.time_limit_key),
        getResources().getInteger(R.integer.time_limit_default));
    gameDuration = preferences.getInt(getString(R.string.game_time_key),
        getResources().getInteger(R.integer.game_time_default));
  }

  private void pauseGame() {
    running = false;
    stopValueTimer();
    stopGameTimer();
    valueDisplay.setText("");
    invalidateOptionsMenu();
    showItems();
  }

  private void resumeGame() {
    running = true;
    if (game == null) {
      initgame();
    }
    updateValue();
    startGameTimer();
    startValueTimer();
    invalidateOptionsMenu();
    hideItems();
  }

  private void stopValueTimer() {
    if (valueTimer != null) {
      valueTimer.cancel();
      valueTimer = null;
    }
  }

  private void stopGameTimer() {
    if (gameTimer != null) {
      gameTimer.cancel();
      gameTimer = null;
      gameTimeElapsed += System.currentTimeMillis() - gameTimerStart;
    }
    if (clockTimer != null) {
      clockTimer.cancel();
      clockTimer = null;
    }
  }

  private void recordRound(Category selection) {
    Category category = Category.fromValue(value);
    Round round = new Round(value, category, selection);
    game.add(round);
    ImageView indicator;
    Animator fade = AnimatorInflater.loadAnimator(this, R.animator.indicator_fade);
    switch (category) {
      case FIZZ:
        indicator = findViewById(
            round.isCorrect() ? R.id.correct_fizz_indicator : R.id.incorrect_fizz_indicator);
        break;
      case BUZZ:
        indicator = findViewById(
            round.isCorrect() ? R.id.correct_buzz_indicator : R.id.incorrect_buzz_indicator);
        break;
      case FIZZBUZZ:
        indicator = findViewById(
            round.isCorrect() ? R.id.correct_fizzbuzz_indicator
                : R.id.incorrect_fizzbuzz_indicator);
        break;
      default:
        indicator = findViewById(
            round.isCorrect() ? R.id.correct_neither_indicator : R.id.incorrect_neither_indicator);
        break;
    }
    fade.setTarget(indicator);
    fade.start();
  }

  private void updateValue() {
    int valueLimit = (int) Math.pow(10, numDigits) - 1;
    value = 1 + rng.nextInt(valueLimit);
    valueString = Integer.toString(value);
    valueDisplay.setTranslationX(0);
    valueDisplay.setTranslationY(0);
    valueDisplay.setText(valueString);
  }

  private void startValueTimer() {
    if (timeLimit != 0) {
      valueTimer = new Timer();
      valueTimer.schedule(new TimeoutTask(), timeLimit * 1000);
    }
  }

  private void startGameTimer() {
    gameTimer = new Timer();
    gameTimer.schedule(new GameTimeoutTask(), 1000L * gameDuration - gameTimeElapsed);
    gameTimerStart = System.currentTimeMillis();
    clockTimer = new Timer();
    clockTimer.schedule(new ClockTimerTask(), 0, 100);
  }

  private void updateClock() {
    long remaining = (running || gameTimeElapsed > 0) ?
        gameDuration * 1000L - (System.currentTimeMillis() - gameTimerStart + gameTimeElapsed)
        : gameDuration * 1000L;

    if (remaining > 0) {
      min = (int) remaining / 60000;
      sec = (remaining % 60000) / 1000.0;
    } else {
      min = 0;
      sec = 0;
    }
    clockDisplay.setText(String.format(clockFormat, min, sec));
  }

  private class TimeoutTask extends TimerTask {

    @Override
    public void run() {
      runOnUiThread(() -> {
        recordRound(null);
        updateValue();
        startValueTimer();
      });
    }
  }

  private class GameTimeoutTask extends TimerTask {

    @Override
    public void run() {
      complete = true;
      runOnUiThread(() -> {
        pauseGame();
        Toast.makeText(MainActivity.this, R.string.game_over_toast, Toast.LENGTH_LONG).show();
        showStats();
      });
    }

  }

  private class ClockTimerTask extends TimerTask {

    @Override
    public void run() {
      runOnUiThread(() -> updateClock());
    }
  }

  private class FlingListener extends GestureDetector.SimpleOnGestureListener {

    private static final int RADIUS_FACTOR = 5;
    private static final double SPEED_THRESHOLD = 300;

    private float originX;
    private float originY;
    private int dragValue;

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
      boolean handled = false;
      if (value == dragValue) {
        valueDisplay.setTranslationX(e2.getX() - originX);
        valueDisplay.setTranslationY(e2.getY() - originY);
        handled = true;
      }
      return handled;
    }


    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
      boolean handled = false;
      if (value == dragValue) {
        int containerHeight = valueContainer.getHeight();
        int containerWidth = valueContainer.getWidth();
        int radiusX = containerWidth / RADIUS_FACTOR;
        int radiusY = containerHeight / RADIUS_FACTOR;
        double deltaX = e2.getX() - e1.getX();
        double deltaY = e2.getY() - e1.getY();
        double ellipticalDistance =
            deltaX * deltaX / radiusX / radiusX + deltaY * deltaY / radiusY / radiusY;
        double speed = Math.hypot(velocityX, velocityY);
        if (speed >= SPEED_THRESHOLD && ellipticalDistance >= 1) {
          stopValueTimer();
          Category selection;
          if (Math.abs(deltaY) * containerWidth <= Math.abs(deltaX) * containerHeight) {
            if (deltaX > 0) {
              selection = Category.BUZZ;
            } else {
              selection = Category.FIZZ;
            }
          } else {
            if (deltaY > 0) {
              selection = Category.NEITHER;
            } else {
              selection = Category.FIZZBUZZ;
            }
          }
          recordRound(selection);
          updateValue();
          startValueTimer();
          handled = true;
        }
      }
      return handled;
    }

    @Override
    public boolean onDown(MotionEvent evt) {
      boolean handled = false;
      int containerHeight = valueContainer.getHeight();
      int containerWidth = valueContainer.getWidth();
      int textHeight;
      int textWidth;

      // HACK This assumes text is centered in layout.

      valueDisplay.getPaint().getTextBounds(valueString, 0, valueString.length(), displayRect);
      textHeight = displayRect.height();
      textWidth = displayRect.width();
      displayRect.top = (containerHeight - textHeight) / 2;
      displayRect.bottom = (containerHeight + textHeight) / 2;
      displayRect.left = (containerWidth - textWidth) / 2;
      displayRect.right = (containerWidth + textWidth) / 2;
      if (displayRect.contains(Math.round(evt.getX()), Math.round(evt.getY()))) {
        originX = evt.getX() - valueDisplay.getTranslationX();
        originY = evt.getY() - valueDisplay.getTranslationY();
        dragValue = value;
        handled = true;
      }
      return handled;
    }
  }
}
