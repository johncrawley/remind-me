package com.jcrawley.remindme;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

public class ConfigureDialogFragment extends DialogFragment {


    private String minutesStr;
    private String secondsStr;
    private MainViewModel viewModel;
    private EditText minutesEditText;
    private EditText secondsEditText;
    private EditText messageEditText;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.configure_dialog, container, false);
        Dialog dialog =  getDialog();
        if(getActivity() == null){
            return rootView;
        }
        ViewModelProvider vmp = new ViewModelProvider(getActivity());
        viewModel = vmp.get(MainViewModel.class);

        if(dialog != null){
            dialog.setTitle("Configure");
        }
        return rootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        minutesStr = "";
        minutesEditText = getEditTextAndAssignString(view, R.id.minutesInputEditText, viewModel.mins);
        secondsEditText = getEditTextAndAssignString(view, R.id.secondsInputEditText, viewModel.secs);
        messageEditText = getEditTextAndAssignString(view, R.id.messageInputEditText, viewModel.msg);
        setupMinutesWatcher();
        setupSecondsWatcher();
    }


    private EditText getEditTextAndAssignString(View parentView, int id, String str){
        EditText editText = parentView.findViewById(id);
        if(str != null) {
            editText.setText(str);
        }
        return editText;
    }


    private void setupMinutesWatcher(){

        minutesEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }


            @Override
            public void afterTextChanged(Editable editable) {
                removeNewValueIfOutsideAcceptableRange(editable, 99, minutesStr);
                minutesStr = validate(editable.toString());
            }
        });
    }


    private void setupSecondsWatcher(){

        secondsEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }


            @Override
            public void afterTextChanged(Editable editable) {
                removeNewValueIfOutsideAcceptableRange(editable, 59, secondsStr);
                secondsStr = editable.toString();
                viewModel.secs = validate(secondsStr);
            }
        });
    }


    private String validate(String str){
        if(str.isEmpty()){
            return "0";
        }
        return str;
    }


    public void onDismiss(@NonNull DialogInterface dialog){
        Activity activity = getActivity();
        if(activity instanceof CustomDialogCloseListener) {
            viewModel.mins = minutesEditText.getText().toString();
            viewModel.secs = secondsEditText.getText().toString();
            int minutes = parse(viewModel.mins);
            int seconds = parse(viewModel.secs);

            viewModel.msg = messageEditText.getText().toString();
            ((CustomDialogCloseListener) activity).handleDialogClose(dialog, minutes, seconds);
        }
    }


    private int parse(String str){
        if(null == str || str.isEmpty()){
            return 0;
        }
        return Integer.parseInt(str);
    }


    private void removeNewValueIfOutsideAcceptableRange(Editable editable, int limit, String previousValue){
        String str = editable.toString();
        if(str.isEmpty()){
            return;
        }
        int editedNumber = Integer.parseInt(str);
        if(editedNumber > limit){
            editable.clear();
            editable.append(previousValue);
        }
    }

}