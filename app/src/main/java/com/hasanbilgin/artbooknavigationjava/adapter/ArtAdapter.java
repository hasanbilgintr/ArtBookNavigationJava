package com.hasanbilgin.artbooknavigationjava.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.hasanbilgin.artbooknavigationjava.databinding.RecyclerRowBinding;
import com.hasanbilgin.artbooknavigationjava.model.ArtModel;
import com.hasanbilgin.artbooknavigationjava.other.StaticData;
import com.hasanbilgin.artbooknavigationjava.page.first.FirstFragmentDirections;

import java.util.ArrayList;
import java.util.List;

public class ArtAdapter extends RecyclerView.Adapter<ArtAdapter.ArtHolder> {

    List<ArtModel> artList;

    public ArtAdapter(List<ArtModel> artList) {
        this.artList = artList;
    }

    class ArtHolder extends RecyclerView.ViewHolder {

        private RecyclerRowBinding binding;

        public ArtHolder(RecyclerRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @Override
    public ArtHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        RecyclerRowBinding binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ArtHolder(binding);
    }

    @Override
    public void onBindViewHolder(ArtAdapter.ArtHolder holder, int position) {
        holder.binding.recyclerTextView.setText(artList.get(position).artname);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticData.setActiveFragment("SecondFragment");
                FirstFragmentDirections.ActionFirstFragmentToSecondFragment action = FirstFragmentDirections.actionFirstFragmentToSecondFragment("old");
                action.setArtId(artList.get(position).id);
                action.setInfo("old");
                Navigation.findNavController(view).navigate(action);
            }
        });
    }

    @Override
    public int getItemCount() {
        return artList.size();
    }
}
