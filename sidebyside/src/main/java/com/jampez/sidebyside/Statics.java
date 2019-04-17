package com.jampez.sidebyside;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;

import java.util.Calendar;
import java.util.Date;

import androidx.core.content.ContextCompat;

class Statics {
    static void showTimePickerDialog(Context context, TimePickerDialog.OnTimeSetListener callback){
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog tp = new TimePickerDialog(context, R.style.DateTimePickerTheme, callback, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        tp.setCancelable(false);
        tp.show();
    }

    static void showDatePickerDialog(Calendar calendar, Context context, DatePickerDialog.OnDateSetListener callback){
        DatePickerDialog dp = new DatePickerDialog(context, R.style.DateTimePickerTheme, callback, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 1); // to get previous year add -1
        Date nextYear = cal.getTime();

        cal.add(Calendar.YEAR, -2);
        Date lastYear = cal.getTime();

        dp.getDatePicker().setMaxDate(nextYear.getTime());
        dp.getDatePicker().setMinDate(lastYear.getTime());
        dp.setCancelable(false);
        dp.show();
    }

    static void preventDoubleClick(final View view) {
        if(view != null) {
            view.setEnabled(false);
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    view.setEnabled(true);
                }
            }, 500);
        }
    }

    @SuppressWarnings("deprecation")
    static Drawable getDrawableFromInt(Context context, int id) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 21)
            return ContextCompat.getDrawable(context, id);
        else
            return context.getResources().getDrawable(id);
    }

    static int getInputType(String value){
        if(value != null){
            int returnType = -1;
            if(value.contains("|")){
                String[] types = value.split("\\|", -1);
                for (String type : types)
                    returnType += translateInputType(type);
            }else
                return translateInputType(value);
            return returnType;
        }else
            return InputType.TYPE_CLASS_TEXT;
    }

    static int getTypeFace(String value){
        if(value != null) {
            switch (value) {
                case "bold":
                    return Typeface.BOLD;
                case "italic":
                    return Typeface.ITALIC;
                case "bold|italic":
                    return Typeface.BOLD_ITALIC;
                default:
                    return Typeface.NORMAL;
            }
        }
        return Typeface.NORMAL;
    }

    static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private static int translateInputType(String value){
        switch (value){
            case "date":
                return InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_DATE;
            case "datetime":
                return InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_NORMAL;
            case "none":
                return 0;
            case "number":
                return InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL;
            case "numberDecimal":
                return InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL;
            case "numberPassword":
                return InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD;
            case "numberSigned":
                return InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED;
            case "phone":
                return InputType.TYPE_CLASS_PHONE;
            case "text":
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL;
            case "textAutoComplete":
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE;
            case "textAutoCorrect":
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT;
            case "textCapCharacters":
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS;
            case "textCapSentences":
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES;
            case "textCapWords":
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS;
            case "textEmailAddress":
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
            case "textEmailSubject":
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_SUBJECT;
            case "textFilter":
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_FILTER;
            case "textImeMultiLine":
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE;
            case "textLongMessage":
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE;
            case "textMultiLine":
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE;
            case "textNoSuggestions":
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
            case "textPassword":
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
            case "textPersonName":
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME;
            case "textPhonetic":
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PHONETIC;
            case "textPostalAddress":
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS;
            case "textShortMessage":
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE;
            case "textUri":
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI;
            case "textVisiblePassword":
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
            case "textWebEditText":
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT;
            case "textWebEmailAddress":
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS;
            case "textWebPassword":
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD;
            case "time":
                return InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_TIME;
            default:
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL;
        }
    }
}
