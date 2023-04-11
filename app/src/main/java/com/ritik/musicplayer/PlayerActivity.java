package com.ritik.musicplayer;

import static com.ritik.musicplayer.AlbumDetailsAdapter.albumFiles;
import static com.ritik.musicplayer.MainActivity.musicFiles;
import static com.ritik.musicplayer.MainActivity.repeatBoolean;
import static com.ritik.musicplayer.MainActivity.shuffleBoolean;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Random;

public class PlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener{
    TextView song_name,artist_name,duration_played,duration_total;

    ImageView cover_art,nextBtn,prevBtn,backBtn,shuffleBtn,repeatBtn;
    FloatingActionButton playPauseBtn;
    SeekBar seekBar;

    int position =-1;
    static Uri uri;
    static ArrayList<MusicFiles>listSongs= new ArrayList<>();
    static MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private Thread playThread,prevThread,nextThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initViews();
        getIntentMethod();
        song_name.setText(listSongs.get(position).title);
        artist_name.setText(listSongs.get(position).artist);
        mediaPlayer.setOnCompletionListener(this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer!=null &&  fromUser){
                    mediaPlayer.seekTo(progress*1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
               if (mediaPlayer!=null){
                   int mcCurrentPosition = mediaPlayer.getCurrentPosition()/1000;
                   seekBar.setProgress(mcCurrentPosition);
                   duration_played.setText(formattedTIme(mcCurrentPosition));
               }
               handler.postDelayed(this,1000);
            }
        });
        shuffleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              if (shuffleBoolean){
                 shuffleBoolean =false;
                 shuffleBtn.setImageResource(R.drawable.baseline_shuffle_off);
              }
              else{
                  shuffleBoolean =true;
                  shuffleBtn.setImageResource(R.drawable.baseline_shuffle_on);
              }
            }
        });
        repeatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (repeatBoolean){
                    repeatBoolean=false;
                    repeatBtn.setImageResource(R.drawable.baseline_repeat_off);
                }
                else {
                    repeatBoolean=true;
                    repeatBtn.setImageResource(R.drawable.baseline_repeat_24);
                }
            }
        });
    }
    public void onResume(){
        playThreadBtn();
        nextThreadBtn();
        prevThreadBtn();
        super.onResume();
    }

    private void prevThreadBtn() {
        prevThread=new Thread(){
            @Override
            public void run() {
                super.run();
                prevBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        prevBtnClicked();
                    }
                });
            }
        };
        prevThread.start();
    }

    private void prevBtnClicked() {
        if (mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            if (shuffleBoolean && !repeatBoolean){
                position=getRandom(listSongs.size()-1);
            }
            else if (!shuffleBoolean && ! repeatBoolean){
                position=((position-1)<0 ? (listSongs.size()-1):(position-1));
            }

            uri =Uri.parse(listSongs.get(position).path);
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            song_name.setText(listSongs.get(position).title);
            artist_name.setText(listSongs.get(position).artist);
            seekBar.setMax(mediaPlayer.getDuration()/1000);

            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer!=null){
                        int mcCurrentPosition = mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mcCurrentPosition);

                    }
                    handler.postDelayed(this,1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            playPauseBtn.setImageResource(R.drawable.baseline_pause_24);
            mediaPlayer.start();
        }
        else{
            mediaPlayer.stop();
            mediaPlayer.release();
            if (shuffleBoolean && !repeatBoolean){
                position=getRandom(listSongs.size()-1);
            }
            else if (!shuffleBoolean && ! repeatBoolean){
                position=((position-1)<0 ? (listSongs.size()-1):(position-1));
            }
            uri =Uri.parse(listSongs.get(position).path);
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            song_name.setText(listSongs.get(position).title);
            artist_name.setText(listSongs.get(position).artist);
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer!=null){
                        int mcCurrentPosition = mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mcCurrentPosition);

                    }
                    handler.postDelayed(this,1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            playPauseBtn.setImageResource(R.drawable.baseline_play_arrow_24);
        }


    }

    private void nextThreadBtn() {
        nextThread=new Thread(){
            @Override
            public void run() {
                super.run();
                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        nextBtnClicked();
                    }
                });
            }
        };
        nextThread.start();
        
    }

    private void nextBtnClicked() {
        if (mediaPlayer.isPlaying()){
        mediaPlayer.stop();
        mediaPlayer.release();
        if (shuffleBoolean && !repeatBoolean){
            position=getRandom(listSongs.size()-1);
        }
        else if (!shuffleBoolean && ! repeatBoolean){
            position=((position+1)%listSongs.size());
        }

        uri =Uri.parse(listSongs.get(position).path);
        mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
        metaData(uri);
        song_name.setText(listSongs.get(position).title);
        artist_name.setText(listSongs.get(position).artist);
        seekBar.setMax(mediaPlayer.getDuration()/1000);

            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer!=null){
                        int mcCurrentPosition = mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mcCurrentPosition);

                    }
                    handler.postDelayed(this,1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            playPauseBtn.setImageResource(R.drawable.baseline_pause_24);
            mediaPlayer.start();
        }
        else{
            mediaPlayer.stop();
            mediaPlayer.release();

            if (shuffleBoolean && repeatBoolean){
                position=getRandom(listSongs.size()-1);
            }
            else if (!shuffleBoolean && ! repeatBoolean){
                position=((position+1)%listSongs.size());
            }

            uri =Uri.parse(listSongs.get(position).path);
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            song_name.setText(listSongs.get(position).title);
            artist_name.setText(listSongs.get(position).artist);
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer!=null){
                        int mcCurrentPosition = mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mcCurrentPosition);

                    }
                    handler.postDelayed(this,1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            playPauseBtn.setImageResource(R.drawable.baseline_play_arrow_24);
        }
    }

    private int getRandom(int i) {
        Random random = new Random();

        return random.nextInt(i+1);
    }

    private void playThreadBtn() {
       playThread=new Thread(){
           @Override
           public void run() {
               super.run();
               playPauseBtn.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       playPauseBtnClicked();
                   }
               });
           }
       };
       playThread.start();
    }

    private void playPauseBtnClicked() {
        if (mediaPlayer.isPlaying()){
          playPauseBtn.setImageResource(R.drawable.baseline_play_arrow_24);
          mediaPlayer.pause();
          seekBar.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer!=null){
                        int mcCurrentPosition = mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mcCurrentPosition);

                    }
                    handler.postDelayed(this,1000);
                }
            });
        }else{
            playPauseBtn.setImageResource(R.drawable.baseline_pause_24);
            mediaPlayer.start();
            seekBar.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer!=null){
                        int mcCurrentPosition = mediaPlayer.getCurrentPosition()/1000;
                        seekBar.setProgress(mcCurrentPosition);

                    }
                    handler.postDelayed(this,1000);
                }
            });
        }
    }

    private String formattedTIme(int mcCurrentPosition) {
        String totalout="";
        String totalNew="";
        String seconds=String.valueOf(mcCurrentPosition%60);
        String minutes=String.valueOf(mcCurrentPosition/60);
        totalout=minutes + ":" + "0"+ seconds;
        totalNew=minutes + ":" +"0" +seconds;
        if (seconds.length()==1){
            return totalNew;
        }else{
           return totalout;
        }
    }
    private void getIntentMethod() {
        position=getIntent().getIntExtra("position",-1);
        String sender =getIntent().getStringExtra("sender");
        if (sender!=null && sender.equals("albumDetails")){
            listSongs=albumFiles;
        }else{
            listSongs=musicFiles;
        }
        listSongs=musicFiles;
        if (listSongs!=null){
            playPauseBtn.setImageResource(R.drawable.baseline_pause_24);
            uri =Uri.parse(listSongs.get(position).path);

        }
        if (mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            mediaPlayer.start();
        }else{
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            mediaPlayer.start();
        }
        seekBar.setMax(mediaPlayer.getDuration()/1000);
        metaData(uri);
    }

    private void initViews() {
        song_name= findViewById(R.id.song_name);
        artist_name=findViewById(R.id.song_artist);
        duration_played=findViewById(R.id.durationPlayed);
        duration_total=findViewById(R.id.durationTotal);

        cover_art=findViewById(R.id.cover_art);
        nextBtn=findViewById(R.id.id_next);
        prevBtn=findViewById(R.id.id_prev);
        backBtn=findViewById(R.id.back_btn);
        shuffleBtn=findViewById(R.id.id_shuffle);
        repeatBtn=findViewById(R.id.id_repeat);

        playPauseBtn=findViewById(R.id.play_pause);

        seekBar=findViewById(R.id.seekBar);

    }
    private void metaData(Uri uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int durationTotal = Integer.parseInt(listSongs.get(position).duration)/1000;
        duration_total.setText(formattedTIme(durationTotal));
        byte[] art= retriever.getEmbeddedPicture();
        Bitmap bitmap;
        if (art!=null){

            bitmap= BitmapFactory.decodeByteArray(art,0,art.length);
            ImageAnimation(this,cover_art,bitmap);
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    Palette.Swatch swatch = palette.getDominantSwatch();
                    if (swatch!=null){
                        ImageView gradient = findViewById(R.id.imageViewGredient);
                        RelativeLayout mContainer =findViewById(R.id.mContainer);
                        gradient.setBackgroundResource(R.drawable.gradient_bg);
                        mContainer.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{swatch.getRgb(),0x00000000});
                        gradient.setBackground(gradientDrawable);
                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{swatch.getRgb(),swatch.getRgb()});
                        mContainer.setBackground(gradientDrawableBg);
                        song_name.setTextColor(swatch.getTitleTextColor());
                        artist_name.setTextColor(swatch.getBodyTextColor());

                    }else{
                        ImageView gradient = findViewById(R.id.imageViewGredient);
                        RelativeLayout mContainer =findViewById(R.id.mContainer);
                        gradient.setBackgroundResource(R.drawable.gradient_bg);
                        mContainer.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{0xff000000,0x00000000});
                        gradient.setBackground(gradientDrawable);
                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{0xff000000,0xff000000});
                        mContainer.setBackground(gradientDrawableBg);
                        song_name.setTextColor(Color.WHITE);
                        artist_name.setTextColor(Color.DKGRAY);
                    }
                }
            });
        }else{
            Glide.with(this)
                    .asBitmap()
                    .load(R.drawable.todo)
                    .into(cover_art);
            ImageView gradient = findViewById(R.id.imageViewGredient);
            RelativeLayout mContainer =findViewById(R.id.mContainer);
            gradient.setBackgroundResource(R.drawable.gradient_bg);
            mContainer.setBackgroundResource(R.drawable.main_bg);
            song_name.setTextColor(Color.WHITE);
            artist_name.setTextColor(Color.DKGRAY);
        }
    }

    public void ImageAnimation(Context context,ImageView imgview, Bitmap bitmap){
        Animation animout= AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        Animation animIn =AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
         animout.setAnimationListener(new Animation.AnimationListener() {
             @Override
             public void onAnimationStart(Animation animation) {

             }

             @Override
             public void onAnimationEnd(Animation animation) {
                 Glide.with(context).load(bitmap).into(imgview);
                 animIn.setAnimationListener(new Animation.AnimationListener() {
                     @Override
                     public void onAnimationStart(Animation animation) {

                     }

                     @Override
                     public void onAnimationEnd(Animation animation) {

                     }

                     @Override
                     public void onAnimationRepeat(Animation animation) {

                     }
                 });
                 imgview.startAnimation(animIn);
             }

             @Override
             public void onAnimationRepeat(Animation animation) {

             }
         });
        imgview.startAnimation(animout);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        nextBtnClicked();
        if (mediaPlayer!=null){
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(this);
        }
    }
}

