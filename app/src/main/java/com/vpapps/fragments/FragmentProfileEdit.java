package com.vpapps.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.vpapps.AsyncTask.LoadProfileEdit;
import com.vpapps.allinonenewsapp.R;
import com.vpapps.interfaces.SuccessListener;
import com.vpapps.item.ItemEventBus;
import com.vpapps.utils.Constant;
import com.vpapps.utils.GlobalBus;
import com.vpapps.utils.Methods;
import com.vpapps.utils.SharedPref;

import java.io.File;
import java.io.IOException;

public class FragmentProfileEdit extends Fragment {

    private Methods methods;
    private EditText editText_name, editText_email, editText_phone, editText_pass, editText_cpass;
    private SharedPref sharedPref;
    private ProgressDialog progressDialog;
    private RoundedImageView imageView;
    private String imagePath = "";
    private int PICK_IMAGE_REQUEST = 1;
    private final static int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 102;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile_edit, container, false);

        sharedPref = new SharedPref(getActivity());
        methods = new Methods(getActivity());

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getActivity().getResources().getString(R.string.loading));

        AppCompatButton button_update = rootView.findViewById(R.id.button_prof_update);
        imageView = rootView.findViewById(R.id.iv_pro_edit);
        editText_name = rootView.findViewById(R.id.et_profedit_name);
        editText_email = rootView.findViewById(R.id.et_profedit_email);
        editText_phone = rootView.findViewById(R.id.et_profedit_phone);
        editText_pass = rootView.findViewById(R.id.et_profedit_password);
        editText_cpass = rootView.findViewById(R.id.et_profedit_cpassword);

        setProfileVar();

        if (!Constant.itemUser.getDp().equals("")) {
            Picasso.get()
                    .load(Constant.URL_IMAGE + Constant.itemUser.getDp())
                    .placeholder(R.drawable.placeholder_prof)
                    .into(imageView);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPer()) {
                    pickImage();
                }
            }
        });

        button_update.setBackground(methods.getRoundDrawable(getResources().getColor(R.color.colorPrimary)));
        button_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    loadUpdateProfile();
                }
            }
        });

        setHasOptionsMenu(true);
        return rootView;
    }

    private void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_image)), PICK_IMAGE_REQUEST);
    }

    private Boolean validate() {
        editText_name.setError(null);
        editText_email.setError(null);
        editText_cpass.setError(null);
        if (editText_name.getText().toString().trim().isEmpty()) {
            editText_name.setError(getString(R.string.cannot_empty));
            editText_name.requestFocus();
            return false;
        } else if (editText_email.getText().toString().trim().isEmpty()) {
            editText_email.setError(getString(R.string.email_empty));
            editText_email.requestFocus();
            return false;
        } else if (editText_pass.getText().toString().endsWith(" ")) {
            editText_pass.setError(getString(R.string.pass_end_space));
            editText_pass.requestFocus();
            return false;
        } else if (!editText_pass.getText().toString().trim().equals(editText_cpass.getText().toString().trim())) {
            editText_cpass.setError(getString(R.string.pass_nomatch));
            editText_cpass.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private void updateData() {
        Constant.itemUser.setName(editText_name.getText().toString());
        Constant.itemUser.setEmail(editText_email.getText().toString());
        Constant.itemUser.setMobile(editText_phone.getText().toString());

        if (!editText_pass.getText().toString().equals("")) {
            sharedPref.setRemeber(false);
        }
    }

    private void loadUpdateProfile() {
        if (methods.isNetworkAvailable()) {
            File file = null;
            if (!imagePath.equals("")) {
                file = new File(imagePath);
            }
            LoadProfileEdit loadProfileEdit = new LoadProfileEdit(new SuccessListener() {
                @Override
                public void onStart() {
                    progressDialog.show();
                }

                @Override
                public void onEnd(String success, String registerSuccess, String message) {
                    progressDialog.dismiss();
                    if (success.equals("1")) {
                        if (registerSuccess.equals("1")) {
                            updateData();
                            Constant.isUpdate = true;
                            GlobalBus.getBus().postSticky(new ItemEventBus(getString(R.string.profile), null,0));
                        } else {
                            editText_email.setError(message);
                            editText_email.requestFocus();
                        }
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.err_server_no_conn), Toast.LENGTH_SHORT).show();
                    }
                }
            }, methods.getAPIRequest(Constant.METHOD_PROFILE_EDIT, 0, "", "", "", "", "", "", editText_email.getText().toString(), editText_pass.getText().toString(), editText_name.getText().toString(), editText_phone.getText().toString(), Constant.itemUser.getId(), "", file));
            loadProfileEdit.execute();
        } else {
            Toast.makeText(getActivity(), getString(R.string.err_internet_not_conn), Toast.LENGTH_SHORT).show();
        }
    }

    private void setProfileVar() {
        editText_name.setText(Constant.itemUser.getName());
        editText_phone.setText(Constant.itemUser.getMobile());
        editText_email.setText(Constant.itemUser.getEmail());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            imagePath = methods.getPathImage(uri);

            try {
                Bitmap bitmap_upload = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                imageView.setImageBitmap(bitmap_upload);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Boolean checkPer() {
        if ((ContextCompat.checkSelfPermission(getActivity(), "android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED)) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                return false;
            }
            return true;
        } else {
            return true;
        }
    }
}