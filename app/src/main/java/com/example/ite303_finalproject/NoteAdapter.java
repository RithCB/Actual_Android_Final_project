package com.example.ite303_finalproject;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private ArrayList<Note> noteList;
    private OnItemClickListener clickListener;

    // Interface for click handling
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public NoteAdapter(ArrayList<Note> noteList, OnItemClickListener clickListener) {
        this.noteList = noteList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_lists, parent, false);
        return new NoteViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = noteList.get(position);
        holder.title.setText(note.getNote_title());
        holder.description.setText(note.getNote_description());
        holder.priority.setText(note.getPriority());
        switch (note.getPriority()) {
            case "High":
                holder.priority.setTextColor(Color.parseColor("#FF1744")); // Bright Red
                break;
            case "Medium":
                holder.priority.setTextColor(Color.parseColor("#FFA500")); // Bright Orange
                break;
            case "Low":
                holder.priority.setTextColor(Color.parseColor("#00C853")); // Bright Green
                break;
            default:
                holder.priority.setTextColor(Color.BLACK);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, priority;

        public NoteViewHolder(@NonNull View itemView, OnItemClickListener clickListener) {
            super(itemView);
            title = itemView.findViewById(R.id.note_title);
            description = itemView.findViewById(R.id.note_desc);
            priority = itemView.findViewById(R.id.note_priority);

            // Set click listener on the whole itemView
            itemView.setOnClickListener(v -> {
                if (clickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    clickListener.onItemClick(getAdapterPosition());
                }
            });
        }
    }

    public void updateList(ArrayList<Note> newList) {
        noteList = newList;
        notifyDataSetChanged();
    }
}
