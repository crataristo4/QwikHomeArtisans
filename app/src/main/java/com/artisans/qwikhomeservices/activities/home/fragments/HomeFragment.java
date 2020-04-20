package com.artisans.qwikhomeservices.activities.home.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.activities.home.serviceTypes.AllServicesActivity;
import com.artisans.qwikhomeservices.databinding.FragmentHomeBinding;
import com.artisans.qwikhomeservices.utils.MyConstants;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding fragmentHomeBinding;
    private Intent intent;
    private ArrayAdapter<String> adapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        // Inflate the layout for this fragment
        return fragmentHomeBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView listView = fragmentHomeBinding.servicesListView;
        //string list from xml
        List<String> serviceList = Arrays.asList(getResources().getStringArray(R.array.services));

        adapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),android.R.layout.simple_list_item_1,serviceList);
        listView.setAdapter(adapter);

        //on item click
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            switch (position){
                case 0:
                    intent = new Intent(getContext(), AllServicesActivity.class);
                    intent.putExtra(MyConstants.ACCOUNT_TYPE, MyConstants.WOMEN_HAIR_STYLIST);
                    startActivity(intent);
                    break;
                case 1:
                    intent = new Intent(getContext(), AllServicesActivity.class);
                    intent.putExtra(MyConstants.ACCOUNT_TYPE, MyConstants.INTERIOR_DERCORATOR);
                    startActivity(intent);
                    break;
                case 2:
                    intent = new Intent(getContext(), AllServicesActivity.class);
                    intent.putExtra(MyConstants.ACCOUNT_TYPE, MyConstants.BARBERS);
                    startActivity(intent);
                    break;
                case 3:
                    intent = new Intent(getContext(), AllServicesActivity.class);
                    intent.putExtra(MyConstants.ACCOUNT_TYPE, MyConstants.CARPENTERS);
                    startActivity(intent);
                    break;
                // TODO: 19-Apr-20 do same for rest of artisans



            }
        });


        fragmentHomeBinding.searchList.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });


    }
}
