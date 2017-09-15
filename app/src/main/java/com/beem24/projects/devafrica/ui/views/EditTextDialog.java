package com.beem24.projects.devafrica.ui.views;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.beem24.projects.devafrica.R;

/**
 * Created By Adigun Hammed Olalekan
 * 7/7/2017.
 * Beem24, Inc
 */

public class EditTextDialog {

    private LayoutInflater mLayoutInflater;
    private AlertDialog mAlertDialog;
    private IEditTextDialogListener iEditTextDialogListener;
    private EditText editText;

    public EditTextDialog(Context context, String title,
                          String hint, IEditTextDialogListener editTextDialogListener, final int id) {
        mLayoutInflater = LayoutInflater.from(context);
        iEditTextDialogListener = editTextDialogListener;
        View view =  mLayoutInflater.inflate(R.layout.edit_text_dialog, null, false);
         editText = (EditText) view.findViewById(R.id.edt_edt_dialog);
        if(hint != null)
            editText.setHint(hint);
        mAlertDialog = new AlertDialog.Builder(context, R.style.AlertDialogStyle)
                .setTitle(title == null ? "Edit" : title).setView(
                       view
                ).setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = editText.getText().toString().trim();
                        if(!TextUtils.isEmpty(text)) {
                            if(iEditTextDialogListener != null)
                                iEditTextDialogListener.onFinish(text, id);
                        }else {
                            if(iEditTextDialogListener != null)
                                iEditTextDialogListener.onCancel();
                        }
                    }
                }).setNegativeButton("CANCEL", null).create();
    }
    public EditTextDialog(Context context, String title, String hint, String preText, IEditTextDialogListener editTextDialogListener,
                          int id) {
        this(context, title, hint, editTextDialogListener, id);
        if(preText != null)
            editText.setText(preText);
    }
    public interface IEditTextDialogListener {
        void onFinish(String text, int id);
        void onCancel();
    }
    public void show() {
        mAlertDialog.show();
    }
    public void cancel() {
        mAlertDialog.cancel();
    }
}
