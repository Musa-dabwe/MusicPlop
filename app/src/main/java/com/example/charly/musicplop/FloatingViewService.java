package com.example.charly.musicplop;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.media.AudioManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.zip.Inflater;

/**
 * Created by Charly on 07/03/2017.
 */

public class FloatingViewService extends Service {

    private View mFloatingView;
    private WindowManager mWindowManager;
    private ImageView closeButt;
    private ImageView closeButter;
    private ImageView openButt;
    private ImageView prevButt;
    private ImageView playButt;
    private ImageView nextButt;
    private TextView title;
    private TextView album;
    private TextView artist;
    private Context context = this;
    //private AudioManager mAudioManager = (AudioManager) getSystemService(context.AUDIO_SERVICE);
    private long eventtime = SystemClock.uptimeMillis();
    //private int play_state = 0; // 0 for pause
    private int play_state = 0; // 0 for pause
    public FloatingViewService(){
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Inflating the layout:
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null);

        // Adding the view to the window with a WindowManager:
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        //Display display = getWindowManager.getDefaultDisplay(); // If we are inside an activity
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        //int height = size.y;

        // View Position:
        params.gravity = Gravity.TOP | Gravity.LEFT; // At the top left of the screen
        //params.x = 0;
        params.x = width;
        params.y = 0;

        // Merging:
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView,params);


        // Interactions with the View:

        // Root Element of the Collapsed View:
        final View collapsedView = mFloatingView.findViewById(R.id.collapsed_view);
        // Root Element of the Expended View:
        final View expandedView = mFloatingView.findViewById(R.id.expanded_view);

        // Close Butt when collapsed:
        closeButt = (ImageView) mFloatingView.findViewById(R.id.close_butt);
        closeButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSelf();
                //finish();
            }
        });

        /*String SERVICECMD = "com.android.music.musicservicecommand";
        String CMDNAME = "command";
        String CMDSTOP = "stop";
        String CMDNEXT = "next";
        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if(mAudioManager.isMusicActive()) {
            Intent i = new Intent(SERVICECMD);
            i.putExtra(CMDNAME, CMDNEXT);
            FloatingViewService.this.sendBroadcast(i);
        }*/

        // Previous Butt:
        prevButt = (ImageView) mFloatingView.findViewById(R.id.previous_butt);
        prevButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(FloatingViewService.this,"Playing Previous Song",Toast.LENGTH_SHORT).show();
                /*Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
                KeyEvent downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PREVIOUS, 0);
                downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
                sendOrderedBroadcast(downIntent, null);*/
                String SERVICECMD = "com.android.music.musicservicecommand";
                String CMDNAME = "command";
                String CMDPREVIOUS = "previous";
                AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                if(mAudioManager.isMusicActive()) {
                    Intent i = new Intent(SERVICECMD);
                    i.putExtra(CMDNAME, CMDPREVIOUS);
                    FloatingViewService.this.sendBroadcast(i);
                }
            }
        });
        // Play Butt:
        playButt = (ImageView) mFloatingView.findViewById(R.id.play_butt);
        playButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // public static final String CMDTOGGLEPAUSE = "togglepause";
                //public static final String CMDPAUSE = "pause";
                /*if (mAudioManager == null) mAudioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
                KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY);
                mAudioManager.dispatchMediaKeyEvent(event);*/
                if(play_state == 0) {
                    //Toast.makeText(FloatingViewService.this, "Playing a Song", Toast.LENGTH_SHORT).show();
                    Intent upIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
                    KeyEvent upEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, 0);
                    upIntent.putExtra(Intent.EXTRA_KEY_EVENT, upEvent);
                    sendOrderedBroadcast(upIntent, null);
                    play_state--;
                    Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
                    KeyEvent downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, 0);
                    downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
                    sendOrderedBroadcast(downIntent, null);
                    play_state++;
                }
                else if (play_state == 1){
                    Intent upIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
                    KeyEvent upEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, 0);
                    upIntent.putExtra(Intent.EXTRA_KEY_EVENT, upEvent);
                    sendOrderedBroadcast(upIntent, null);
                    play_state--;
                }
            }
        });

        // Next Butt:
        nextButt = (ImageView) mFloatingView.findViewById(R.id.next_butt);
        nextButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(FloatingViewService.this,"Playing Next Song",Toast.LENGTH_SHORT).show();
                /*Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
                KeyEvent downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN,   KeyEvent.KEYCODE_MEDIA_NEXT, 0);
                downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
                sendOrderedBroadcast(downIntent, null);*/
                String SERVICECMD = "com.android.music.musicservicecommand";
                String CMDNAME = "command";
                String CMDNEXT = "next";
                AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                if(mAudioManager.isMusicActive()) {
                    Intent i = new Intent(SERVICECMD);
                    i.putExtra(CMDNAME, CMDNEXT);
                    FloatingViewService.this.sendBroadcast(i);
                }
            }
        });

        // Close Butt when Expanded:
        closeButter = (ImageView) mFloatingView.findViewById(R.id.close_butt_exp);
        closeButter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(FloatingViewService.this,"Closing the application",Toast.LENGTH_SHORT).show();
                collapsedView.setVisibility(View.VISIBLE);
                expandedView.setVisibility(View.GONE);
            }
        });

        // Open Butt when Expanded:
        openButt = (ImageView) mFloatingView.findViewById(R.id.open_butt);
        openButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(FloatingViewService.this,"Closing the application",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(FloatingViewService.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                // Close the floatHead:
                stopSelf();
            }
        });


        // Moving the FloatingView when touched and dragged:
        mFloatingView.findViewById(R.id.RelativeLayout1).setOnTouchListener(new View.OnTouchListener() {
            private int x_axis;
            private int y_axis;
            private float x_move;
            private float y_move;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                Display display = wm.getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int width = size.x;

                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        x_axis = params.x;
                        y_axis = params.y;

                        x_move = event.getRawX();
                        y_move = event.getRawY();

                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = x_axis + (int)(event.getRawX() - x_move);
                        params.y = y_axis + (int)(event.getRawY() - y_move);

                        mWindowManager.updateViewLayout(mFloatingView,params);
                        return true;

                    // Checks if the user is just pressing for Expanding/ Collapsing:
                    case MotionEvent.ACTION_UP:
                        int X_after = (int)(event.getRawX() - x_move);
                        int Y_after = (int)(event.getRawY() - y_move);
                        if(X_after < 10 && Y_after < 10){
                            if(isViewCollapsed()){
                                collapsedView.setVisibility(View.GONE);
                                expandedView.setVisibility(View.VISIBLE);
                            }
                        }
                        else{
                            if(params.x < width/2){
                                params.x = 0;
                            }
                            if(params.x > width/2){
                                params.x = width;
                            }
                           /*if(X_after < width/2){
                                params.x = 0;
                            }
                            if(X_after > width/2){
                                params.x = width;
                            }*/
                            mWindowManager.updateViewLayout(mFloatingView,params);
                        }


                        return true;
                }

                return false;
            }
        });

        title = (TextView) mFloatingView.findViewById(R.id.title_s);
        artist = (TextView) mFloatingView.findViewById(R.id.artist);
        album = (TextView) mFloatingView.findViewById(R.id.album);

        IntentFilter iF = new IntentFilter();

        iF.addAction("com.android.music.metachanged");
        /*iF.addAction("com.htc.music.metachanged");
        iF.addAction("fm.last.android.metachanged");
        iF.addAction("com.sec.android.app.music.metachanged");
        iF.addAction("com.nullsoft.winamp.metachanged");
        iF.addAction("com.amazon.mp3.metachanged");
        iF.addAction("com.miui.player.metachanged");
        iF.addAction("com.real.IMP.metachanged");
        iF.addAction("com.sonyericsson.music.metachanged");
        iF.addAction("com.rdio.android.metachanged");
        iF.addAction("com.samsung.sec.android.MusicPlayer.metachanged");
        iF.addAction("com.andrew.apollo.metachanged");*/
        registerReceiver(mReceiver, iF);

        /*ContentResolver musicResolve = getContentResolver();
        Uri smusicUri = android.provider.MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        Cursor music =musicResolve.query(smusicUri,null         //should use where clause(_ID==albumid)
                ,null, null, null);
        music.moveToFirst();            //i put only one song in my external storage to keep things simple
        int x=music.getColumnIndex(android.provider.MediaStore.Audio.Albums.ALBUM_ART);
        String thisArt = music.getString(x);


        Bitmap bm= BitmapFactory.decodeFile(thisArt);
        ImageView image=(ImageView)mFloatingView.findViewById(R.id.album_picture);
        image.setImageBitmap(bm);*/

        /*requestStoragePermission();
        Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");
        String plop = MediaStore.Audio.Media.ALBUM;
        Cursor cursor = context.getContentResolver().query(albumArtUri,null,null,null,null);
        cursor.moveToFirst();*/
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String track = intent.getStringExtra("track");
            String artist_ = intent.getStringExtra("artist");
            String album_ = intent.getStringExtra("album");
            String albumid_ = intent.getStringExtra("album_id");
            //Log.i("Cover",albumid_);
            title.setText("Title :" + track);
            artist.setText("Artist :" + artist_);
            album.setText("Album :" + album_);
            //Toast.makeText(CurrentMusicTrackInfoActivity.this, track, Toast.LENGTH_SHORT).show();

        }
    };

    public boolean isViewCollapsed(){
        // if mFloatingView.findViewById(R.id.collapsed_view).getVisibility() == View.VISIBLE
        // Or
        // if mFloatingView == null

        return mFloatingView == null || mFloatingView.findViewById(R.id.collapsed_view).getVisibility() == View.VISIBLE;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // If destroyed:
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mFloatingView != null){
            mWindowManager.removeView(mFloatingView);
        }
    }




}
