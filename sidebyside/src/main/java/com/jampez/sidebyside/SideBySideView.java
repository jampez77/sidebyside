package com.jampez.sidebyside;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import androidx.core.widget.TextViewCompat;
import static com.jampez.sidebyside.Statics.getDrawableFromInt;
import static com.jampez.sidebyside.Statics.getInputType;
import static com.jampez.sidebyside.Statics.getScreenSizeName;
import static com.jampez.sidebyside.Statics.getTypeFace;
import static com.jampez.sidebyside.Statics.isValidEmail;
import static com.jampez.sidebyside.Statics.preventDoubleClick;
import static com.jampez.sidebyside.Statics.showDatePickerDialog;
import static com.jampez.sidebyside.Statics.showTimePickerDialog;
import static com.jampez.sidebyside.Statics.transformString;
import static java.util.Calendar.getInstance;

public class SideBySideView extends LinearLayout implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private boolean hideRightView, leftRequired, rightRequired, leftEnabled, rightEnabled;
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

    private final String TextView = "TextView", EditText = "EditText", CheckBox = "CheckBox", Spinner = "Spinner", Time = "Time", DateTime = "DateTime";

    private final Context context;

    private String leftText;
    private String rightText;
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
    private final String passwordValidationExpression;
    private final String passwordErrorMessage;
    private Object leftInputListener = null, rightInputListener = null;
    private LinearLayout leftLayout, rightLayout;

    private String SET_DATE, leftTimeVal, rightTimeVal, rightDateTimeVal, leftDateTimeVal;
    private boolean rightCbVal, leftCbVal, rightTimeBool, leftTimeBool, rightDateTimeBool, leftDateTimeBool;
    private int leftSpinnerVal = 0, rightSpinnerVal = 0;
    private final int textAppearance, leftBackground, rightBackground;
    private float leftPadding, rightPadding;
    private int leftTextViewInputType, rightTextViewInputType, titleTextColor, inputTextColor;

    private boolean leftETUsed, leftCBUsed, leftSpUsed, leftTimeUsed, leftDateTimeUsed, rightETUsed, rightCBUsed, rightSpUsed, rightTimeUsed, rightDateTimeUsed, leftHideTitle, rightHideTitle;

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
        textAppearance = a.getResourceId(R.styleable.SideBySideView_textAppearance, android.R.style.TextAppearance);
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
        leftRequired = a.getBoolean(R.styleable.SideBySideView_leftRequired, false);
        rightRequired = a.getBoolean(R.styleable.SideBySideView_rightRequired, false);
        passwordValidationExpression = a.getString(R.styleable.SideBySideView_passwordValidationExpression);
        passwordErrorMessage = a.getString(R.styleable.SideBySideView_passwordErrorMessage);
        leftEnabled = a.getBoolean(R.styleable.SideBySideView_leftInputEnabled, true);
        rightEnabled = a.getBoolean(R.styleable.SideBySideView_rightInputEnabled, true);
        leftPadding = a.getDimension(R.styleable.SideBySideView_leftPadding, 0);
        rightPadding = a.getDimension(R.styleable.SideBySideView_rightPadding, 0);
        leftBackground = a.getResourceId(R.styleable.SideBySideView_leftBackground, R.drawable.blank);
        rightBackground = a.getResourceId(R.styleable.SideBySideView_rightBackground, R.drawable.blank);

        leftHideTitle = a.getBoolean(R.styleable.SideBySideView_leftHideTitle, false);
        rightHideTitle = a.getBoolean(R.styleable.SideBySideView_rightHideTitle, false);

        rightTextViewInputType = a.getInt(R.styleable.SideBySideView_rightTextViewInputType, 0);
        leftTextViewInputType = a.getInt(R.styleable.SideBySideView_leftTextViewInputType, 0);

        titleTextColor = a.getColor(R.styleable.SideBySideView_titleTextColor, getResources().getColor(R.color.black));
        inputTextColor = a.getColor(R.styleable.SideBySideView_inputTextColor, getResources().getColor(R.color.grey));

        a.recycle();
        init();
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        removeAllViews();
        init();
        validation();
    }

    private void init() {
        inflate(getContext(), R.layout.side_by_side, this);

        leftLayout = findViewById(R.id.left_layout);
        rightLayout = findViewById(R.id.right_layout);

        TextView upperLeftTV = findViewById(R.id.left_tv);
        lowerLeftTV = findViewById(R.id.lower_left_tv);
        TextView upperRightTV = findViewById(R.id.right_tv);
        lowerRightTV = findViewById(R.id.lower_right_tv);

        leftET = findViewById(R.id.left_et);
        rightET = findViewById(R.id.right_et);
        leftCB = findViewById(R.id.left_cb);
        rightCB = findViewById(R.id.right_cb);
        leftSP = findViewById(R.id.left_sp);
        rightSP = findViewById(R.id.right_sp);

        calendar = getInstance();

        Log.d("screen size", getScreenSizeName(getContext()));

        switch (leftInput) {
            case TextView:
                //Set EditText to be visible element for leftInput
                leftET.setVisibility(GONE);
                leftCB.setVisibility(GONE);
                leftSP.setVisibility(GONE);
                lowerLeftTV.setVisibility(VISIBLE);


                //Set TextAppearance
                TextViewCompat.setTextAppearance(lowerLeftTV, textAppearance);

                //Set typeface style
                lowerLeftTV.setTypeface(lowerLeftTV.getTypeface(), getTypeFace(editTextTextStyle));
                lowerLeftTV.setTextColor(inputTextColor);
                lowerLeftTV.setText(transformString(leftText, leftTextViewInputType));
                if (leftPadding > 0) lowerLeftTV.setPadding((int)leftPadding, (int)leftPadding, (int)leftPadding, (int)leftPadding);
                if(leftBackground > 0) lowerLeftTV.setBackgroundResource(leftBackground);
                break;
            case EditText:
                //Set EditText to be visible element for leftInput
                leftET.setVisibility(VISIBLE);
                leftCB.setVisibility(GONE);
                leftSP.setVisibility(GONE);
                lowerLeftTV.setVisibility(GONE);

                //Set TextAppearance
                TextViewCompat.setTextAppearance(leftET, textAppearance);

                //Set Hint
                if (leftEditTextHint != null && leftEditTextHint.length() > 0)
                    leftET.setHint(leftEditTextHint);
                else
                    leftET.setHint(leftText);

                //Set input type
                leftET.setInputType(getInputType(leftEditInputType));

                //Set typeface style
                leftET.setTypeface(leftET.getTypeface(), getTypeFace(editTextTextStyle));

                //Set text
                leftET.setText(leftEditTextText);
                leftET.setTextColor(inputTextColor);
                leftET.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (leftET.hasFocus()) {
                            leftETUsed = true;
                            leftEditTextText = s.toString();
                            validEditText(leftET, s.toString(), leftEditInputType, leftRequired);
                        }
                    }
                });

                if(leftBackground > 0) leftET.setBackgroundResource(leftBackground);
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
                        leftCBUsed = true;
                    }
                });

                leftCB.setTextColor(inputTextColor);
                if (leftCbVal) leftCB.setChecked(true);
                if(leftBackground > 0) leftLayout.setBackgroundResource(leftBackground);
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
                        leftSpUsed = true;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                leftSP.setSelection(leftSpinnerVal);
                if(leftBackground > 0) leftSP.setBackgroundResource(leftBackground);
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
                        leftTimeUsed = true;
                        leftTimeBool = true;
                        showTimePickerDialog(context, SideBySideView.this);
                    }
                });

                lowerLeftTV.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        lowerLeftTV.setText("");
                        leftTimeVal = null;
                        leftTimeUsed = true;
                        return true;
                    }
                });

                lowerLeftTV.setTextColor(inputTextColor);
                lowerLeftTV.setText(leftTimeVal);
                if(leftBackground > 0) lowerLeftTV.setBackgroundResource(leftBackground);
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
                        leftDateTimeUsed = true;
                        leftDateTimeBool = true;
                        showDatePickerDialog(calendar, context, SideBySideView.this);
                    }
                });

                lowerLeftTV.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        lowerLeftTV.setText("");
                        leftDateTimeVal = null;
                        leftDateTimeUsed = true;
                        return true;
                    }
                });

                lowerLeftTV.setTextColor(inputTextColor);
                lowerLeftTV.setText(leftDateTimeVal);
                if(leftBackground > 0) lowerLeftTV.setBackgroundResource(leftBackground);
                break;
        }

        setLeftInputListener(leftInputListener);
        setLeftInputEnabled(leftEnabled);

        if (!hideRightView) {
            switch (rightInput) {
                case TextView:
                    //Set EditText to be visible element for leftInput
                    rightET.setVisibility(GONE);
                    rightCB.setVisibility(GONE);
                    rightCB.setVisibility(GONE);
                    lowerRightTV.setVisibility(VISIBLE);


                    //Set TextAppearance
                    TextViewCompat.setTextAppearance(lowerRightTV, textAppearance);

                    //Set typeface style
                    lowerRightTV.setTypeface(lowerRightTV.getTypeface(), getTypeFace(editTextTextStyle));
                    lowerRightTV.setTextColor(inputTextColor);
                    lowerRightTV.setText(transformString(rightText, rightTextViewInputType));
                    if (rightPadding > 0) lowerRightTV.setPadding((int)rightPadding, (int)rightPadding, (int)rightPadding, (int)rightPadding);
                    if(rightBackground > 0) lowerRightTV.setBackgroundResource(rightBackground);
                    break;
                case EditText:
                    //Set EditText to be visible element for leftInput
                    rightET.setVisibility(VISIBLE);
                    rightCB.setVisibility(GONE);
                    rightCB.setVisibility(GONE);
                    lowerRightTV.setVisibility(GONE);

                    //Set TextAppearance
                    TextViewCompat.setTextAppearance(rightET, textAppearance);

                    //Set Hint
                    if (rightEditTextHint != null && rightEditTextHint.length() > 0)
                        rightET.setHint(rightEditTextHint);
                    else
                        rightET.setHint(rightText);

                    //Set input type
                    rightET.setInputType(getInputType(rightEditInputType));

                    //Set typeface style
                    rightET.setTypeface(rightET.getTypeface(), getTypeFace(editTextTextStyle));
                    rightET.setTextColor(inputTextColor);

                    //Set text
                    rightET.setText(rightEditTextText);
                    rightET.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            if (rightET.hasFocus()) {
                                rightEditTextText = s.toString();
                                rightETUsed = true;
                                validEditText(rightET, rightEditTextText, rightEditInputType, rightRequired);
                            }
                        }
                    });
                    if(rightBackground > 0) rightET.setBackgroundResource(rightBackground);
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
                            rightCBUsed = true;
                        }
                    });

                    rightCB.setTextColor(inputTextColor);
                    if (rightCbVal) rightCB.setChecked(true);
                    if(rightBackground > 0) rightLayout.setBackgroundResource(rightBackground);
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
                            rightSpUsed = true;
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });

                    rightSP.setSelection(rightSpinnerVal);
                    if(rightBackground > 0) rightSP.setBackgroundResource(rightBackground);
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
                            rightTimeUsed = true;
                            rightTimeBool = true;
                            showTimePickerDialog(context, SideBySideView.this);
                        }
                    });

                    lowerRightTV.setOnLongClickListener(new OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            lowerRightTV.setText("");
                            rightTimeUsed = true;
                            rightTimeVal = null;
                            return true;
                        }
                    });
                    lowerRightTV.setTextColor(inputTextColor);
                    lowerRightTV.setText(rightTimeVal);
                    if(rightBackground > 0) lowerRightTV.setBackgroundResource(rightBackground);
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
                            rightDateTimeUsed = true;
                            rightDateTimeBool = true;
                            showDatePickerDialog(calendar, context, SideBySideView.this);
                        }
                    });

                    lowerRightTV.setOnLongClickListener(new OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            lowerRightTV.setText("");
                            rightDateTimeUsed = true;
                            rightDateTimeVal = null;
                            return true;
                        }
                    });
                    lowerRightTV.setTextColor(inputTextColor);
                    lowerRightTV.setText(rightDateTimeVal);
                    if(rightBackground > 0) lowerRightTV.setBackgroundResource(rightBackground);
                    break;
            }

            upperRightTV.setVisibility((rightHideTitle) ? GONE : VISIBLE);
            if(upperRightTV.getVisibility() == VISIBLE) {
                TextViewCompat.setTextAppearance(upperRightTV, textAppearance);
                upperRightTV.setText(rightText);
                upperRightTV.setTypeface(upperRightTV.getTypeface(), getTypeFace(textViewTextStyle));
                upperRightTV.setTextColor(titleTextColor);
            }

            if (rightET.getVisibility() == View.GONE || rightLayout.getVisibility() == View.GONE)
                leftET.setImeOptions(EditorInfo.IME_ACTION_DONE);

            if (!rightET.isEnabled())
                leftET.setImeOptions(EditorInfo.IME_FLAG_NAVIGATE_NEXT);

            setRightInputEnabled(rightEnabled);
            setRightInputListener(rightInputListener);
        } else {
            rightLayout.setVisibility(GONE);
            rightET.setFocusable(false);
        }

        upperLeftTV.setVisibility((leftHideTitle) ? GONE : VISIBLE);
        if (upperLeftTV.getVisibility() == VISIBLE) {
            //Setting Text Appearance
            TextViewCompat.setTextAppearance(upperLeftTV, textAppearance);

            //Setting Labels
            upperLeftTV.setText(leftText);
            upperLeftTV.setTextColor(titleTextColor);

            //Setting TextView Style
            upperLeftTV.setTypeface(upperLeftTV.getTypeface(), getTypeFace(textViewTextStyle));
        }

        //Override EditText focusing based on visibility of neighbouring view.
        if(leftET.getVisibility() == View.GONE || leftLayout.getVisibility() == View.GONE)
            rightET.setImeOptions(EditorInfo.IME_ACTION_DONE);
    }

    public boolean haveInputsBeenEdited(){

        boolean leftInputUsed = false;
        if(leftEnabled) {
            switch (leftInput) {
                case EditText:
                    leftInputUsed = leftETUsed;
                    break;
                case CheckBox:
                    leftInputUsed = leftCBUsed;
                    break;
                case Spinner:
                    leftInputUsed = leftSpUsed;
                    break;
                case Time:
                    leftInputUsed = leftTimeUsed;
                    break;
                case DateTime:
                    leftInputUsed = leftDateTimeUsed;
                    break;
            }
        }

        boolean haveInputBeenUsed = leftInputUsed;

        if(!hideRightView && rightEnabled) {
            boolean rightInputUsed = false;
            switch (rightInput) {
                case EditText:
                    rightInputUsed = rightETUsed;
                    break;
                case CheckBox:
                    rightInputUsed = rightCBUsed;
                    break;
                case Spinner:
                    rightInputUsed = rightSpUsed;
                    break;
                case Time:
                    rightInputUsed = rightTimeUsed;
                    break;
                case DateTime:
                    rightInputUsed = rightDateTimeUsed;
                    break;
            }
            haveInputBeenUsed = (leftInputUsed || rightInputUsed);
        }

        return haveInputBeenUsed;
    }

    @SuppressWarnings("unused")
    public boolean isValid(){
        boolean isValid = true;


        boolean leftIsValid = true;
        boolean rightIsValid = true;

        //if inputs have been disabled the override any requirement for validation.

        if(!leftEnabled)
            leftRequired = false;
        else
            leftIsValid = validEditText(leftET, leftET.getText().toString(), leftEditInputType, leftRequired);

        if(!rightEnabled)
            rightRequired = false;
        else
            rightIsValid = validEditText(rightET, rightET.getText().toString(), rightEditInputType, rightRequired);

        boolean bothRequired = (leftRequired && rightRequired);

        //left and right
        if(bothRequired)
            isValid = (leftIsValid && rightIsValid);
        else {
            //left only
            if (leftRequired)
                isValid = leftIsValid;
            //right only
            if (rightRequired)
                isValid = rightIsValid;
        }
        return isValid;
    }

    private void validation(){
        if(haveInputsBeenEdited()) {
            validEditText(leftET, leftET.getText().toString(), leftEditInputType, leftRequired);

            if (!hideRightView)
                validEditText(rightET, rightET.getText().toString(), rightEditInputType, rightRequired);
        }
    }

    private boolean validEditTextPasswords(){
        //if both inputTypes are passwords then check if they match
        if(!hideRightView && rightEditInputType != null && rightEditInputType.contains("Password") && leftEditInputType != null && leftEditInputType.contains("Password")){
            if(passwordValidationExpression != null && passwordValidationExpression.length() > 0) {
                Pattern ps = Pattern.compile(passwordValidationExpression);
                Matcher ms = ps.matcher(leftEditTextText);
                boolean bs = ms.matches();
                if (!bs) {
                    leftET.setError((passwordErrorMessage != null && passwordErrorMessage.length() > 0) ? passwordErrorMessage : "Password not valid!");
                    return true;
                }else
                    leftET.setError(null);

            }
            if(((rightEditTextText != null && rightEditTextText.length() > 0) || (leftEditTextText != null && leftEditTextText.length() > 0)) && (rightEditTextText != null && !rightEditTextText.equals(leftEditTextText))) {
                rightET.setError("'" + leftET.getHint().toString() + "' does not match '" + rightET.getHint().toString() + "'");
                return true;
            }else{
                rightET.setError(null);
                return false;
            }
        }
        return false;
    }

    private boolean validEditText(EditText editText, String input, String inputType, boolean isRequired){
        boolean isValid = true;
        boolean validEmail = isValidEmail(input);

        if (editText != null){
            if(isRequired && editText.isEnabled() && (input == null || input.length() == 0)) {
                editText.setError("Required");
                isValid = false;
            }else if(input != null && editText.isEnabled() && input.length() > 0 && inputType != null && inputType.contains("EmailAddress") && !validEmail) {
                editText.setError("Invalid Email Address");
                isValid = false;
            }else if(editText.isEnabled() && validEditTextPasswords()){
                return false;
            }else
                editText.setError(null);
        }

        return isValid;
    }

    @SuppressWarnings("unused")
    public void setLeftError(String error){
        if(leftInput.equals(Time) || leftInput.equals(DateTime))
            lowerLeftTV.setError(error);
        else{
            switch (leftInput) {
                case TextView:
                    lowerLeftTV.setError(error);
                    break;
                case EditText:
                    leftET.setError(error);
                    break;
                case CheckBox:
                    leftCB.setError(error);
                    break;
                case Spinner:
                    TextView errorText = (TextView) leftSP.getSelectedView();
                    errorText.setError(error);
                    break;
            }
        }

    }

    @SuppressWarnings("unused")
    public void setRightError(String error){
        if (rightInput.equals(Time) || rightInput.equals(DateTime))
            lowerRightTV.setError(error);
        else{
            switch (rightInput) {
                case TextView:
                    lowerRightTV.setError(error);
                    break;
                case EditText:
                    rightET.setError(error);
                    break;
                case CheckBox:
                    rightCB.setError(error);
                    break;
                case Spinner:
                    TextView errorText = (TextView) rightSP.getSelectedView();
                    errorText.setError(error);
                    break;
            }
        }
    }

    @SuppressWarnings("unused")
    public View getLeftInputView(){
        if (leftInput.equals(Time) || leftInput.equals(DateTime))
            return lowerLeftTV;
        else{
            switch (leftInput) {
                case TextView:
                    return lowerLeftTV;
                case EditText:
                    return leftET;
                case CheckBox:
                    return leftCB;
                case Spinner:
                    return leftSP;
            }
        }
        return null;
    }

    @SuppressWarnings("unused")
    public View getRightInputView(){
        if (rightInput.equals(Time) || rightInput.equals(DateTime))
            return lowerRightTV;
        else{
            switch (rightInput) {
                case TextView:
                    return lowerRightTV;
                case EditText:
                    return rightET;
                case CheckBox:
                    return rightCB;
                case Spinner:
                    return rightSP;
            }
        }
        return null;
    }

    @SuppressWarnings("unused")
    public String rightInput(){
        if (rightInput.equals(Time) || rightInput.equals(DateTime))
            return lowerRightTV.getText().toString();
        else{
            switch (rightInput) {
                case TextView:
                    return lowerRightTV.getText().toString();
                case EditText:
                    return rightET.getText().toString();
                case CheckBox:
                    return String.valueOf(rightCB.isChecked());
                case Spinner:
                    return (rightSP.getSelectedItemPosition() == 0) ? null : String.valueOf(rightSP.getSelectedItemPosition());
            }
        }
        return null;
    }

    @SuppressWarnings("unused")
    public String leftInput(){
        if (leftInput.equals(Time) || leftInput.equals(DateTime))
            return lowerLeftTV.getText().toString();
        else{
            switch (leftInput) {
                case TextView:
                    return lowerLeftTV.getText().toString();
                case EditText:
                    return leftET.getText().toString();
                case CheckBox:
                    return String.valueOf(leftCB.isChecked());
                case Spinner:
                    return (leftSP.getSelectedItemPosition() == 0) ? null : String.valueOf(leftSP.getSelectedItemPosition());
            }
        }
        return null;
    }

    @SuppressWarnings("unused")
    public void setLeftInputEnabled(boolean isEnabled){
        leftEnabled = isEnabled;
        if (leftInput.equals(Time) || leftInput.equals(DateTime))
            lowerLeftTV.setEnabled(leftEnabled);
        else{
            switch (leftInput) {
                case TextView:
                    lowerLeftTV.setEnabled(leftEnabled);
                    break;
                case EditText:
                    leftET.setEnabled(leftEnabled);
                    leftET.setFocusable(leftEnabled);
                    break;
                case CheckBox:
                    leftCB.setEnabled(leftEnabled);
                    leftLayout.setEnabled(leftEnabled);
                    break;
                case Spinner:
                    leftSP.setEnabled(leftEnabled);
                    break;
            }
        }
    }

    @SuppressWarnings("unused")
    public void setRightInputEnabled(boolean isEnabled){
        rightEnabled = isEnabled;
        if (rightInput.equals(Time) || rightInput.equals(DateTime))
            lowerRightTV.setEnabled(rightEnabled);
        else{
            switch (rightInput) {
                case TextView:
                    lowerRightTV.setEnabled(rightEnabled);
                    break;
                case EditText:
                    rightET.setEnabled(rightEnabled);
                    rightET.setFocusable(rightEnabled);
                    break;
                case CheckBox:
                    rightCB.setEnabled(rightEnabled);
                    rightLayout.setEnabled(rightEnabled);
                    break;
                case Spinner:
                    rightSP.setEnabled(rightEnabled);
                    break;
            }
        }
    }

    @SuppressWarnings("unused")
    public void setLeftInput(Object input){
        if(input != null){
            switch (leftInput) {
                case TextView:
                    if(input instanceof String) {
                        lowerLeftTV.setText((String)input);
                        leftText = (String)input;
                    }else
                        throw new RuntimeException("Left " + leftInput + " Input MUST be type String");
                    break;
                case EditText:
                    if(input instanceof String) {
                        leftET.setText((String)input);
                        leftEditTextText = (String)input;
                    }else
                        throw new RuntimeException("Left " + leftInput + " Input MUST be type String");
                    break;
                case CheckBox:
                    if(input instanceof Boolean) {
                        leftCB.setChecked((boolean)input);
                        leftCbVal = (boolean)input;
                    }else
                        throw new RuntimeException("Left " + leftInput + " Input MUST be type Boolean");
                    break;
                case Spinner:
                    if(input instanceof Integer) {
                        leftSP.setSelection((int)input);
                        leftSpinnerVal = (int)input;
                    }else
                        throw new RuntimeException("Left " + leftInput + " Input MUST be type Integer");
                    break;
                case Time:
                    if(input instanceof String) {
                        lowerLeftTV.setText((String)input);
                        leftTimeVal = (String)input;
                    }else
                        throw new RuntimeException("Left " + leftInput + " Input MUST be type String");
                    break;
                case DateTime:
                    if(input instanceof String) {
                        lowerLeftTV.setText((String)input);
                        leftDateTimeVal = (String)input;
                    }else
                        throw new RuntimeException("Left " + leftInput + " Input MUST be type String");
                    break;
            }
        }
    }

    @SuppressWarnings("unused")
    public void setRightInput(Object input){
        if(input != null){
            switch (rightInput) {
                case TextView:
                    if(input instanceof String) {
                        lowerRightTV.setText((String)input);
                        rightText = (String)input;
                    }else
                        throw new RuntimeException("Left " + rightInput + " Input MUST be type String");
                    break;
                case EditText:
                    if(input instanceof String) {
                        rightET.setText((String)input);
                        rightEditTextText = (String)input;
                    }else
                        throw new RuntimeException("Right " + rightInput + " Input MUST be type String");
                    break;
                case CheckBox:
                    if(input instanceof Boolean) {
                        rightCB.setChecked((boolean)input);
                        rightCbVal = (boolean)input;
                    }else
                        throw new RuntimeException("Right " + rightInput + " Input MUST be type Boolean");
                    break;
                case Spinner:
                    if(input instanceof Integer) {
                        rightSP.setSelection((int)input);
                        rightSpinnerVal = (int)input;
                    }else
                        throw new RuntimeException("Right " + rightInput + " Input MUST be type Integer");
                    break;
                case Time:
                    if(input instanceof String) {
                        lowerRightTV.setText((String)input);
                        rightTimeVal = (String)input;
                    }else
                        throw new RuntimeException("Right " + rightInput + " Input MUST be type String");
                    break;
                case DateTime:
                    if(input instanceof String) {
                        lowerRightTV.setText((String)input);
                        rightDateTimeVal = (String)input;
                    }else
                        throw new RuntimeException("Right " + rightInput + " Input MUST be type String");
                    break;
            }
        }
    }

    @SuppressWarnings("unused")
    public void setLeftInputListener(Object listener){
        if(listener != null){
            this.leftInputListener = listener;
            boolean shouldThrow = false;
            String listenerName = "";
            if (leftInput.equals(Time) || leftInput.equals(DateTime)){
                if(listener instanceof TextWatcher)
                    lowerLeftTV.addTextChangedListener((TextWatcher)listener);
                else {
                    shouldThrow = true;
                    listenerName = TextWatcher.class.getSimpleName();
                }
            }else{
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
                }
            }
            if(shouldThrow)
                throw new RuntimeException(leftInput + " must use " + listenerName);
        }
    }

    @SuppressWarnings("unused")
    public void setRightInputListener(Object listener){
        if(listener != null){
            this.rightInputListener = listener;
            boolean shouldThrow = false;
            String listenerName = "";
            if (rightInput.equals(Time) || rightInput.equals(DateTime)){
                if(listener instanceof TextWatcher)
                    lowerRightTV.addTextChangedListener((TextWatcher)listener);
                else {
                    shouldThrow = true;
                    listenerName = TextWatcher.class.getSimpleName();
                }
            }else{
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
                }
            }
            if(shouldThrow)
                throw new RuntimeException(rightInput + " must use " + listenerName);
        }
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