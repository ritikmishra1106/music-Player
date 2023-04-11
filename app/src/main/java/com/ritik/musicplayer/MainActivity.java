package com.ritik.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    int REQUEST_CODE=80;
    public static ArrayList<MusicFiles>musicFiles;
    public static boolean shuffleBoolean=false,repeatBoolean=false;
    static ArrayList<MusicFiles>albums = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permission();


    }
    private void permission(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
            REQUEST_CODE);
        }
        else{

            musicFiles=getAllAudio(this);
            initViewPager();
        }
    }

    public void onRequestPermissionResult(int requestCode,String[] permission,int []grantResults){
        super.onRequestPermissionsResult(requestCode,permission,grantResults);
        if (requestCode==REQUEST_CODE){
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED){

                musicFiles=getAllAudio(this);
                initViewPager();
            }else{
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE);
            }
        }
    }
    private void initViewPager(){
        ViewPager viewPager =findViewById(R.id.viewPager);
        TabLayout tabLayout =findViewById(R.id.tabLayout);

        ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragments(new SongsFragment(),"Songs");
        viewPagerAdapter.addFragments(new AlbumFragment(),"Albums");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);


    }
    public static class ViewPagerAdapter extends FragmentPagerAdapter{

        private ArrayList<Fragment>fragments;
        private ArrayList<String>titles;

        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            this.fragments=new ArrayList<>();
            this.titles=new ArrayList<>();
        }
        void addFragments(Fragment fragment,String title){
            fragments.add(fragment);
            titles.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
        public CharSequence getPageTitle(int position){
            return titles.get(position);
        }
    }
    public static ArrayList<MusicFiles>getAllAudio(Context  context){
       ArrayList<String>duplicates = new ArrayList<>();
      ArrayList<MusicFiles>tempAudioList = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection={
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media._ID
        };
        Cursor cursor =context.getContentResolver().query(uri,projection,null
        ,null,null);

        if (cursor!=null){
            while (cursor.moveToNext()){
                String album =cursor.getString(0);
                String title =cursor.getString(1);
                String duration =cursor.getString(2);
                String path =cursor.getString(3);
                String artist =cursor.getString(4);
                String id =cursor.getString(5);
                MusicFiles musicFiles =new MusicFiles(path,title,artist,album,duration,id);
                Log.e("Path: "+path,"Album: "+album);
                tempAudioList.add(musicFiles);
                if (!duplicates.contains(album)){
                    albums.add(musicFiles);
                    duplicates.add(album);
                }

            }
            cursor.close();
        }
        return tempAudioList;
    }
}