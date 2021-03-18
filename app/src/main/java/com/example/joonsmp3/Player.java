package com.example.joonsmp3;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class Player {
    private ListView listViewMP3;
    private Button btnPlay, btnStop, btnPause;
    private TextView tvMP3, tvTime;
    private SeekBar sbMP3;
    private boolean flag = true;
    private ArrayList<String> mp3List = new ArrayList<String>();
    private ArrayList<String> mp3ListSdcard = new ArrayList<String>();
    private ArrayList<MusicData> sdCardList = new ArrayList<MusicData>();

    private SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");

    private String MP3PATH = Environment.getExternalStorageDirectory().getPath() + "/Music/";
    MediaPlayer mPlayer;
    private String selectedMP3;
    private int selectPosition;

    private void eventHandlerFunc() {
        listViewMP3.setOnItemClickListener((AdapterView<?> adapterView, View view, int position, long l) -> {
            selectedMP3 = mp3ListSdcard.get(position);
            selectPosition = position;

        });

        btnPlay.setOnClickListener(v -> {
            mPlayer = new MediaPlayer();
            try
            {
                MusicData musicData = sdCardList.get(selectPosition);
                Uri musicURI = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, musicData.getId());
                mPlayer.setDataSource(MainActivity.class, musicURI);
                mPlayer.prepare();
                mPlayer.start();

                btnPlay.setEnabled(false);
                btnStop.setEnabled(true);
                tvMP3.setText(selectedMP3);

                //seekBar도 같이 움직여준다.

            }
            catch (IOException e)
            {
                Log.d("MainActivity", "음악파일에 접근 실패");
            }
        });

        btnStop.setOnClickListener(v ->{
            mPlayer.stop();
            mPlayer.reset();

            btnPlay.setEnabled(true);
            btnStop.setEnabled(false);
            tvMP3.setText("Empty");
        });

        btnStop.setEnabled(false);

        btnPause.setOnClickListener(v -> {
            if(flag == true)
            {
                flag = false;
                mPlayer.pause();
                btnPause.setText("Keep Play");
            }
            else
            {
                flag = true;
                mPlayer.start();
                btnPause.setText("Pause");
            }
        });

        sbMP3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean myTouchStart) {
                if(myTouchStart == true)
                {
                    mPlayer.seekTo(progress);   //중요
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }
}
