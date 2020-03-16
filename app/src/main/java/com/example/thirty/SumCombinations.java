package com.example.thirty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;


/**
 * SumCombinations
 *
 * Calculates and stores arraylist of solving combinations that sum up to a target sum.
 *
 * @author  Vidar Häggström Fordell, vifo0018
 * @version 1.0
 * @since   2019-07-05
 */

public class SumCombinations {

    //All possible combinations that sum the targetsum
    private Collection<ArrayList<Integer>> solvingCombinations = new LinkedList<>();

    /**
     * Builds all possible combinations of Arraylist of integers that sum the target sum.
     *
     * @param faceValues The dice facevalues as integers
     * @param targetSum The sum determined by scoringmethod,
     */
    public SumCombinations(ArrayList<Integer> faceValues, int targetSum) {
        sum_up_recursive(faceValues, targetSum, new ArrayList<Integer>());
    }

    /**
     * Recursively add dice values to build solving combinations that sum up to target.
     *
     * @param numbers the list of numbers available to use
     * @param target the target to build solving sum combinations
     * @param partial combination used recusively.
     */
    private void sum_up_recursive(ArrayList<Integer> numbers, int target, ArrayList<Integer> partial) {
        int s = 0;
        for (int x : partial) s += x;
        if (s == target) {
            addCombination(partial);
        }
        if (s >= target)
            return;
        for (int i = 0; i < numbers.size(); i++) {
            ArrayList<Integer> remaining = new ArrayList<Integer>();
            int n = numbers.get(i);
            for (int j = i + 1; j < numbers.size(); j++) remaining.add(numbers.get(j));
            ArrayList<Integer> partial_rec = new ArrayList<Integer>(partial);
            partial_rec.add(n);
            sum_up_recursive(remaining, target, partial_rec);
        }
    }

    /**
     * Adds combination and its size to solvingCombinations
     *
     * @param partial the combination of face value(s)
     */
    private void addCombination(ArrayList<Integer> partial) {
        this.getSolvingCombinations().add(partial);
    }

    public Collection<ArrayList<Integer>> getSolvingCombinations() {
        return solvingCombinations;
    }

}
