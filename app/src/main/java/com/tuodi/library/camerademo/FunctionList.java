package com.tuodi.library.camerademo;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by dsliang on 2016/5/29.
 */
public class FunctionList extends Fragment {

    public static FunctionList newInstance() {

        Bundle args = new Bundle();

        FunctionList fragment = new FunctionList();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_functoin_list, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        ButterKnife.unbind(this);
    }

    @OnClick({R.id.txtExistingCamera, R.id.txtCustomCamera})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtExistingCamera:
                Intent intent;

                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivity(intent);
                break;
            case R.id.txtCustomCamera:
                Fragment fragment = null;

                try {
                    fragment = CustomCamera.class.newInstance();
                } catch (java.lang.InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

                replaceContenView(fragment);
                break;
        }
    }

    private void replaceContenView(Fragment fragment) {

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(fragment.getClass().getSimpleName())
                .replace(android.R.id.content, fragment, fragment.getClass().getSimpleName())
                .commit();

    }
}
