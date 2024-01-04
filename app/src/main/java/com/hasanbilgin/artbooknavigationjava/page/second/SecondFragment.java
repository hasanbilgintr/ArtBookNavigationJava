package com.hasanbilgin.artbooknavigationjava.page.second;

import static android.content.Context.MODE_PRIVATE;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.room.Room;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.hasanbilgin.artbooknavigationjava.R;
import com.hasanbilgin.artbooknavigationjava.databinding.FragmentSecondBinding;
import com.hasanbilgin.artbooknavigationjava.model.ArtModel;
import com.hasanbilgin.artbooknavigationjava.roomdb.ArtDao;
import com.hasanbilgin.artbooknavigationjava.roomdb.ArtDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SecondFragment extends Fragment {

    Bitmap selectedImage;
    String info = "";
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    private FragmentSecondBinding binding;
    private final CompositeDisposable mDisposable = new CompositeDisposable();
    ArtDatabase artDatabase;
    ArtDao artDao;
    ArtModel artFromMain;

    public static SecondFragment newInstance() {
        return new SecondFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registerLauncher();

        artDatabase = Room.databaseBuilder(requireContext(),
                        ArtDatabase.class, "Arts")
                .build();

        artDao = artDatabase.artDao();
        //database = requireActivity().openOrCreateDatabase("Arts", MODE_PRIVATE,null);


        if (getArguments() != null) {
            info = SecondFragmentArgs.fromBundle(getArguments()).getInfo();
        } else {
            info = "new";
        }


        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save(view);
            }
        });

        binding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(view);
            }
        });

        binding.delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete(v);
            }
        });


        if (info.equals("new")) {
            binding.artNameText.setText("");
            binding.painterNameText.setText("");
            binding.yearText.setText("");
            binding.saveButton.setVisibility(View.VISIBLE);
            binding.delButton.setVisibility(View.GONE);

            binding.imageView.setImageResource(R.drawable.select);

        } else {

            int artId = SecondFragmentArgs.fromBundle(getArguments()).getArtId();
            binding.saveButton.setVisibility(View.GONE);
            binding.delButton.setVisibility(View.VISIBLE);

            mDisposable.add(artDao.getArtById(artId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(SecondFragment.this::handleResponseWithOldArt));
        }
    }

    private void handleResponseWithOldArt(ArtModel art) {
        artFromMain = art;
        binding.artNameText.setText(art.artname);
        binding.painterNameText.setText(art.artistName);
        binding.yearText.setText(art.year);

        Bitmap bitmap = BitmapFactory.decodeByteArray(art.image, 0, art.image.length);
        binding.imageView.setImageBitmap(bitmap);
    }

    public void selectImage(View view) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_MEDIA_IMAGES)) {
                    Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                        }
                    }).show();
                } else {
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                }
            } else {
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intentToGallery);
            }
        } else {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                        }
                    }).show();
                } else {
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
            } else {
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intentToGallery);
            }
        }

    }

    public void save(View view) {

        String artName = binding.artNameText.getText().toString();
        String painterName = binding.painterNameText.getText().toString();
        String year = binding.yearText.getText().toString();

        Bitmap smallImage = makeSmallerImage(selectedImage, 300);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        smallImage.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
        byte[] byteArray = outputStream.toByteArray();

        ArtModel art = new ArtModel(artName, painterName, year, byteArray);

        mDisposable.add(artDao.insert(art)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(SecondFragment.this::handleResponse));
    }

    public void delete(View view) {
        mDisposable.add(artDao.delete(artFromMain)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(SecondFragment.this::handleResponse));
    }

    private void handleResponse() {
        NavDirections action = SecondFragmentDirections.actionSecondFragmentToFirstFragment();
        Navigation.findNavController(requireView()).navigate(action);
    }

    public void registerLauncher() {
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent intentFromResult = result.getData();
                            if (intentFromResult != null) {
                                Uri imageData = intentFromResult.getData();
                                try {

                                    if (Build.VERSION.SDK_INT >= 28) {
                                        ImageDecoder.Source source = ImageDecoder.createSource(requireActivity().getContentResolver(), imageData);
                                        selectedImage = ImageDecoder.decodeBitmap(source);
                                        binding.imageView.setImageBitmap(selectedImage);

                                    } else {
                                        selectedImage = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageData);
                                        binding.imageView.setImageBitmap(selectedImage);
                                    }

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    }
                });

        permissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
                    @Override

                    public void onActivityResult(Boolean result) {
                        if (result) {
                            //permission granted
                            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            //startActivityForResult(intentToGallery,2);
                            activityResultLauncher.launch(intentToGallery);

                        } else {
                            //permission denied
                            Toast.makeText(requireActivity(), "Permisson needed!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public Bitmap makeSmallerImage(Bitmap image, int maximumSize) {

        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;

        if (bitmapRatio > 1) {
            width = maximumSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maximumSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        mDisposable.clear();
    }
}