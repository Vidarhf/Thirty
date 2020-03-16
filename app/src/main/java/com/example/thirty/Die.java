package com.example.thirty;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Random;

/**
 * Created by johane on 2018-01-09.
 * A representation of a 6 sided Die
 * Edited by Vidar Häggström, 2019-06-11.
 */


/**
 * Die
 *
 * A 6 sided die with random value. Possible to save or unsave.
 *
 * @author  Vidar Häggström Fordell, vifo0018
 * @version 1.0
 * @since   2019-07-05
 */
public class Die implements Parcelable {
    private int value;
    private Random rand;
    private Boolean isSaved;

    public Die() {
        rand=new Random();
        isSaved = false;
        roll();
    }

    protected Die(Parcel in) {
        value = in.readInt();
        rand = new Random();
        isSaved = in.readInt() != 0;
    }


    public static final Creator<Die> CREATOR = new Creator<Die>() {
        @Override
        public Die createFromParcel(Parcel in) {
            return new Die(in);
        }

        @Override
        public Die[] newArray(int size) {
            return new Die[size];
        }
    };

    public void setSaved(Boolean saved) {
        isSaved = saved;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(getValue());
        dest.writeInt(getIsSaved() ? 1 : 0);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Sets die to saved if unsaved, otherwise unsave die
     */
    public void flipSaved(){
        isSaved = !isSaved;
    }

    /**
     * Rolls die by setting changing value to new random number between 1-6.
     */
    public void roll() {
        value=rand.nextInt(6)+1;
    }

    public int getValue() {
        return value;
    }

    public Boolean getIsSaved(){
        return isSaved;
    }

}

