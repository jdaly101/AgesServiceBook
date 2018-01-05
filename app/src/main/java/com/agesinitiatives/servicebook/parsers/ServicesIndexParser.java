package com.agesinitiatives.servicebook.parsers;

import android.util.Log;

import com.agesinitiatives.servicebook.entities.AgesDate;
import com.agesinitiatives.servicebook.entities.AgesService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class ServicesIndexParser {
    private static final String TAG = "ServiceListParser";
    private JSONObject _jsonObj;

    public ServicesIndexParser(JSONObject jsonObject) {
        this._jsonObj = jsonObject;
    }

    public List<AgesDate> parse() {
        Calendar calendar = Calendar.getInstance();
        List<AgesDate> datesListing = new ArrayList();

        try {
            JSONArray yearsArray = this._jsonObj.getJSONArray("years");
            for (int i=0; i < yearsArray.length(); i++) {
                JSONObject year = yearsArray.getJSONObject(i);
                String yearStr = year.keys().next();
                JSONArray monthArray = year.getJSONArray(yearStr);

                for (int j=0; j < monthArray.length(); j++) {
                    JSONObject month = monthArray.getJSONObject(j);
                    String monthStr = month.keys().next();
                    JSONArray datesInMonth = month.getJSONArray(monthStr);

                    for (int k=0; k < datesInMonth.length(); k++) {
                        JSONObject dateObj = datesInMonth.getJSONObject(k);
                        String dateStr = dateObj.keys().next();
                        JSONArray servicesArray = dateObj.getJSONArray(dateStr);
                        String dateNum = dateStr.substring(0, 2);
                        calendar.set(
                                Integer.parseInt(yearStr),
                                this.parseMonthName(monthStr),
                                Integer.parseInt(dateStr)
                        );
                        Date d = calendar.getTime();

                        List<AgesService> agesServices = new ArrayList();

                        for (int m=0; m < servicesArray.length(); m++) {
                            JSONObject serviceObj = servicesArray.getJSONObject(m);
                            String serviceType = serviceObj.keys().next();
                            agesServices.add(new AgesService(d, serviceType, "tmp"));
                        }

                        datesListing.add(new AgesDate(d, agesServices));
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error during service list parsing: " + e.toString());
        }

        return datesListing;
    }

    private int parseMonthName(String m) {
        int monthInt;
        switch (m) {
            case "January":
                monthInt = 1;
                break;
            case "February":
                monthInt = 2;
                break;
            case "March":
                monthInt = 3;
                break;
            case "April":
                monthInt = 4;
                break;
            case "May":
                monthInt = 5;
                break;
            case "June":
                monthInt = 6;
                break;
            case "July":
                monthInt = 7;
                break;
            case "August":
                monthInt = 8;
                break;
            case "September":
                monthInt = 9;
                break;
            case "October":
                monthInt = 10;
                break;
            case "November":
                monthInt = 11;
                break;
            case "December":
                monthInt = 12;
                break;
            default:
                monthInt = -1;
                break;
        }

        return monthInt;
    }
}
