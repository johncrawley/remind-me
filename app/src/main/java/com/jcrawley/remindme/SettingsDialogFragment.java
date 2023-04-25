package com.jcrawley.remindme;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;


public class SettingsDialogFragment extends DialogFragment {


    private String minutesStr = "";
    private String secondsStr = "";
    private MainViewModel viewModel;
    private EditText minutesEditText;
    private EditText secondsEditText;
    private EditText messageEditText;


    static SettingsDialogFragment newInstance() {
        return new SettingsDialogFragment();
    }

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
        minutesEditText = getEditTextForNumberAndAssignString(view, R.id.minutesInputEditText, viewModel.initialMinutes);
        secondsEditText = getEditTextForNumberAndAssignString(view, R.id.secondsInputEditText, viewModel.initialSeconds);
        messageEditText = getEditTextAndAssignString(view, R.id.messageInputEditText, viewModel.reminderMessage);
        setupMinutesWatcher();
        setupSecondsWatcher();
        setupOkButton(view);
    }


    private void setupOkButton(View parentView){
        Button button = parentView.findViewById(R.id.configOkButton);
        button.setOnClickListener((View v) -> dismiss());
    }


    private EditText getEditTextAndAssignString(View parentView, int id, String str){
        EditText editText = parentView.findViewById(id);
        if(str != null) {
            editText.setText(str);
        }
        return editText;
    }


    private EditText getEditTextForNumberAndAssignString(View parentView, int id, String str){
        EditText editText = parentView.findViewById(id);
        if(str != null) {
            editText.setText(str.trim().isEmpty() ? "0" : str);
        }
        editText.setOnFocusChangeListener((view, b) -> setContentsToZeroIfBlank(view));
        return editText;
    }


    private void setContentsToZeroIfBlank(View view){
        EditText editText = (EditText) view;
        if(editText.getText().toString().isEmpty()){
            editText.setText("0");
        }
    }


    private void setupMinutesWatcher(){
        minutesEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                removeNewValueIfOutsideAcceptableRange(editable, 99, minutesStr);
                minutesStr = validate(editable.toString());
            }
        });
    }


    private void setupSecondsWatcher(){
        secondsEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                removeNewValueIfOutsideAcceptableRange(editable, 59, secondsStr);
                secondsStr = editable.toString();
                viewModel.initialSeconds = validate(secondsStr);
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
        super.onDismiss(dialog);
        updateViewModel();
        saveSettings();
    }


    private void saveSettings(){
        MainActivity activity = (MainActivity) getActivity();
        if(activity != null){
            int minutes = parse(getStrFrom(minutesEditText));
            int seconds = parse(getStrFrom(secondsEditText));
            activity.assignSettings(minutes, seconds, viewModel.reminderMessage);
        }
    }


    private void updateViewModel(){
        viewModel.initialMinutes = getStrFrom(minutesEditText);
        viewModel.initialSeconds = getStrFrom(secondsEditText);
        viewModel.reminderMessage = getStrFrom(messageEditText);
    }


    private String getStrFrom(EditText editText){
        return editText.getText().toString();
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
        if(Integer.parseInt(str) > limit){
            editable.clear();
            editable.append(previousValue);
        }
    }

}