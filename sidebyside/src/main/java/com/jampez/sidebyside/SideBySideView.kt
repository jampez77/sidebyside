package com.jampez.sidebyside

import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.content.res.Configuration
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.core.widget.TextViewCompat
import com.jampez.sidebyside.Statics.getColourFromInt
import com.jampez.sidebyside.Statics.getDrawableFromInt
import com.jampez.sidebyside.Statics.getInputType
import com.jampez.sidebyside.Statics.getScreenSizeName
import com.jampez.sidebyside.Statics.getTypeFace
import com.jampez.sidebyside.Statics.isValidEmail
import com.jampez.sidebyside.Statics.preventDoubleClick
import com.jampez.sidebyside.Statics.showDatePickerDialog
import com.jampez.sidebyside.Statics.showTimePickerDialog
import com.jampez.sidebyside.Statics.transformString
import java.util.*
import java.util.regex.Pattern

@Suppress("unused", "MemberVisibilityCanBePrivate")
class SideBySideView @JvmOverloads constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr), OnDateSetListener, OnTimeSetListener {
    private val hideRightView: Boolean
    private var leftRequired: Boolean
    private var rightRequired: Boolean
    private var leftEnabled: Boolean
    private var rightEnabled: Boolean
    private val rightSpinnerEntries: Array<CharSequence>?
    private val leftSpinnerEntries: Array<CharSequence>?

    //EditTexts
    private lateinit var leftET: EditText
    private lateinit var rightET: EditText

    //Checkboxes
    private lateinit var leftCB: CheckBox
    private lateinit var rightCB: CheckBox

    //Spinners
    private lateinit var leftSP: Spinner
    private lateinit var rightSP: Spinner
    private lateinit var calendar: Calendar
    private lateinit var updateView: View

    //TextViews
    private lateinit var lowerLeftTV: TextView
    private lateinit var lowerRightTV: TextView
    private val textView = 0
    private val editText = 1
    private val checkBox = 2
    private val spinner = 3
    private val time = 4
    private val dateTime = 5
    private var leftText: String?
    private var rightText: String?
    private var rightEditTextText: String?
    private var leftEditTextText: String?
    private val textViewTextStyle: String?
    private val editTextTextStyle: String?
    private val leftInput: Int
    private val rightInput: Int
    private val leftEditInputType: String?
    private val rightEditInputType: String?
    private val rightEditTextHint: String?
    private val leftEditTextHint: String?
    private val passwordValidationExpression: String?
    private val passwordErrorMessage: String?
    private var leftInputListener: Any? = null
    private var rightInputListener: Any? = null
    private lateinit var leftLayout: LinearLayout
    private lateinit var rightLayout: LinearLayout
    private lateinit var setDate: String
    private var leftTimeVal: String? = null
    private var rightTimeVal: String? = null
    private var rightDateTimeVal: String? = null
    private var leftDateTimeVal: String? = null
    private var rightCbVal = false
    private var leftCbVal = false
    private var rightTimeBool = false
    private var leftTimeBool = false
    private var rightDateTimeBool = false
    private var leftDateTimeBool = false
    private var leftSpinnerVal = 0
    private var rightSpinnerVal = 0
    private val textAppearance: Int
    private val leftBackground: Int
    private val rightBackground: Int
    private val leftPadding: Float
    private val rightPadding: Float
    private val leftTextViewInputType: Int
    private val rightTextViewInputType: Int
    private val titleTextColor: Int
    private val inputTextColor: Int
    private var leftETUsed = false
    private var leftCBUsed = false
    private var leftSpUsed = false
    private var leftTimeUsed = false
    private var leftDateTimeUsed = false
    private var rightETUsed = false
    private var rightCBUsed = false
    private var rightSpUsed = false
    private var rightTimeUsed = false
    private var rightDateTimeUsed = false
    private val leftHideTitle: Boolean
    private val rightHideTitle: Boolean
    
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        removeAllViews()
        init()
        validation()
    }

    private fun init() {
        View.inflate(context, R.layout.side_by_side, this)
        leftLayout = findViewById(R.id.left_layout)
        rightLayout = findViewById(R.id.right_layout)
        val upperLeftTV = findViewById<TextView>(R.id.left_tv)
        lowerLeftTV = findViewById(R.id.lower_left_tv)
        val upperRightTV = findViewById<TextView>(R.id.right_tv)
        lowerRightTV = findViewById(R.id.lower_right_tv)
        leftET = findViewById(R.id.left_et)
        rightET = findViewById(R.id.right_et)
        leftCB = findViewById(R.id.left_cb)
        rightCB = findViewById(R.id.right_cb)
        leftSP = findViewById(R.id.left_sp)
        rightSP = findViewById(R.id.right_sp)
        calendar = Calendar.getInstance()
        Log.d("screen size", getScreenSizeName(context))
        when (leftInput) {
            textView -> {
                //Set EditText to be visible element for leftInput
                leftET.visibility = View.GONE
                leftCB.visibility = View.GONE
                leftSP.visibility = View.GONE
                lowerLeftTV.visibility = View.VISIBLE

                //Set TextAppearance
                TextViewCompat.setTextAppearance(lowerLeftTV, textAppearance)

                //Set typeface style
                lowerLeftTV.setTypeface(lowerLeftTV.typeface, getTypeFace(editTextTextStyle))
                lowerLeftTV.setTextColor(inputTextColor)
                lowerLeftTV.text = transformString(leftText!!, leftTextViewInputType)
                if (leftPadding > 0) lowerLeftTV.setPadding(leftPadding.toInt(), leftPadding.toInt(), leftPadding.toInt(), leftPadding.toInt())
                if (leftBackground > 0) lowerLeftTV.setBackgroundResource(leftBackground)
            }
            editText -> {
                //Set EditText to be visible element for leftInput
                leftET.visibility = View.VISIBLE
                leftCB.visibility = View.GONE
                leftSP.visibility = View.GONE
                lowerLeftTV.visibility = View.GONE

                //Set TextAppearance
                TextViewCompat.setTextAppearance(leftET, textAppearance)

                //Set Hint
                if (leftEditTextHint != null && leftEditTextHint.isNotEmpty()) leftET.hint = leftEditTextHint else leftET.hint = leftText

                //Set input type
                leftET.inputType = getInputType(leftEditInputType)

                //Set typeface style
                leftET.setTypeface(leftET.typeface, getTypeFace(editTextTextStyle))

                //Set text
                leftET.setText(leftEditTextText)
                leftET.setTextColor(inputTextColor)
                leftET.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable) {
                        if (leftET.hasFocus()) {
                            leftETUsed = true
                            leftEditTextText = s.toString()
                            validEditText(leftET, s.toString(), leftEditInputType, leftRequired)
                        }
                    }
                })
                if (leftBackground > 0) leftET.setBackgroundResource(leftBackground)
            }
            checkBox -> {
                //Set CheckBox to be visible element for leftInput
                leftET.visibility = View.GONE
                leftCB.visibility = View.VISIBLE
                leftSP.visibility = View.GONE
                lowerLeftTV.visibility = View.GONE
                leftLayout.setOnClickListener{
                    leftCbVal = !leftCB.isChecked
                    leftCB.isChecked = leftCbVal
                    leftCBUsed = true
                }
                leftCB.setTextColor(inputTextColor)
                if (leftCbVal) leftCB.isChecked = true
                if (leftBackground > 0) leftLayout.setBackgroundResource(leftBackground)
            }
            spinner -> {
                //Set Spinner to be visible element for leftInput
                leftET.visibility = View.GONE
                leftCB.visibility = View.GONE
                leftSP.visibility = View.VISIBLE
                lowerLeftTV.visibility = View.GONE
                val spinnerArrayAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, leftSpinnerEntries!!)
                leftSP.adapter = spinnerArrayAdapter
                leftSP.onItemSelectedListener = object : OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                        leftSpinnerVal = position
                        leftSpUsed = true
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
                leftSP.setSelection(leftSpinnerVal)
                if (leftBackground > 0) leftSP.setBackgroundResource(leftBackground)
            }
            time -> {
                leftET.visibility = View.GONE
                leftCB.visibility = View.GONE
                leftSP.visibility = View.GONE
                lowerLeftTV.visibility = View.VISIBLE
                lowerLeftTV.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawableFromInt(context, R.mipmap.ic_access_time_black_24dp), null)
                lowerLeftTV.setOnClickListener{ v ->
                    preventDoubleClick(v)
                    updateView = v
                    leftTimeUsed = true
                    leftTimeBool = true
                    showTimePickerDialog(context, this@SideBySideView)
                }
                lowerLeftTV.setOnLongClickListener{
                    lowerLeftTV.text = ""
                    leftTimeVal = null
                    leftTimeUsed = true
                    true
                }
                lowerLeftTV.setTextColor(inputTextColor)
                lowerLeftTV.text = leftTimeVal
                if (leftBackground > 0) lowerLeftTV.setBackgroundResource(leftBackground)
            }
            dateTime -> {
                leftET.visibility = View.GONE
                leftCB.visibility = View.GONE
                leftSP.visibility = View.GONE
                lowerLeftTV.visibility = View.VISIBLE
                lowerLeftTV.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawableFromInt(context, R.mipmap.ic_date_range_black_24dp), null)
                lowerLeftTV.setOnClickListener{ v ->
                    preventDoubleClick(v)
                    updateView = v
                    leftDateTimeUsed = true
                    leftDateTimeBool = true
                    showDatePickerDialog(calendar, context, this@SideBySideView)
                }
                lowerLeftTV.setOnLongClickListener{
                    lowerLeftTV.text = ""
                    leftDateTimeVal = null
                    leftDateTimeUsed = true
                    true
                }
                lowerLeftTV.setTextColor(inputTextColor)
                lowerLeftTV.text = leftDateTimeVal
                if (leftBackground > 0) lowerLeftTV.setBackgroundResource(leftBackground)
            }
        }
        setLeftInputListener(leftInputListener)
        setLeftInputEnabled(leftEnabled)
        if (!hideRightView) {
            when (rightInput) {
                textView -> {
                    //Set EditText to be visible element for leftInput
                    rightET.visibility = View.GONE
                    rightCB.visibility = View.GONE
                    rightCB.visibility = View.GONE
                    lowerRightTV.visibility = View.VISIBLE


                    //Set TextAppearance
                    TextViewCompat.setTextAppearance(lowerRightTV, textAppearance)

                    //Set typeface style
                    lowerRightTV.setTypeface(lowerRightTV.typeface, getTypeFace(editTextTextStyle))
                    lowerRightTV.setTextColor(inputTextColor)
                    lowerRightTV.text = transformString(rightText!!, rightTextViewInputType)
                    if (rightPadding > 0) lowerRightTV.setPadding(rightPadding.toInt(), rightPadding.toInt(), rightPadding.toInt(), rightPadding.toInt())
                    if (rightBackground > 0) lowerRightTV.setBackgroundResource(rightBackground)
                }
                editText -> {
                    //Set EditText to be visible element for leftInput
                    rightET.visibility = View.VISIBLE
                    rightCB.visibility = View.GONE
                    rightCB.visibility = View.GONE
                    lowerRightTV.visibility = View.GONE

                    //Set TextAppearance
                    TextViewCompat.setTextAppearance(rightET, textAppearance)

                    //Set Hint
                    if (rightEditTextHint != null && rightEditTextHint.isNotEmpty()) rightET.hint = rightEditTextHint else rightET.hint = rightText

                    //Set input type
                    rightET.inputType = getInputType(rightEditInputType)

                    //Set typeface style
                    rightET.setTypeface(rightET.typeface, getTypeFace(editTextTextStyle))
                    rightET.setTextColor(inputTextColor)

                    //Set text
                    rightET.setText(rightEditTextText)
                    rightET.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                        override fun afterTextChanged(s: Editable) {
                            if (rightET.hasFocus()) {
                                rightEditTextText = s.toString()
                                rightETUsed = true
                                validEditText(rightET, rightEditTextText, rightEditInputType, rightRequired)
                            }
                        }
                    })
                    if (rightBackground > 0) rightET.setBackgroundResource(rightBackground)
                }
                checkBox -> {
                    //Set CheckBox to be visible element for leftInput
                    rightET.visibility = View.GONE
                    rightCB.visibility = View.VISIBLE
                    rightSP.visibility = View.GONE
                    lowerRightTV.visibility = View.GONE
                    rightLayout.setOnClickListener{
                        rightCbVal = !rightCB.isChecked
                        rightCB.isChecked = rightCbVal
                        rightCBUsed = true
                    }
                    rightCB.setTextColor(inputTextColor)
                    if (rightCbVal) rightCB.isChecked = true
                    if (rightBackground > 0) rightLayout.setBackgroundResource(rightBackground)
                }
                spinner -> {
                    //Set Spinner to be visible element for leftInput
                    rightET.visibility = View.GONE
                    rightCB.visibility = View.GONE
                    rightSP.visibility = View.VISIBLE
                    lowerRightTV.visibility = View.GONE
                    val spinnerArrayAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, rightSpinnerEntries!!)
                    rightSP.adapter = spinnerArrayAdapter
                    rightSP.onItemSelectedListener = object : OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                            rightSpinnerVal = position
                            rightSpUsed = true
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }
                    rightSP.setSelection(rightSpinnerVal)
                    if (rightBackground > 0) rightSP.setBackgroundResource(rightBackground)
                }
                time -> {
                    rightET.visibility = View.GONE
                    rightCB.visibility = View.GONE
                    rightSP.visibility = View.GONE
                    lowerRightTV.visibility = View.VISIBLE
                    lowerRightTV.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawableFromInt(context, R.mipmap.ic_access_time_black_24dp), null)
                    lowerRightTV.setOnClickListener{ v ->
                        preventDoubleClick(v)
                        updateView = v
                        rightTimeUsed = true
                        rightTimeBool = true
                        showTimePickerDialog(context, this@SideBySideView)
                    }
                    lowerRightTV.setOnLongClickListener{
                        lowerRightTV.text = ""
                        rightTimeUsed = true
                        rightTimeVal = null
                        true
                    }
                    lowerRightTV.setTextColor(inputTextColor)
                    lowerRightTV.text = rightTimeVal
                    if (rightBackground > 0) lowerRightTV.setBackgroundResource(rightBackground)
                }
                dateTime -> {
                    rightET.visibility = View.GONE
                    rightCB.visibility = View.GONE
                    rightSP.visibility = View.GONE
                    lowerRightTV.visibility = View.VISIBLE
                    lowerRightTV.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawableFromInt(context, R.mipmap.ic_date_range_black_24dp), null)
                    lowerRightTV.setOnClickListener{ v ->
                        preventDoubleClick(v)
                        updateView = v
                        rightDateTimeUsed = true
                        rightDateTimeBool = true
                        showDatePickerDialog(calendar, context, this@SideBySideView)
                    }
                    lowerRightTV.setOnLongClickListener{
                        lowerRightTV.text = ""
                        rightDateTimeUsed = true
                        rightDateTimeVal = null
                        true
                    }
                    lowerRightTV.setTextColor(inputTextColor)
                    lowerRightTV.text = rightDateTimeVal
                    if (rightBackground > 0) lowerRightTV.setBackgroundResource(rightBackground)
                }
            }
            upperRightTV.visibility = if (rightHideTitle) View.GONE else View.VISIBLE
            if (upperRightTV.visibility == View.VISIBLE) {
                TextViewCompat.setTextAppearance(upperRightTV, textAppearance)
                upperRightTV.text = rightText
                upperRightTV.setTypeface(upperRightTV.typeface, getTypeFace(textViewTextStyle))
                upperRightTV.setTextColor(titleTextColor)
            } else {
                //if title is hidden then force input to MATCH_PARENT with full left layout
                val params = rightInputView!!.layoutParams
                params.height = ViewGroup.LayoutParams.MATCH_PARENT
                rightInputView!!.layoutParams = params
                rightInputView!!.requestLayout()
            }
            if (rightET.visibility == View.GONE || rightLayout.visibility == View.GONE) leftET.imeOptions = EditorInfo.IME_ACTION_DONE
            if (!rightET.isEnabled) leftET.imeOptions = EditorInfo.IME_FLAG_NAVIGATE_NEXT
            setRightInputEnabled(rightEnabled)
            setRightInputListener(rightInputListener)
        } else {
            rightLayout.visibility = View.GONE
            rightET.isFocusable = false
        }
        upperLeftTV.visibility = if (leftHideTitle) View.GONE else View.VISIBLE
        if (upperLeftTV.visibility == View.VISIBLE) {
            //Setting Text Appearance
            TextViewCompat.setTextAppearance(upperLeftTV, textAppearance)

            //Setting Labels
            upperLeftTV.text = leftText
            upperLeftTV.setTextColor(titleTextColor)

            //Setting TextView Style
            upperLeftTV.setTypeface(upperLeftTV.typeface, getTypeFace(textViewTextStyle))
        } else {
            //if title is hidden then force input to MATCH_PARENT with full left layout
            val params = leftInputView!!.layoutParams
            params.height = ViewGroup.LayoutParams.MATCH_PARENT
            leftInputView!!.layoutParams = params
            leftInputView!!.requestLayout()
        }

        //Override EditText focusing based on visibility of neighbouring view.
        if (leftET.visibility == View.GONE || leftLayout.visibility == View.GONE) rightET.imeOptions = EditorInfo.IME_ACTION_DONE
    }

    fun haveInputsBeenEdited(): Boolean {
        var leftInputUsed = false
        if (leftEnabled) {
            when (leftInput) {
                editText -> leftInputUsed = leftETUsed
                checkBox -> leftInputUsed = leftCBUsed
                spinner -> leftInputUsed = leftSpUsed
                time -> leftInputUsed = leftTimeUsed
                dateTime -> leftInputUsed = leftDateTimeUsed
            }
        }
        var haveInputBeenUsed = leftInputUsed
        if (!hideRightView && rightEnabled) {
            var rightInputUsed = false
            when (rightInput) {
                editText -> rightInputUsed = rightETUsed
                checkBox -> rightInputUsed = rightCBUsed
                spinner -> rightInputUsed = rightSpUsed
                time -> rightInputUsed = rightTimeUsed
                dateTime -> rightInputUsed = rightDateTimeUsed
            }
            haveInputBeenUsed = leftInputUsed || rightInputUsed
        }
        return haveInputBeenUsed
    }//left only
    //right only

    //if inputs have been disabled the override any requirement for validation.

    //left and right
    val isValid: Boolean
        get() {
            var isValid = true
            var leftIsValid = true
            var rightIsValid = true

            //if inputs have been disabled the override any requirement for validation.
            if (!leftEnabled) leftRequired = false else leftIsValid = validEditText(leftET, leftET.text.toString(), leftEditInputType, leftRequired)
            if (!rightEnabled) rightRequired = false else rightIsValid = validEditText(rightET, rightET.text.toString(), rightEditInputType, rightRequired)
            val bothRequired = leftRequired && rightRequired

            //left and right
            if (bothRequired) isValid = leftIsValid && rightIsValid else {
                //left only
                if (leftRequired) isValid = leftIsValid
                //right only
                if (rightRequired) isValid = rightIsValid
            }
            return isValid
        }

    private fun validation() {
        if (haveInputsBeenEdited()) {
            validEditText(leftET, leftET.text.toString(), leftEditInputType, leftRequired)
            if (!hideRightView) validEditText(rightET, rightET.text.toString(), rightEditInputType, rightRequired)
        }
    }

    private fun validEditTextPasswords(): Boolean {
        //if both inputTypes are passwords then check if they match
        if (!hideRightView && rightEditInputType != null && rightEditInputType.contains("Password") && leftEditInputType != null && leftEditInputType.contains("Password")) {
            if (passwordValidationExpression != null && passwordValidationExpression.isNotEmpty()) {
                val ps = Pattern.compile(passwordValidationExpression)
                val ms = ps.matcher(leftEditTextText!!)
                val bs = ms.matches()
                if (!bs) {
                    leftET.error = if (passwordErrorMessage != null && passwordErrorMessage.isNotEmpty()) passwordErrorMessage else "Password not valid!"
                    return true
                } else leftET.error = null
            }
            return if ((rightEditTextText != null && rightEditTextText!!.isNotEmpty() || leftEditTextText != null && leftEditTextText!!.isNotEmpty()) && rightEditTextText != null && rightEditTextText != leftEditTextText) {
                rightET.error = "'" + leftET.hint.toString() + "' does not match '" + rightET.hint.toString() + "'"
                true
            } else {
                rightET.error = null
                false
            }
        }
        return false
    }

    private fun validEditText(editText: EditText?, input: String?, inputType: String?, isRequired: Boolean): Boolean {
        var isValid = true
        val validEmail = isValidEmail(input!!)
        if (editText != null) {
            if (isRequired && editText.isEnabled && input.isEmpty()) {
                editText.error = "Required"
                isValid = false
            } else if (editText.isEnabled && input.isNotEmpty() && inputType != null && inputType.contains("EmailAddress") && !validEmail) {
                editText.error = "Invalid Email Address"
                isValid = false
            } else if (editText.isEnabled && validEditTextPasswords()) {
                return false
            } else editText.error = null
        }
        return isValid
    }

    fun setLeftError(error: String?) {
        if (leftInput == time || leftInput == dateTime) lowerLeftTV.error = error else {
            when (leftInput) {
                textView -> lowerLeftTV.error = error
                editText -> leftET.error = error
                checkBox -> leftCB.error = error
                spinner -> {
                    val errorText = leftSP.selectedView as TextView
                    errorText.error = error
                }
            }
        }
    }

    fun setRightError(error: String?) {
        if (rightInput == time || rightInput == dateTime) lowerRightTV.error = error else {
            when (rightInput) {
                textView -> lowerRightTV.error = error
                editText -> rightET.error = error
                checkBox -> rightCB.error = error
                spinner -> {
                    val errorText = rightSP.selectedView as TextView
                    errorText.error = error
                }
            }
        }
    }

    val leftInputView: View?
        get() {
            if (leftInput == time || leftInput == dateTime) return lowerLeftTV else {
                when (leftInput) {
                    textView -> return lowerLeftTV
                    editText -> return leftET
                    checkBox -> return leftCB
                    spinner -> return leftSP
                }
            }
            return null
        }

    val rightInputView: View?
        get() {
            if (rightInput == time || rightInput == dateTime) return lowerRightTV else {
                when (rightInput) {
                    textView -> return lowerRightTV
                    editText -> return rightET
                    checkBox -> return rightCB
                    spinner -> return rightSP
                }
            }
            return null
        }

    fun rightInput(): String? {
        if (rightInput == time || rightInput == dateTime) return lowerRightTV.text.toString() else {
            when (rightInput) {
                textView -> return lowerRightTV.text.toString()
                editText -> return rightET.text.toString()
                checkBox -> return rightCB.isChecked.toString()
                spinner -> return if (rightSP.selectedItemPosition == 0) null else rightSP.selectedItemPosition.toString()
            }
        }
        return null
    }

    fun leftInput(): String? {
        if (leftInput == time || leftInput == dateTime) return lowerLeftTV.text.toString() else {
            when (leftInput) {
                textView -> return lowerLeftTV.text.toString()
                editText -> return leftET.text.toString()
                checkBox -> return leftCB.isChecked.toString()
                spinner -> return if (leftSP.selectedItemPosition == 0) null else leftSP.selectedItemPosition.toString()
            }
        }
        return null
    }

    fun setLeftInputEnabled(isEnabled: Boolean) {
        leftEnabled = isEnabled
        if (leftInput == time || leftInput == dateTime) lowerLeftTV.isEnabled = leftEnabled else {
            when (leftInput) {
                textView -> lowerLeftTV.isEnabled = leftEnabled
                editText -> {
                    leftET.isEnabled = leftEnabled
                    leftET.isFocusable = leftEnabled
                }
                checkBox -> {
                    leftCB.isEnabled = leftEnabled
                    leftLayout.isEnabled = leftEnabled
                }
                spinner -> leftSP.isEnabled = leftEnabled
            }
        }
    }

    fun setRightInputEnabled(isEnabled: Boolean) {
        rightEnabled = isEnabled
        if (rightInput == time || rightInput == dateTime) lowerRightTV.isEnabled = rightEnabled else {
            when (rightInput) {
                textView -> lowerRightTV.isEnabled = rightEnabled
                editText -> {
                    rightET.isEnabled = rightEnabled
                    rightET.isFocusable = rightEnabled
                }
                checkBox -> {
                    rightCB.isEnabled = rightEnabled
                    rightLayout.isEnabled = rightEnabled
                }
                spinner -> rightSP.isEnabled = rightEnabled
            }
        }
    }

    fun setLeftInput(input: Any?) {
        if (input != null) {
            when (leftInput) {
                textView -> if (input is String) {
                    lowerLeftTV.text = input
                    leftText = input
                } else throw RuntimeException("Left $leftInput Input MUST be type String")
                editText -> leftEditTextText = if (input is String) {
                    leftET.setText(input as String?)
                    input
                } else throw RuntimeException("Left $leftInput Input MUST be type String")
                checkBox -> if (input is Boolean) {
                    leftCB.isChecked = input
                    leftCbVal = input
                } else throw RuntimeException("Left $leftInput Input MUST be type Boolean")
                spinner -> leftSpinnerVal = if (input is Int) {
                    leftSP.setSelection(input)
                    input
                } else throw RuntimeException("Left $leftInput Input MUST be type Integer")
                time -> if (input is String) {
                    lowerLeftTV.text = input
                    leftTimeVal = input
                } else throw RuntimeException("Left $leftInput Input MUST be type String")
                dateTime -> if (input is String) {
                    lowerLeftTV.text = input
                    leftDateTimeVal = input
                } else throw RuntimeException("Left $leftInput Input MUST be type String")
            }
        }
    }

    fun setRightInput(input: Any?) {
        if (input != null) {
            when (rightInput) {
                textView -> if (input is String) {
                    lowerRightTV.text = input
                    rightText = input
                } else throw RuntimeException("Left $rightInput Input MUST be type String")
                editText -> rightEditTextText = if (input is String) {
                    rightET.setText(input as String?)
                    input
                } else throw RuntimeException("Right $rightInput Input MUST be type String")
                checkBox -> if (input is Boolean) {
                    rightCB.isChecked = input
                    rightCbVal = input
                } else throw RuntimeException("Right $rightInput Input MUST be type Boolean")
                spinner -> rightSpinnerVal = if (input is Int) {
                    rightSP.setSelection(input)
                    input
                } else throw RuntimeException("Right $rightInput Input MUST be type Integer")
                time -> if (input is String) {
                    lowerRightTV.text = input
                    rightTimeVal = input
                } else throw RuntimeException("Right $rightInput Input MUST be type String")
                dateTime -> if (input is String) {
                    lowerRightTV.text = input
                    rightDateTimeVal = input
                } else throw RuntimeException("Right $rightInput Input MUST be type String")
            }
        }
    }

    fun setLeftInputListener(listener: Any?) {
        if (listener != null) {
            leftInputListener = listener
            var shouldThrow = false
            var listenerName = ""
            if (leftInput == time || leftInput == dateTime) {
                if (listener is TextWatcher) lowerLeftTV.addTextChangedListener(listener as TextWatcher?) else {
                    shouldThrow = true
                    listenerName = TextWatcher::class.java.simpleName
                }
            } else {
                when (leftInput) {
                    editText -> if (listener is TextWatcher) leftET.addTextChangedListener(listener as TextWatcher?) else {
                        shouldThrow = true
                        listenerName = TextWatcher::class.java.simpleName
                    }
                    checkBox -> if (listener is CompoundButton.OnCheckedChangeListener) leftCB.setOnCheckedChangeListener(listener as CompoundButton.OnCheckedChangeListener?) else {
                        shouldThrow = true
                        listenerName = CompoundButton.OnCheckedChangeListener::class.java.simpleName
                    }
                    spinner -> if (listener is OnItemSelectedListener) leftSP.onItemSelectedListener = listener else {
                        shouldThrow = true
                        listenerName = OnItemSelectedListener::class.java.simpleName
                    }
                }
            }
            if (shouldThrow) throw RuntimeException("$leftInput must use $listenerName")
        }
    }

    fun setRightInputListener(listener: Any?) {
        if (listener != null) {
            rightInputListener = listener
            var shouldThrow = false
            var listenerName = ""
            if (rightInput == time || rightInput == dateTime) {
                if (listener is TextWatcher) lowerRightTV.addTextChangedListener(listener as TextWatcher?) else {
                    shouldThrow = true
                    listenerName = TextWatcher::class.java.simpleName
                }
            } else {
                when (rightInput) {
                    editText -> if (listener is TextWatcher) rightET.addTextChangedListener(listener as TextWatcher?) else {
                        shouldThrow = true
                        listenerName = TextWatcher::class.java.simpleName
                    }
                    checkBox -> if (listener is CompoundButton.OnCheckedChangeListener) rightCB.setOnCheckedChangeListener(listener as CompoundButton.OnCheckedChangeListener?) else {
                        shouldThrow = true
                        listenerName = CompoundButton.OnCheckedChangeListener::class.java.simpleName
                    }
                    spinner -> if (listener is OnItemSelectedListener) rightSP.onItemSelectedListener = listener else {
                        shouldThrow = true
                        listenerName = OnItemSelectedListener::class.java.simpleName
                    }
                }
            }
            if (shouldThrow) throw RuntimeException("$rightInput must use $listenerName")
        }
    }

    override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val month = monthOfYear + 1
        var dayNum = "" + dayOfMonth
        var monthNum = "" + month
        dayNum = if (dayOfMonth < 10) "0$dayOfMonth" else dayNum
        monthNum = if (month < 10) "0$month" else monthNum
        val setDate = "$dayNum/$monthNum/$year"
        if (rightDateTimeBool && updateView.id == R.id.lower_right_tv) {
            showTimePickerDialog(context, this@SideBySideView)
            this.setDate = setDate
        }
        if (leftDateTimeBool && updateView.id == R.id.lower_left_tv) {
            showTimePickerDialog(context, this@SideBySideView)
            this.setDate = setDate
        }
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        var min = "" + minute
        var hour = "" + hourOfDay
        min = if (minute < 10) "0$minute" else min
        hour = if (hourOfDay < 10) "0$hourOfDay" else hour
        val setTime = "$hour:$min"
        if (rightTimeBool && updateView.id == R.id.lower_right_tv) {
            rightTimeVal = setTime
            (updateView as TextView?)!!.text = setTime
            rightTimeBool = false
        }
        if (leftTimeBool && updateView.id == R.id.lower_left_tv) {
            leftTimeVal = setTime
            (updateView as TextView?)!!.text = setTime
            leftTimeBool = false
        }
        if (rightDateTimeBool && updateView.id == R.id.lower_right_tv) {
            val str = "$setDate $setTime"
            (updateView as TextView?)!!.text = str
            rightDateTimeBool = false
            rightDateTimeVal = str
        }
        if (leftDateTimeBool && updateView.id == R.id.lower_left_tv) {
            val str = "$setDate $setTime"
            (updateView as TextView?)!!.text = str
            leftDateTimeBool = false
            leftDateTimeVal = str
        }
    }

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.SideBySideView, 0, 0)
        leftText = a.getString(R.styleable.SideBySideView_leftText)
        rightText = a.getString(R.styleable.SideBySideView_rightText)
        textAppearance = a.getResourceId(R.styleable.SideBySideView_textAppearance, android.R.style.TextAppearance)
        textViewTextStyle = a.getString(R.styleable.SideBySideView_textViewTextStyle)
        editTextTextStyle = a.getString(R.styleable.SideBySideView_editTextTextStyle)
        leftInput = a.getInt(R.styleable.SideBySideView_leftInput, 0)
        rightInput = a.getInt(R.styleable.SideBySideView_rightInput, 0)
        leftEditInputType = a.getString(R.styleable.SideBySideView_leftEditInputType)
        rightEditInputType = a.getString(R.styleable.SideBySideView_rightEditInputType)
        leftSpinnerEntries = a.getTextArray(R.styleable.SideBySideView_leftSpinnerEntries)
        rightSpinnerEntries = a.getTextArray(R.styleable.SideBySideView_rightSpinnerEntries)
        rightEditTextText = a.getString(R.styleable.SideBySideView_rightEditTextText)
        leftEditTextText = a.getString(R.styleable.SideBySideView_leftEditTextText)
        leftEditTextHint = a.getString(R.styleable.SideBySideView_leftEditTextHint)
        rightEditTextHint = a.getString(R.styleable.SideBySideView_rightEditTextHint)
        hideRightView = a.getBoolean(R.styleable.SideBySideView_hideRightView, false)
        leftRequired = a.getBoolean(R.styleable.SideBySideView_leftRequired, false)
        rightRequired = a.getBoolean(R.styleable.SideBySideView_rightRequired, false)
        passwordValidationExpression = a.getString(R.styleable.SideBySideView_passwordValidationExpression)
        passwordErrorMessage = a.getString(R.styleable.SideBySideView_passwordErrorMessage)
        leftEnabled = a.getBoolean(R.styleable.SideBySideView_leftInputEnabled, true)
        rightEnabled = a.getBoolean(R.styleable.SideBySideView_rightInputEnabled, true)
        leftPadding = a.getDimension(R.styleable.SideBySideView_leftPadding, 0f)
        rightPadding = a.getDimension(R.styleable.SideBySideView_rightPadding, 0f)
        leftBackground = a.getResourceId(R.styleable.SideBySideView_leftBackground, 0)
        rightBackground = a.getResourceId(R.styleable.SideBySideView_rightBackground, 0)
        leftHideTitle = a.getBoolean(R.styleable.SideBySideView_leftHideTitle, false)
        rightHideTitle = a.getBoolean(R.styleable.SideBySideView_rightHideTitle, false)
        rightTextViewInputType = a.getInt(R.styleable.SideBySideView_rightTextViewInputType, 0)
        leftTextViewInputType = a.getInt(R.styleable.SideBySideView_leftTextViewInputType, 0)
        titleTextColor = a.getColor(R.styleable.SideBySideView_titleTextColor, getColourFromInt(context, R.color.black))
        inputTextColor = a.getColor(R.styleable.SideBySideView_inputTextColor, getColourFromInt(context, R.color.grey))
        a.recycle()
        init()
    }
}