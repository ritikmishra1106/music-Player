package com.ritik.musicplayer;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadata;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder> {
    Context context;
    ArrayList<MusicFiles>mFiles;
    MusicAdapter(Context context,ArrayList<MusicFiles>mFiles){
        this.mFiles=mFiles;
        this.context=context;
    }

    @NonNull
    @Override
    public MusicAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.music_item,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.music_file_name.setText(mFiles.get(position).title);
        byte[] image=getAlbumArt(mFiles.get(position).path);
        if (image!=null){
            Glide.with(context).asBitmap()
                    .load(image)
                    .into(holder.music_img);
        }else{
            Glide.with(context)
                    .load(R.drawable.music)
                    .into(holder.music_img);

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,PlayerActivity.class);
                intent.putExtra("position",position);
                context.startActivity(intent);

            }
        });
        holder.menuMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context,view);
                popupMenu.getMenuInflater().inflate(R.menu.popup,popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.delete:
                                Toast.makeText(context,"Delete Clicked",Toast.LENGTH_SHORT).show();
                                deleteFile(position,view);
                                break;
                        }
                        return true;
                    }
                });
            }
        });
    }

    private void deleteFile(int position,View view){
        Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                Long.parseLong(mFiles.get(position).id));

        File file= new File(mFiles.get(position).path);
        boolean deleted= file.delete();
        if (deleted){
            context.getContentResolver().delete(contentUri,null,null);
            mFiles.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position,mFiles.size());
            Snackbar.make(view,"File Deleted",Snackbar.LENGTH_LONG)
                    .show();
        }else{
            Snackbar.make(view,"Can't be Deleted",Snackbar.LENGTH_LONG)
                    .show();
        }


    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView music_file_name;
        ImageView music_img,menuMore;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            music_file_name=itemView.findViewById(R.id.music_file_name);
            music_img=itemView.findViewById(R.id.music_img);
            menuMore=itemView.findViewById(R.id.menuMore);

        }
    }
    private byte[] getAlbumArt(String uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[]art = retriever.getEmbeddedPicture();
        return art;
    }
}
