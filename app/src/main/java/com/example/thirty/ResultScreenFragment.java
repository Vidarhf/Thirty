package com.example.thirty;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;



/**
 * ResultScreenFragment
 *
 * Fragment displaying total score and a list populated by stringarray argument.
 *
 * @author  Vidar Häggström Fordell, vifo0018
 * @version 1.0
 * @since   2019-07-05
 */

public class ResultScreenFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String[] LIST_ENTRIES = new String[30];
    private static final Boolean IS_END = false;
    private static final String TOTAL_SCORE = "";


    // TODO: Rename and change types of parameters //Ta in en round
    private Boolean misEnd;
    private String btnEndText = "Play again?";
    private String[] mListEntries;
    private String mTotalScore;

    private OnFragmentInteractionListener mListener;

    private Button buttonFragment;
    private TextView textViewTotalScore;
    private ListView listView;
    ArrayAdapter<String> listViewAdapter;

    public ResultScreenFragment() {
        // Required empty public constructor
    }

    /**
     * Constructor
     *
     * @param listEntries to populate list shown
     * @param isEnd If this is the final resultscreen
     * @param totalScoreString to show at top of screen
     * @return
     */
    public static ResultScreenFragment newInstance(String[] listEntries, boolean isEnd, String totalScoreString) {
        ResultScreenFragment fragment = new ResultScreenFragment();
        Bundle args = new Bundle();
        args.putStringArray("LIST_ENTRIES", listEntries);
        args.putBoolean("IS_END", isEnd);
        args.putString("TOTAL_SCORE", totalScoreString);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mListEntries = getArguments().getStringArray("LIST_ENTRIES");
            misEnd = getArguments().getBoolean("IS_END");
            mTotalScore = getArguments().getString("TOTAL_SCORE");

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_result_screen, container, false);

        listView = view.findViewById(R.id.roundListView);
        textViewTotalScore = view.findViewById(R.id.textView_totalScore);
        textViewTotalScore.setText(mTotalScore);
        //Toast.makeText(getApplicationContext(), "Rolling dice...", Toast.LENGTH_SHORT).show();
        listViewAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                mListEntries);
        listView.setAdapter(listViewAdapter);
        buttonFragment = view.findViewById(R.id.button_fragment);
        if(misEnd){
            buttonFragment.setText(btnEndText);
            buttonFragment.setBackgroundColor(Color.RED);
        }

        buttonFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBack();
            }
        });

        return view;
    }

    public void sendBack() {
        if (mListener != null) {
            mListener.onFragmentInteraction();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }
}
