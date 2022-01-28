package com.example.signup;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;


public class myAdapter extends FirebaseRecyclerAdapter<fileModel, myAdapter.myviewholder> {
    public myAdapter(@NonNull FirebaseRecyclerOptions<fileModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull fileModel model) {
        Glide.with(holder.post.getContext()).load(model.getPost()).into(holder.post);
        holder.authorname.setText(model.getAuthorname());
        holder.bookname.setText(model.getBookname());
        holder.publishername.setText(model.getPublishername());
        holder.description.setText(model.getDescription());

    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singlerow, parent, false);
        return new myviewholder(view);
    }

    class myviewholder extends RecyclerView.ViewHolder {
        TextView authorname, bookname, publishername, description;
        ImageView post;
        public myviewholder(@NonNull View itemView) {
            super(itemView);
            authorname = (TextView) itemView.findViewById(R.id.author_name);
            bookname = (TextView) itemView.findViewById(R.id.book_name);
            publishername = (TextView) itemView.findViewById(R.id.publisher_name);
            description = (TextView) itemView.findViewById(R.id.description);
            post = (ImageView) itemView.findViewById(R.id.img1);

        }
    }
}
