package com.musicbox.mobilemusicbox;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by fescalona on 12/26/2015.
 */
public class SongViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    //private final View.OnClickListener mOnClickListener = new MyOnClickListener();
    protected TextView title;
    protected TextView artist;
    protected ImageView bitmap;
    protected Float id;

    public SongViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        title = (TextView) itemView.findViewById(R.id.titleText);
        artist = (TextView) itemView.findViewById(R.id.artistText);
        bitmap = (ImageView) itemView.findViewById(R.id.imageViewCard);
    }


    @Override
    public void onClick(View v) {
        //Toast.makeText(v.getContext(), "position = " + id, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(v.getContext(), DetailActivity.class);
        intent.putExtra(HomeActivity.SONG_ID, id);
        v.getContext().startActivity(intent);
    }
}
