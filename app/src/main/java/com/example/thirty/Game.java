package com.example.thirty;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;


/**
 * Game
 * <p>
 * Records information about the dice game in String arrays.
 *
 * @author Vidar Häggström Fordell, vifo0018
 * @version 1.0
 * @since 2019-07-05
 */

public class Game implements Parcelable {

    private int roundNumber;
    private String[] roundEntries;
    private String[] scoreSummary; //of each scoring method used
    private int[] scoringMethodTotal;
    private int totalScore;
    private String totalScoreString;
    private Boolean isEnd;



    private boolean[] methodHasBeenUsed;


    public Game() {
        setTotalScore(0);
        roundNumber = 1;
        isEnd = false;
        roundEntries = new String[10];
        scoreSummary = new String[10];
        scoringMethodTotal = new int[10];
        methodHasBeenUsed = new boolean[11];
        //Populate entries
        for (int i = 0; i <= 9; i++) {
            roundEntries[i] = (i + 1) + ". ";
            scoreSummary[i] = (scoringMethodToText(i + 1)) + ": ";
            scoringMethodTotal[i] = 0;
        }

    }

    protected Game(Parcel in) {
        roundNumber = in.readInt();
        roundEntries = in.createStringArray();
        scoreSummary = in.createStringArray();
        scoringMethodTotal = in.createIntArray();
        methodHasBeenUsed = in.createBooleanArray();
        totalScore = in.readInt();
        totalScoreString = in.readString();
        byte tmpIsEnd = in.readByte();
        isEnd = tmpIsEnd == 0 ? null : tmpIsEnd == 1;
    }

    public static final Creator<Game> CREATOR = new Creator<Game>() {
        @Override
        public Game createFromParcel(Parcel in) {
            return new Game(in);
        }

        @Override
        public Game[] newArray(int size) {
            return new Game[size];
        }
    };

    /**
     * Adds round to be saved in the game.
     *
     * @param scoringMethodUsed chosen by user 1 is low, 2 is four, 3 is five and so on..
     * @param collectionDices   at the end of round
     */
    public void addRound(int scoringMethodUsed, Collection<Die> collectionDices) {
        if (getRoundNumber() == 10) isEnd = true;
        int score = calculateScore(scoringMethodUsed, collectionDices);

        String scoringMethod = scoringMethodToText(scoringMethodUsed);
        roundEntries[getRoundNumber() - 1] = roundEntries[getRoundNumber() - 1] +
                "Score: " + score + "p  - with scoring method: " + scoringMethod;

        addToSummary(score, scoringMethodUsed);
        setRoundNumber(getRoundNumber() + 1);
        methodHasBeenUsed[scoringMethodUsed] = true;
    }


    /**
     * Calculate round score
     * if scoringmethod is not low
     * Build all combinations of the die/dice that matches sum
     * Remove single die matching sum
     * With remaining dice try match 2 dice that match sum, if succeed repeat
     * With remaining dice try match 3 dice that match sum, if succeed repeat
     * -and so on until all remaining (at most six) cant be matched
     *
     * @param scoringMethodUsed chosen by user 1 is low, 2 is four, 3 is five and so on..
     * @param collectionDices   at the end of round.
     * @return the total score.
     */
    private int calculateScore(int scoringMethodUsed, Collection<Die> collectionDices) {
        int roundTotal = 0;
        int scoringSum = scoringMethodUsed + 2; //scoringMethodUsed + 2 is always the sum of scoring method
        LinkedList<Die> dices = new LinkedList<>(collectionDices);
        ListIterator<Die> listIterator = dices.listIterator();
        ArrayList<Integer> faceValues = new ArrayList<>(); //The facevalues of dice
        int i = 0;

        //Add all dice values to be used in calculation
        int fillValuesIt = 0;
        for (Die die : dices) {
            faceValues.add(fillValuesIt, die.getValue());
            fillValuesIt++;

        }

        if (scoringMethodUsed == 1) { //Low chosen, add face values lower than 4
            for (Die die : dices
            ) {
                if (die.getValue() < 4) {
                    roundTotal = roundTotal + die.getValue();
                }
            }
        } else if (scoringMethodUsed >= 2 && scoringMethodUsed <= 10) {
            SumCombinations sumUp = new SumCombinations(faceValues, scoringSum);
            Collection<ArrayList<Integer>> combos = sumUp.getSolvingCombinations();
            int comboSize = 0;
            boolean available = false;

            for (int minSize = 1; minSize <= 6; minSize++) {
                Iterator<ArrayList<Integer>> iterator = combos.iterator();
                while (iterator.hasNext()) {
                    ArrayList<Integer> combo = iterator.next();

                    if (combo.size() != minSize || !faceValues.containsAll(combo)) {
                        available = false;
                    } else {
                        available = true;
                        //Check that facevalues contain all occurances of integers in combo else
                        //not available!
                        for (int j = 0; j < combo.size(); j++) {
                            int number = combo.get(j);
                            int comboOcc = Collections.frequency(combo, number);
                            int fvFreq = Collections.frequency(faceValues, number);
                            if (comboOcc > fvFreq) available = false;
                        }
                    }
                    if (available) {
                        for (int j = 0; j < combo.size(); j++) faceValues.remove(combo.get(j));
                        roundTotal = roundTotal + scoringSum;
                    }


                }
            }


        }
        return roundTotal;
    }

    /**
     * Updates total score, round summary and scoring summary
     *
     * @param score
     * @param scoringMethod
     */
    private void addToSummary(int score, int scoringMethod) {
        setTotalScore(getTotalScore() + score);
        setTotalScoreString("Total score: " + getTotalScore() + "p");
        scoringMethodTotal[scoringMethod - 1] = scoringMethodTotal[scoringMethod - 1] + score;
        scoreSummary[scoringMethod - 1] = (scoringMethodToText(scoringMethod)) + ": "
                + scoringMethodTotal[scoringMethod - 1] + "p";
    }

    /**
     * Translates integer of scoring method to text
     *
     * @param scoringMethodUsed chosen by user, 1-10
     * @return Low if 1, 2 if four, 3 if five and so on..
     */
    private String scoringMethodToText(int scoringMethodUsed) {
        switch (scoringMethodUsed) {
            case 1:

                return "Low";


            case 2:

                return "Four";

            case 3:

                return "Five";

            case 4:

                return "Six";

            case 5:

                return "Seven";

            case 6:

                return "Eight";

            case 7:

                return "Nine";

            case 8:

                return "Ten";

            case 9:

                return "Eleven";

            case 10:

                return "Twelve";

            default:
                return "ERR";

        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(roundNumber);
        dest.writeStringArray(roundEntries);
        dest.writeStringArray(scoreSummary);
        dest.writeIntArray(scoringMethodTotal);
        dest.writeBooleanArray(methodHasBeenUsed);
        dest.writeInt(totalScore);
        dest.writeString(totalScoreString);
        dest.writeByte((byte) (isEnd == null ? 0 : isEnd ? 1 : 2));
    }

    /**************GETTERS AND SETTERS**************/

    public int getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
    }

    public Boolean getIsEnd() {
        return isEnd;
    }

    public String[] getRoundEntries() {
        return roundEntries;
    }

    public boolean[] getMethodHasBeenUsed() {
        return methodHasBeenUsed;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public String[] getScoreSummary() {
        return scoreSummary;
    }

    public String getTotalScoreString() {
        return totalScoreString;
    }

    public void setTotalScoreString(String totalScoreString) {
        this.totalScoreString = totalScoreString;
    }


}
