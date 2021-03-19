package com.example.joonsmp3;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private NavigationView nav_view;
    private RecyclerView recyclerView, recyclerLike;
    private MusicAdapter musicAdapter;
    private MusicAdapter musicAdapter_like;
    private FragmentPagerAdapter fpa;
    private LinearLayoutManager linearLayoutManager;
    private LinearLayoutManager linearLayoutManager_like;
    private MusicDB musicDB;
    private ArrayList<MusicData> musicList = new ArrayList<MusicData>();
    private ArrayList<MusicData> musicLikeArrayList = new ArrayList<>();
    private Fragment player;
    private ViewPager vp;

    private String MP3PATH = Environment.getExternalStorageDirectory().getPath() + "/Music/";
    MediaPlayer mPlayer;
    private String selectedMP3;
    private int selectPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("플레이 리스트인거임");

        findViewByIdFunc();

        ViewPagerFunc();

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MODE_PRIVATE);

        findMP3ContentProviderToArrayList();

        musicDB = musicDB.getInstance(getApplicationContext());

        musicAdapter = new MusicAdapter(getApplicationContext());
        musicAdapter_like = new MusicAdapter(getApplicationContext());

        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager_like = new LinearLayoutManager(getApplicationContext());

        recyclerView.setAdapter(musicAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerLike.setAdapter(musicAdapter_like);
        recyclerLike.setLayoutManager(linearLayoutManager_like);

        musicList = musicDB.compareArrayList();

        insertDB(musicList);

        recyclerViewUpdate(musicList);
        likeRecyclerViewListUpdate(getLikeList());
        
        replaceFragment();

        musicAdapter.setOnItemClickListener(new MusicAdapter.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemClick(View v, int pos) {
                // 플레이어 화면 처리
                ((Player)player).setPlayerData(pos,true);
            }
        });

        // like_recyclerview 클릭 이벤트
        musicAdapter_like.setOnItemClickListener(new MusicAdapter.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemClick(View v, int pos) {
                // 플레이어 화면 처리
                ((Player)player).setPlayerData(pos,false);
            }
        });

    }

    private void ViewPagerFunc() {
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);

        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(vpPager);
    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 3;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:

                    return FirstFragment.newInstance(0, "Page # 1");
                case 1:
                    return SecondFragment.newInstance(1, "Page # 2");
                case 2:
                    return ThirdFragment.newInstance(2, "Page # 3");
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

    }
    }

    private void replaceFragment() {
        player = new Player();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        
        ft.replace(R.id.recyclerView, player);
        ft.commit();
    }

    private void likeRecyclerViewListUpdate(ArrayList<MusicData> likeList) {

        musicAdapter_like.setMusicList(likeList);

        recyclerLike.setAdapter(musicAdapter_like);
        musicAdapter_like.notifyDataSetChanged();
    }

    private ArrayList<MusicData> getLikeList() {
        musicLikeArrayList = musicDB.saveLikeList();

        if(musicLikeArrayList.isEmpty()){
            Toast.makeText(getApplicationContext(), "가져오기 실패", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(), "가져오기 성공", Toast.LENGTH_SHORT).show();
        }

        return musicLikeArrayList;
    }

    private void recyclerViewUpdate(ArrayList<MusicData> musicList) {

        musicAdapter.setMusicList(musicList);

        recyclerView.setAdapter(musicAdapter);
        musicAdapter.notifyDataSetChanged();

    }

    private void insertDB(ArrayList<MusicData> musicList) {

        boolean returnValue = musicDB.insertMusicDataToDB(musicList);

        if(returnValue){
            Toast.makeText(getApplicationContext(), "삽입 성공", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(), "삽입 실패", Toast.LENGTH_SHORT).show();
        }
    }

    private void findViewByIdFunc() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerLike = (RecyclerView) findViewById(R.id.recyclerLike);
        vp = (ViewPager) findViewById(R.id.viewP);
    }


    private void findMP3ContentProviderToArrayList() {

        String[] data = {MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DURATION
        };

        Cursor cursor = getApplicationContext().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, data,
                null, null, data[2] + " ASC");


    }
}