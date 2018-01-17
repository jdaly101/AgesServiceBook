package com.agesinitiatives.servicebook.parsers;

import android.util.Log;

import com.agesinitiatives.servicebook.entities.AgesDate;
import com.agesinitiatives.servicebook.entities.AgesService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class ServicesIndexParser {
    private static final String TAG = "ServicesIndexParser";
    private JSONObject _jsonObj;
    private List<AgesDate> _parsedDatesList;

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
                                Integer.parseInt(dateNum )
                        );
                        Date d = calendar.getTime();

                        List<AgesService> agesServices = new ArrayList();

                        for (int m=0; m < servicesArray.length(); m++) {
                            JSONObject serviceObj = servicesArray.getJSONObject(m);
                            String serviceType = serviceObj.keys().next();
                            String serviceUrl = getServiceTextUrl(serviceObj.getJSONArray(serviceType));
                            agesServices.add(new AgesService(d, serviceType, serviceUrl));
                        }

                        datesListing.add(new AgesDate(d, agesServices));
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error during service list parsing: " + e.toString());
        }

        _parsedDatesList = datesListing;

        return datesListing;
    }

    public List<String> getDatesList() {
        List<String> retList = new ArrayList();
        for (int i=0; i < _parsedDatesList.size(); i++) {
            retList.add(_parsedDatesList.get(i).toString());
        }
        return retList;
    }

    public HashMap<String, List<String>> getServicesHashMap() {
        HashMap<String, List<String>> retHash = new HashMap<>();
        for (int i=0; i < _parsedDatesList.size(); i++) {
            List<String> dateServices = new ArrayList<>();
            AgesDate agesDate = _parsedDatesList.get(i);
            for (int j=0; j < agesDate.services.size(); j++) {
                dateServices.add(agesDate.services.get(j).toString());
            }
            retHash.put(agesDate.toString(), dateServices);
        }
        return retHash;
    }

    private String getServiceTextUrl(JSONArray serviceArray) {
        String retString = "";
        for (int i=0; i < serviceArray.length(); i++) {
            try {
                JSONObject serviceObj = serviceArray.getJSONObject(i);
                String translationLangs = serviceObj.keys().next();
                String serviceHref = serviceObj.getJSONArray(translationLangs).getJSONObject(0).getString("href");
                String serviceType = serviceObj.getJSONArray(translationLangs).getJSONObject(0).getString("type");
                if (serviceType.equalsIgnoreCase("Text/Music"))
                    if (translationLangs.equalsIgnoreCase("GR-EN"))
                        retString = serviceHref;
            } catch (JSONException e) {
                Log.e(TAG, e.toString());
            }
        }
        return retString;
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

        return monthInt-1;
    }
}
