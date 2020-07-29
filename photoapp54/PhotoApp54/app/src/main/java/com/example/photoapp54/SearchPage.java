package com.example.photoapp54;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.RadioGroup;
import android.widget.Button;
import android.widget.RadioButton;
import androidx.appcompat.app.AppCompatActivity;
import com.example.photoapp54.model.*;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class SearchPage extends AppCompatActivity {
    private TextInputLayout personTag;
    private TextInputLayout locationTag;
    private RadioGroup andOr;
    private RadioButton radAnd;
    private RadioButton radOr;
    private GridView imageView;
    private Button search;
    private Button reset;
    private Button back;
    private ArrayList<Album> allAlbums;
    private Album currAlbum;
    private int currAlbumPos;
    public String path;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        path = this.getApplicationInfo().dataDir + "/data.dat";
        andOr = findViewById(R.id.grp2Tag);
        personTag = findViewById(R.id.txtPersonTag);
        locationTag = findViewById(R.id.txtLocationTag);
        radAnd = findViewById(R.id.btnAnd);
        radOr = findViewById(R.id.btnOr);
        imageView = findViewById(R.id.imageView);
        search = findViewById(R.id.btnSearch);
        reset = findViewById(R.id.btnReset);
        back = findViewById(R.id.btnBack);

        Intent intent = getIntent();
        allAlbums = (ArrayList<Album>) intent.getSerializableExtra("allAlbums");
        if (allAlbums == null || allAlbums.isEmpty())
            return;

        currAlbumPos = intent.getIntExtra("currAlbumPos", 0);
        currAlbum = allAlbums.get(currAlbumPos);
    }

    public void search(View view) {
        ArrayList<Photo> resList = new ArrayList<>();
        personTag.setError(null);
        locationTag.setError(null);

        String strPerson;
        String strLocation;
        try {
            strPerson = personTag.getEditText().getText().toString().trim();
            strLocation = locationTag.getEditText().getText().toString().trim();
        }
        catch (NullPointerException e) {
            strPerson = "";
            strLocation = "";
        }


        if (radAnd.isChecked() || radOr.isChecked()) {
            if (strPerson.isEmpty()) {
                personTag.setError("Field can't be empty!");
                return;
            }
            if (strLocation.isEmpty()) {
                locationTag.setError("Field can't be empty!");
                return;
            }

            if (!strPerson.isEmpty() && !strLocation.isEmpty()) {
                personTag.setError(null);
                locationTag.setError(null);
                imageView.removeAllViewsInLayout();
                resList = tagCheck(strPerson, strLocation);
            }
        }
        else {
            if (strPerson.isEmpty() && strLocation.isEmpty()) {
                personTag.setError("Both fields can't be empty!");
                locationTag.setError("Both fields can't be empty!");
                return;
            }
            else if (!strPerson.isEmpty() && !strLocation.isEmpty()) {
                personTag.setError("Please only select one tag!");
                locationTag.setError("Or choose 'And' or 'Or'!");
                return;
            }
            else {
                personTag.setError(null);
                locationTag.setError(null);
                imageView.removeAllViewsInLayout();
                resList = tagCheck(strPerson, strLocation);
            }
        }

        if (resList.isEmpty())
            imageView.setAdapter(null);
        else {
            PhotoAdaptor photoAdaptor = new PhotoAdaptor(this, R.layout.adaptor_view , resList);
            imageView.setAdapter(photoAdaptor);
        }
    }

    private ArrayList<Photo> tagCheck(String pTag, String lTag) {
        ArrayList<Photo> resList = new ArrayList<>();
        for (int i = 0; i < allAlbums.size(); i++) {
            Album currentAlbum = allAlbums.get(i);

            if (radAnd.isChecked()) {
                Tag pTagTemp = new Tag("person", pTag, false);
                Tag lTagTemp = new Tag("location", lTag, false);

                for (int j = 0; j < currentAlbum.getPictureList().size(); j++) {
                    boolean pFound = false;
                    boolean lFound = false;
                    Photo currentPhoto = currentAlbum.getPictureList().get(j);

                    for (int k = 0; k < currentPhoto.getTags().size(); k++) {
                        Tag currTagTemp = currentPhoto.getTags().get(k);
                        if (currTagTemp.getName().toLowerCase().equals("person") && currTagTemp.getValue().length() > pTag.length()) {
                            currTagTemp = new Tag("person", currTagTemp.getValue().substring(0, pTag.length()), false);
                        }
                        else if (currTagTemp.getName().toLowerCase().equals("location") && currTagTemp.getValue().length() > lTag.length()) {
                            currTagTemp = new Tag("location", currTagTemp.getValue().substring(0, lTag.length()), false);
                        }

                        if (currTagTemp.equals(pTagTemp))
                            pFound = true;
                        if (currTagTemp.equals(lTagTemp))
                            lFound = true;

                        if ((pFound && lFound) && !listContains(resList, currentPhoto)) {
                            resList.add(currentPhoto);
                            break;
                        }
                    }
                }
            }
            else if (radOr.isChecked()) {
                Tag pTagTemp = new Tag("person", pTag, false);
                Tag lTagTemp = new Tag("location", lTag, false);

                for (int j = 0; j < currentAlbum.getPictureList().size(); j++) {
                    Photo currentPhoto = currentAlbum.getPictureList().get(j);

                    for (int k = 0; k < currentPhoto.getTags().size(); k++) {
                        Tag currTagTemp = currentPhoto.getTags().get(k);
                        if (currTagTemp.getName().toLowerCase().equals("person") && currTagTemp.getValue().length() > pTag.length()) {
                            currTagTemp = new Tag("person", currTagTemp.getValue().substring(0, pTag.length()), false);
                        }
                        else if (currTagTemp.getName().toLowerCase().equals("location") && currTagTemp.getValue().length() > lTag.length()) {
                            currTagTemp = new Tag("location", currTagTemp.getValue().substring(0, lTag.length()), false);
                        }

                        if ((currTagTemp.equals(pTagTemp) || currTagTemp.equals(lTagTemp)) && !listContains(resList, currentPhoto)) {
                            resList.add(currentPhoto);
                            break;
                        }
                    }
                }
            }
            else {
                Tag temp;
                if (pTag.isEmpty())
                    temp = new Tag("location", lTag, false);
                else
                    temp = new Tag("person", pTag, false);

                for (int j = 0; j < currentAlbum.getPictureList().size(); j++) {
                    Photo currentPhoto = currentAlbum.getPictureList().get(j);

                    for (int k = 0; k < currentPhoto.getTags().size(); k++) {
                        Tag currTagTemp = currentPhoto.getTags().get(k);
                        if (currTagTemp.getName().toLowerCase().equals("person") && currTagTemp.getValue().length() > pTag.length()) {
                            currTagTemp = new Tag("person", currTagTemp.getValue().substring(0, pTag.length()), false);
                        }
                        else if (currTagTemp.getName().toLowerCase().equals("location") && currTagTemp.getValue().length() > lTag.length()) {
                            currTagTemp = new Tag("location", currTagTemp.getValue().substring(0, lTag.length()), false);
                        }

                        if (currTagTemp.equals(temp) && !listContains(resList, currentPhoto)) {
                            resList.add(currentPhoto);
                            break;
                        }
                    }
                }
            }
        }

        return resList;
    }

    private boolean listContains(ArrayList<Photo> picList, Photo currPhoto) {
        for (int i = 0; i < picList.size(); i++) {
            if (picList.get(i).getPath().equals(currPhoto.getPath()))
                return true;
        }

        return false;
    }

    public void reset(View view) {
        personTag.getEditText().setText("");
        locationTag.getEditText().setText("");
        personTag.setError(null);
        locationTag.setError(null);
        andOr.clearCheck();
        imageView.removeAllViewsInLayout();
    }

    public void backHome(View view) {
        Intent intent = new Intent(this, MainActivity.class);

        intent.putExtra("allAlbums", allAlbums);
        intent.putExtra("currAlbum", currAlbum);
        intent.putExtra("currAlbumPos", currAlbumPos);
        startActivity(intent);
    }
}
