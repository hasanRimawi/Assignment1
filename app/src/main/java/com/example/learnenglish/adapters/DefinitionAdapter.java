package com.example.learnenglish.adapters;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnenglish.R;

public class DefinitionAdapter extends RecyclerView.Adapter<DefinitionAdapter.ViewHolder> {

    private String[] words;
    private String[] definitions;

    public DefinitionAdapter(String[] words, String[] definitions){
        this.words = words;
        this.definitions = definitions;
    }
    @NonNull
    @Override
    public DefinitionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.word_card,
                parent,
                false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DefinitionAdapter.ViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        TextView word = (TextView)cardView.findViewById(R.id.txt_cardWord);
        TextView definition = (TextView)cardView.findViewById(R.id.txt_cardDefinition);
        Log.d("onbindviewholder", words[position]);
        Log.d("onbindviewholder", definitions[position]);
        word.setText(words[position]);
        Log.d("counter1", String.valueOf(position));
        definition.setText(definitions[position]);
        Log.d("counter2", String.valueOf(position));
    }

    @Override
    public int getItemCount() {
        return words.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private CardView cardView;
        public ViewHolder(CardView cardView){
            super(cardView);
            this.cardView = cardView;
        }

    }
}
