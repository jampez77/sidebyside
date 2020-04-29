@file:Suppress("unused")

package com.jampez.sidebyside

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.content.res.Configuration
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.InputType
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import androidx.core.content.ContextCompat
import java.util.*

internal object Statics {
    fun showTimePickerDialog(context: Context?, callback: OnTimeSetListener?) {
        val calendar = Calendar.getInstance()
        val tp = TimePickerDialog(context, R.style.DateTimePickerTheme, callback, calendar[Calendar.HOUR_OF_DAY], calendar[Calendar.MINUTE], true)
        tp.setCancelable(false)
        tp.show()
    }

    fun showDatePickerDialog(calendar: Calendar, context: Context?, callback: OnDateSetListener?) {
        val dp = DatePickerDialog(context!!, R.style.DateTimePickerTheme, callback, calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH])
        val cal = Calendar.getInstance()
        cal.add(Calendar.YEAR, 1) // to get previous year add -1
        val nextYear = cal.time
        cal.add(Calendar.YEAR, -2)
        val lastYear = cal.time
        dp.datePicker.maxDate = nextYear.time
        dp.datePicker.minDate = lastYear.time
        dp.setCancelable(false)
        dp.show()
    }

    fun preventDoubleClick(view: View?) {
        if (view != null) {
            view.isEnabled = false
            view.postDelayed(Runnable { view.isEnabled = true }, 500)
        }
    }

    @Suppress("DEPRECATION")
    fun getDrawableFromInt(context: Context, id: Int): Drawable? {
        val version = Build.VERSION.SDK_INT
        return if (version >= 21) ContextCompat.getDrawable(context, id) else context.resources.getDrawable(id)
    }

    @Suppress("DEPRECATION")
    fun getColourFromInt(context: Context, id: Int): Int {
        val version = Build.VERSION.SDK_INT
        return if (version >= 21) ContextCompat.getColor(context, id) else context.resources.getColor(id)
    }

    fun getScreenSizeName(context: Context): String {
        var screenLayout = context.resources.configuration.screenLayout
        screenLayout = screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK
        var toReturn = "undefined"
        when (screenLayout) {
            Configuration.SCREENLAYOUT_SIZE_SMALL -> toReturn = "small"
            Configuration.SCREENLAYOUT_SIZE_NORMAL -> toReturn = "normal"
            Configuration.SCREENLAYOUT_SIZE_LARGE -> toReturn = "large"
            Configuration.SCREENLAYOUT_SIZE_XLARGE -> toReturn = "xlarge"
        }
        return toReturn
    }

    fun transformString(input: String, inputType: Int): String {
        return when (inputType) {
            1 -> uppercaseString(input)
            2 -> lowercaseString(input)
            3 -> capitalizeWords(input)
            4 -> capitalizeSentence(input)
            else -> input
        }
    }

    private fun uppercaseString(input: String): String {
        return input.toUpperCase(Locale.getDefault())
    }

    private fun lowercaseString(input: String): String {
        return input.toLowerCase(Locale.getDefault())
    }

    private fun capitalizeSentence(input: String): String {
        return Character.toUpperCase(input[0]).toString() + input.substring(1)
    }

    private fun capitalizeWords(str: String): String {
        val words = str.trim { it <= ' ' }.split(" ").toTypedArray()
        val ret = StringBuilder()
        for (i in words.indices) {
            if (words[i].trim { it <= ' ' }.isNotEmpty()) {
                ret.append(Character.toUpperCase(words[i].trim { it <= ' ' }[0]))
                ret.append(words[i].trim { it <= ' ' }.substring(1))
                if (i < words.size - 1) ret.append(' ')
            }
        }
        return ret.toString()
    }

    fun getInputType(value: String?): Int {
        return if (value != null) {
            var returnType = -1
            if (value.contains("|")) {
                val types = value.split("\\|").dropLastWhile { it.isEmpty() }.toTypedArray()
                for (type in types) returnType += translateInputType(type)
            } else return translateInputType(value)
            returnType
        } else InputType.TYPE_CLASS_TEXT
    }

    fun getTypeFace(value: String?): Int {
        return if (value != null) {
            when (value) {
                "bold" -> Typeface.BOLD
                "italic" -> Typeface.ITALIC
                "bold|italic" -> Typeface.BOLD_ITALIC
                else -> Typeface.NORMAL
            }
        } else Typeface.NORMAL
    }

    fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target!!).matches()
    }

    private fun translateInputType(value: String): Int {
        return when (value) {
            "date" -> InputType.TYPE_CLASS_DATETIME or InputType.TYPE_DATETIME_VARIATION_DATE
            "datetime" -> InputType.TYPE_CLASS_DATETIME or InputType.TYPE_DATETIME_VARIATION_NORMAL
            "none" -> 0
            "number" -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_NORMAL
            "numberDecimal" -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            "numberPassword" -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            "numberSigned" -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
            "phone" -> InputType.TYPE_CLASS_PHONE
            "textAutoComplete" -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE
            "textAutoCorrect" -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_AUTO_CORRECT
            "textCapCharacters" -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
            "textCapSentences" -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            "textCapWords" -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
            "textEmailAddress" -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            "textEmailSubject" -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_SUBJECT
            "textFilter" -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_FILTER
            "textImeMultiLine" -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE
            "textLongMessage" -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE
            "textMultiLine" -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
            "textNoSuggestions" -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
            "textPassword" -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            "textPersonName" -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PERSON_NAME
            "textPhonetic" -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PHONETIC
            "textPostalAddress" -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS
            "textShortMessage" -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE
            "textUri" -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_URI
            "textVisiblePassword" -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            "textWebEditText" -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT
            "textWebEmailAddress" -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS
            "textWebPassword" -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD
            "time" -> InputType.TYPE_CLASS_DATETIME or InputType.TYPE_DATETIME_VARIATION_TIME
            else -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
        }
    }
}