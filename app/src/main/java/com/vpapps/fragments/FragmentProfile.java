package com.vpapps.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.vpapps.AsyncTask.LoadProfile;
import com.vpapps.allinonenewsapp.MainActivity;
import com.vpapps.allinonenewsapp.R;
import com.vpapps.interfaces.SuccessListener;
import com.vpapps.item.ItemEventBus;
import com.vpapps.utils.Constant;
import com.vpapps.utils.GlobalBus;
import com.vpapps.utils.Methods;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class FragmentProfile extends Fragment {

    private Methods methods;
    private RoundedImageView imageView;
    private TextView textView_name, textView_email, textView_mobile, textView_notlog;
    private LinearLayout ll_mobile;
    private View view_phone;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        methods = new Methods(getActivity());

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        imageView = rootView.findViewById(R.id.iv_pro);
        textView_name = rootView.findViewById(R.id.tv_prof_fname);
        textView_email = rootView.findViewById(R.id.tv_prof_email);
        textView_mobile = rootView.findViewById(R.id.tv_prof_mobile);
        textView_notlog = rootView.findViewById(R.id.tv_prof_empty);
        ll_mobile = rootView.findViewById(R.id.ll_prof_phone);
        view_phone = rootView.findViewById(R.id.view_prof_phone);

        if (Constant.itemUser != null && !Constant.itemUser.getId().equals("")) {
            loadUserProfile();
        }

        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_profile, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_profile_edit:
                FragmentProfileEdit fprof = new FragmentProfileEdit();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
//                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.hide(getFragmentManager().getFragments().get(getFragmentManager().getBackStackEntryCount()));
                ft.add(R.id.frame_nav, fprof, getString(R.string.profile_edit));
                ft.addToBackStack(getString(R.string.profile_edit));
                ft.commit();
                ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.profile_edit));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadUserProfile() {
        if (methods.isNetworkAvailable()) {
            LoadProfile loadProfile = new LoadProfile(new SuccessListener() {
                @Override
                public void onStart() {
                    progressDialog.show();
                }

                @Override
                public void onEnd(String success, String registerSuccess, String message) {
                    if (getActivity() != null) {
                        progressDialog.dismiss();
                        if (success.equals("1")) {
                            if (registerSuccess.equals("1")) {
                                setVariables();
                            } else {
                                setEmpty(false, getString(R.string.invalid_user));
                                methods.logout(getActivity());
                            }
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.err_server_no_conn), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }, methods.getAPIRequest(Constant.METHOD_PROFILE, 0, "", "", "", "", "", "", "", "", "", "", Constant.itemUser.getId(), "", null));
            loadProfile.execute();
        } else {
            Toast.makeText(getActivity(), getString(R.string.err_internet_not_conn), Toast.LENGTH_SHORT).show();
        }
    }

    private void setVariables() {
        textView_name.setText(Constant.itemUser.getName());
        textView_mobile.setText(Constant.itemUser.getMobile());
        textView_email.setText(Constant.itemUser.getEmail());

        if (!Constant.itemUser.getMobile().trim().isEmpty()) {
            ll_mobile.setVisibility(View.VISIBLE);
            view_phone.setVisibility(View.VISIBLE);
        }

        if (!Constant.itemUser.getDp().equals("")) {
            Picasso.get()
                    .load(Constant.URL_IMAGE + Constant.itemUser.getDp())
                    .placeholder(R.drawable.placeholder_prof)
                    .into(imageView);
        }

        textView_notlog.setVisibility(View.GONE);
    }

    public void setEmpty(Boolean flag, String message) {
        if (flag) {
            textView_notlog.setText(message);
            textView_notlog.setVisibility(View.VISIBLE);
        } else {
            textView_notlog.setVisibility(View.GONE);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onProfileChange(ItemEventBus itemEventBus) {
        if (itemEventBus.getMessage().equals(getString(R.string.profile))) {
            setVariables();
        }
    }

    @Override
    public void onResume() {
        if (Constant.isUpdate) {
            Constant.isUpdate = false;
            setVariables();
        }
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        GlobalBus.getBus().register(this);
    }

    @Override
    public void onStop() {
        GlobalBus.getBus().unregister(this);
        super.onStop();
    }
}