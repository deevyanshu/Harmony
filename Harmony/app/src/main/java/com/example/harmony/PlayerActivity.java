package com.example.harmony;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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

public class PlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {
TextView songname,artistname,durationplayed,durationtotal;
ImageView coverart,nextbtn,prevbtn,backbtn,shufflebtn,repeatbtn;
FloatingActionButton playpause;
SeekBar seekbar;
int position=-1;
static ArrayList<MusicFiles> listsong=new ArrayList<>();
static Uri uri;
static MediaPlayer mp;
 Handler handler=new Handler();
 Thread play,previous,next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        intitviews();
        getintentmethod();

        songname.setText(listsong.get(position).getTitle());
        artistname.setText(listsong.get(position).getArtist());
        mp.setOnCompletionListener(this);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mp!=null && fromUser)
                {
                    mp.seekTo(progress*1000);
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
                if(mp!=null)
                {
                    int curpos=mp.getCurrentPosition()/1000;
                    seekbar.setProgress(curpos);
                    durationplayed.setText(formattedtime(curpos));
                }
                handler.postDelayed(this,1000);
            }
        });
        shufflebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.shuffle)
                {
                    MainActivity.shuffle=false;
                    shufflebtn.setImageResource(R.drawable.ic_shuffle_black_24dp);
                }else
                {
                    MainActivity.shuffle=true;
                    shufflebtn.setImageResource(R.drawable.ic_shuffle_on_24dp);
                }
            }
        });
        repeatbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.repeat)
                {
                    MainActivity.repeat=false;
                    repeatbtn.setImageResource(R.drawable.ic_repeat_black_24dp);
                }else
                {
                    MainActivity.repeat=true;
                    repeatbtn.setImageResource(R.drawable.ic_repeat_on_24dp);
                }
            }
        });
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        playThreadbtn();
        nextThreadbtn();
        prevThreaadbtn();
    }

    private void prevThreaadbtn() {
        previous=new Thread()
        {
            @Override
            public void run() {
                super.run();
                prevbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        previousclicked();
                    }
                });
            }
        };
        previous.start();
    }

    private void previousclicked() {
        if(mp.isPlaying())
        {
            mp.stop();
            mp.release();

            if(MainActivity.shuffle && !MainActivity.repeat)
            {
                position=getrandom(listsong.size()-1);
            }else if(!MainActivity.shuffle && !MainActivity.repeat)
            {
                position=((position-1)<0?(listsong.size()-1):(position-1));
            }



            uri=Uri.parse(listsong.get(position).getPath());
            mp=MediaPlayer.create(getApplicationContext(),uri);
            metadata(uri);
            songname.setText(listsong.get(position).getTitle());
            artistname.setText(listsong.get(position).getArtist());
            seekbar.setMax(mp.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mp!=null)
                    {
                        int curpos=mp.getCurrentPosition()/1000;
                        seekbar.setProgress(curpos);

                    }
                    handler.postDelayed(this,1000);
                }
            });
            mp.setOnCompletionListener(this);
            playpause.setBackgroundResource(R.drawable.ic_pause_black_24dp);
            mp.start();
        }else
        {


            if(MainActivity.shuffle && !MainActivity.repeat)
            {
                position=getrandom(listsong.size()-1);
            }else if(!MainActivity.shuffle && !MainActivity.repeat)
            {
                position=((position-1)<0?(listsong.size()-1):(position-1));
            }


            uri=Uri.parse(listsong.get(position).getPath());
            mp=MediaPlayer.create(getApplicationContext(),uri);
            metadata(uri);
            songname.setText(listsong.get(position).getTitle());
            artistname.setText(listsong.get(position).getArtist());
            seekbar.setMax(mp.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mp!=null)
                    {
                        int curpos=mp.getCurrentPosition()/1000;
                        seekbar.setProgress(curpos);

                    }
                    handler.postDelayed(this,1000);
                }
            });
            mp.setOnCompletionListener(this);
            playpause.setBackgroundResource(R.drawable.ic_play_arrow_black_24dp);

        }
    }

    private void playThreadbtn() {
        play=new Thread()
        {
            @Override
            public void run() {
                super.run();
                playpause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playpauseclicked();
                    }
                });
            }
        };
        play.start();
    }

    private void nextThreadbtn() {
        next=new Thread()
        {
            @Override
            public void run() {
                super.run();
                nextbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       nextclicked();
                    }
                });
            }
        };
        next.start();
    }

    private void nextclicked() {
        if(mp.isPlaying())
        {
            mp.stop();
            mp.release();
            if(position==listsong.size()-1)
            {
                position=0;
            }else
            {
                if(MainActivity.shuffle && !MainActivity.repeat)
                {
                    position=getrandom(listsong.size()-1);
                }else if(!MainActivity.shuffle && !MainActivity.repeat)
                {
                    position=(position+1)%listsong.size();
                }

            }
            uri=Uri.parse(listsong.get(position).getPath());
            mp=MediaPlayer.create(getApplicationContext(),uri);
            metadata(uri);
            songname.setText(listsong.get(position).getTitle());
            artistname.setText(listsong.get(position).getArtist());
            seekbar.setMax(mp.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mp!=null)
                    {
                        int curpos=mp.getCurrentPosition()/1000;
                        seekbar.setProgress(curpos);

                    }
                    handler.postDelayed(this,1000);
                }
            });
            mp.setOnCompletionListener(this);
            playpause.setBackgroundResource(R.drawable.ic_pause_black_24dp);
            mp.start();
        }else
        {
            if(position==listsong.size()-1)
            {
                position=0;
            }else
            {
                if(MainActivity.shuffle && !MainActivity.repeat)
                {
                    position=getrandom(listsong.size()-1);
                }else if(!MainActivity.shuffle && !MainActivity.repeat)
                {
                    position=(position+1)%listsong.size();
                }
            }
            uri=Uri.parse(listsong.get(position).getPath());
            mp=MediaPlayer.create(getApplicationContext(),uri);
            metadata(uri);
            songname.setText(listsong.get(position).getTitle());
            artistname.setText(listsong.get(position).getArtist());
            seekbar.setMax(mp.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mp!=null)
                    {
                        int curpos=mp.getCurrentPosition()/1000;
                        seekbar.setProgress(curpos);

                    }
                    handler.postDelayed(this,1000);
                }
            });
            mp.setOnCompletionListener(this);
            playpause.setBackgroundResource(R.drawable.ic_play_arrow_black_24dp);

        }
    }

    private int getrandom(int i) {
        Random random=new Random();
        return random.nextInt(i+1);
    }

    private void playpauseclicked() {
        if(mp.isPlaying())
        {
            playpause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            mp.pause();
            seekbar.setMax(mp.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mp!=null)
                    {
                        int curpos=mp.getCurrentPosition()/1000;
                        seekbar.setProgress(curpos);

                    }
                    handler.postDelayed(this,1000);
                }
            });
        }else
        {
            playpause.setImageResource(R.drawable.ic_pause_black_24dp);
            mp.start();
            seekbar.setMax(mp.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mp!=null)
                    {
                        int curpos=mp.getCurrentPosition()/1000;
                        seekbar.setProgress(curpos);

                    }
                    handler.postDelayed(this,1000);
                }
            });
        }
    }

    private String formattedtime(int curpos) {
        String totalout="";
        String totalnew="";
        String seconds=String.valueOf(curpos%60);
        String min=String.valueOf(curpos/60);
        totalout=min+":"+seconds;
        totalnew=min+":"+"0"+seconds;
        if(seconds.length()==1)
        {
            return totalnew;
        }
        else
        {
            return totalout;
        }
    }

    private void getintentmethod() {
        position=getIntent().getIntExtra("pos",-1);
        String sender=getIntent().getStringExtra("sender");
        if(sender!=null && sender.equals("albumdetails"))
        {
            listsong=AlbumDetailsAdapter.albumfiles;
        }else
        {listsong=MusicAdapter.mfiles;}

        if(listsong!=null)
        {
            playpause.setImageResource(R.drawable.ic_pause_black_24dp);
            uri=Uri.parse(listsong.get(position).getPath());
        }
        if(mp!=null)
        {
            mp.stop();
            mp.release();
            mp=MediaPlayer.create(getApplicationContext(),uri);
            mp.start();

        }else
        {
            mp=MediaPlayer.create(getApplicationContext(),uri);
            mp.start();

        }
        seekbar.setMax(mp.getDuration()/1000);
        metadata(uri);
    }

    private void intitviews() {
        songname=findViewById(R.id.song_name);
        artistname=findViewById(R.id.song_artist);
        durationplayed=findViewById(R.id.duration_played);
        durationtotal=findViewById(R.id.duration_total);
        coverart=findViewById(R.id.cover_art);
        nextbtn=findViewById(R.id.next);
        prevbtn=findViewById(R.id.prev);
        backbtn=findViewById(R.id.back_btn);
        shufflebtn=findViewById(R.id.shuffle);
        repeatbtn=findViewById(R.id.repeat);
        playpause=findViewById(R.id.play_pause);
        seekbar=findViewById(R.id.seekbar);
    }

    private void metadata(Uri uri)
    {
        MediaMetadataRetriever retriever=new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int totaldur=Integer.parseInt(listsong.get(position).getDuration())/1000;
        durationtotal.setText(formattedtime(totaldur));
        byte[] art=retriever.getEmbeddedPicture();
        Bitmap bitmap;
        if(art!=null)
        {
            bitmap= BitmapFactory.decodeByteArray(art,0,art.length);
            Imageanimation(getApplicationContext(),coverart,bitmap);
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    Palette.Swatch swatch=palette.getDominantSwatch();
                    if(swatch!=null)
                    {
                        ImageView gradient=findViewById(R.id.imageviewgradient);
                        RelativeLayout container=findViewById(R.id.mcontainer);
                        gradient.setBackgroundResource(R.drawable.gradient_bg);
                        container.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable=new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,new int[]{swatch.getRgb(),0x00000000});
                        gradient.setBackground(gradientDrawable);
                        GradientDrawable gradientDrawablebg=new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,new int[]{swatch.getRgb(),swatch.getRgb()});
                        container.setBackground(gradientDrawablebg);
                        songname.setTextColor(swatch.getTitleTextColor());
                        artistname.setTextColor(swatch.getBodyTextColor());
                    }else
                    {
                        ImageView gradient=findViewById(R.id.imageviewgradient);
                        RelativeLayout container=findViewById(R.id.mcontainer);
                        gradient.setBackgroundResource(R.drawable.gradient_bg);
                        container.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable=new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,new int[]{0xff000000,0x00000000});
                        gradient.setBackground(gradientDrawable);
                        GradientDrawable gradientDrawablebg=new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,new int[]{0xff000000,0xff000000});
                        container.setBackground(gradientDrawablebg);
                        songname.setTextColor(Color.WHITE);
                        artistname.setTextColor(Color.DKGRAY);
                    }
                }

            });
        }else
        {
            Glide.with(this).asBitmap().load(R.drawable.ic_action_name).into(coverart);
            ImageView gradient=findViewById(R.id.imageviewgradient);
            RelativeLayout container=findViewById(R.id.mcontainer);
            gradient.setBackgroundResource(R.drawable.gradient_bg);
            container.setBackgroundResource(R.drawable.main_bg);
            songname.setTextColor(Color.WHITE);
            artistname.setTextColor(Color.DKGRAY);
        }
    }
public void Imageanimation(final Context context, final ImageView imageView, final Bitmap bitmap)
{
    Animation animationout= AnimationUtils.loadAnimation(context,android.R.anim.fade_out);
    final Animation animationin= AnimationUtils.loadAnimation(context,android.R.anim.fade_in);
    animationout.setAnimationListener(new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            Glide.with(context).load(bitmap).into(imageView);
            animationin.setAnimationListener(new Animation.AnimationListener() {
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
            imageView.startAnimation(animationin);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    });
    imageView.startAnimation(animationout);
}

    @Override
    public void onCompletion(MediaPlayer mp) {
        nextclicked();
        if(mp!=null)
        {
            mp=MediaPlayer.create(getApplicationContext(),uri);
            mp.start();
            mp.setOnCompletionListener(this);
        }
    }
}
