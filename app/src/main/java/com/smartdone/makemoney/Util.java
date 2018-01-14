package com.smartdone.makemoney;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by smartdone on 2018/1/13.
 */

public class Util {
    public static String getQuestion(String s){
        String question = null;
        try {
            JSONObject jsonObject = new JSONObject(s);
            question = jsonObject.get("desc").toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return question;
    }

    public static String[] getAnswers(String s) {
        List<String> answers = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray jsonArray = new JSONArray(jsonObject.get("options").toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                answers.add(jsonArray.get(i).toString().trim());
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return answers.toArray(new String[answers.size()]);
    }
}
