package com.artisans.qwikhomeservices.activities.auth.signup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.databinding.FragmentAboutBinding;
import com.artisans.qwikhomeservices.utils.DisplayViewUI;
import com.artisans.qwikhomeservices.utils.MyConstants;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;


public class AboutFragment extends Fragment {

    private FragmentAboutBinding fragmentAboutBinding;
    String mGetFirstName, mGetLatName, mGetAccountType;
    private TextInputLayout txtAbout;


    public AboutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentAboutBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_about, container, false);
        return fragmentAboutBinding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle getBundle = getArguments();
        if (getBundle != null) {

            mGetAccountType = getBundle.getString(MyConstants.ACCOUNT_TYPE);
            mGetFirstName = getBundle.getString(MyConstants.FIRST_NAME);
            mGetLatName = getBundle.getString(MyConstants.LAST_NAME);

            DisplayViewUI.displayToast(getActivity(), getBundle.getString(MyConstants.ACCOUNT_TYPE) + " " +
                    getBundle.getString(MyConstants.FIRST_NAME) + " " + getBundle.getString(MyConstants.LAST_NAME));
        }

        fragmentAboutBinding.btnContinue.setOnClickListener(this::onClick);

    }

    private void onClick(View view) {
        txtAbout = fragmentAboutBinding.txtAbout;
        String about = Objects.requireNonNull(txtAbout.getEditText()).getText().toString();
        if (!about.trim().isEmpty() && about.length() > 15) {

            PhoneNumberFragment phoneNumberFragment = new PhoneNumberFragment();
            Bundle args = new Bundle();
            args.putString(MyConstants.FIRST_NAME, mGetFirstName);
            args.putString(MyConstants.LAST_NAME, mGetLatName);
            args.putString(MyConstants.ACCOUNT_TYPE, mGetAccountType);
            args.putString(MyConstants.ABOUT, about);
            phoneNumberFragment.setArguments(args);
            FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
            fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right,
                    R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                    .replace(R.id.containerFragment, phoneNumberFragment)
                    .addToBackStack("phoneNumberFragment")
                    .commit();

        } else if (about.trim().isEmpty()) {
            txtAbout.setError("about required");
            txtAbout.setErrorEnabled(true);
        } else if (!about.trim().isEmpty() && about.length() < 15) {
            txtAbout.setError("content too less");
            txtAbout.setErrorEnabled(true);
        }
    }
}
