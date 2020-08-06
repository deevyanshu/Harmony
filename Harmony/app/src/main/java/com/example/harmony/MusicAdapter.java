package com.example.harmony;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewholder> {

    Context context;
    static ArrayList<MusicFiles> mfiles;

    MusicAdapter(Context context,ArrayList<MusicFiles> mfiles)
    {
        this.context=context;
        this.mfiles=mfiles;
    }

    @NonNull
    @Override
    public MyViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.music_item,parent,false);
        return new MyViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewholder holder, final int position) {
        holder.filename.setText(mfiles.get(position).getTitle());
        byte[] image=getalbumart(mfiles.get(position).getPath());
        if(image!=null)
        {
            Glide.with(context).asBitmap().load(image).into(holder.album);
        }else
        {
            Glide.with(context).load(R.drawable.ic_action_name).into(holder.album);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(context,PlayerActivity.class);
                in.putExtra("pos",position);
                context.startActivity(in);
            }
        });
        holder.menumore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                PopupMenu popupMenu=new PopupMenu(context,v);
                popupMenu.getMenuInflater().inflate(R.menu.popup,popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId())
                        {
                            case R.id.delete:
                                deletefile(position,v);
                                break;
                        }
                        return true;
                    }
                });
            }
        });
    }

    private void deletefile(int position,View v)
    {
        Uri contenturi= ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,Long.parseLong(mfiles.get(position).getId()));
        mfiles.remove(position);
        File file=new File(mfiles.get(position).getPath());
        boolean deleted=file.delete();
        if(deleted)
        {
            context.getContentResolver().delete(contenturi,null,null);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position,mfiles.size());
            Snackbar.make(v,"File Deleted:",Snackbar.LENGTH_SHORT).show();
        }else
        {
            Snackbar.make(v,"File cannot be Deleted:",Snackbar.LENGTH_SHORT).show();

        }

    }

    @Override
    public int getItemCount() {
        return mfiles.size();
    }

    public class MyViewholder extends RecyclerView.ViewHolder{

        TextView filename;
        ImageView album,menumore;
        public MyViewholder(@NonNull View itemView) {
            super(itemView);
            filename=itemView.findViewById(R.id.music_file_name);
            album=itemView.findViewById(R.id.music_img);
            menumore=itemView.findViewById(R.id.menumore);
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

    void updatelist(ArrayList<MusicFiles> musicFilesArrayList)
    {
        mfiles=new ArrayList<>();
        mfiles.addAll(musicFilesArrayList);
        notifyDataSetChanged();
    }
}
