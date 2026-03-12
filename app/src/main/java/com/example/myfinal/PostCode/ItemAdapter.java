package com.example.myfinal.PostCode;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfinal.Objects.Post;
import com.example.myfinal.R;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.PostViewHolder> {

    private Context context;
    private List<Post> postList;

    public ItemAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_post_item, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post currentPost = postList.get(position);

        holder.tvAuthor.setText(currentPost.getAuthorName());
        holder.tvHeadline.setText(currentPost.getHeadline());
        holder.tvDetails.setText(currentPost.getDetails());

        // NEW: Check if tags exist, and if so, display them formatted like hashtags
        if (currentPost.getTags() != null && !currentPost.getTags().isEmpty()) {
            // Formatting the comma-separated words into hashtags
            String formattedTags = "#" + currentPost.getTags().replace(", ", " #").replace(",", " #");
            holder.tvTags.setText(formattedTags);
            holder.tvTags.setVisibility(View.VISIBLE);
        } else {
            // Hide the view if there are no tags (for older posts)
            holder.tvTags.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeadline, tvDetails, tvTags, tvAuthor;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAuthor = itemView.findViewById(R.id.tv_post_author);
            tvHeadline = itemView.findViewById(R.id.tv_post_headline);
            tvDetails = itemView.findViewById(R.id.tv_post_details);
            tvTags = itemView.findViewById(R.id.tv_post_tags); // NEW
        }
    }
}