package com.example.remileblanc.stopwatch;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    //Define variables for our views
    private TextView tv_count = null;

    private Button bt_strt_stp_rsme = null;
    //private Button bt_stop = null;
    private Button bt_reset = null;
    private Timer t = new Timer();
    private Counter ctr = new Counter(); //TimerTask

    private AudioAttributes aa = null;
    private SoundPool soundPool = null;
    private int bloopSound = 0;

    private int time = 0;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //R is a resource class automatically created by android
                                                // activity main is the xml layout file
        //initialize views
        this.tv_count = findViewById(R.id.tv_count);
        this.bt_strt_stp_rsme = findViewById(R.id.bt_strt_stp_rsme);
        bt_strt_stp_rsme.setBackgroundColor(Color.GREEN);
        this.bt_reset = findViewById(R.id.bt_reset);
        bt_reset.setBackgroundColor(Color.BLACK);
        bt_reset.setTextColor(Color.WHITE);
        time = 0;//getPreferences(MODE_PRIVATE).getInt("COUNT",0);
        //System.out.println("here");


        time = getPreferences(MODE_PRIVATE).getInt("COUNT", 0);

        ctr.count = time;

        String s = setVisualTime(ctr.count);
        tv_count.setText(s);


        //this.tv_count.setText("Hello"); //changes number to hello

        this.bt_strt_stp_rsme.setOnClickListener(new View.OnClickListener() {
            int c;

            @Override
            public void onClick(View view) {
                if(bt_strt_stp_rsme.getText().equals("Stop")){
                    bt_strt_stp_rsme.setText("Resume");
                    bt_strt_stp_rsme.setBackgroundColor(Color.GREEN);
                    c = ctr.count;
                    t.cancel();
                    ctr.cancel();
                } else if(bt_strt_stp_rsme.getText().equals("Resume")){
                    bt_strt_stp_rsme.setText("Stop");
                    bt_strt_stp_rsme.setBackgroundColor(Color.RED);
                    t = new Timer();
                    ctr = new Counter();
                    ctr.count = c;
                    t.scheduleAtFixedRate(ctr, 0, 10);
                } else if(bt_strt_stp_rsme.getText().equals("Start")){
                    bt_strt_stp_rsme.setText("Stop");
                    bt_strt_stp_rsme.setBackgroundColor(Color.RED);
                    t = new Timer();
                    ctr = new Counter();
                    ctr.count = time;
                    t.scheduleAtFixedRate(ctr, 0, 10);
                }
            }
        });


        this.bt_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time = 0;
                bt_strt_stp_rsme.setText("Start");
                bt_strt_stp_rsme.setBackgroundColor(Color.GREEN);
                t.cancel();
                ctr.cancel();
                ctr.count = 0;
                t = new Timer();
                ctr = new Counter();
                tv_count.setText("00:00.0");
            }
        });

        this.aa = new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).setUsage(AudioAttributes.USAGE_GAME).build();

        this.soundPool = new SoundPool.Builder().setMaxStreams(1).setAudioAttributes(aa).build();
        this.bloopSound = this.soundPool.load(this, R.raw.bloop, 1);

        this.tv_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(bloopSound, 1f, 1f, 1, 0, 1f);

                Animator anim = AnimatorInflater.loadAnimator(MainActivity.this, R.animator.counter);
                anim.setTarget(tv_count);
                anim.start();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        //factory method - design pattern
        //creates a Toast object, but we don't call a constructor
        //Toast.makeText(this, "Stopwatch is started", Toast.LENGTH_LONG).show();


//        time = getPreferences(MODE_PRIVATE).getInt("COUNT", 0);
//
//        ctr.count = time;
//
//        String s = setVisualTime(ctr.count);
//        tv_count.setText(s);

        //System.out.println("here");
    }

    @Override
    protected void onPause() {
        super.onPause();

        getPreferences(MODE_PRIVATE).edit().putInt("COUNT", ctr.count).apply(); //method chaining

    }

    @Override
    protected void onStop() {
        super.onStop();

        //getPreferences(MODE_PRIVATE).edit().putInt("COUNT", ctr.count).apply(); //method chaining
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //getPreferences(MODE_PRIVATE).edit().putInt("COUNT", ctr.count).apply(); //method chaining

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //t.cancel();
        //getPreferences(MODE_PRIVATE).edit().putInt("COUNT", ctr.count).apply();
        //time = getPreferences(MODE_PRIVATE).getInt("COUNT",0);

    }


    class Counter extends TimerTask {
        //TimerTask is a runnable, meaning we need a run method
        private int count = 0;
        @Override
        public void run() {
            //need this bc timer cant access UI without it
            //Now we have to put it on the UI thread
            MainActivity.this.runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {

                            String visualTime = setVisualTime(count);

                            MainActivity.this.tv_count.setText(visualTime);
                            count++; //the instance variable we made up top
                        }
                    }
            );

        }
    }

//    public void setTime(int time){
//
//    }

    public String setVisualTime(int count){
        int seconds = (count / 100) % 60 ;
        int minutes = ((count / (100*60)) % 60);
        String stringMin;
        String stringSec;
        String stringMilli = Integer.toString(count%100/10);
        if(seconds>=0 && seconds<=9){
            stringSec = "0"+Integer.toString(seconds);
        } else {
            stringSec = Integer.toString(seconds);
        }
        if(minutes>=0 && minutes<=9){
            stringMin = "0"+Integer.toString(minutes);
        } else {
            stringMin = Integer.toString(minutes);
        }
        return stringMin+ ":"+stringSec+ "."+ stringMilli;
    }

}
