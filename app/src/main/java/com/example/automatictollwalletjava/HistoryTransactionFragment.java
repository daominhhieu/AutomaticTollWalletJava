package com.example.automatictollwalletjava;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.automatictollwalletjava.MyUtilities.MyAdapterHandler;
import com.example.automatictollwalletjava.ui.login.TestStoredFunction;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryTransactionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryTransactionFragment extends Fragment {
    public static int MESSAGE_KEY = 0;
    ListView History_lv;
    String[] mTitle = {"12/7/2020", "14/7/2020", "28/7/2020", "10/8/2020", "12/8/2020"};
    String[] mDescription = {"", "", "", "", ""};
    int[] mImages = {R.drawable.road, R.drawable.nap_tien, R.drawable.road ,R.drawable.road,R.drawable.nap_tien};
    String theUser;
    ArrayList<Integer> budget = new TestStoredFunction().budget_local_test;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HistoryTransactionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HisotryTransactionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryTransactionFragment newInstance(String param1, String param2) {
        HistoryTransactionFragment fragment = new HistoryTransactionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history_transaction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        History_lv = (ListView) getActivity().findViewById(R.id.History_lv);
        MyAdapterHandler History_lv_adapter = new MyAdapterHandler(
                getActivity(),
                String2ArrayList_String(mTitle),
                String2ArrayList_String(mDescription),
                Integer2ArrayList_Integer(mImages));


        History_lv.setAdapter(History_lv_adapter);

        TestStoredFunction.Track_profile track = new TestStoredFunction.Track_profile(true);

        track.start_coord = TestStoredFunction.coordinates_set.get(0);
        track.end_coord = TestStoredFunction.coordinates_set.get(1);
        int fee1 = new TestStoredFunction.Transaction_Profile(mTitle[0], track).transaction_value;
        mDescription[0] = "Pay Fee "+ fee1 + " VND";

        mDescription[1] = "Insert "+ new TestStoredFunction.Transaction_Profile(mTitle[1], 20000).transaction_value + " VND";

        track.start_coord = TestStoredFunction.coordinates_set.get(2);
        track.end_coord = TestStoredFunction.coordinates_set.get(3);
        int fee2 = new TestStoredFunction.Transaction_Profile(mTitle[0], track).transaction_value;
        mDescription[2] = "Pay Fee "+ fee2 + " VND";

        track.start_coord = TestStoredFunction.coordinates_set.get(3);
        track.end_coord = TestStoredFunction.coordinates_set.get(4);
        int fee3 = new TestStoredFunction.Transaction_Profile(mTitle[0], track).transaction_value;
        mDescription[3] = "Pay Fee "+ fee3 + " VND";

        mDescription[4] = "Insert "+ new TestStoredFunction.Transaction_Profile(mTitle[4], 10000).transaction_value + " VND";

        History_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position ==  0) {
                    MapsActivity.MESSAGE_KEY = 0;
                    startActivity(new Intent(getActivity(), MapsActivity.class));
                }
                if (position ==  1) {

                }
                if (position ==  2) {
                    MapsActivity.MESSAGE_KEY = 2;
                    startActivity(new Intent(getActivity(), MapsActivity.class));
                }
                if (position ==  3) {
                    MapsActivity.MESSAGE_KEY = 3;
                    startActivity(new Intent(getActivity(), MapsActivity.class));
                }
                if (position ==  4) {

                }
            }
        });
    }

    private ArrayList<String> String2ArrayList_String(String[] strings)
    {
        ArrayList<String> tmp_ArrayList = new ArrayList<String>();
        if(strings.length > 0)
        {
            for(String item : strings)
            {
                tmp_ArrayList.add(item);
            }
        }
        return tmp_ArrayList;
    }

    private ArrayList<Integer> Integer2ArrayList_Integer(int[] integers)
    {
        ArrayList<Integer> tmp_ArrayList = new ArrayList<Integer>();
        if(integers.length > 0)
        {
            for(Integer item : integers)
            {
                tmp_ArrayList.add(item);
            }
        }
        return tmp_ArrayList;
    }

}