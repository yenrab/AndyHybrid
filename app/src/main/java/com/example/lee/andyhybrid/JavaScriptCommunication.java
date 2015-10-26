package com.example.lee.andyhybrid;

import android.app.Activity;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import org.quickconnect.family.json.JSONException;
import org.quickconnect.family.json.JSONUtilities;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by lee on 10/21/15.
 */
public class JavaScriptCommunication {
    Activity theActivity;
    WebView containingWebView;

    public JavaScriptCommunication(Activity theActivity, WebView containingWebView) {
        this.theActivity = theActivity;
        this.containingWebView = containingWebView;
    }

    /**
     *
     * @param aMessageAsJSON - the data and an indicator as to what Java code is to be executed.
     *                       There is an optional key, 'callback', that has as its value a
     *                       JavaScript function.
     */
    @JavascriptInterface
    public void postMessage(String aMessageAsJSON){
        if(aMessageAsJSON != null) {
            try {
                HashMap<String, Object> message = (HashMap) JSONUtilities.parse(aMessageAsJSON);
                /*
                 * Evaluate the request to determine what should be done and the data to use to perform the request.
                 * Then do what was requested here.
                 */
                String command = (String)message.get("cmd");
                /*
                 * A simple if-else series of statements is used here. For scalability reasons
                 * you should consider using a verson of the Application Controller Pattern.
                 * A version of it is available at https://github.com/yenrab/qcnative.
                 * Use the Java/Android version by including the source files in your project.
                 */
                Serializable response = null;
                if(command.equals("increment")){
                    long count = (long)message.get("count");
                    count++;
                    HashMap<String,Object> dataMap = new HashMap<>();
                    dataMap.put("count",count);
                    response = dataMap;
                }

                String asyncCallback = (String) message.get("callbackFunc");
                //after performing the request call sendResponse.
                sendResponse(response, asyncCallback);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param aResponse - the data for the response
     * @param callback - a JavaScript callback function (declared or anonymous)
     * @throws Exception - If there is a callback function, but the required 'responseAsJSON' paramter is missing, this Exception is thrown.
     */
    public void sendResponse(Serializable aResponse, String callback) throws JSONException{
        if(aResponse != null && callback != null) {
            //Turn the data to send back to the JavaScript into a string.
            String responseAsJSON = JSONUtilities.stringify(aResponse);
            //turn the callback into something that can be executed imediately in JavaScript.
            final String changedCallback = "(".concat(callback).concat("('").concat(responseAsJSON).concat("'))");
            System.out.println(changedCallback);
            //make sure to execute all methods of WebViews on the UI thread.
            this.theActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //execute the JavaScript callback
                    JavaScriptCommunication.this.containingWebView.evaluateJavascript(changedCallback, null);
                }
            });
        }
    }

}
