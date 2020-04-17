package com.artisans.qwikhomeservices;

import android.os.Bundle;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.artisans.qwikhomeservices.models.GridBaseAdapter;
import com.artisans.qwikhomeservices.models.ImageModel;

import java.util.ArrayList;

public class Handymantypes extends AppCompatActivity {
    private GridView gvGallery;
    private GridBaseAdapter gridBaseAdapter;
    private ArrayList<ImageModel> imageModelArrayList;

    private int[] myImageList = new int[]{R.drawable.mechanic, R.drawable.pest,
            R.drawable.plumber, R.drawable.tiler
            , R.drawable.tv, R.drawable.carpenter,
            R.drawable.roller, R.drawable.paint, R.drawable.gardener};
    private String[] myImageNameList = new String[]{"Mechanic", "Pest Control",
            "Plumber", "Tiler"
            , "TV Installation", "Carpenter",
            "Roller", "Painter", "Gardener"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handymantypes);

        gvGallery = findViewById(R.id.gv);

        imageModelArrayList = populateList();

        gridBaseAdapter = new GridBaseAdapter(getApplicationContext(), imageModelArrayList);
        gvGallery.setAdapter(gridBaseAdapter);

        gvGallery.setOnItemClickListener((parent, view, position, id) -> Toast.makeText(Handymantypes.this, myImageNameList[position] + " Clicked", Toast.LENGTH_SHORT).show());

    }

    private ArrayList<ImageModel> populateList() {

        ArrayList<ImageModel> list = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            ImageModel imageModel = new ImageModel();
            imageModel.setName(myImageNameList[i]);
            imageModel.setImage_drawable(myImageList[i]);
            list.add(imageModel);
        }

        return list;
    }
}
