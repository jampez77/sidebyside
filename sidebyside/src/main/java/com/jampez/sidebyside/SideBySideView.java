package com.jampez.sidebyside;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import androidx.core.widget.TextViewCompat;

import static com.jampez.sidebyside.Statics.getDrawableFromInt;
import static com.jampez.sidebyside.Statics.getInputType;
import static com.jampez.sidebyside.Statics.getTypeFace;
import static com.jampez.sidebyside.Statics.preventDoubleClick;
import static com.jampez.sidebyside.Statics.showDatePickerDialog;
import static com.jampez.sidebyside.Statics.showTimePickerDialog;

public class SideBySideView extends LinearLayout implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private final int textAppearance;
    private final boolean hideRightView;
    private final CharSequence[] rightSpinnerEntries, leftSpinnerEntries;
    //EditTexts
    private EditText leftET, rightET;
    //Checkboxes
    private CheckBox leftCB, rightCB;
    //Spinners
    private Spinner leftSP, rightSP;

    private Calendar calendar;
    private View updateView;

    //TextViews
    private TextView lowerLeftTV, lowerRightTV;

    private final String EditText = "EditText", CheckBox = "CheckBox", Spinner = "Spinner", Time = "Time", DateTime = "DateTime";

    private final Context context;

    private final String leftText;
    private final String rightText;
    private String rightEditTextText;
    private String leftEditTextText;
    private final String textViewTextStyle;
    private final String editTextTextStyle;
    private final String leftInput;
    private final String rightInput;
    private final String leftEditInputType;
    private final String rightEditInputType;
    private final String rightEditTextHint;
    private final String leftEditTextHint;

    private String SET_DATE, leftTimeVal, rightTimeVal, rightDateTimeVal, leftDateTimeVal;
    private boolean rightCbVal, leftCbVal, rightTimeBool, leftTimeBool, rightDateTimeBool, leftDateTimeBool;
    private int leftSpinnerVal, rightSpinnerVal;

    public SideBySideView(Context context) {
        this(context,null);
    }

    public SideBySideView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SideBySideView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SideBySideView, 0, 0);
        leftText = a.getString(R.styleable.SideBySideView_leftText);
        rightText = a.getString(R.styleable.SideBySideView_rightText);
        textAppearance = a.getResourceId(R.styleable.SideBySideView_textAppearance, -1);
        textViewTextStyle = a.getString(R.styleable.SideBySideView_textViewTextStyle);
        editTextTextStyle = a.getString(R.styleable.SideBySideView_editTextTextStyle);
        leftInput = a.getString(R.styleable.SideBySideView_leftInput);
        rightInput = a.getString(R.styleable.SideBySideView_rightInput);
        leftEditInputType = a.getString(R.styleable.SideBySideView_leftEditInputType);
        rightEditInputType = a.getString(R.styleable.SideBySideView_rightEditInputType);
        leftSpinnerEntries = a.getTextArray(R.styleable.SideBySideView_leftSpinnerEntries);
        rightSpinnerEntries = a.getTextArray(R.styleable.SideBySideView_rightSpinnerEntries);
        rightEditTextText = a.getString(R.styleable.SideBySideView_rightEditTextText);
        leftEditTextText = a.getString(R.styleable.SideBySideView_leftEditTextText);
        leftEditTextHint = a.getString(R.styleable.SideBySideView_leftEditTextHint);
        rightEditTextHint = a.getString(R.styleable.SideBySideView_rightEditTextHint);
        hideRightView = a.getBoolean(R.styleable.SideBySideView_hideRightView, false);

        a.recycle();
        init();
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        removeAllViews();
        init();
    }

    private void init(){
        inflate(getContext(), R.layout.side_by_side,this);

        LinearLayout leftLayout = findViewById(R.id.left_layout);
        LinearLayout rightLayout = findViewById(R.id.right_layout);

        lowerLeftTV = findViewById(R.id.lower_left_tv);
        lowerRightTV = findViewById(R.id.lower_right_tv);

        leftET  = findViewById(R.id.left_et);
        rightET = findViewById(R.id.right_et);
        leftCB  = findViewById(R.id.left_cb);
        rightCB = findViewById(R.id.right_cb);
        leftSP = findViewById(R.id.left_sp);
        rightSP = findViewById(R.id.right_sp);

        calendar = Calendar.getInstance();

        switch (leftInput){
            case EditText:
                //Set EditText to be visible element for leftInput
                leftET.setVisibility(VISIBLE);
                leftCB.setVisibility(GONE);
                leftSP.setVisibility(GONE);
                lowerLeftTV.setVisibility(GONE);

                //Set TextAppearance
                TextViewCompat.setTextAppearance(leftET, textAppearance);

                //Set Hint
                if(leftEditTextHint != null && leftEditTextHint.length() > 0)
                    leftET.setHint(leftEditTextHint);
                else
                    leftET.setHint(leftText);

                //Set input type
                leftET.setInputType(getInputType(leftEditInputType));

                //Set typeface style
                leftET.setTypeface(leftET.getTypeface(), getTypeFace(editTextTextStyle));

                //Set text
                leftET.setText(leftEditTextText);
                leftET.addTextChangedListener(new TextWatcher() {
                    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                    @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
                    @Override public void afterTextChanged(Editable s) { leftEditTextText = s.toString(); }
                });

                break;
            case CheckBox:
                //Set CheckBox to be visible element for leftInput
                leftET.setVisibility(GONE);
                leftCB.setVisibility(VISIBLE);
                leftSP.setVisibility(GONE);
                lowerLeftTV.setVisibility(GONE);

                leftLayout.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        leftCbVal = !leftCB.isChecked();
                        leftCB.setChecked(leftCbVal);
                    }
                });

                if(leftCbVal)rightCB.setChecked(true);
                break;
            case Spinner:
                //Set Spinner to be visible element for leftInput
                leftET.setVisibility(GONE);
                leftCB.setVisibility(GONE);
                leftSP.setVisibility(VISIBLE);
                lowerLeftTV.setVisibility(GONE);

                ArrayAdapter<CharSequence> spinnerArrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, leftSpinnerEntries);
                leftSP.setAdapter(spinnerArrayAdapter);
                leftSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        leftSpinnerVal = position;
                    }
                    @Override public void onNothingSelected(AdapterView<?> parent) { }
                });
                if(leftSpinnerVal > 0)
                    leftSP.setSelection(leftSpinnerVal);
                break;
            case Time:
                leftET.setVisibility(GONE);
                leftCB.setVisibility(GONE);
                leftSP.setVisibility(GONE);
                lowerLeftTV.setVisibility(VISIBLE);

                lowerLeftTV.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawableFromInt(context, R.mipmap.ic_access_time_black_24dp), null);
                lowerLeftTV.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        preventDoubleClick(v);
                        updateView = v;
                        leftTimeBool = true;
                        showTimePickerDialog(context, SideBySideView.this);
                    }
                });

                lowerLeftTV.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        lowerLeftTV.setText("");
                        leftTimeVal = null;
                        return true;
                    }
                });

                lowerLeftTV.setText(leftTimeVal);
                break;
            case DateTime:

                leftET.setVisibility(GONE);
                leftCB.setVisibility(GONE);
                leftSP.setVisibility(GONE);
                lowerLeftTV.setVisibility(VISIBLE);

                lowerLeftTV.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawableFromInt(context, R.mipmap.ic_date_range_black_24dp), null);
                lowerLeftTV.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        preventDoubleClick(v);
                        updateView = v;
                        leftDateTimeBool = true;
                        showDatePickerDialog(calendar, context, SideBySideView.this);
                    }
                });

                lowerLeftTV.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        lowerLeftTV.setText("");
                        leftDateTimeVal = null;
                        return true;
                    }
                });

                lowerLeftTV.setText(leftDateTimeVal);
                break;
        }

        switch (rightInput){
            case EditText:
                //Set EditText to be visible element for leftInput
                rightET.setVisibility(VISIBLE);
                rightCB.setVisibility(GONE);
                rightCB.setVisibility(GONE);
                lowerRightTV.setVisibility(GONE);

                //Set TextAppearance
                TextViewCompat.setTextAppearance(rightET, textAppearance);

                //Set Hint
                if(rightEditTextHint != null && rightEditTextHint.length() > 0)
                    rightET.setHint(rightEditTextHint);
                else
                    rightET.setHint(rightText);

                //Set input type
                rightET.setInputType(getInputType(rightEditInputType));

                //Set typeface style
                rightET.setTypeface(rightET.getTypeface(), getTypeFace(editTextTextStyle));

                //Set text
                rightET.setText(rightEditTextText);
                rightET.addTextChangedListener(new TextWatcher() {
                    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                    @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
                    @Override public void afterTextChanged(Editable s) {
                        rightEditTextText = s.toString();

                        //if both inputTypes are passwords then check if they match
                        if(rightEditInputType.contains("Password") && leftEditInputType.contains("Password")){
                            if(!rightEditTextText.equals(leftEditTextText))
                                rightET.setError("'" + leftET.getHint().toString() + "' does not match '" + rightET.getHint().toString() + "'");
                            else
                                rightET.setError(null);
                        }
                    }
                });

                break;
            case CheckBox:
                //Set CheckBox to be visible element for leftInput
                rightET.setVisibility(GONE);
                rightCB.setVisibility(VISIBLE);
                rightSP.setVisibility(GONE);
                lowerRightTV.setVisibility(GONE);

                rightLayout.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rightCbVal = !rightCB.isChecked();
                        rightCB.setChecked(rightCbVal);
                    }
                });

                if(rightCbVal)rightCB.setChecked(true);
                break;
            case Spinner:
                //Set Spinner to be visible element for leftInput
                rightET.setVisibility(GONE);
                rightCB.setVisibility(GONE);
                rightSP.setVisibility(VISIBLE);
                lowerRightTV.setVisibility(GONE);

                ArrayAdapter<CharSequence> spinnerArrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, rightSpinnerEntries);
                rightSP.setAdapter(spinnerArrayAdapter);
                rightSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        rightSpinnerVal = position;
                    }
                    @Override public void onNothingSelected(AdapterView<?> parent) { }
                });
                if(rightSpinnerVal > 0)
                    rightSP.setSelection(rightSpinnerVal);
                break;
            case Time:
                rightET.setVisibility(GONE);
                rightCB.setVisibility(GONE);
                rightSP.setVisibility(GONE);
                lowerRightTV.setVisibility(VISIBLE);

                lowerRightTV.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawableFromInt(context, R.mipmap.ic_access_time_black_24dp), null);
                lowerRightTV.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        preventDoubleClick(v);
                        updateView = v;
                        rightTimeBool = true;
                        showTimePickerDialog(context, SideBySideView.this);
                    }
                });

                lowerRightTV.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        lowerRightTV.setText("");
                        rightTimeVal = null;
                        return true;
                    }
                });

                lowerRightTV.setText(rightTimeVal);
                break;
            case DateTime:
                rightET.setVisibility(GONE);
                rightCB.setVisibility(GONE);
                rightSP.setVisibility(GONE);
                lowerRightTV.setVisibility(VISIBLE);

                lowerRightTV.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawableFromInt(context, R.mipmap.ic_date_range_black_24dp), null);
                lowerRightTV.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        preventDoubleClick(v);
                        updateView = v;
                        rightDateTimeBool = true;
                        showDatePickerDialog(calendar, context, SideBySideView.this);
                    }
                });

                lowerRightTV.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        lowerRightTV.setText("");
                        rightDateTimeVal = null;
                        return true;
                    }
                });

                lowerRightTV.setText(rightDateTimeVal);
                break;
        }

        TextView leftTV  = findViewById(R.id.left_tv);
        TextView rightTV = findViewById(R.id.right_tv);

        //Setting Text Appearance
        TextViewCompat.setTextAppearance(leftTV, textAppearance);
        TextViewCompat.setTextAppearance(rightTV, textAppearance);

        //Setting Labels
        leftTV.setText(leftText);
        rightTV.setText(rightText);

        //Setting TextView Style
        leftTV.setTypeface(leftTV.getTypeface(), getTypeFace(textViewTextStyle));
        rightTV.setTypeface(rightTV.getTypeface(), getTypeFace(textViewTextStyle));

        if(hideRightView)
            rightLayout.setVisibility(GONE);

        //Override EditText focusing based on visibility of neighbouring view.
        if(rightET.getVisibility() == View.GONE || rightLayout.getVisibility() == View.GONE)
            leftET.setImeOptions(EditorInfo.IME_ACTION_DONE);

        if(leftET.getVisibility() == View.GONE || leftLayout.getVisibility() == View.GONE)
            rightET.setImeOptions(EditorInfo.IME_ACTION_DONE);
    }

    @SuppressWarnings("unused")
    public String rightInput(){
        switch (rightInput) {
            case EditText:
                return rightET.getText().toString();
            case CheckBox:
                return String.valueOf(rightCB.isChecked());
            case Spinner:
                return String.valueOf(rightSP.getSelectedItemPosition());
            case Time:
                return lowerRightTV.getText().toString();
            case DateTime:
                return lowerRightTV.getText().toString();
        }
        return null;
    }

    @SuppressWarnings("unused")
    public String leftInput(){
        switch (leftInput) {
            case EditText:
                return leftET.getText().toString();
            case CheckBox:
                return String.valueOf(leftCB.isChecked());
            case Spinner:
                return String.valueOf(leftSP.getSelectedItemPosition());
            case Time:
                return lowerLeftTV.getText().toString();
            case DateTime:
                return lowerLeftTV.getText().toString();
        }
        return null;
    }

    @SuppressWarnings("unused")
    public void setLeftInputListener(Object listener){
        boolean shouldThrow = false;
        String listenerName = "";
        switch (leftInput){
            case EditText:
                if(listener instanceof TextWatcher)
                    leftET.addTextChangedListener((TextWatcher)listener);
                else {
                    shouldThrow = true;
                    listenerName = TextWatcher.class.getSimpleName();
                }
                break;
            case CheckBox:
                if(listener instanceof CompoundButton.OnCheckedChangeListener)
                    leftCB.setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener)listener);
                else {
                    shouldThrow = true;
                    listenerName = CompoundButton.OnCheckedChangeListener.class.getSimpleName();
                }
                break;
            case Spinner:
                if(listener instanceof AdapterView.OnItemSelectedListener)
                    leftSP.setOnItemSelectedListener((AdapterView.OnItemSelectedListener)listener);
                else {
                    shouldThrow = true;
                    listenerName = AdapterView.OnItemSelectedListener.class.getSimpleName();
                }
                break;
            case Time:
                if(listener instanceof TextWatcher)
                    lowerLeftTV.addTextChangedListener((TextWatcher)listener);
                else {
                    shouldThrow = true;
                    listenerName = TextWatcher.class.getSimpleName();
                }
                break;
            case DateTime:
                if(listener instanceof TextWatcher)
                    lowerLeftTV.addTextChangedListener((TextWatcher)listener);
                else {
                    shouldThrow = true;
                    listenerName = TextWatcher.class.getSimpleName();
                }
                break;
        }

        if(shouldThrow)
            throw new RuntimeException(leftInput + " must use " + listenerName);
    }

    @SuppressWarnings("unused")
    public void setRightInputListener(Object listener){
        boolean shouldThrow = false;
        String listenerName = "";
        switch (rightInput){
            case EditText:
                if(listener instanceof TextWatcher)
                    rightET.addTextChangedListener((TextWatcher)listener);
                else {
                    shouldThrow = true;
                    listenerName = TextWatcher.class.getSimpleName();
                }
                break;
            case CheckBox:
                if(listener instanceof CompoundButton.OnCheckedChangeListener)
                    rightCB.setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener)listener);
                else {
                    shouldThrow = true;
                    listenerName = CompoundButton.OnCheckedChangeListener.class.getSimpleName();
                }
                break;
            case Spinner:
                if(listener instanceof AdapterView.OnItemSelectedListener)
                    rightSP.setOnItemSelectedListener((AdapterView.OnItemSelectedListener)listener);
                else {
                    shouldThrow = true;
                    listenerName = AdapterView.OnItemSelectedListener.class.getSimpleName();
                }
                break;
            case Time:
                if(listener instanceof TextWatcher)
                    lowerRightTV.addTextChangedListener((TextWatcher)listener);
                else {
                    shouldThrow = true;
                    listenerName = TextWatcher.class.getSimpleName();
                }
                break;
            case DateTime:
                if(listener instanceof TextWatcher)
                    lowerRightTV.addTextChangedListener((TextWatcher)listener);
                else {
                    shouldThrow = true;
                    listenerName = TextWatcher.class.getSimpleName();
                }
                break;
        }

        if(shouldThrow)
            throw new RuntimeException(rightInput + " must use " + listenerName);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        int month = monthOfYear + 1;
        String dayNum = ""+dayOfMonth;
        String monthNum = ""+month;

        dayNum = ((dayOfMonth < 10) ? "0" + dayOfMonth : dayNum);
        monthNum = ((month < 10) ? "0" + month : monthNum);

        String setDate = dayNum + "/" + monthNum + "/" + year;

        if(rightDateTimeBool && (updateView).getId() == R.id.lower_right_tv){
            showTimePickerDialog(context, SideBySideView.this);
            SET_DATE = setDate;
        }

        if(leftDateTimeBool && (updateView).getId() == R.id.lower_left_tv){
            showTimePickerDialog(context, SideBySideView.this);
            SET_DATE = setDate;
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String min = ""+minute;
        String hour = ""+hourOfDay;

        min = ((minute < 10) ? "0" + minute : min);
        hour = ((hourOfDay < 10) ? "0" + hourOfDay: hour);

        String setTime  = hour + ":" + min;

        if(rightTimeBool && (updateView).getId() == R.id.lower_right_tv) {
            rightTimeVal = setTime;
            ((TextView) updateView).setText(setTime);
            rightTimeBool = false;
        }

        if(leftTimeBool && (updateView).getId() == R.id.lower_left_tv) {
            leftTimeVal = setTime;
            ((TextView) updateView).setText(setTime);
            leftTimeBool = false;
        }

        if(rightDateTimeBool && (updateView).getId() == R.id.lower_right_tv) {
            String str = SET_DATE + " " + setTime;
            ((TextView) updateView).setText(str);
            rightDateTimeBool = false;
            rightDateTimeVal = str;
        }

        if(leftDateTimeBool && (updateView).getId() == R.id.lower_left_tv) {
            String str = SET_DATE + " " + setTime;
            ((TextView) updateView).setText(str);
            leftDateTimeBool = false;
            leftDateTimeVal = str;
        }
    }
}