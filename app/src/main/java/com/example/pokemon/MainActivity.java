package com.example.pokemon;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    Button button1;
    Button button2;
    Button button3;
    Button button4;
    Button playAgainButton;
    CountDownTimer x;
    int timeLimitSecs =0 ;
    int timeLimitMins=1;
    int secs=timeLimitSecs;
    int mins=timeLimitMins;
    int correctCount=0;
    int totalCount=0;
    ArrayList<String>names = new ArrayList<>();
   // ArrayList<String>urls=new ArrayList<>();
    int correctAnswer=0;
    int chosenPokemon=0;
    String[] answers=new String[4];
    String result = null;
    TextView timeCounter;
    TextView score;
    TextView scoreView;
    ConstraintLayout gameLayout;


    public void playAgain(View view)
    {
        secs=timeLimitSecs;
        mins=timeLimitMins;

        correctCount=0;
        totalCount=0;
        gameLayout.setVisibility(View.VISIBLE);
        playAgainButton.setVisibility(View.INVISIBLE);
        scoreView.setVisibility(View.INVISIBLE);
        score.setText("0/0");
        updateTimer();
        x.start();
        newQuestion();
    }
    public void updateTimer()
    {

        if( secs==0)
        {
            mins=mins-1;
            secs=59;
            timeCounter.setText(Integer.toString(mins)+":"+Integer.toString(secs));
        }
        else
        {
            secs=secs-1;
            timeCounter.setText(Integer.toString(mins)+":"+Integer.toString(secs));

        }
    }

    public void buttonClicked(View view)
    {
        if(view.getTag().toString().equals(Integer.toString(correctAnswer)))
        {
            Toast.makeText(getApplicationContext(),"correct",Toast.LENGTH_LONG).show();
            totalCount+=1;
            correctCount+=1;
        }
        else
        {
            Toast.makeText(getApplicationContext(),"wrong !!! it was  "+names.get(chosenPokemon-1).toUpperCase(),Toast.LENGTH_LONG).show();
            totalCount+=1;
        }
        score.setText(correctCount+"/"+totalCount);
        newQuestion();
    }
    public class DownloadImage extends AsyncTask<String,Void, Bitmap>
    {

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                Bitmap myBitMap = BitmapFactory.decodeStream(inputStream);
                return myBitMap;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
    public class DownloadTask extends AsyncTask <String,Void,String>
    {

        @Override
        protected String doInBackground(String... urls) {
            String result="";
            URL url ;
            HttpURLConnection connection = null;

            try
            {
                url=new URL(urls[0]);
                connection= (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data  = reader.read();
                while(data!=-1)
                {
                    char current = (char)data;
                    result+=current;
                    data=reader.read();

                }

            }
            catch(Exception e)
            {
                System.out.println(e);
            }
            return result;
        }


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timeCounter = (TextView) findViewById(R.id.Counter);
        score = (TextView)findViewById(R.id.Score);
        playAgainButton=(Button)findViewById(R.id.playAgainButton);
        gameLayout = (ConstraintLayout)findViewById(R.id.gameLayout);
        scoreView = (TextView)findViewById(R.id.scoreView);
        gameLayout.setVisibility(View.VISIBLE);
        playAgainButton.setVisibility(View.INVISIBLE);
        scoreView.setVisibility(View.INVISIBLE);
        timeCounter.setText(Integer.toString(timeLimitMins)+":"+Integer.toString(timeLimitSecs));
        score.setText("0/0");

        x = new CountDownTimer(timeLimitMins*60000+timeLimitSecs*1000,1000)
        {

            @Override
            public void onTick(long millisUntilFinished) {
                updateTimer();
            }

            @Override
            public void onFinish() {
                scoreView.setText("Your Score Is : "+Integer.toString(correctCount));
                gameLayout.setVisibility(View.INVISIBLE);
                playAgainButton.setVisibility(View.VISIBLE);
                scoreView.setVisibility(View.VISIBLE);
            }
        };
        x.start();
        try {
            DownloadTask task = new DownloadTask();
            result = task.execute("https://pokeapi.co/api/v2/pokemon/?limit=300").get();
            JSONObject jsonObject = null;
            jsonObject = new JSONObject(result);
            String results=jsonObject.getString("results");
            Log.i("results",results);
            JSONArray jArray = new JSONArray(results);

            for(int i=0;i<jArray.length();i++)
            {
                JSONObject jObject = jArray.getJSONObject(i);
                String name =jObject.getString("name");
                names.add(name);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        newQuestion();

    }
    public void newQuestion()
    {
        button1=(Button)findViewById(R.id.button1);
        button2=(Button)findViewById(R.id.button2);
        button3=(Button)findViewById(R.id.button3);
        button4=(Button)findViewById(R.id.button4);




        try {
            Bitmap bitmapImage;
            DownloadImage img = new DownloadImage();
            ImageView imageView = (ImageView)findViewById(R.id.pokemonImage);
            Random r=new Random();
            correctAnswer = r.nextInt(4);
            chosenPokemon =r.nextInt(299)+1;

            bitmapImage=img.execute("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/back/"+chosenPokemon+".png").get();
            imageView.setImageBitmap(bitmapImage);
            for(int i=0;i<4;i++)
            {
                if(i==correctAnswer)
                {
                    answers[i]=names.get(chosenPokemon-1);
                }
                else
                {
                    int incorrectAnswer = r.nextInt(299);

                    while(incorrectAnswer==chosenPokemon)
                    {
                        incorrectAnswer=r.nextInt(299);
                    }
                    answers[i]=names.get(incorrectAnswer);
                }
            }
            button1.setText(answers[0]);
            button2.setText(answers[1]);
            button3.setText(answers[2]);
            button4.setText(answers[3]);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
