package com.example.pavol.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pavol.popularmovies.R;
import com.example.pavol.popularmovies.custom_objects.VideoModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.ViewHolder> {

    private final ArrayList<VideoModel> mListOfVideoKeys;
    private Context context;
    private String mUrl;


    private static final String BASE_YOUTUBE_URL = "https://www.youtube.com/watch?v=";

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.trailer_list_item_tv)
        TextView listItemTv;


        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public VideosAdapter(final ArrayList<VideoModel> listOfVideoKeys) {
        this.mListOfVideoKeys = listOfVideoKeys;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.trailer_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final VideoModel singleVideoKey = mListOfVideoKeys.get(position);

        holder.listItemTv.setText(singleVideoKey.mVideoName);

        // short click to open the video
        holder.listItemTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUrl = BASE_YOUTUBE_URL + singleVideoKey.mVideoKey;
                Intent openVideo = new Intent(Intent.ACTION_VIEW);
                openVideo.setData(Uri.parse(mUrl));
                context.startActivity(openVideo);
            }
        });

        //long click to share the video
        holder.listItemTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mUrl = BASE_YOUTUBE_URL + singleVideoKey.mVideoKey;
                shareIntent(mUrl);
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return mListOfVideoKeys.size();
    }

    private void shareIntent(String url) {

        String mimeType = "text/plain";

        String chooser = "Share the video";


        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType(mimeType);
                shareIntent.putExtra(Intent.EXTRA_TEXT, url);
                shareIntent.setType(mimeType);
                context.startActivity(Intent.createChooser(shareIntent, chooser));
    }

}
