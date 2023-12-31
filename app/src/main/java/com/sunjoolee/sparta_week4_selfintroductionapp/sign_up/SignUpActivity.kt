package com.sunjoolee.sparta_week4_selfintroductionapp.sign_up

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import com.sunjoolee.sparta_week4_selfintroductionapp.ActivityCode
import com.sunjoolee.sparta_week4_selfintroductionapp.R
import com.sunjoolee.sparta_week4_selfintroductionapp.extentions.setInvisible
import com.sunjoolee.sparta_week4_selfintroductionapp.extentions.setVisible
import com.sunjoolee.sparta_week4_selfintroductionapp.text_watchers.EmailTextWatcher
import com.sunjoolee.sparta_week4_selfintroductionapp.text_watchers.NameTextWatcher
import com.sunjoolee.sparta_week4_selfintroductionapp.text_watchers.PasswordTextWatcher
import com.sunjoolee.sparta_week4_selfintroductionapp.user_info.UserInfoManager

class SignUpActivity : AppCompatActivity() {
    private val TAG = "SignUpActivity"

    private val nameEditText by lazy { findViewById<EditText>(R.id.et_name) }
    private val nameWarningTextView by lazy { findViewById<TextView>(R.id.tv_name_warning) }

    private val emailEditText by lazy { findViewById<EditText>(R.id.et_email) }
    private val emailProviderEditText by lazy { findViewById<EditText>(R.id.et_email_provider) }
    private val emailProviderSpinner by lazy { findViewById<Spinner>(R.id.spinner_email_provider) }
    private val emailWarningTextView by lazy { findViewById<TextView>(R.id.tv_email_warning) }

    private val passwordEditText by lazy { findViewById<EditText>(R.id.et_password) }
    private val passwordWarningTextView by lazy { findViewById<TextView>(R.id.tv_password_warning) }

    private val passwordCheckEditText by lazy { findViewById<EditText>(R.id.et_password_check) }
    private val passwordCheckWarningTextView by lazy { findViewById<TextView>(R.id.tv_password_check_warning) }

    private val signUpWarningTextView by lazy { findViewById<TextView>(R.id.tv_sign_up_warning) }
    private val signUpButton by lazy { findViewById<Button>(R.id.btn_sign_up) }

    companion object {
        var isNameValid = false
        var isEmailValid = false
        var isPasswordValid = false
    }

    private var isPasswordCheckValid = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        nameEditText.addTextChangedListener(
            NameTextWatcher(nameWarningTextView, ActivityCode.SIGN_UP)
        )
        emailEditText.addTextChangedListener(
            EmailTextWatcher(emailWarningTextView)
        )
        passwordEditText.addTextChangedListener(
            PasswordTextWatcher(passwordWarningTextView, ActivityCode.SIGN_UP)
        )
        passwordCheckEditText.addTextChangedListener(passwordCheckTextWatcher)

        ArrayAdapter.createFromResource(
            this,
            R.array.email_providers_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            emailProviderSpinner.adapter = adapter
        }
        emailProviderSpinner.onItemSelectedListener = spinnerOnItemSelectListener

        signUpButton.setOnClickListener(signUpOnClickListner)
    }

    private val passwordCheckTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            //비어있는지 확인
            if (p0.isNullOrBlank()) {
                passwordCheckWarningTextView.apply {
                    text = resources.getString(R.string.empty_password_warning)
                    setVisible()
                }
                isPasswordCheckValid = false
            }
            //비밀번호와 일치하는지 확인
            else if (p0.toString() != passwordEditText.text.toString()) {
                passwordCheckWarningTextView.apply {
                    text = resources.getString(R.string.password_different_warning)
                    setVisible()
                }
                isPasswordCheckValid = false
            } else {
                passwordCheckWarningTextView.setInvisible()
                isPasswordCheckValid = true
            }
        }

        override fun afterTextChanged(p0: Editable?) = Unit
    }

    private val spinnerOnItemSelectListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
            Log.d(TAG, "email provider spinner) onItemSelected")
            if (position == emailProviderSpinner.adapter.count - 1) {
                emailProviderSpinner.visibility = View.GONE
                emailProviderEditText.visibility = View.VISIBLE
            }
        }

        override fun onNothingSelected(p0: AdapterView<*>?) {
            Log.d(TAG, "email provider spinner) onNothingSelected")
        }
    }

    private val signUpOnClickListner = object : OnClickListener {
        override fun onClick(p0: View?) {
            //회원가입하기 위해 모든 입력 유효한지 확인
            if (isNameValid && isEmailValid && isPasswordValid && isPasswordCheckValid) {
                Log.d(TAG, "sign up button) sign up success")
                signUpWarningTextView.setInvisible()

                //회원가입
                val name = nameEditText.text.toString()
                val password = passwordEditText.text.toString()
                var email = emailEditText.text.toString()
                //email 서비스 제공자 붙이기
                email += if (emailProviderSpinner.visibility == View.VISIBLE) {
                            emailProviderSpinner.selectedItem.toString()
                        } else {
                            emailProviderEditText.text?.toString() ?: ""
                        }

                val userInfoManager = UserInfoManager.getInstance()
                userInfoManager.signUp(name, email, password)

                finishSignUpActivity(name, password)
            } else {
                Log.d(TAG, "sign up button) sign up fail")
                signUpWarningTextView.setVisible()
            }
        }
    }

    private fun finishSignUpActivity(name:String, password:String){
        val resultIntent = Intent()
        resultIntent.putExtra("signUpName", name)
        resultIntent.putExtra("signUpPassword", password)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }
}