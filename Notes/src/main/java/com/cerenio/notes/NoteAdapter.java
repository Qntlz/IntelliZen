package com.cerenio.notes;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private List<Note> notes = new ArrayList<>();
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEdit(Note note);
        void onDelete(Note note);
    }

    public NoteAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<Note> list) {
        notes = new ArrayList<>(list); // Use a new list reference
        notifyDataSetChanged(); // Force RecyclerView refresh
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);
        //Log.d("NoteAdapter", "Binding note: Title=" + note.title + ", Content=" + note.content);
        holder.title.setText(note.title);
        holder.content.setText(note.content);
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(note));
        holder.itemView.setOnClickListener(v -> listener.onEdit(note));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView title, content;
        ImageView btnDelete;

        NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textNoteTitle);
            content = itemView.findViewById(R.id.textNoteContent);
            btnDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}