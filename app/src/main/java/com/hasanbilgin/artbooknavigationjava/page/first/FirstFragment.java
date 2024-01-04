package com.hasanbilgin.artbooknavigationjava.page.first;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.hasanbilgin.artbooknavigationjava.R;
import com.hasanbilgin.artbooknavigationjava.adapter.ArtAdapter;
import com.hasanbilgin.artbooknavigationjava.databinding.FragmentFirstBinding;
import com.hasanbilgin.artbooknavigationjava.model.ArtModel;
import com.hasanbilgin.artbooknavigationjava.other.StaticData;
import com.hasanbilgin.artbooknavigationjava.page.second.SecondFragmentDirections;
import com.hasanbilgin.artbooknavigationjava.roomdb.ArtDao;
import com.hasanbilgin.artbooknavigationjava.roomdb.ArtDatabase;
import java.util.List;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FirstFragment extends Fragment {
    ArtAdapter artAdapter;
    private FragmentFirstBinding binding;
    ArtDatabase artDatabase;
    ArtDao artDao;
    private final CompositeDisposable mDisposable = new CompositeDisposable();

    public static FirstFragment newInstance() {
        return new FirstFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //menü için eklendi
        setHasOptionsMenu(true);
      StaticData.setActiveFragment("FirstFragment");
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
        
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        artDatabase = Room.databaseBuilder(requireContext(), ArtDatabase.class, "Arts").build();
        artDao = artDatabase.artDao();
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getContext());
        binding.listRecyclerView.setLayoutManager(layoutManager);
        getData();
    }

    public void getData() {
        mDisposable.add(artDao.getArtWithNameAndId()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(FirstFragment.this::handleResponse));
    }

    private void handleResponse(List<ArtModel> artList) {
        binding.listRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        artAdapter = new ArtAdapter(artList);
        binding.listRecyclerView.setAdapter(artAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        mDisposable.clear();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        MenuInflater menuInflater = requireActivity().getMenuInflater();
        menuInflater.inflate(R.menu.add_art,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.add_art_item) {
            NavDirections directions = FirstFragmentDirections.actionFirstFragmentToSecondFragment("new");
            NavHostFragment.findNavController(this).navigate(directions);
        }
        return super.onOptionsItemSelected(item);
    }
}