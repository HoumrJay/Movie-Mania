package com.example.pavol.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pavol.popularmovies.R;
import com.example.pavol.popularmovies.custom_objects.ReviewModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

    private final ArrayList<ReviewModel> mListOfReviews;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.actual_user_name)
        TextView actualUserName;
        @BindView(R.id.review_content)
        TextView reviewContent;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    public ReviewsAdapter(final ArrayList<ReviewModel> listOfReviews) {
        this.mListOfReviews = listOfReviews;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.review_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final ReviewModel singleReview = mListOfReviews.get(position);

        holder.actualUserName.setText(singleReview.mUserName);
        holder.reviewContent.setText(singleReview.mReview);
    }

    @Override
    public int getItemCount() {
        return mListOfReviews.size();
    }
}
