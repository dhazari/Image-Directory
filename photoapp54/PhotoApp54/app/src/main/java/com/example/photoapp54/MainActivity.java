package com.example.photoapp54;

import android.content.Intent;
import android.content.DialogInterface;
import android.os.Bundle;

import com.example.photoapp54.model.Album;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.InputType;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Album> allAlbums;
    private Album currAlbum;
    private int currAlbumPos;
    public String path;
    String currAlbumTxt;

    private ListView albumList;
    private GridView imageList;
    private TextView albumName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        path = this.getApplicationInfo().dataDir + "/data.dat";
        currAlbumTxt = this.getApplicationInfo().dataDir + "/currAlbum.txt";
        File baseFile = new File(path);
        File currAlbumFile = new File(currAlbumTxt);

        if (!baseFile.exists()) {
            try {
                baseFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                FileInputStream fileInputStream = new FileInputStream(path);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

                allAlbums = (ArrayList<Album>) objectInputStream.readObject();

                objectInputStream.close();
                fileInputStream.close();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        if (!currAlbumFile.exists()) {
            try {
                currAlbumFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                currAlbumPos = readPos(currAlbumFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        albumList = findViewById(R.id.albumList);
        imageList = findViewById(R.id.picList);
        albumName = findViewById(R.id.albumName);
        albumName.setText(("Album:"));

        Intent intent = getIntent();
        if (intent.getSerializableExtra("allAlbums") != null)
            allAlbums = (ArrayList<Album>) intent.getSerializableExtra("allAlbums");

        if (allAlbums != null && !allAlbums.isEmpty()) {
            if (intent.getIntExtra("currAlbumPos", -1) != -1)
                currAlbumPos = intent.getIntExtra("currAlbumPos", 0);

            currAlbum = allAlbums.get(currAlbumPos);
            albumName.setText(("Album: " + currAlbum.getTitle()));

            ArrayList<String> titles = new ArrayList<>();
            for (int i = 0; i < allAlbums.size(); i++)
                titles.add(allAlbums.get(i).getTitle());

            updateAlbums(allAlbums);
            updatePhotos(currAlbum);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SearchPage.class);
                if (allAlbums != null && !allAlbums.isEmpty()) {
                    intent.putExtra("allAlbums", allAlbums);
                    intent.putExtra("currAlbum", currAlbum);
                    intent.putExtra("currAlbumPos", currAlbumPos);
                }
                else {
                    intent.putExtra("allAlbums", new ArrayList<Album>());
                    intent.putExtra("currAlbum", new Album("temp"));
                    intent.putExtra("currAlbumPos", 0);
                }

                startActivity(intent);
            }
        });

        albumList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                albumList.setItemChecked(position, true);
                currAlbumPos = position;
                currAlbum = allAlbums.get(currAlbumPos);
                albumName.setText(("Album: " + currAlbum.getTitle()));
                updatePhotos(currAlbum);
                try{
                    savePos(currAlbumFile, currAlbumPos);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_open) {
            return openAlbum();
        }
        else if (id == R.id.action_create) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Name the album");

            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String title = input.getText().toString();
                    createAlbum(title);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
            return true;
        }
        else if (id == R.id.action_delete) {
            if (allAlbums != null && !allAlbums.isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Are you sure you want to delete " + currAlbum.getTitle());

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAlbum();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
                return true;
            }
            else {
                Snackbar.make(findViewById(R.id.toolbar), "Please select an album", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
        else if (id == R.id.action_rename) {
            if (allAlbums != null && !allAlbums.isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Rename the album");

                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String title = input.getText().toString();
                        renameAlbum(title);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
                return true;
            }
            else {
                Snackbar.make(findViewById(R.id.toolbar), "Please select an album", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

        }

        return super.onOptionsItemSelected(item);
    }

    private void updateAlbums(ArrayList<Album> albums) {
        if (albums == null || albums.isEmpty()) {
            albumList.setAdapter(null);
            return;
        }
        ArrayList<String> titles = new ArrayList<>();
        for (int i = 0; i < albums.size(); i++)
            titles.add(albums.get(i).getTitle());

        ArrayAdapter<String> titleAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, titles);
        titleAdapter.setNotifyOnChange(true);
        albumList.setAdapter(titleAdapter);
        albumList.setItemChecked(currAlbumPos, true);
        currAlbum = allAlbums.get(currAlbumPos);
        File currAlbumFile = new File(currAlbumTxt);
        try{
            savePos(currAlbumFile, currAlbumPos);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        albumName.setText(("Album: " + currAlbum.getTitle()));
    }

    private void updatePhotos(Album album) {
        if (album == null || album.getPictureList().size() == 0) {
            imageList.setAdapter(null);
            return;
        }
        PhotoAdaptor adaptor = new PhotoAdaptor(this, R.layout.adaptor_view, album.getPictureList());
        adaptor.setNotifyOnChange(true);
        imageList = findViewById(R.id.picList);
        imageList.setAdapter(adaptor);
    }

    private boolean openAlbum() {
        if (allAlbums != null && !allAlbums.isEmpty()) {
            //send all info to album page
            Intent intent = new Intent(getApplicationContext(), AlbumActivity.class);
            intent.putExtra("allAlbums", allAlbums);
            intent.putExtra("currAlbum", currAlbum);
            intent.putExtra("currAlbumPos", currAlbumPos);
            startActivity(intent);
            return true;
        }
       else {
            //no album selected
            Snackbar.make(findViewById(R.id.toolbar), "Please select an album", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return false;
        }
    }

    private void createAlbum(String title) {
        //check to see if there's another one of the same name
        if (allAlbums != null && !allAlbums.isEmpty()) {
            for (int i = 0; i < allAlbums.size(); i++) {
                if (allAlbums.get(i).getTitle().equals(title)) {
                    Snackbar.make(findViewById(R.id.toolbar), "There is already an album with that name", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }
            }
        }
        else
            allAlbums = new ArrayList<>();

        //create and select album
        allAlbums.add(new Album(title));
        currAlbumPos = allAlbums.size() - 1;
        currAlbum = allAlbums.get(currAlbumPos);

        //update views
        updateAlbums(allAlbums);
        imageList.setAdapter(null);
        saveData(allAlbums);
    }

    private void deleteAlbum() {
        //delete an album
        allAlbums.remove(currAlbum);
        if (allAlbums.size() > 0) {
            if (currAlbumPos >= allAlbums.size())
                currAlbumPos--;

            currAlbum = allAlbums.get(currAlbumPos);

            updateAlbums(allAlbums);
            updatePhotos(currAlbum);
        }
        else {
            albumList.setAdapter(null);
            imageList.setAdapter(null);
            albumName.setText("Album:");
        }

        saveData(allAlbums);
    }

    private void renameAlbum(String title) {
        //check to see if there's another one of the same name
        for (int i = 0; i < allAlbums.size(); i++) {
            if (allAlbums.get(i).getTitle().equals(title)) {
                Snackbar.make(findViewById(R.id.toolbar), "There is already an album with that name", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return;
            }
        }

        currAlbum.setTitle(title);
        updateAlbums(allAlbums);
        saveData(allAlbums);
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

    private int readPos(File fp) throws IOException {
        Scanner r1 = new Scanner(fp);
        String temp = r1.nextLine();
        int res = Integer.parseInt(temp);
        r1.close();
        return res;
    }

    private void savePos(File fp, int pos) throws IOException {
        if (fp.delete()) {
            fp.createNewFile();
        }
        PrintWriter w1 = new PrintWriter(fp);
        w1.write((pos + ""));
        w1.close();
    }
}
