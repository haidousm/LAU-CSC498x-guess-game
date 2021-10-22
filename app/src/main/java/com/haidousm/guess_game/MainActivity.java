package com.haidousm.guess_game;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    private ImageView appIconImageView;
    private Button[] answerButtons;

    private final int ROUND_TIME = 3000; // in milliseconds


    private Map<String, String> titleImgMap;
    private ArrayList<String> titles;

    private int currentRound = 0;
    private int currentScore = 0;

    enum LEVEL {
        EASY,
        MEDIUM,
        HARD
    }

    private LEVEL currentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.appIconImageView = findViewById(R.id.appIconImageView);


        this.answerButtons = new Button[]{
                findViewById(R.id.answerAButton),
                findViewById(R.id.answerBButton),
                findViewById(R.id.answerCButton),
                findViewById(R.id.answerDButton)};


        String listSiteURL = "https://www.pcmag.com/picks/best-android-apps";

        DownloadListTask listDownloader = new DownloadListTask();
        listDownloader.execute(listSiteURL);

        // TODO: get level from choose level view;
        this.currentLevel = LEVEL.EASY; //mock level

        if (this.currentLevel != LEVEL.EASY) {

            // TODO: show score counter

        }

        if (this.currentLevel == LEVEL.HARD) {
            // TODO: show round timer
        }

    }

    private void finishedLoadingData(Map<String, String> titleImgMap, ArrayList<String> titles) {

        this.titleImgMap = titleImgMap;
        this.titles = titles;
        Collections.shuffle(titles);

        prepareLoadingNextRound();
    }

    private void prepareLoadingNextRound() {

        new CountDownTimer(1000, 100) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                startLoadingNextRound();

            }
        }.start();

    }

    private void startLoadingNextRound() {
        String correctTitle = titles.get(currentRound);
        String imgUrl = titleImgMap.get(correctTitle);

        new DownloadImageTask(appIconImageView).execute(imgUrl);
    }

    private void finishedLoadingNextRound() {

        String correctTitle = this.titles.get(currentRound);
        Random random = new Random();
        ArrayList<String> chosenTitles = new ArrayList<>(Arrays.asList(this.titles.get(random.nextInt(this.titles.size())),
                this.titles.get(random.nextInt(this.titles.size())),
                this.titles.get(random.nextInt(this.titles.size())), this.titles.get(currentRound)));

        Collections.shuffle(chosenTitles);

        for (int i = 0; i < this.answerButtons.length; i++) {

            String chosenTitle = chosenTitles.get(i);
            answerButtons[i].setText(chosenTitle);
            answerButtons[i].setTag(chosenTitle.equals(correctTitle) ? 1 : 0);
        }
        currentRound++;
        currentRound %= titles.size();

        if (this.currentLevel == LEVEL.HARD) {
            startRoundTimer();
        }

    }

    private void startRoundTimer() {
        new CountDownTimer(this.ROUND_TIME, 100) {

            @Override
            public void onTick(long millisUntilFinished) {
                //TODO: update timer text view
            }

            @Override
            public void onFinish() {

                wrongAnswer();
                prepareLoadingNextRound();

            }
        }.start();
    }

    private void correctAnswer() {
        //TODO: play correct answer sound fx
        this.currentScore += 2;

    }

    private void wrongAnswer() {
        //TODO: play incorrect answer sound fx
        this.currentScore -= 1;
    }

    public void answerClicked(View v) {

        Button clickedButton = (Button) v;
        if ((Integer) clickedButton.getTag() == 1) {

            correctAnswer();

        } else {

            wrongAnswer();

        }
        prepareLoadingNextRound();

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

                }

                MainActivity.this.finishedLoadingData(titleImgMap, titles);


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
            MainActivity.this.finishedLoadingNextRound();
        }
    }
}