package com.example.vraja03.newsearch.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.vraja03.newsearch.R;
import com.example.vraja03.newsearch.activities.ArticleActivity;
import com.example.vraja03.newsearch.helper.DynamicHeightImageView;
import com.example.vraja03.newsearch.model.Article;

import java.util.List;

/**
 * Created by VRAJA03 on 2/11/2016.
 */
public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    private List<Article> mArticle;
    Context context;

   // Pass in the contact array into the constructor
    public ArticleAdapter(List<Article> articles) {
        mArticle = articles;
    }

    @Override
    public ArticleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View articleView = inflater.inflate(R.layout.item_article_result, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(articleView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get the data model based on position
        final Article article = mArticle.get(position);

        // Set item views based on the data model
        TextView textView = holder.tvTitle;
        textView.setText(article.getHeadline());

        DynamicHeightImageView imageView = holder.ivImage;
        imageView.setImageResource(0);

        String thumbnail = article.getThumbNail();

        imageView.setHeightRatio(((double) article.getHeight()) / article.getWidth());

        if (!TextUtils.isEmpty(thumbnail)) {
            Glide.with(context).load(thumbnail).fitCenter().diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(context, ArticleActivity.class);
                in.putExtra("url", article.getWebUrl());
                context.startActivity(in);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mArticle.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public DynamicHeightImageView ivImage;
        public TextView tvTitle;


        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            ivImage = (DynamicHeightImageView) itemView.findViewById(R.id.ivImage);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);

        }
    }

}
