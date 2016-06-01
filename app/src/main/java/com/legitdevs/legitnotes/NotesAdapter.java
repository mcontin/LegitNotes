package com.legitdevs.legitnotes;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.legitdevs.legitnotes.filemanager.FileManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by mattia on 07/04/16.
 */

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.CardViewHolder> {

    private ArrayList<Note> notes;  //lista di eventi
    private Context ctx;

    public NotesAdapter(ArrayList<Note> notes, Context ctx) {
        this.notes = notes;
        this.ctx = ctx;
    }

    /**
     * Chiamato quando il recycler view ha bisogno di una card per mostrare una nota
     *
     * @param viewGroup view padre di ogni carta (recyclerview in teoria)
     * @param viewType  tipo della view che sarà popolata (CardView)
     * @return oggetto CardViewHolder definito alla fine che setterà i vari TextView presenti nella CardView
     */
    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card, viewGroup, false);
        return new CardViewHolder(v);
    }

    /**
     * Crea una card, chiamato ogni volta che deve essere mostrata una CardView
     *
     * @param cardHolder CardViewHolder restituito dal metodo precedente
     * @param position   posizione di un evento nella lista
     */
    @Override
    public void onBindViewHolder(final CardViewHolder cardHolder, final int position) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        cardHolder.noteDate.setText(dateFormat.format(notes.get(position).getDate()));


        cardHolder.noteTitle.setText(notes.get(position).getTitle());

        cardHolder.noteSnippet.setText(Html.fromHtml(notes.get(position).getText()));

        cardHolder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent vIntent = new Intent(ctx, NoteDetailActivity.class);
                vIntent.putExtra(NoteDetailActivity.KEY_NOTE, notes.get(position));
                ctx.startActivity(vIntent);
            }
        });

        cardHolder.card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                EditDialog.getInstance(notes.get(position), position).show(((HomeActivity) ctx).getSupportFragmentManager(), "dialog");

                return true;
            }
        });

//        if (!notes.get(position).getText().equals(""))
//            cardHolder.textIcon.setImageResource(R.drawable.ic_text_format_blue_24dp);
//        else
//            cardHolder.textIcon.setImageResource(R.drawable.ic_text_format_gray_24dp);

        if (notes.get(position).getMedias().get(FileManager.TYPE_AUDIO) != null)
            cardHolder.audioIcon.setImageResource(R.drawable.ic_keyboard_voice_blue_24dp);
        else
            cardHolder.audioIcon.setImageResource(R.drawable.ic_keyboard_voice_gray_24dp);

        if (notes.get(position).getMedias().get(FileManager.TYPE_IMAGE) != null)
            cardHolder.imageIcon.setImageResource(R.drawable.ic_image_blue_24dp);
        else
            cardHolder.imageIcon.setImageResource(R.drawable.ic_image_gray_24dp);

        if (notes.get(position).getMedias().get(FileManager.TYPE_VIDEO) != null)
            cardHolder.videoIcon.setImageResource(R.drawable.ic_local_movies_blue_24dp);
        else
            cardHolder.videoIcon.setImageResource(R.drawable.ic_local_movies_gray_24dp);


    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void updateNotes(ArrayList<Note> notes) {
        this.notes = notes;

        notifyDataSetChanged();
    }

    public void addNote(Note note) {
        notes.add(note);

        notifyItemInserted(notes.indexOf(note));
    }

    public void removeNote(int position) {
        notes.remove(position);

        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
    }

    /**
     * "Contenitore" di ogni card
     */
    public static class CardViewHolder extends RecyclerView.ViewHolder {
        CardView card;
        TextView noteTitle, noteSnippet, noteDate;
        ImageView textIcon, audioIcon, imageIcon, videoIcon;

        CardViewHolder(View itemView) {
            super(itemView);
            card = (CardView) itemView.findViewById(R.id.cardView);
            noteDate = (TextView) itemView.findViewById(R.id.date);
            noteTitle = (TextView) itemView.findViewById(R.id.title);
            noteSnippet = (TextView) itemView.findViewById(R.id.text);
//            textIcon = (ImageView) itemView.findViewById(R.id.text_icon);
            audioIcon = (ImageView) itemView.findViewById(R.id.audio_icon);
            imageIcon = (ImageView) itemView.findViewById(R.id.image_icon);
            videoIcon = (ImageView) itemView.findViewById(R.id.video_icon);
        }
    }

    public void animateTo(ArrayList<Note> notes) {
        applyAndAnimateRemovals(notes);
        applyAndAnimateAdditions(notes);
        applyAndAnimateMovedItems(notes);
    }

    private void applyAndAnimateRemovals(ArrayList<Note> newNotes) {
        for (int i = notes.size() - 1; i >= 0; i--) {
            final Note note = notes.get(i);
            if (!newNotes.contains(note)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(ArrayList<Note> newNotes) {
        for (int i = 0, count = newNotes.size(); i < count; i++) {
            final Note note = newNotes.get(i);
            if (!notes.contains(note)) {
                addItem(i, note);
            }
        }
    }

    private void applyAndAnimateMovedItems(ArrayList<Note> newNotes) {
        for (int toPosition = newNotes.size() - 1; toPosition >= 0; toPosition--) {
            final Note note = newNotes.get(toPosition);
            final int fromPosition = notes.indexOf(note);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public Note removeItem(int position) {
        final Note note = notes.remove(position);
        notifyDataSetChanged();
        return note;
    }

    public void addItem(int position, Note note) {
        notes.add(position, note);
        notifyDataSetChanged();
    }

    public void moveItem(int fromPosition, int toPosition) {
        final Note note = notes.remove(fromPosition);
        notes.add(toPosition, note);
        notifyDataSetChanged();
    }

}