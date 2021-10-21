package com.haidousm.guess_game;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String listSiteURL = "https://www.pcmag.com/picks/best-android-apps";

        DownloadListTask listDownloader = new DownloadListTask();
        listDownloader.execute(listSiteURL);

    }

    public void finishedLoadingData(Map<String, String> titleImgMap) {

        Log.i("guess-game", String.valueOf(titleImgMap.size()));

    }

    public class DownloadListTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {

            if (android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();
            String res = "";
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream()));
                String inputLine;
                while ((inputLine = reader.readLine()) != null)
                    res += inputLine;
                reader.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return res;
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {

                String divMatcher = "border-gray-lighter grouped-roundup-product-card flex flex-col md:block";
                String imgMatcher = "data-image-loader=";
                String titleMatcher = "order-last md:order-first font-bold font-brand text-lg md:text-xl leading-normal w-full\">";

                int imgMatcherLength = imgMatcher.length();
                int titleMatcherLength = titleMatcher.length();

                String[] cardDivs = s.split(divMatcher);

                Map<String, String> titleImgMap = new HashMap<>();

                for (int i = 1; i < cardDivs.length; i++) {

                    String div = cardDivs[i];
                    int leftPointer = div.indexOf(imgMatcher) + imgMatcherLength + 1;
                    int rightPointer = div.indexOf('"', leftPointer);

                    String imageUrl = div.substring(leftPointer, rightPointer);

                    leftPointer = div.indexOf(titleMatcher) + titleMatcherLength;
                    rightPointer = div.indexOf("</h2>", leftPointer);

                    String titleUrl = div.substring(leftPointer, rightPointer);

                    titleImgMap.put(titleUrl, imageUrl);

                }

                MainActivity.this.finishedLoadingData(titleImgMap);


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}