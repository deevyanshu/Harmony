package com.example.harmony;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MyHolder> {

    Context context;
    ArrayList<MusicFiles> albumfiles;

    public AlbumAdapter(Context context, ArrayList<MusicFiles> albumfiles) {
        this.context = context;
        this.albumfiles = albumfiles;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.album_item,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {
        holder.albumname.setText(albumfiles.get(position).getAlbum());
        byte[] image=getalbumart(albumfiles.get(position).getPath());
        if(image!=null)
        {
            Glide.with(context).asBitmap().load(image).into(holder.albumimg);
        }else
        {
            Glide.with(context).load(R.drawable.ic_action_name).into(holder.albumimg);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,AlbumDetails.class);
                intent.putExtra("albumname",albumfiles.get(position).getAlbum());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return albumfiles.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
ImageView albumimg;
TextView albumname;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            albumimg=itemView.findViewById(R.id.album_image);
            albumname=itemView.findViewById(R.id.album_name);
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
