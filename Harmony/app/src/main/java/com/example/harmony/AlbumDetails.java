package com.example.harmony;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AlbumDetails extends AppCompatActivity {
RecyclerView recyclerView;
ImageView albumphoto;
String name;
ArrayList<MusicFiles> albumsongs=new ArrayList<>();
AlbumDetailsAdapter albumDetailsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_details);
        recyclerView=findViewById(R.id.recyclerview);
        albumphoto=findViewById(R.id.album_photo);
        name=getIntent().getStringExtra("albumname");
        int j=0;
        for(int i=0;i<MainActivity.musicFiles.size();i++)
        {
            if(name.equals(MainActivity.musicFiles.get(i).getAlbum()))
            {
                albumsongs.add(j,MainActivity.musicFiles.get(i));
                j++;
            }

        }
        byte[] image=getalbumart(albumsongs.get(0).getPath());
        if(image!=null)
        {
            Glide.with(this).load(image).into(albumphoto);
        }else
        {
            Glide.with(this).load(R.drawable.ic_action_name).into(albumphoto);
        }
    }
    protected void onResume()
    {
        super.onResume();
        if(!(albumsongs.size()<1))
        {
            albumDetailsAdapter=new AlbumDetailsAdapter(this,albumsongs);
            recyclerView.setAdapter(albumDetailsAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
        }
    }
    public byte[] getalbumart(String uri)
    {
        MediaMetadataRetriever retriever=new MediaMetadataRetriever();
        try{
            retriever.setDataSource(uri);}catch (Exception e)
        {
            e.printStackTrace();
        }
        byte[] art=retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }

}
