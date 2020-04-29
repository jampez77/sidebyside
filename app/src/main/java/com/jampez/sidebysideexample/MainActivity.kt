package com.jampez.sidebysideexample

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.jampez.sidebyside.SideBySideView

class MainActivity : AppCompatActivity() {
    private lateinit var names: SideBySideView
    private lateinit var contactInfo: SideBySideView
    private lateinit var passwords: SideBySideView
    private lateinit var authCode: SideBySideView
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val text = findViewById<TextView>(R.id.text)
        names = findViewById(R.id.names)
        contactInfo = findViewById(R.id.contact_info)
        passwords = findViewById(R.id.password)
        authCode = findViewById(R.id.authorisation_code)
        names.setLeftInputListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                text.text = s.toString()
            }
        })
        val validate = findViewById<Button>(R.id.validate)
        validate.setOnClickListener {
            val namesValid = names.isValid
            val contactInfoValid = contactInfo.isValid
            val passwordsValid = passwords.isValid
            val authCodeValid = authCode.isValid
            if (namesValid && contactInfoValid && passwordsValid && authCodeValid) text.text = "Validated!" else text.text = "Not Validated!"
        }
    }

    override fun onBackPressed() {
        if (!names.haveInputsBeenEdited()) finish() else {
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("Are you sure you want to quit?")
            alertDialog.setCancelable(false)
            alertDialog.setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            alertDialog.setPositiveButton("Yes") { _, _ -> finish() }
            alertDialog.create()
            alertDialog.show()
        }
    }
}