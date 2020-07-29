package com.example.photoapp54;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.example.photoapp54.model.Album;
import com.example.photoapp54.model.Photo;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class AlbumActivity extends AppCompatActivity {

    private GridView photoList;
    private TextView albumName;

    private ArrayList<Album> allAlbums;
    private Album currAlbum;
    private int currAlbumPos;
    private Photo currPhoto;
    public String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_page);
        path = this.getApplicationInfo().dataDir + "/data.dat";
        Intent intent = getIntent();
        allAlbums = (ArrayList<Album>) intent.getSerializableExtra("allAlbums");

        currAlbumPos = intent.getIntExtra("currAlbumPos", 0);
        currAlbum = allAlbums.get( currAlbumPos);
        albumName = findViewById(R.id.albumName);
        albumName.setText(currAlbum.getTitle());

        PhotoAdaptor adapter = new PhotoAdaptor(this, R.layout.adaptor_view, currAlbum.getPictureList());
        adapter.setNotifyOnChange(true);
        photoList = findViewById(R.id.photoList);
        photoList.setAdapter(adapter);
        if(!currAlbum.getPictureList().isEmpty()) {
            photoList.setItemChecked(0, true);
            currPhoto = currAlbum.getPictureList().get(0);
        }

        photoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                photoList.setItemChecked(position, true);
                currPhoto = currAlbum.getPictureList().get(position);
            }
        });
    }

    public void displayPhoto(View view) {
        if (currAlbum.getPictureList().size() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Oops! There was an error...");
            builder.setMessage("There are no Images to display!");
            builder.setPositiveButton("Close", null);
            builder.show();
            return;
        }

        Intent intent = new Intent(this, DisplayActivity.class);

        intent.putExtra("allAlbums", allAlbums);
        intent.putExtra("currAlbum", currAlbum);
        intent.putExtra("currAlbumPos", currAlbumPos);
        intent.putExtra("currPhoto", currPhoto);
        int pos = 0;
        for (int i = 0; i < currAlbum.getPictureList().size(); i++) {
            if (currPhoto.equals(currAlbum.getPictureList().get(i))) {
                pos = i;
                break;
            }
        }
        intent.putExtra("currPhotoPos", pos);
        startActivity(intent);
    }

    public void toHome(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        try {
            intent.putExtra("allAlbums", allAlbums);
            intent.putExtra("currAlbum", currAlbum);
            intent.putExtra("currAlbumPos", currAlbumPos);
            startActivity(intent);
        }
        catch (Exception e) {
            albumName.setTextSize(12);
            albumName.setText(e.toString());
        }
    }

    public void deletePhoto(View view){
        PhotoAdaptor adaptor = (PhotoAdaptor) photoList.getAdapter();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if(adaptor.getCount() == 0){
            builder.setTitle("Oops! There was an error...");
            builder.setMessage("There are no Images selected to delete!");
            builder.setPositiveButton("Close", null);
            builder.show();

            return;
        }

        builder.setTitle("Delete");
        builder.setMessage("Delete " + currPhoto.getPhotoName() + " from " + currAlbum.getTitle()+"?");
        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        currAlbum.removePicture(currPhoto);
                        saveData(allAlbums);
                        updatePhotos(currAlbum);
                        photoList.refreshDrawableState();
                        if(!currAlbum.getPictureList().isEmpty()){
                            currPhoto = currAlbum.getPictureList().get(0);
                            photoList.setItemChecked(0, true);
                        }
                        else {
                            photoList.setAdapter(null);
                        }
                    }
                });
        builder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.show();

        return;
    }

    public void addPhoto(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");

        startActivityForResult(intent, 2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        super.onActivityResult(requestCode, resultCode, resultData);

        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {

            if (resultData != null) {
                Uri uri = resultData.getData();
                Bitmap bitmap = null;
                try {
                    ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
                    FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                    bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                    parcelFileDescriptor.close();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                if(bitmap!=null) {
                    String name = uri.toString();
                    Photo photo = new Photo(name);
                    for (int i = 0; i < currAlbum.getPictureList().size(); i++) {
                        if (photo.getPath().equals(currAlbum.getPictureList().get(i).getPath())) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setTitle("Oops! There was an error...");
                            builder.setMessage(name + " already exists in " + currAlbum.getTitle());
                            builder.setPositiveButton("OK", null);
                            builder.show();

                            return;
                        }
                    }

                    currAlbum.addPicture(photo);
                    currPhoto = currAlbum.getPictureList().get(currAlbum.getPictureList().size() - 1);
                    updatePhotos(currAlbum);
                    photoList.setItemChecked(currAlbum.getPictureList().size() - 1, true);
                    photoList.refreshDrawableState();
                    saveData(allAlbums);
                }
            }
        }
    }

    public void copyBtn(View view){
        if (allAlbums.size() <= 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);builder.setTitle("Oops! There was an error...");
            builder.setMessage("There are no other albums to copy to.");
            builder.setPositiveButton("OK", null);
            builder.show();
            return;
        }
        if (currAlbum.getPictureList().size() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Oops! There was an error...");
            builder.setMessage("There are no Images to copy!");
            builder.setPositiveButton("Close", null);
            builder.show();
            return;
        }

        CharSequence[] albumsPossible = new CharSequence[allAlbums.size()-1];

        int value = 0;
        for(int i=0; i<allAlbums.size(); i++){
            if(!allAlbums.get(i).getTitle().equals(currAlbum.getTitle())) {
                albumsPossible[value] = allAlbums.get(i).getTitle();
                value++;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final String[] moveToAlbum = {albumsPossible[0].toString()};

        builder.setSingleChoiceItems(albumsPossible, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                moveToAlbum[0] = albumsPossible[item].toString();
            }
        });

        builder.setPositiveButton("Copy",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        //Photo photo = adaptor.getItem(photoList.getCheckedItemPosition());
                        for(int i=0; i< allAlbums.size(); i++){
                            if(allAlbums.get(i).getTitle().equals(moveToAlbum[0])){
                                Album newAlbum = allAlbums.get(i);
                                for(int j=0; j<newAlbum.getPictureList().size(); j++){
                                    if(currPhoto.getPath().equals(newAlbum.getPictureList().get(j).getPath())){
                                        builder.setTitle("Oops! There was an error...");
                                        builder.setMessage("This photo already exists in " + moveToAlbum[0]);
                                        builder.setPositiveButton("OK", null);
                                        builder.show();

                                        return;
                                    }
                                }

                                newAlbum.addPicture(currPhoto);
                                saveData(allAlbums);
                            }
                        }
                    }
                });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        builder.show();
        return;
    }

    public void moveBtn(View view){
        if (allAlbums.size() <= 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);builder.setTitle("Oops! There was an error...");
            builder.setMessage("There are no other albums to copy to.");
            builder.setPositiveButton("OK", null);
            builder.show();
            return;
        }
        if (currAlbum.getPictureList().size() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Oops! There was an error...");
            builder.setMessage("There are no Images to move!");
            builder.setPositiveButton("Close", null);
            builder.show();
            return;
        }

        CharSequence[] albumsPossible = new CharSequence[allAlbums.size()-1];

        int value = 0;
        for(int i=0; i<allAlbums.size(); i++){
            if(!allAlbums.get(i).getTitle().equals(currAlbum.getTitle())) {
                albumsPossible[value] = allAlbums.get(i).getTitle();
                value++;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final String[] moveToAlbum = {albumsPossible[0].toString()};

        builder.setSingleChoiceItems(albumsPossible, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                moveToAlbum[0] = albumsPossible[item].toString();
            }
        });

        builder.setPositiveButton("Move",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        for(int i=0; i< allAlbums.size(); i++){
                            if(allAlbums.get(i).getTitle().equals(moveToAlbum[0])){
                                Album newAlbum = allAlbums.get(i);
                                for(int j=0; j<newAlbum.getPictureList().size(); j++){
                                    if(currPhoto.getPath().equals(newAlbum.getPictureList().get(j).getPath())){
                                        builder.setTitle("Oops! There was an error...");
                                        builder.setMessage("This photo already exists in " + moveToAlbum[0]);
                                        builder.setPositiveButton("OK", null);
                                        builder.show();

                                        return;
                                    }
                                }

                                newAlbum.addPicture(currPhoto);
                                currAlbum.removePicture(currPhoto);
                                updatePhotos(currAlbum);
                                saveData(allAlbums);
                            }
                        }
                    }
                });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.show();
        return;
    }

    private void updatePhotos(Album album) {
        if (album == null || (album.getPictureList() == null || album.getPictureList().size() == 0)) {
            photoList.setAdapter(null);
            return;
        }
        PhotoAdaptor adaptor = new PhotoAdaptor(this, R.layout.adaptor_view, album.getPictureList());
        adaptor.setNotifyOnChange(true);
        photoList = findViewById(R.id.photoList);
        photoList.setAdapter(adaptor);
    }

    public void saveData(ArrayList<Album> albums) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(path);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(albums);

            objectOutputStream.close();
            fileOutputStream.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
