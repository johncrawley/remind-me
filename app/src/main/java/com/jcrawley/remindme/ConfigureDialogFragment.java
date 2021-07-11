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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.configure_dialog, container, false);
        Dialog dialog =  getDialog();
        ViewModelProvider vmp = new ViewModelProvider(this);
        viewModel = vmp.get(MainViewModel.class);
        if(viewModel == null){
            System.out.println("View model is null!");
        }
        if(dialog != null){
            dialog.setTitle("Simple Dialog");
        }


        return rootView;
    }


    private EditText minutesEditText;
    private EditText secondsEditText;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        minutesStr = "";
        minutesEditText = view.findViewById(R.id.minutesInputEditText);
        secondsEditText = view.findViewById(R.id.secondsInputEditText);

        setupMinutesWatcher();
        setupSecondsWatcher();
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
                minutesStr = editable.toString();
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
                viewModel.secs = secondsStr;
            }

        });
    }


    public void onDismiss(@NonNull DialogInterface dialog)
    {
        Activity activity = getActivity();
        if(activity instanceof CustomDialogCloseListener)
            ((CustomDialogCloseListener)activity).handleDialogClose(dialog, Integer.parseInt(minutesStr), Integer.parseInt(secondsStr));
    }


    private void removeNewValueIfOutsideAcceptableRange(Editable editable, int limit, String previousValue){
        String str = editable.toString();
        if(str.equals("")){
            return;
        }
        int editedNumber = Integer.parseInt(str);
        if(editedNumber > limit){
            editable.clear();
            editable.append(previousValue);
        }
    }


}