package com.smartdone.makemoney;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private String question = "";
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == SearchThread.SEARCH_SUCCESS) {
                HashMap<String, String> answers = (HashMap<String, String>) msg.obj;
                List<Integer> as = new ArrayList<>();
                String showMsg = "";
                for(String key : answers.keySet()) {
                    as.add(Integer.parseInt(answers.get(key)));
                    showMsg += key + "（结果个数）:" + answers.get(key) + "\n";
                }
                Collections.sort(as);

                if(question.contains("不")) {
                    for(String key : answers.keySet()) {
                        if(Integer.parseInt(answers.get(key)) == as.get(0)) {
                            Log.w(Main.TAG, "推荐答案： " + key);
                            showMsg += "推荐答案：" + key;
                            break;
                        }
                    }
                } else {
                    for(String key : answers.keySet()) {
                        if(Integer.parseInt(answers.get(key)) == as.get(as.size() - 1)) {
                            Log.w(Main.TAG, "推荐答案： " + key);
                            showMsg += "推荐答案：" + key;
                            break;
                        }
                    }
                }

                Toast.makeText(getApplicationContext(), showMsg, Toast.LENGTH_SHORT).show();

            } else if(msg.what == SearchThread.SEARCH_FAILER) {
                Log.d(Main.TAG, "failer");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.text);

//        String[] answers = {"威龙", "飞鲨", "火牙"};
//        new SearchThread(handler, question, answers).start();
        String content = "{\"answerTime\":10,\"correctOption\":1,\"desc\":\"5.在日本送给朋友蛋包饭可以表示什么？\",\"displayOrder\":4,\"liveId\":110,\"options\":\"[\\\"嫌恶与排斥\\\",\\\"深厚的友谊\\\",\\\"表达爱慕之意\\\"]\",\"questionId\":1250,\"showTime\":1515848850984,\"stats\":[6614,153688,95952],\"status\":2,\"type\":\"showAnswer\"}";
        question = Util.getQuestion(content);
        String[] answers = Util.getAnswers(content);
        String msg = question;
        for(String s : answers) {
            msg += "\n" + s;
        }
        textView.setText(msg);
        Log.d(Main.TAG, question);
        new SearchThread(handler, question, answers).start();
    }
}
