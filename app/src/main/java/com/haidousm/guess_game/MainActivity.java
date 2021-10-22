package com.haidousm.guess_game;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private Map<String, String> titleImgMap;
    private ArrayList<String> titles;
    private ArrayList<String> imgUrls;

    private int currentAppIndex = 0;

    private DownloadImageTask downloadImageTask;

    private ImageView appIconImageView;
    private Button answerAButton;
    private Button answerBButton;
    private Button answerCButton;
    private Button answerDButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.appIconImageView = findViewById(R.id.appIconImageView);
        this.answerAButton = findViewById(R.id.answerAButton);
        this.answerBButton = findViewById(R.id.answerBButton);
        this.answerCButton = findViewById(R.id.answerCButton);
        this.answerDButton = findViewById(R.id.answerDButton);

        this.downloadImageTask = new DownloadImageTask(appIconImageView);

        String listSiteURL = "https://www.pcmag.com/picks/best-android-apps";

        DownloadListTask listDownloader = new DownloadListTask();
        listDownloader.execute(listSiteURL);

    }

    public void finishedLoadingData(Map<String, String> titleImgMap, ArrayList<String> titles, ArrayList<String> imgUrls) throws IOException {

        this.titleImgMap = titleImgMap;
        this.titles = titles;
        this.imgUrls = imgUrls;
        Collections.shuffle(titles);

        startLoadingNextApp();
    }

    public void startLoadingNextApp() {
        String correctTitle = titles.get(currentAppIndex);
        String imgUrl = titleImgMap.get(correctTitle);
        this.downloadImageTask.execute(imgUrl);
    }

    public void finishedLoadingNextApp() {

        Random random = new Random();
        String[] chosenTitles = new String[]{
                this.titles.get(currentAppIndex),
                this.titles.get(random.nextInt(this.titles.size())),
                this.titles.get(random.nextInt(this.titles.size())),
                this.titles.get(random.nextInt(this.titles.size()))};

        this.answerAButton.setText(chosenTitles[0]);
        this.answerBButton.setText(chosenTitles[1]);
        this.answerCButton.setText(chosenTitles[2]);
        this.answerDButton.setText(chosenTitles[3]);

        currentAppIndex++;

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

                ArrayList<String> titles = new ArrayList<>();
                ArrayList<String> imgUrls = new ArrayList<>();

                for (int i = 1; i < cardDivs.length; i++) {

                    String div = cardDivs[i];
                    int leftPointer = div.indexOf(imgMatcher) + imgMatcherLength + 1;
                    int rightPointer = div.indexOf('"', leftPointer);

                    String imageUrl = div.substring(leftPointer, rightPointer);

                    int lastSlashIndex = imageUrl.lastIndexOf("/");
                    imageUrl = imageUrl.substring(0, lastSlashIndex) + imageUrl.substring(lastSlashIndex, imageUrl.indexOf(".", lastSlashIndex)) + imageUrl.substring(imageUrl.lastIndexOf("."));

                    leftPointer = div.indexOf(titleMatcher) + titleMatcherLength;
                    rightPointer = div.indexOf("</h2>", leftPointer);

                    String titleUrl = div.substring(leftPointer, rightPointer);

                    titleImgMap.put(titleUrl, imageUrl);

                    titles.add(titleUrl);
                    imgUrls.add(imageUrl);

                }

                MainActivity.this.finishedLoadingData(titleImgMap, titles, imgUrls);


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
            MainActivity.this.finishedLoadingNextApp();
        }
    }
}