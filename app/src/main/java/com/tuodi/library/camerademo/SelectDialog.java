package com.tuodi.library.camerademo;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import java.util.List;

/**
 * Created by dsliang on 2016/5/30.
 */
public class SelectDialog extends DialogFragment {

    private CharSequence[] mSelectItems;
    private int mCurrentItem;
    private int mFunction;

    public static SelectDialog newInstance(int function, List<String> list, String mode) {

        Bundle args;
        SelectDialog fragment;
        int currentItem;
        CharSequence[] charSequences;

        args = new Bundle();
        fragment = new SelectDialog();

        charSequences = new CharSequence[list.size()];
        for (int i = 0; i < list.size(); i++) {
            charSequences[i] = list.get(i);
        }
        currentItem = list.indexOf(mode);
        args.putCharSequenceArray("items", charSequences);
        args.putInt("currentItem", currentItem);
        args.putInt("function", function);
        fragment.setArguments(args);

        return fragment;
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
                        if (getActivity() instanceof DialogOnClink) {
                            ((DialogOnClink) getActivity()).onClink(mFunction, which);
                        }
                    }
                })
                .create();

    }

    public interface DialogOnClink {
        public void onClink(int function, int postion);
    }

}
