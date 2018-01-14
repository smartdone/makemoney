package com.smartdone.makemoney;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by smartdone on 2018/1/13.
 */

public class Main implements IXposedHookLoadPackage {

    public static final String TAG = "DATI_XPOSED";

    private Context context;
    private String question;
    private String[] questionAnswers;

    private Handler handler;


    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if(loadPackageParam.packageName.equals("com.chongdingdahui.app")) {
            XposedHelpers.findAndHookMethod("com.stub.StubApp", loadPackageParam.classLoader,
                    "getNewAppInstance", Context.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            context = (Context) param.args[0];
                            ClassLoader classLoader = context.getClassLoader();

                            Log.d(TAG, "开始解题");

                            handler = new Handler(){
                                @Override
                                public void handleMessage(Message msg) {
                                    super.handleMessage(msg);
                                    if(msg.what == SearchThread.SEARCH_SUCCESS) {
                                        HashMap<String, String> answers = (HashMap<String, String>) msg.obj;
                                        List<Integer> as = new ArrayList<>();
                                        String showMsg = question + "\n";
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

                                        Toast.makeText(context.getApplicationContext(), showMsg, Toast.LENGTH_SHORT).show();

                                    } else if(msg.what == SearchThread.SEARCH_FAILER) {
                                        Log.d(Main.TAG, "failer");
                                    }
                                }
                            };

//                            String content = "{\"answerTime\":10,\"correctOption\":1,\"desc\":\"5.在日本送给朋友蛋包饭可以表示什么？\",\"displayOrder\":4,\"liveId\":110,\"options\":\"[\\\"嫌恶与排斥\\\",\\\"深厚的友谊\\\",\\\"表达爱慕之意\\\"]\",\"questionId\":1250,\"showTime\":1515848850984,\"stats\":[6614,153688,95952],\"status\":2,\"type\":\"showAnswer\"}";
//                            question = Util.getQuestion(content);
//                            questionAnswers = Util.getAnswers(content);
//                            new SearchThread(handler, question, questionAnswers).start();

                            XposedHelpers.findAndHookMethod("com.chongdingdahui.app.socket.MessageManager$7", classLoader,
                                    "call", Object[].class, new XC_MethodHook() {
                                @Override
                                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                    super.beforeHookedMethod(param);
                                    Object[] objs = (Object[]) param.args[0];
                                    String content = objs[0].toString();
                                    Log.w(TAG, content);
                                    question = Util.getQuestion(content);
                                    questionAnswers = Util.getAnswers(content);

                                    new SearchThread(handler, question, questionAnswers).start();

                                }
                            });

                        }
                    });
        }
    }
}
