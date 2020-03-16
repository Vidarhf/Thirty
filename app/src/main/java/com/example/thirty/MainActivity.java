package com.example.thirty;

import android.content.pm.ActivityInfo;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * MainActivity
 * <p>
 * Activity where dice game thirty is played. Forced portraitmode, game is recreated on
 * restoreInstanceState
 *
 * @author Vidar Häggström Fordell, vifo0018
 * @version 1.0
 * @since 2019-07-05
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener
        , PopupMenu.OnMenuItemClickListener,
        ResultScreenFragment.OnFragmentInteractionListener {

    private static final String GAME_PARCEL = "com.example.thirty.game";
    private static final String DIE_1 = "die1";
    private static final String DIE_2 = "die2";
    private static final String DIE_3 = "die3";
    private static final String DIE_4 = "die4";
    private static final String DIE_5 = "die5";
    private static final String DIE_6 = "die6";
    private static final String THROW_NMB = "thrownmb";
    private ImageView imageView1, imageView2, imageView3, imageView4, imageView5, imageView6;
    private Die die1, die2, die3, die4, die5, die6;
    Collection<Die> collectionDices = new LinkedList<>();
    Collection<ImageView> collectionImageViews = new ArrayList<ImageView>();
    Iterator<Die> dieIterator;
    Iterator<ImageView> imageViewIterator;
    int throwNumber;
    int scoringMethod; //1 represents LOW, 2 four, 3 five and so on
    String roundNmbText;
    Game game;
    Button button; //The one button which roll dice or end round

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        collectionImageViews.add(imageView1 = (ImageView) findViewById(R.id.imageView1));
        collectionImageViews.add(imageView2 = (ImageView) findViewById(R.id.imageView2));
        collectionImageViews.add(imageView3 = (ImageView) findViewById(R.id.imageView3));
        collectionImageViews.add(imageView4 = (ImageView) findViewById(R.id.imageView4));
        collectionImageViews.add(imageView5 = (ImageView) findViewById(R.id.imageView5));
        collectionImageViews.add(imageView6 = (ImageView) findViewById(R.id.imageView6));

        imageView1.setOnClickListener(this);
        imageView2.setOnClickListener(this);
        imageView3.setOnClickListener(this);
        imageView4.setOnClickListener(this);
        imageView5.setOnClickListener(this);
        imageView6.setOnClickListener(this);

        button = findViewById(R.id.buttonRoll);
        game = new Game();
        collectionDices.add(die1 = new Die());
        collectionDices.add(die2 = new Die());
        collectionDices.add(die3 = new Die());
        collectionDices.add(die4 = new Die());
        collectionDices.add(die5 = new Die());
        collectionDices.add(die6 = new Die());

        if (savedInstanceState == null) { //First setup
            Log.d("Main", "== null");
            resetRoundUI(collectionDices, collectionImageViews);

        } else { //Being reconstructed, load game
            Log.d("Main", "!= null");
            game = savedInstanceState.getParcelable(GAME_PARCEL);
            resetRoundUI(collectionDices, collectionImageViews);

            //Read dice
            collectionDices.clear();
            collectionDices.add(die1 = savedInstanceState.getParcelable(DIE_1));
            collectionDices.add(die2 = savedInstanceState.getParcelable(DIE_2));
            collectionDices.add(die3 = savedInstanceState.getParcelable(DIE_3));
            collectionDices.add(die4 = savedInstanceState.getParcelable(DIE_4));
            collectionDices.add(die5 = savedInstanceState.getParcelable(DIE_5));
            collectionDices.add(die6 = savedInstanceState.getParcelable(DIE_6));
            throwNumber = savedInstanceState.getInt(THROW_NMB);

            updateThrowNumber(throwNumber);
            setValueImage(collectionDices, collectionImageViews);
        }

        //Button to roll dice and after three throws end round
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (throwNumber < 3) {
                    // Code here executes on main thread after user presses button
                    rollDice();
                    if (incrementThrowNmb() == 3) {
                        setBtnText("End");
                    }
                    setValueImage(collectionDices, collectionImageViews);
                } else if (throwNumber == 3) { //All throws are done, change button to end
                    game.addRound(scoringMethod, collectionDices);
                    openResults(game);
                    resetRoundUI(collectionDices, collectionImageViews);
                }
            }
        });

    }

    /**
     * Updates UI to correspond to thrownumber.
     *
     * @param throwNumber, The number to set correct text
     */
    private void updateThrowNumber(int throwNumber) {
        String text = "";
        if (throwNumber == 0){
            text = "Throw your dice!";
        }
        else if (throwNumber == 1) text = "First throw out of three";
        else if (throwNumber == 2) text = "Second throw out of three";
        else if (throwNumber == 3) {
            text = "Third throw out of three";
            setBtnText("End");
        }
        setThrowNumberText(text);
    }

    /**
     * Resets all the user interface for a new round, and new game if isEnd.
     *
     * @param dices
     * @param imageViews dice images shown and clickable to players
     */
    private void resetRoundUI(Collection<Die> dices, Collection<ImageView> imageViews) {
        // Set score method default is low (1), automatically change to available method.
        for(int i = 10; i > 0; i--) {
            if(game.getMethodHasBeenUsed()[i] == false){
                scoringMethod = i;
            }
        }
        //not able to save during throw number 0
        throwNumber = 0;
        setThrowNumberText("Throw your dice!");
        setBtnText("Roll");
        updateRoundNumberText();

        for (Die die : dices) {
            die.setSaved(false);
            die.roll();
        }
        setValueImage(collectionDices, collectionImageViews);
        if (game.getIsEnd()) {
            game = new Game();
            this.recreate();
        }
    }


    /**
     * Translate roundnumber to correct text to be shown in UI
     */
    private void updateRoundNumberText() {
        roundNmbText = "Round: " + game.getRoundNumber() + "/10";
        ((TextView) findViewById(R.id.txtRoundNmb)).setText(roundNmbText);
    }


    /**
     * Increases roundnumber and updates corresponding UI
     *
     * @return the incremented number
     */
    private int incrementThrowNmb() {
        String nmbText = "";
        String text = "";
        throwNumber++;
        if (throwNumber == 1) nmbText = "First";
        if (throwNumber == 2) nmbText = "Second";
        if (throwNumber == 3) {
            nmbText = "Third";
        }
        text = nmbText + " throw out of three";
        setThrowNumberText(text);
        return throwNumber;
    }

    private void setThrowNumberText(String text) {
        TextView view = this.findViewById(R.id.txtThrowNmb);
        view.setText(text);
    }

    /**
     * Updates the dice faces to current values, grey color if unused, white if thrown and
     * unsaved.
     *
     * @param collectionDices      dices used in game
     * @param collectionImageViews UI images of dice faces
     */
    private void setValueImage(Collection<Die> collectionDices,
                               Collection<ImageView> collectionImageViews) {

        dieIterator = collectionDices.iterator();
        imageViewIterator = collectionImageViews.iterator();
        Die die = new Die();
        ImageView imageView;

        while (dieIterator.hasNext() && imageViewIterator.hasNext()) {
            die = dieIterator.next();
            imageView = imageViewIterator.next();

            if (!die.getIsSaved() && throwNumber == 0) {
                int res = getResources().getIdentifier("grey" + die.getValue(),
                        "drawable",
                        "com.example.thirty");
                imageView.setImageResource(res);
            } else if (!die.getIsSaved()) {
                int res = getResources().getIdentifier("white" + die.getValue(),
                        "drawable",
                        "com.example.thirty");
                imageView.setImageResource(res);
            } else{
                int res = getResources().getIdentifier("red" + die.getValue(),
                        "drawable",
                        "com.example.thirty");
                imageView.setImageResource(res);
            }
        }
    }

    /**
     * Rolls all unsaved dice
     */
    public void rollDice() {
        for (Die die : collectionDices) {
            if (!die.getIsSaved()) {
                die.roll();
            }
        }
    }

    /**
     * Saves or unsaves die when user clicks die face
     *
     * @param v the view clicked
     */
    @Override
    public void onClick(View v) {
        if (throwNumber != 0) {
            switch (v.getId()) {
                case R.id.imageView1:
                    die1.flipSaved();
                    setImageViewSaved(imageView1, die1);
                    break;

                case R.id.imageView2:
                    die2.flipSaved();
                    setImageViewSaved(imageView2, die2);
                    break;

                case R.id.imageView3:
                    die3.flipSaved();
                    setImageViewSaved(imageView3, die3);
                    break;

                case R.id.imageView4:
                    die4.flipSaved();
                    setImageViewSaved(imageView4, die4);
                    break;

                case R.id.imageView5:
                    die5.flipSaved();
                    setImageViewSaved(imageView5, die5);
                    break;

                case R.id.imageView6:
                    die6.flipSaved();
                    setImageViewSaved(imageView6, die6);
                    break;
            }
        }

    }

    /**
     * onClick help method to set die face image. white if unsaved, red if saved.
     *
     * @param imageView die face clicked
     * @param die       corresponding die
     */
    private void setImageViewSaved(ImageView imageView, Die die) {
        ImageView tmp_imageView = imageView;
        if (die.getIsSaved()) {
            int res = getResources().getIdentifier("red" + die.getValue(),
                    "drawable",
                    "com.example.thirty");
            tmp_imageView.setImageResource(res);
        } else if (!die.getIsSaved()) {
            int res = getResources().getIdentifier("white" + die.getValue(),
                    "drawable",
                    "com.example.thirty");
            tmp_imageView.setImageResource(res);
        }
    }

    /**
     * Shows scoringmethod popup menu.
     *
     * @param view the button clicked
     */
    public void showPopup(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.popup_scoring);
        popup.show();
    }

    /**
     * Sets scoringmethod to the alternative chosen in popup-menu
     *
     * @param scoring_method the item selected
     * @return true if item is chosen, false otherwise
     */
    @Override
    public boolean onMenuItemClick(MenuItem scoring_method) {
        switch (scoring_method.getItemId()) {
            case R.id.low:
                if (game.getMethodHasBeenUsed()[1] == false) {
                    Toast.makeText(this, "low chosen", Toast.LENGTH_SHORT).show();
                    setScoringMethod(1);
                    return true;
                } else {
                    Toast.makeText(this,
                            "Already used, pick another", Toast.LENGTH_SHORT).show();
                    return true;
                }

            case R.id.four:
                if (game.getMethodHasBeenUsed()[2] == false) {
                    Toast.makeText(this, "four chosen", Toast.LENGTH_SHORT).show();
                    setScoringMethod(2);
                    return true;
                } else {
                    Toast.makeText(this,
                            "Already used, pick another", Toast.LENGTH_SHORT).show();
                    return true;
                }
            case R.id.five:
                if (game.getMethodHasBeenUsed()[3] == false) {
                    Toast.makeText(this, "five chosen", Toast.LENGTH_SHORT).show();
                    setScoringMethod(3);
                    return true;
                } else {
                    Toast.makeText(this,
                            "Already used, pick another", Toast.LENGTH_SHORT).show();
                    return true;
                }
            case R.id.six:
                if (game.getMethodHasBeenUsed()[4] == false) {
                    Toast.makeText(this, "six chosen", Toast.LENGTH_SHORT).show();
                    setScoringMethod(4);
                    return true;
                } else {
                    Toast.makeText(this,
                            "Already used, pick another", Toast.LENGTH_SHORT).show();
                    return true;
                }
            case R.id.seven:
                if (game.getMethodHasBeenUsed()[5] == false) {
                    Toast.makeText(this, "seven chosen", Toast.LENGTH_SHORT).show();
                    setScoringMethod(5);
                    return true;
                } else {
                    Toast.makeText(this,
                            "Already used, pick another", Toast.LENGTH_SHORT).show();
                    return true;
                }
            case R.id.eight:
                if (game.getMethodHasBeenUsed()[6] == false) {
                    Toast.makeText(this, "eight chosen", Toast.LENGTH_SHORT).show();
                    setScoringMethod(6);
                    return true;
                } else {
                    Toast.makeText(this,
                            "Already used, pick another", Toast.LENGTH_SHORT).show();
                    return true;
                }
            case R.id.nine:
                if (game.getMethodHasBeenUsed()[7] == false) {
                    Toast.makeText(this, "nine chosen", Toast.LENGTH_SHORT).show();
                    setScoringMethod(7);
                    return true;
                } else {
                    Toast.makeText(this,
                            "Already used, pick another", Toast.LENGTH_SHORT).show();
                    return true;
                }
            case R.id.ten:
                if (game.getMethodHasBeenUsed()[8] == false) {
                    Toast.makeText(this, "ten chosen", Toast.LENGTH_SHORT).show();
                    setScoringMethod(8);
                    return true;
                } else {
                    Toast.makeText(this,
                            "Already used, pick another", Toast.LENGTH_SHORT).show();
                    return true;
                }
            case R.id.eleven:
                if (game.getMethodHasBeenUsed()[9] == false) {
                    Toast.makeText(this, "eleven chosen", Toast.LENGTH_SHORT).show();
                    setScoringMethod(9);
                    return true;
                } else {
                    Toast.makeText(this,
                            "Already used, pick another", Toast.LENGTH_SHORT).show();
                    return true;
                }
            case R.id.twelve:
                if (game.getMethodHasBeenUsed()[10] == false) {
                    Toast.makeText(this, "twelve chosen", Toast.LENGTH_SHORT).show();
                    setScoringMethod(10);
                    return true;
                } else {
                    Toast.makeText(this,
                            "Already used, pick another", Toast.LENGTH_SHORT).show();
                    return true;
                }
            default:
                return false;
        }
    }

    /**
     * Open resultscreen
     *
     * @param game to open corresponding resultScreen
     */
    public void openResults(Game game) {
        ResultScreenFragment fragment;
        if (!game.getIsEnd()) {
            fragment = ResultScreenFragment.newInstance(game.getRoundEntries(), game.getIsEnd(),
                    game.getTotalScoreString());
        } else {
            //Change to end screen
            fragment = ResultScreenFragment.newInstance(game.getScoreSummary(), game.getIsEnd(),
                    game.getTotalScoreString());

        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right,
                R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.add(R.id.fragment_container, fragment, "BLANK_FRAGMENT").commit();
    }

    /**
     * Saves current game, dice and throw number to instancestate.
     *
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(GAME_PARCEL, game);
        Iterator<Die> iterator = collectionDices.iterator();
        outState.putParcelable(DIE_1, iterator.next());
        outState.putParcelable(DIE_2, iterator.next());
        outState.putParcelable(DIE_3, iterator.next());
        outState.putParcelable(DIE_4, iterator.next());
        outState.putParcelable(DIE_5, iterator.next());
        outState.putParcelable(DIE_6, iterator.next());
        outState.putInt(THROW_NMB, throwNumber);
    }

    @Override
    public void onFragmentInteraction() {
        onBackPressed();
    }

    /**************GETTERS AND SETTERS**************/
    private void setScoringMethod(int i) {
        scoringMethod = i;
    }

    private void setBtnText(String text) {
        button.setText(text);
    }

}
