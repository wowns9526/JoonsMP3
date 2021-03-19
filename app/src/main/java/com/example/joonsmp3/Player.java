package com.example.joonsmp3;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class Player extends Fragment implements View.OnClickListener {

    private ListView listViewMP3;
    private TextView tvTitle, tvArtist, tvCurrentTime, tvDuration;
    private SeekBar seekBar;
    private ImageView ivAlbum;
    private ImageButton ibLike, ibPrevious, ibPlay, ibNext;
    private boolean flag = true;
    private MainActivity mainActivity;
    private MusicAdapter musicAdapter;


    private ArrayList<String> mp3List = new ArrayList<String>();
    private ArrayList<String> mp3ListSdcard = new ArrayList<String>();
    private ArrayList<MusicData> sdCardList = new ArrayList<MusicData>();

    private SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");

    private String MP3PATH = Environment.getExternalStorageDirectory().getPath() + "/Music/";
    MediaPlayer mPlayer;
    private String selectedMP3;
    private int selectPosition;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity)getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mainActivity = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.player, container, false);

        findViewByIdFunc(view);

        // 어댑터 가져옴
        musicAdapter = mainActivity.getMusicAdapter_like();
        // 좋아요 리스트 가져오기
        likeArrayList = mainActivity.getMusicLikeArrayList();

        musicAdapter.setMusicList(likeArrayList);

        seekBarChangeMethod();

        return view;
    }

    private void findViewByIdFunc(View view) {
        ivAlbum = view.findViewById(R.id.ivAlbum);
        tvArtist = view.findViewById(R.id.tvArtist);
        tvTitle = view.findViewById(R.id.tvTitle);
        tvCurrentTime = view.findViewById(R.id.tvCurrentTime);
        tvDuration = view.findViewById(R.id.tvDuration);
        seekBar = view.findViewById(R.id.seekBar);
        ibPlay = view.findViewById(R.id.ibPlay);
        ibPrevious = view.findViewById(R.id.ibPrevious);
        ibNext = view.findViewById(R.id.ibNext);
        ibLike = view.findViewById(R.id.ibLike);

        ibPlay.setOnClickListener(this);
        ibPrevious.setOnClickListener(this);
        ibNext.setOnClickListener(this);
        ibLike.setOnClickListener(this);
    }

    private void eventHandlerFunc() {
        listViewMP3.setOnItemClickListener((AdapterView<?> adapterView, View view, int position, long l) -> {
            selectedMP3 = mp3ListSdcard.get(position);
            selectPosition = position;

        });

        ibPlay.setOnClickListener(v -> {
            mPlayer = new MediaPlayer();
            try
            {
                MusicData musicData = sdCardList.get(selectPosition);
                Uri musicURI = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, musicData.getId());
               //.. mPlayer.setDataSource(MainActivity.class, musicURI);
                mPlayer.prepare();
                mPlayer.start();

                ibPlay.setEnabled(false);
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

    @Override
    public void onClick(View view) {

    }

}
