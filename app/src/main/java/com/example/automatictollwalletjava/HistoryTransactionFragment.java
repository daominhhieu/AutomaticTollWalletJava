package com.example.automatictollwalletjava;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.automatictollwalletjava.MyUtilities.MyAdapterHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;
import static com.example.automatictollwalletjava.MainActivity.MainActivityPhone;
import static com.example.automatictollwalletjava.MainActivity.MainSocketHandler;

public class HistoryTransactionFragment extends Fragment {
    public static int MESSAGE_KEY = 0;
    ListView History_lv;


    String[] mTitle_dummy = {"12/7/2020", "14/7/2020", "28/7/2020", "10/8/2020", "12/8/2020"};
    String[] mDescription_dummy = {"", "", "", "", ""};
    int pay_fee_symbol = R.drawable.history_pay_fee;
    int money_symbol = R.drawable.history_add_money;
    int[] mSymbol_dummy = {pay_fee_symbol, money_symbol, pay_fee_symbol,pay_fee_symbol,money_symbol};

    ArrayList<String> mTitle = new ArrayList<String>();
    ArrayList<String> mDescription = new ArrayList<String>();
    ArrayList<Integer> mSymbol = new ArrayList<Integer>();

    private HashMap<String, String> message = new HashMap<String, String>();
    private int record_index=0;
    private MutableLiveData<Integer> init_record_cnt = new MutableLiveData<Integer>();



    private final int INIT_RECORD = 8;
//    ArrayList<Integer> budget = new TestStoredFunction().budget_local_test;

    public HistoryTransactionFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.history_transaction_record_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init_record_cnt.postValue(INIT_RECORD);

        History_lv = (ListView) getActivity().findViewById(R.id.History_lv);
        MyAdapterHandler History_lv_adapter_dummy = new MyAdapterHandler(
                requireActivity(),
                String2ArrayList_String(mTitle_dummy),
                String2ArrayList_String(mDescription_dummy),
                Integer2ArrayList_Integer(mSymbol_dummy));

        History_lv.setAdapter(History_lv_adapter_dummy);

        History_lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                int temp_index = visibleItemCount + firstVisibleItem;
//                if(temp_index > record_index){
//                    record_index = temp_index;
//                    search_history(record_index);
//                }
            }
        });

        init_record_cnt.observe(requireActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                LogError("init_record_cnt:..." + String.valueOf(integer));
                int temp_index = INIT_RECORD - integer;
                LogError("INIT_RECORD - integer:..." + String.valueOf(temp_index));
                if(temp_index < INIT_RECORD){
                    search_history(temp_index);
                }

            }
        });

        MainSocketHandler.BodyHashMutate.observe(requireActivity(), new Observer<HashMap<String, String>>() {
            @Override
            public void onChanged(HashMap<String, String> stringStringHashMap) {
                String result = stringStringHashMap.get("result");
                String action = stringStringHashMap.get("action");
                if(action == null || result == null){
                    LogError("Null result or action");
                    return;
                }
                if (action.equals("gethistory")) {
                    if (result.equals("good")) {
                        try{
                            String tmp_Longitude1 = stringStringHashMap.get("Longitude1");
                            String tmp_Latitude1 = stringStringHashMap.get("Latitude1");
                            String tmp_money = stringStringHashMap.get("money");
                            String tmp_time = stringStringHashMap.get("time");
                            String tmp_vehicle_name = stringStringHashMap.get("vehicle_name");
                            String tmp_vehicle_mass = stringStringHashMap.get("vehicle_mass");
                            String tmp_Longitude2 = stringStringHashMap.get("Longitude2");
                            String tmp_Latitude2 = stringStringHashMap.get("Latitude2");
                            String tmp_street = stringStringHashMap.get("street");
                            
                            if(tmp_Longitude1.equals("0.0") ||
                                    tmp_Latitude1.equals("0.0") ||
                                    tmp_Longitude2.equals("0.0") ||
                                    tmp_Latitude2.equals("0.0") ||
                                    tmp_vehicle_name.equals("None") ||
                                    tmp_vehicle_mass.equals("0") ||
                                    tmp_street.equals("None")){
                                if(tmp_money.contains("-")){
                                    mTitle.add(tmp_time + "   Retrieve");
                                }else{
                                    mTitle.add(tmp_time + "   Add");
                                }
                                mDescription.add(tmp_money + " VND");
                                mSymbol.add(money_symbol);

                                MyAdapterHandler History_lv_adapter = new MyAdapterHandler(
                                        requireActivity(), mTitle, mDescription, mSymbol);

                                History_lv.setAdapter(History_lv_adapter);
                            }
                            if(init_record_cnt.getValue() > 0){
                                Timer search_history_init_timer = new Timer();
                                search_history_init_timer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        init_record_cnt.postValue(init_record_cnt.getValue() - 1);
                                    }
                                }, 2000L);

                            }


                        }catch (Exception e){
                            LogError("Exception:..."+e);
                        }
                        return;
                    }
                    LogError("Bad result");
                    init_record_cnt.setValue(0);
                }
            }
        });

//        TestStoredFunction.Track_profile track = new TestStoredFunction.Track_profile(true);
//
//        track.start_coord = TestStoredFunction.coordinates_set.get(0);
//        track.end_coord = TestStoredFunction.coordinates_set.get(1);
//        int fee1 = new TestStoredFunction.Transaction_Profile(mTitle[0], track).transaction_value;
//        mDescription[0] = "Pay Fee "+ fee1 + " VND";
//
//        mDescription[1] = "Insert "+ new TestStoredFunction.Transaction_Profile(mTitle[1], 20000).transaction_value + " VND";
//
//        track.start_coord = TestStoredFunction.coordinates_set.get(2);
//        track.end_coord = TestStoredFunction.coordinates_set.get(3);
//        int fee2 = new TestStoredFunction.Transaction_Profile(mTitle[0], track).transaction_value;
//        mDescription[2] = "Pay Fee "+ fee2 + " VND";
//
//        track.start_coord = TestStoredFunction.coordinates_set.get(3);
//        track.end_coord = TestStoredFunction.coordinates_set.get(4);
//        int fee3 = new TestStoredFunction.Transaction_Profile(mTitle[0], track).transaction_value;
//        mDescription[3] = "Pay Fee "+ fee3 + " VND";
//
//        mDescription[4] = "Insert "+ new TestStoredFunction.Transaction_Profile(mTitle[4], 10000).transaction_value + " VND";

        History_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position ==  0) {
                    MapsFragment.MESSAGE_KEY = 0;
                    startActivity(new Intent(getActivity(), MapsFragment.class));
                }
                if (position ==  1) {

                }
                if (position ==  2) {
                    MapsFragment.MESSAGE_KEY = 2;
                    startActivity(new Intent(getActivity(), MapsFragment.class));
                }
                if (position ==  3) {
                    MapsFragment.MESSAGE_KEY = 3;
                    startActivity(new Intent(getActivity(), MapsFragment.class));
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

    private void search_history(int loc_index){
        try{
            LogError("INIT_RECORD - integer search history:..."+String.valueOf(loc_index));
            message.put("phone", MainActivityPhone);
            message.put("action", "gethistory");
            message.put("index", String.valueOf(loc_index));
            MainSocketHandler.StartWrite(message);
        }catch (Exception e){
            LogError("search history error:..." + e);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void LogError(String string)
    {
        Log.d(TAG,this.getClass().getSimpleName() + " : " +  string);
    }
}