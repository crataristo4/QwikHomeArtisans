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
import com.artisans.qwikhomeservices.utils.MyConstants;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;


public class NameFragment extends Fragment {
    private FragmentNameBinding fragmentNameBinding;
    private String mGetAccountType;

    public NameFragment() {
        // Required empty public constructor
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
        Spinner spinnerAccountType = fragmentNameBinding.spinnerAccountType;
        spinnerAccountType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    fragmentNameBinding.btnNext.setEnabled(false);
                } else {
                    fragmentNameBinding.btnNext.setEnabled(true);
                    mGetAccountType = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if (parent.getSelectedItemPosition() == 0) {
                    fragmentNameBinding.btnNext.setEnabled(false);

                }

            }
        });


        if (fragmentNameBinding.btnNext.isEnabled()) {
            fragmentNameBinding.btnNext.setOnClickListener(this::validateInput);

        }


    }

    private void validateInput(View view) {
        TextInputLayout txtFirstName = fragmentNameBinding.txtFirstName;
        TextInputLayout txtLastName = fragmentNameBinding.txtLastName;

        String mGetFirstName = Objects.requireNonNull(txtFirstName.getEditText()).getText().toString();
        String mGetLatName = Objects.requireNonNull(txtLastName.getEditText()).getText().toString();
        String fullName = mGetFirstName.concat(" ").concat(mGetLatName);


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
            args.putString(MyConstants.FIRST_NAME, mGetFirstName);
            args.putString(MyConstants.LAST_NAME, mGetLatName);
            args.putString(MyConstants.ACCOUNT_TYPE, mGetAccountType);
            args.putString(MyConstants.FULL_NAME, fullName);
            aboutFragment.setArguments(args);
            FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
            fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right,
                    R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                    .replace(R.id.containerFragment, aboutFragment)
                    .addToBackStack("aboutFragment")
                    .commit();
        }
    }
}
