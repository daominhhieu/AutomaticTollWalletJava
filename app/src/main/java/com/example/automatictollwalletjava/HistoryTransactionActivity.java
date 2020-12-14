package com.example.automatictollwalletjava;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class HistoryTransactionActivity extends AppCompatActivity {

    public static int MESSAGE_KEY = 0;
    ListView listView;
    String[] mTitle = {"12/7/2020", "14/7/2020", "28/7/2020", "10/8/2020", "12/8/2020"};
    String[] mDescription = {"", "", "", "", ""};
    int[] images = {R.drawable.road, R.drawable.nap_tien, R.drawable.road ,R.drawable.road,R.drawable.nap_tien};
    String theUser;

    ArrayList<Integer> budget = new TestStoredFunction().budget_local_test;

    // so our images and other things are set in array
    // now paste some images in drawable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        listView = findViewById(R.id.listView);
        // now create an adapter class
        getSupportActionBar().setTitle("BUDGET: " + budget.get(MESSAGE_KEY).toString());
        HistoryTransactionActivity.MyAdapter adapter = new HistoryTransactionActivity.MyAdapter(this, mTitle, mDescription, images);
        listView.setAdapter(adapter);
        // there is my mistake...
        // now again check this..
        // now set item click on list view
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position ==  0) {
                    MapsActivity.MESSAGE_KEY = 0;
                    startActivity(new Intent(HistoryTransactionActivity.this, MapsActivity.class));
                       }
                if (position ==  1) {

                }
                if (position ==  2) {
                    MapsActivity.MESSAGE_KEY = 2;
                    startActivity(new Intent(HistoryTransactionActivity.this, MapsActivity.class));
                }
                if (position ==  3) {
                    MapsActivity.MESSAGE_KEY = 3;
                    startActivity(new Intent(HistoryTransactionActivity.this, MapsActivity.class));
                }
                if (position ==  4) {

                }
            }
        });
        // so item click is done now check list view
    }

    class MyAdapter extends ArrayAdapter<String> {

        Context context;
        String rTitle[];
        String rDescription[];
        int rImgs[];

        MyAdapter (Context c, String title[], String description[], int imgs[]) {
            super(c, R.layout.row, R.id.textView1, title);
            this.context = c;
            this.rTitle = title;
            this.rDescription = description;
            this.rImgs = imgs;

        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.row, parent, false);
            ImageView images = row.findViewById(R.id.image);
            TextView myTitle = row.findViewById(R.id.textView1);
            TextView myDescription = row.findViewById(R.id.textView2);

            // now set our resources on views
            images.setImageResource(rImgs[position]);
            myTitle.setText(rTitle[position]);
            myDescription.setText(rDescription[position]);




            return row;
        }
    }


}