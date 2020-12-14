package com.example.automatictollwalletjava;

import com.example.automatictollwalletjava.data.Result;
import com.example.automatictollwalletjava.data.model.LoggedInUser;

import java.util.ArrayList;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class TestStoredFunction {

    private final int scale = 1500;

    private double[][] init_coordinates_set = {{10.788661, 106.703486},
            {10.790935, 106.705633},
            {10.849737, 106.768509},
            {10.847595, 106.768415},
            {10.847426, 106.770340}};
    public static ArrayList<double[]> coordinates_set = new ArrayList<double[]>();

    private String[][] init_position_name = {{"Nguyen Thi Minh Khai","Nguyen Binh Khiem"},
            {"Nguyen Thi Minh Khai","Hoang Sa"},
            {"Vo Van Ngan","Doan Ket"},
            {"Doan Ket","Chu Manh Trinh"},
            {"Huu Nghi","Chu Manh Trinh"}};
    public static ArrayList<String[]> position_name = new ArrayList<String[]>();

    private String[][] init_user_information_local_test =
            {{"daominhhieu3@gmail.com", "Hieu2312.","Dao Minh Hieu"},
            {"toiluonlahieu@gmail.com", "Hieu2312.","not Dao Minh Hieu"}};
    public ArrayList<String[]> user_information_local_test = new ArrayList<String[]>();

    private int[] init_budget_local_test ={100000, 200000};
    public ArrayList<Integer> budget_local_test = new ArrayList<Integer>();

    public TestStoredFunction() {
        for (double[] item : this.init_coordinates_set) {
            this.coordinates_set.add(item);
        }
        for (String[] item : this.init_position_name) {
            this.position_name.add(item);
        }
        for (String[] item : this.init_user_information_local_test) {
            this.user_information_local_test.add(item);
        }
        for (int item : this.init_budget_local_test) {
            this.budget_local_test.add(item);
        }

    }

    public static class Transaction_Profile
    {
        public String transaction_date = null;
        public boolean transaction_type = false;
        public int transaction_value = 0;
        public Track_profile transaction_track = null;
        public Transaction_Profile(String transaction_date, Track_profile transaction_track)
        {
            this.transaction_date = transaction_date;
            this.transaction_type = false;
            this.transaction_value = transaction_value_calculation(transaction_track);
            this.transaction_track = transaction_track;
        }

        public Transaction_Profile(String transaction_date, int transaction_value)
        {
            this.transaction_date = transaction_date;
            this.transaction_type = true;
            this.transaction_value = transaction_value;
        }

        private int transaction_value_calculation(Track_profile loc_transaction_track)
        {
            if(loc_transaction_track.validation)
            {
                return (int) (sqrt( pow( (loc_transaction_track.start_coord[0] - loc_transaction_track.end_coord[0]), 2 ) +
                        pow( (loc_transaction_track.start_coord[1] - loc_transaction_track.end_coord[1]), 2 ) )*1000 *1500);
            }
            else
            {
                return 0;
            }
        }

        public String transaction_trajectory(Track_profile loc_transaction_track)
        {
            String[] first_section;
            String[] second_section;

            first_section = position_name.get(coordinates_set.indexOf(loc_transaction_track.start_coord));
            second_section = position_name.get(coordinates_set.indexOf(loc_transaction_track.end_coord));

            if(first_section[0].equals(second_section[0]) || first_section[0].equals(second_section[1]))
            {
                return first_section[0];
            }
            else
            {
                return first_section[1];
            }
        }
    }

    public static class Track_profile
    {
        public boolean validation;
        public double[] start_coord;
        public double[] end_coord;
        public Track_profile(boolean validation)
        {
            this.validation = validation;
        }
        public Track_profile()
        {
            this.validation = false;
        }
    }

    public Result VerifiedUser(String user, String password)
    {
        for(String[] item : this.user_information_local_test)
        {
            if(item[0].equals(user))
            {
                if(item[1].equals(password))
                {
                    int loc_ID =  this.user_information_local_test.indexOf(item);
                    return new Result.Success<>(new LoggedInUser(item[0], item[2], loc_ID));
                }
            }
        }
        return new Result.Error("Login Fail") ;
    }



}
