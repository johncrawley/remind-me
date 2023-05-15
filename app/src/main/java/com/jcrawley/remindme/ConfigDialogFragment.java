package com.jcrawley.remindme;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;


public class ConfigDialogFragment extends DialogFragment {


    private MainViewModel viewModel;
    private EditText messageEditText;

    private TextView minutesLargeDigit, minutesSmallDigit, secondsLargeDigit, secondsSmallDigit;


    static ConfigDialogFragment newInstance() {
        return new ConfigDialogFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.config_dialog, container, false);
        Dialog dialog =  getDialog();
        if(getActivity() == null){
            return rootView;
        }
        ViewModelProvider vmp = new ViewModelProvider(getActivity());
        viewModel = vmp.get(MainViewModel.class);
        setupViews(rootView);
        if(dialog != null){
            dialog.setTitle("Configure");
        }
        return rootView;
    }


    private void setupViews(View parentView){
        minutesLargeDigit = parentView.findViewById(R.id.largeMinuteDigit);
        minutesSmallDigit = parentView.findViewById(R.id.smallMinuteDigit);
        secondsLargeDigit = parentView.findViewById(R.id.largeSecondDigit);
        secondsSmallDigit = parentView.findViewById(R.id.smallSecondDigit);
        assignValuesToDigits();

        setupButton(parentView, R.id.largeMinuteDigitIncreaseButton, ()-> increaseDigitFor(minutesLargeDigit, 9));
        setupButton(parentView, R.id.largeMinuteDigitDecreaseButton, ()-> decreaseDigitFor(minutesLargeDigit, 9));

        setupButton(parentView, R.id.smallMinuteDigitIncreaseButton, ()-> increaseDigitFor(minutesSmallDigit, 9));
        setupButton(parentView, R.id.smallMinuteDigitDecreaseButton, ()-> decreaseDigitFor(minutesSmallDigit, 9));

        setupButton(parentView, R.id.largeSecondDigitIncreaseButton, ()-> increaseDigitFor(secondsLargeDigit, 5));
        setupButton(parentView, R.id.largeSecondDigitDecreaseButton, ()-> decreaseDigitFor(secondsLargeDigit, 5));

        setupButton(parentView, R.id.smallSecondDigitIncreaseButton, ()-> increaseDigitFor(secondsSmallDigit, 9));
        setupButton(parentView, R.id.smallSecondDigitDecreaseButton, ()-> decreaseDigitFor(secondsSmallDigit, 9));
    }


    private void setupButton(View parentView, int buttonId, Runnable runnable){
        ImageButton button = parentView.findViewById(buttonId);
        button.setOnClickListener(v -> runnable.run());
    }


    private void increaseDigitFor(TextView textView, int limit){
        int digit = Integer.parseInt(textView.getText().toString());
        digit++;
        if(digit > limit){
            digit = 0;
        }
        textView.setText(String.valueOf(digit));
    }


    private void decreaseDigitFor(TextView textView, int limit){
        int digit = getDigitFrom(textView);
        digit--;
        if(digit < 0 ){
            digit = limit;
        }
        setText(textView, digit);
    }


    private int getDigitFrom(TextView textView){
        return Integer.parseInt(textView.getText().toString());
    }


    private void setText(TextView textView, int digit){
        textView.setText(String.valueOf(digit));
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        messageEditText = getEditTextAndAssignString(view, R.id.messageInputEditText, viewModel.reminderMessage);

        setupOkButton(view);
    }


    private void assignValuesToDigits(){
        minutesLargeDigit.setText(viewModel.initialMinutesLargeDigit);
        minutesSmallDigit.setText(viewModel.initialMinutesSmallDigit);
        secondsLargeDigit.setText(viewModel.initialSecondsLargeDigit);
        secondsSmallDigit.setText(viewModel.initialSecondsSmallDigit);
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


    public void onDismiss(@NonNull DialogInterface dialog){
        super.onDismiss(dialog);
        updateViewModel();
        saveSettings();
    }


    private void saveSettings(){
        MainActivity activity = (MainActivity) getActivity();
        if(activity != null){

            String minStr = getStrFrom(minutesLargeDigit) + getStrFrom(minutesSmallDigit);
            int minutes = Integer.parseInt(minStr);
            String secStr = getStrFrom(secondsLargeDigit) + getStrFrom(secondsSmallDigit);
            int seconds = Integer.parseInt(secStr);
            Settings settings = new Settings(seconds, minutes, viewModel.reminderMessage);
            settings.minutesLargeDigit = getDigitFrom(minutesLargeDigit);
            settings.minutesSmallDigit = getDigitFrom(minutesSmallDigit);
            settings.secondsLargeDigit = getDigitFrom(secondsLargeDigit);
            settings.secondsSmallDigit = getDigitFrom(secondsSmallDigit);
            activity.assignSettings(settings);
            activity.assignSettings(minutes, seconds, viewModel.reminderMessage);
        }
    }


    private void updateViewModel(){
        viewModel.reminderMessage = getStrFrom(messageEditText);
        viewModel.initialMinutesLargeDigit = getStrFrom(minutesLargeDigit);
        viewModel.initialMinutesSmallDigit = getStrFrom(minutesSmallDigit);
        viewModel.initialSecondsLargeDigit = getStrFrom(secondsLargeDigit);
        viewModel.initialSecondsSmallDigit = getStrFrom(secondsSmallDigit);
    }


    private String getStrFrom(EditText editText){
        return editText.getText().toString();
    }


    private String getStrFrom(TextView textView){
        return textView.getText().toString();
    }


}