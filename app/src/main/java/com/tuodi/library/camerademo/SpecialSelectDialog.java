package com.tuodi.library.camerademo;

import android.app.Dialog;
import android.content.DialogInterface;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dsliang on 2016/6/2.
 */
public class SpecialSelectDialog extends DialogFragment {

    private CharSequence[] mSelectItems;
    private int mCurrentItem;
    private int mFunction;

    public static SpecialSelectDialog newInstance(int function, List<Camera.Size> list, Camera.Size size) {

        Bundle args;
        SpecialSelectDialog fragment;
        int currentItem;
        CharSequence[] charSequences;
        List<String> stringList;

        args = new Bundle();
        fragment = new SpecialSelectDialog();

        stringList = getList(list);

        charSequences = new CharSequence[stringList.size()];
        for (int i = 0; i < stringList.size(); i++) {
            charSequences[i] = stringList.get(i);
        }
        currentItem = list.indexOf(size);
        args.putCharSequenceArray("items", charSequences);
        args.putInt("currentItem", currentItem);
        args.putInt("function", function);
        fragment.setArguments(args);

        return fragment;
    }

    private static List<String> getList(List<Camera.Size> list) {
        List<String> lists;

        lists = new ArrayList<String>();

        for (Camera.Size item : list) {
            lists.add(item.width + ":" + item.height);
        }

        return lists;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args;

        args = getArguments();
        mCurrentItem = args.getInt("currentItem");
        mSelectItems = args.getCharSequenceArray("items");
        mFunction = args.getInt("function");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.app_name)
                .setSingleChoiceItems(mSelectItems, mCurrentItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Fragment fragment;

                        mCurrentItem = which;
                        fragment = getFragmentManager().findFragmentByTag(CustomCamera.class.getSimpleName());

                        if (fragment instanceof SelectDialogOnClink) {
                            ((SelectDialogOnClink) fragment).onClink(mFunction, which);
                        }

                        dismiss();
                    }
                })
                .create();

    }


}
