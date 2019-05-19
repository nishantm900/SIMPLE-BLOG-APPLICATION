package com.example.nishant.simpleblogapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class CommentsRecyclerAdapter extends RecyclerView.Adapter<CommentsRecyclerAdapter.ViewHolder> {
    public List<Comments> commentsList;
    public Context context;

    public CommentsRecyclerAdapter(List<Comments> commentsList){
        this.commentsList = commentsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_list_item, viewGroup, false);
        context = viewGroup.getContext();
        return new CommentsRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.setIsRecyclable(false);

        String commentMessage = commentsList.get(i).getMessage();
        viewHolder.setComment_message(commentMessage);


    }

    @Override
    public int getItemCount() {
        if(commentsList != null) {
            return commentsList.size();

        } else {
            return 0;

        }

        }

    public  class ViewHolder extends RecyclerView.ViewHolder{
        private View mView;
        private TextView comment_message;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;


        }
        public void setComment_message(String message){
            comment_message = mView.findViewById(R.id.comment_message);
            comment_message.setText(message);


        }

        }
}
