package com.example.harmony;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    public static final int REQUEST_CODE=1;
    static ArrayList<MusicFiles> musicFiles;
    static boolean shuffle=false,repeat=false;
    static ArrayList<MusicFiles> albums=new ArrayList<>();
    String MY_SORT_PREFERENCE="sortorder";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permission();

    }

    private void permission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE);
        }else
        {
            musicFiles=getallaudio(this);
            initViewPager();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_CODE)
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                musicFiles=getallaudio(this);
                initViewPager();
            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE);
            }
        }
    }

    private void initViewPager() {
        ViewPager viewPager=findViewById(R.id.viewpager);
        TabLayout tabLayout=findViewById(R.id.tablayout);
        ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new SongsFragment(),"songs");
        viewPagerAdapter.addFragment(new AlbumFragment(),"album");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }



    public static class ViewPagerAdapter extends FragmentPagerAdapter
    {
        ArrayList<Fragment> fragments;
        ArrayList<String> titles;

        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            this.fragments=new ArrayList<>();
            this.titles=new ArrayList<>();
        }

        void addFragment(Fragment fragment,String title)
        {
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

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    public  ArrayList<MusicFiles> getallaudio(Context context)
    {
        SharedPreferences Preferences=getSharedPreferences(MY_SORT_PREFERENCE,MODE_PRIVATE);
        String sortorder=Preferences.getString("sorting","sortbyname");

        ArrayList<String> duplicate=new ArrayList<>();
        albums.clear();
        ArrayList<MusicFiles> tempaudiolist=new ArrayList<>();
        String order=null;
        Uri uri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        switch (sortorder)
        {
            case "name":
                order=MediaStore.MediaColumns.DISPLAY_NAME+"ASC";
                break;
            case "date":
                order=MediaStore.MediaColumns.DATE_ADDED+"ASC";
                break;
            case "size":
                order=MediaStore.MediaColumns.SIZE+"DSEC";
                break;
        }
        String projection[]={MediaStore.Audio.Media.ALBUM,MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,MediaStore.Audio.Media.DATA,MediaStore.Audio.Media.ARTIST,MediaStore.Audio.Media._ID};
        Cursor cursor=context.getContentResolver().query(uri,projection,null,null,order);
        if(cursor!=null)
        {
            while(cursor.moveToNext())
            {
                String album=cursor.getString(0);
                String title=cursor.getString(1);
                String duration=cursor.getString(2);
                String path=cursor.getString(3);
                String artist=cursor.getString(4);
                String id=cursor.getString(5);
                MusicFiles musicFiles=new MusicFiles(path,title,artist,album,duration,id);
                tempaudiolist.add(musicFiles);
                if(!duplicate.contains(album)){
                    albums.add(musicFiles);
                    duplicate.add(album);
                }
            }

            cursor.close();
        }
        return tempaudiolist;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search,menu);
        MenuItem menuItem=menu.findItem(R.id.search_option);
        SearchView searchView= (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String input=newText.toLowerCase();
        ArrayList<MusicFiles> myfiles=new ArrayList<>();
        for(MusicFiles song:musicFiles)
        {
            if(song.getTitle().toLowerCase().contains(input))
            {
                myfiles.add(song);
            }
        }
        SongsFragment.musicAdapter.updatelist(myfiles);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        SharedPreferences.Editor editor=getSharedPreferences(MY_SORT_PREFERENCE,MODE_PRIVATE).edit();
        switch (item.getItemId())
        {
            case R.id.name:
                editor.putString("sorting","sortbyname");
                editor.apply();
                this.recreate();
                break;
            case R.id.date:
                editor.putString("sorting","sortbydate");
                editor.apply();
                this.recreate();
                break;
            case R.id.size:
                editor.putString("sorting","sortbysize");
                editor.apply();
                this.recreate();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
