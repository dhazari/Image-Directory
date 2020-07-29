package com.example.photoapp54;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import com.example.photoapp54.model.Album;
import com.example.photoapp54.model.Photo;
import com.example.photoapp54.model.Tag;
import com.google.android.material.textfield.TextInputLayout;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class DisplayActivity extends AppCompatActivity {

    private ImageView imageView;
    private ListView tagList;
    private TextView textView;
    private RadioGroup typeGroup;
    private RadioButton personType;
    private RadioButton locationType;
    private TextInputLayout tagValue;

    private ArrayList<Album> allAlbums;
    private Album currAlbum;
    private int currAlbumPos;
    private int currPhotoPos;
    public String path;
    private Photo photo;
    private Tag currTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_photo);
        path = this.getApplicationInfo().dataDir + "/data.dat";
        Intent intent = getIntent();
        allAlbums = (ArrayList<Album>) intent.getSerializableExtra("allAlbums");
        currAlbum = (Album) intent.getSerializableExtra("currAlbum");
        currAlbumPos = intent.getIntExtra("currAlbumPos", 0);
        photo = (Photo) intent.getSerializableExtra("currPhoto");
        currPhotoPos = intent.getIntExtra("currPhotoPos", 0);

        typeGroup = findViewById(R.id.typeGroup);
        personType = findViewById(R.id.personType);
        locationType = findViewById(R.id.locationType);
        tagValue = findViewById(R.id.tagValue);
        tagList = findViewById(R.id.tagList);
        textView = findViewById(R.id.textView);
        textView.setText((currAlbum.getTitle() + " images"));
        imageView = findViewById(R.id.imageView);
        imageView.setImageURI(Uri.parse(photo.getPath()));

        if(!photo.getTags().isEmpty()) {
            updateTags(photo);
            tagList.setItemChecked(0, true);
            currTag = photo.getTags().get(0);
            if(currTag.getName().equals("person"))
                typeGroup.check(R.id.personType);
            else
                typeGroup.check(R.id.locationType);

            tagValue.setHint(currTag.getValue());
        }
        else {
            tagList.setAdapter(null);
            typeGroup.check(R.id.personType);
        }

        tagList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tagList.setItemChecked(position,true);
                currTag = photo.getTags().get(position);
                if(currTag.getName().equals("person"))
                    typeGroup.check(R.id.personType);
                else
                    typeGroup.check(R.id.locationType);

                tagValue.setHint(currTag.getValue());
            }
        });
    }

    public void backBtn(View view) {
        Intent intent = new Intent(this, AlbumActivity.class);

        intent.putExtra("allAlbums", allAlbums);
        intent.putExtra("currAlbumPos", currAlbumPos);
        intent.putExtra("currAlbum", currAlbum);

        startActivity(intent);
    }

    public void nextImg(View view){
        if(currPhotoPos+1 >= currAlbum.getPictureList().size()){
            currPhotoPos=0;
            imageView.setImageURI(Uri.parse(currAlbum.getPictureList().get(currPhotoPos).getPath()));
        }
        else{
            currPhotoPos++;
            imageView.setImageURI(Uri.parse(currAlbum.getPictureList().get(currPhotoPos).getPath()));
        }

        photo = allAlbums.get(currAlbumPos).getPictureList().get(currPhotoPos);

        updateTags(photo);
        if(!photo.getTags().isEmpty()) {
            tagList.setItemChecked(0, true);
            currTag = photo.getTags().get(0);
            if(currTag.getName().equals("person"))
                typeGroup.check(R.id.personType);
            else
                typeGroup.check(R.id.locationType);

            tagValue.setHint(currTag.getValue());
        }
        else
            typeGroup.check(R.id.personType);
    }

    public void previousImg(View view){
        if(currPhotoPos-1 <0){
            currPhotoPos=currAlbum.getPictureList().size()-1;
            imageView.setImageURI(Uri.parse(currAlbum.getPictureList().get(currPhotoPos).getPath()));
        }
        else{
            currPhotoPos--;
            imageView.setImageURI(Uri.parse(currAlbum.getPictureList().get(currPhotoPos).getPath()));
        }

        photo = allAlbums.get(currAlbumPos).getPictureList().get(currPhotoPos);

        updateTags(photo);
        if(!photo.getTags().isEmpty()) {
            tagList.setItemChecked(0, true);
            currTag = photo.getTags().get(0);
            if(currTag.getName().equals("person"))
                typeGroup.check(R.id.personType);
            else
                typeGroup.check(R.id.locationType);

            tagValue.setHint(currTag.getValue());
        }
        else
            typeGroup.check(R.id.personType);
    }

    public void addTag(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        tagValue = findViewById(R.id.tagValue);
        if(tagValue.getEditText().getText().toString().isEmpty()){
            builder.setTitle("Oops! There was an error...");
            builder.setMessage("There is no value entered to add!");
            builder.setPositiveButton("Close", null);
            builder.show();

            return;
        }

        String tagTypeSelected = "person";
        boolean multipleValues = true;
        if (personType.isChecked()) {
            tagTypeSelected = "person";
            multipleValues = true;
        }
        else if (locationType.isChecked()) {
            multipleValues = false;
            tagTypeSelected = "location";
        }

        Tag newTag = new Tag(tagTypeSelected, tagValue.getEditText().getText().toString(), multipleValues);
        for(int i=0; i< photo.getTags().size(); i++){
            if(photo.getTags().get(i).equals(newTag)){
                builder.setTitle("Oops! There was an error...");
                builder.setMessage("This tag already exists!");
                builder.setPositiveButton("Close", null);
                builder.show();

                return;
            }
            else if(photo.getTags().get(i).getName().equals("location") && newTag.getName().equals("location")){
                builder.setTitle("Oops! There was an error...");
                builder.setMessage("Location is only allowed one value, and "+currAlbum.getPictureList().get(currPhotoPos).getPhotoName()+" already has a location!");
                builder.setPositiveButton("Close", null);
                builder.show();

                return;
            }
        }

        builder.setTitle("Add Tag");
        builder.setMessage("Add tag " + newTag.toString() + " to "+ photo.getPhotoName()+"?");
        builder.setPositiveButton("Yes",
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    allAlbums.get(currAlbumPos).getPictureList().get(currPhotoPos).addTag(newTag);
                    photo = allAlbums.get(currAlbumPos).getPictureList().get(currPhotoPos);
                    currTag = newTag;
                    saveData(allAlbums);
                    updateTags(photo);
                    tagList.setItemChecked(photo.getTags().size()-1, true);
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

        typeGroup.clearCheck();
        typeGroup.check(R.id.personType);
        tagValue.getEditText().setText("");
        return;
    }

    public void deleteTag(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if(photo.getTags().isEmpty()){
            builder.setTitle("Oops! There was an error...");
            builder.setMessage("There are now tags selected to delete!");
            builder.setPositiveButton("Close", null);
            builder.show();

            return;
        }

        builder.setTitle("Delete Tag");
        builder.setMessage("Delete tag "+ currTag.toString() +" from "+ photo.getPhotoName()+"?");
        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        allAlbums.get(currAlbumPos).getPictureList().get(currPhotoPos).removeTag(currTag.getName(), currTag.getValue());
                        photo = allAlbums.get(currAlbumPos).getPictureList().get(currPhotoPos);
                        saveData(allAlbums);
                        updateTags(photo);
                        if(!photo.getTags().isEmpty()) {
                            tagList.setItemChecked(0, true);
                            currTag = photo.getTags().get(0);
                            if(currTag.getName().equals("person"))
                                typeGroup.check(R.id.personType);
                            else
                                typeGroup.check(R.id.locationType);

                            tagValue.setHint(currTag.getValue());
                        }
                        else
                            typeGroup.check(R.id.personType);
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

    private void updateTags(Photo image) {
        if (image == null || image.getTags().size() == 0) {
            tagList.setAdapter(null);
            return;
        }
        ArrayList<String> strTags = new ArrayList<>();
        for (int i = 0; i < image.getTags().size(); i++)
            strTags.add(image.getTags().get(i).toString());

        ArrayAdapter<String> adaptor = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, strTags);
        adaptor.setNotifyOnChange(true);
        tagList = findViewById(R.id.tagList);
        tagList.setAdapter(adaptor);
        tagList.refreshDrawableState();
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
