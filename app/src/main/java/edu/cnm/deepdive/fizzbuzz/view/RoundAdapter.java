package edu.cnm.deepdive.fizzbuzz.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import edu.cnm.deepdive.fizzbuzz.R;
import edu.cnm.deepdive.fizzbuzz.model.Round;
import edu.cnm.deepdive.fizzbuzz.model.Round.Category;
import java.util.List;

public class RoundAdapter extends ArrayAdapter<Round> {

  private Drawable correctDraw;
  private Drawable incorrectDraw;
  private String correctDescript;
  private String incorrectDescript;
  private int correctColor;
  private int incorrectColor;
  private String[] categoryNames;


  public RoundAdapter(@NonNull Context context, @NonNull List<Round> objects) {
    super(context, R.layout.round_item, objects);
    correctDraw = context.getDrawable(R.drawable.correct_check);
    incorrectDraw = context.getDrawable(R.drawable.incorrect_x);
    correctDescript = context.getString(R.string.correct_description);
    incorrectDescript = context.getString(R.string.incorrect_description);
    correctColor = ContextCompat.getColor(getContext(), R.color.correct);
    incorrectColor = ContextCompat.getColor(getContext(), R.color.incorrect);
    Category[] categories = Category.values();
    categoryNames = new String[categories.length];
    Resources res = context.getResources();
    String pkg = context.getPackageName();
    for (int i = 0; i < categories.length; i++) {
      String name = categories[i].toString();
      int id = res.getIdentifier(name, "string", pkg);
      categoryNames[i] = context.getString(id);
    }
  }

  @SuppressLint("SetTextI18n")
  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    View layout = (convertView != null) ? convertView
        : LayoutInflater.from(getContext()).inflate(R.layout.round_item, parent, false);
    TextView valueDisplay = layout.findViewById(R.id.value_display);
    TextView categoryDisplay = layout.findViewById(R.id.category_display);
    ImageView resultDisplay = layout.findViewById(R.id.result_display);
    TextView userSelectDisplay = layout.findViewById(R.id.user_selected);
    Round round = getItem(position);
    String selectionText =
        (round.getSelection() == null) ? "None" : categoryNames[round.getSelection().ordinal()];
    userSelectDisplay.setText(selectionText);
    valueDisplay.setText(Integer.toString(round.getValue()));
    categoryDisplay.setText(categoryNames[round.getCategory().ordinal()]);
    if (round.isCorrect()) {
      resultDisplay.setImageDrawable(correctDraw);
      resultDisplay.setContentDescription(correctDescript);
      layout.setBackgroundColor(correctColor);
    } else {
      resultDisplay.setImageDrawable(incorrectDraw);
      resultDisplay.setContentDescription(incorrectDescript);
      layout.setBackgroundColor(incorrectColor);
    }
    return layout;
  }

}
