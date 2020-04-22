package com.artisans.qwikhomeservices.activities.home.bottomsheets;

import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.artisans.qwikhomeservices.R;
import com.artisans.qwikhomeservices.activities.home.MainActivity;
import com.artisans.qwikhomeservices.databinding.LayoutEditItemBottomSheetBinding;
import com.artisans.qwikhomeservices.utils.MyConstants;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditItemBottomSheet extends BottomSheetDialogFragment {
    private LayoutEditItemBottomSheetBinding layoutEditItemBottomSheetBinding;
    private TextInputLayout txtInputItem;
    private Bundle bundle;
    private Map<String, Object> updateItem = new HashMap<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        layoutEditItemBottomSheetBinding = DataBindingUtil.inflate(inflater, R.layout.layout_edit_item_bottom_sheet, container, false);

        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        return layoutEditItemBottomSheetBinding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtInputItem = layoutEditItemBottomSheetBinding.textInputLayout;

        bundle = getArguments();
        if (bundle != null) {

            if (Objects.equals(bundle.getString(MyConstants.FIRST_NAME), MainActivity.firstName)) {

                layoutEditItemBottomSheetBinding.textInputLayout.setHint("Edit first name");
                Objects.requireNonNull(layoutEditItemBottomSheetBinding.textInputLayout.getEditText()).setSingleLine(true);
                Objects.requireNonNull(layoutEditItemBottomSheetBinding.textInputLayout
                        .getEditText()).setText(bundle.getString(MyConstants.FIRST_NAME));

            } else if (Objects.equals(bundle.getString(MyConstants.LAST_NAME), MainActivity.lastName)) {

                layoutEditItemBottomSheetBinding.textInputLayout.setHint("Edit last name");
                Objects.requireNonNull(layoutEditItemBottomSheetBinding.textInputLayout.getEditText()).setSingleLine(true);
                Objects.requireNonNull(layoutEditItemBottomSheetBinding.textInputLayout
                        .getEditText()).setText(bundle.getString(MyConstants.LAST_NAME));

            } else if (Objects.equals(bundle.getString(MyConstants.ABOUT), MainActivity.about)) {
                Objects.requireNonNull(layoutEditItemBottomSheetBinding.textInputLayout.getEditText()).setImeOptions(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                layoutEditItemBottomSheetBinding.textInputLayout.setHint("Edit about");
                Objects.requireNonNull(layoutEditItemBottomSheetBinding.textInputLayout
                        .getEditText()).setText(bundle.getString(MyConstants.ABOUT));
            }


        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        layoutEditItemBottomSheetBinding.txtCancel.setOnClickListener(v -> {

            dismiss();
        });

        layoutEditItemBottomSheetBinding.btnOk.setOnClickListener(this::onOkClicked);
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void onOkClicked(View view) {

        if (Objects.equals(bundle.getString(MyConstants.FIRST_NAME), MainActivity.firstName)) {

            updateFirstName();

        } else if (Objects.equals(bundle.getString(MyConstants.LAST_NAME), MainActivity.lastName)) {

            updateLastName();

        } else if (Objects.equals(bundle.getString(MyConstants.ABOUT), MainActivity.about)) {

            updateUserAbout();
        }


    }

    private void updateUserAbout() {

        String about = Objects.requireNonNull(layoutEditItemBottomSheetBinding.textInputLayout.getEditText()).getText().toString();

        if (about.trim().isEmpty()) {
            txtInputItem.setErrorEnabled(true);
            txtInputItem.setError("about field required");
        } else {
            txtInputItem.setErrorEnabled(false);
        }

        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
            if (!about.trim().isEmpty()) {

                updateItem.put("about", about);
                MainActivity.serviceAccountDbRef.updateChildren(updateItem);
                dismiss();
            }

        });
    }

    private void updateFirstName() {
        String name = Objects.requireNonNull(layoutEditItemBottomSheetBinding.textInputLayout.getEditText()).getText().toString();

        if (name.trim().isEmpty()) {
            txtInputItem.setErrorEnabled(true);
            txtInputItem.setError("first name field required");
        } else {
            txtInputItem.setErrorEnabled(false);
        }

        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
            if (!name.trim().isEmpty()) {


                updateItem.put("firstName", name);
                MainActivity.serviceAccountDbRef.updateChildren(updateItem);
                dismiss();
            }

        });


    }

    private void updateLastName() {
        String name = Objects.requireNonNull(layoutEditItemBottomSheetBinding.textInputLayout.getEditText()).getText().toString();

        if (name.trim().isEmpty()) {
            txtInputItem.setErrorEnabled(true);
            txtInputItem.setError("last name field required");
        } else {
            txtInputItem.setErrorEnabled(false);
        }

        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
            if (!name.trim().isEmpty()) {


                updateItem.put("lastName", name);
                MainActivity.serviceAccountDbRef.updateChildren(updateItem);
                dismiss();
            }

        });


    }
}
