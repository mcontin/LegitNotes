package com.legitdevs.legitnotes;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mattia on 07/04/16.
 */

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.CardViewHolder> {

    private ArrayList<Note> notes;  //lista di eventi
    private Context ctx;
    private int filter;

    public NotesAdapter(ArrayList<Note> notes, Context ctx) {
        this.notes = notes;
        this.ctx = ctx;
    }

    /**
     * Chiamato quando il recycler view ha bisogno di una card per mostrare un evento
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
    public void onBindViewHolder(CardViewHolder cardHolder, final int position) {
        cardHolder.noteTitle.setText(notes.get(position).getTitle());

        cardHolder.noteText.setText(notes.get(position).getText());

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void addEvents(List<Note> eventsToAdd) {
        for (Note newEvent : eventsToAdd) {
            notes.add(newEvent);
            notifyItemInserted(notes.size() - 1);
        }
    }

    /**
     * "Contenitore" di ogni card
     */
    public static class CardViewHolder extends RecyclerView.ViewHolder {
        CardView card;
        TextView noteTitle;
        TextView noteText;

        CardViewHolder(View itemView) {
            super(itemView);
            card = (CardView) itemView.findViewById(R.id.cardView);
            noteTitle = (TextView) itemView.findViewById(R.id.title);
            noteText = (TextView) itemView.findViewById(R.id.text);
        }
    }

}