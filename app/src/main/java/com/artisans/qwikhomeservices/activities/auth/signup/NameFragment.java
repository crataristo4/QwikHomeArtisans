package com.artisans.qwikhomeservices.activities.auth.signup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.databinding.FragmentNameBinding;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;


public class NameFragment extends Fragment {

    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String ACCOUNT_TYPE = "accountType";
    private FragmentNameBinding fragmentNameBinding;
    private TextInputLayout txtFirstName, txtLastName;
    private Spinner spinnerAccountType;

    private String mGetFirstName;
    private String mGetLatName;
    private String mGetAccountType;

    public NameFragment() {
        // Required empty public constructor
    }


    public static NameFragment newInstance(String firstName, String lastName, String accountType) {
        NameFragment fragment = new NameFragment();
        Bundle args = new Bundle();
        args.putString(FIRST_NAME, firstName);
        args.putString(LAST_NAME, lastName);
        args.putString(ACCOUNT_TYPE, accountType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGetFirstName = getArguments().getString(FIRST_NAME);
            mGetLatName = getArguments().getString(LAST_NAME);
            mGetAccountType = getArguments().getString(ACCOUNT_TYPE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentNameBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_name, container, false);

        return fragmentNameBinding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        fragmentNameBinding.btnNext.setOnClickListener(this::validateInput);


    }

    private void validateInput(View view) {
        txtFirstName = fragmentNameBinding.txtFirstName;
        txtLastName = fragmentNameBinding.txtLastName;
        spinnerAccountType = fragmentNameBinding.spinnerAccountType;

        mGetFirstName = Objects.requireNonNull(txtFirstName.getEditText()).getText().toString();
        mGetLatName = Objects.requireNonNull(txtLastName.getEditText()).getText().toString();

        spinnerAccountType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    fragmentNameBinding.btnNext.setVisibility(View.GONE);
                } else {
                    fragmentNameBinding.btnNext.setVisibility(View.VISIBLE);
                    mGetAccountType = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (mGetFirstName.trim().isEmpty() || mGetFirstName.length() < 3) {
            txtFirstName.setErrorEnabled(true);
            txtFirstName.setError("first name required");
        } else {
            txtFirstName.setErrorEnabled(false);

        }
        if (mGetLatName.trim().isEmpty() || mGetLatName.length() < 3) {
            txtLastName.setErrorEnabled(true);
            txtLastName.setError("last name required");
        } else {
            txtLastName.setErrorEnabled(false);

        }

        if (!mGetFirstName.trim().isEmpty() && !mGetLatName.trim().isEmpty()) {
            AboutFragment aboutFragment = new AboutFragment();
            Bundle args = new Bundle();
            args.putString(FIRST_NAME, mGetFirstName);
            args.putString(LAST_NAME, mGetLatName);
            args.putString(ACCOUNT_TYPE, mGetAccountType);
            aboutFragment.setArguments(args);
            FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
            fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right,
                    R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                    .replace(R.id.container, aboutFragment)
                    .addToBackStack("aboutFragment")
                    .commit();
        }
    }
}
