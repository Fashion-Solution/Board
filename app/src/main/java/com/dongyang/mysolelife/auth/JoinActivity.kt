package com.dongyang.mysolelife.auth

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.results.SignUpResult
import com.dongyang.mysolelife.MainActivity
import com.dongyang.mysolelife.R
import com.dongyang.mysolelife.databinding.ActivityBoardDailyWriteBinding
import com.dongyang.mysolelife.databinding.ActivityJoinBinding
import java.util.HashMap

class JoinActivity : AppCompatActivity() {

    private lateinit var binding : ActivityJoinBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_join)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_join)

        val view = binding.root
        setContentView(view)

        val spin = binding.spinner
        spin.adapter = ArrayAdapter.createFromResource(this, R.array.ages, android.R.layout.simple_spinner_item)

        val signUp_button2 = findViewById<Button>(R.id.joinBtn) // 회원 가입 버튼
        signUp_button2.setOnClickListener {
            // 이름, 아이디(이메일), 비밀번호 순
            val signUpName = findViewById<View>(R.id.signUpName) as EditText
            val signUpUsername = findViewById<View>(R.id.signUpUsername) as EditText
            val signUpPassword = findViewById<View>(R.id.signUpPassword) as EditText
            val name = signUpName.text.toString()
            val username = signUpUsername.text.toString()
            val password = signUpPassword.text.toString()
            val attributes: MutableMap<String, String> =
                HashMap()
            attributes["name"] = name
            attributes["email"] = username
            /*Log.d("TTT", attributes.toString())
            Log.d("AAA", username.toString())
            Log.d("AAA", password.toString())*/
            AWSMobileClient.getInstance().signUp(
                username,
                password,
                attributes,
                null,
                object :
                    Callback<SignUpResult> {

                    override fun onResult(signUpResult: SignUpResult) {
                        runOnUiThread {

                            if (!signUpResult.confirmationState) {
                                val details = signUpResult.userCodeDeliveryDetails
                                Toast.makeText(applicationContext, "인증 메일을 보냈습니다.: " + details.destination, Toast.LENGTH_SHORT).show()

                                // 이메일에 문제가 없으면 인증 코드 창으로 이동
                                val i = Intent(this@JoinActivity, OkActivity::class.java)

                                i.putExtra("email", username) // username을 인증 코드 창에서 사용하기 위해
                                startActivity(i)
                                finish()
                            } else {
                                // 인증 코드 창으로 이동
                                Toast.makeText(applicationContext, "Sign-up done.", Toast.LENGTH_SHORT).show()

                            }
                        }
                    }
                    override fun onError(e: Exception) {
                        Log.e(TAG, "Sign-up error", e)
                        when {
                            name.length <= 1 -> {
                                errorMessage("이름을 정확히 입력하세요.")
                            }
                            e.message!!.contains("An account with the given email already exists.") -> {
                                errorMessage("주어진 이메일을 가진 계정이 이미 존재합니다.")
                            }
                            e.message!!.contains("Value at 'username' failed to satisfy constraint") -> {
                                errorMessage("이메일을 입력해주세요.")
                            }
                            e.message!!.contains("Invalid email address format.") -> {
                                errorMessage("잘못된 이메일 주소 형식입니다.")
                            }
                            e.message!!.contains("Value at 'password' failed to satisfy constraint") -> {
                                errorMessage("비밀번호를 입력해주세요")
                            }
                            e.message!!.contains("Password did not conform with policy: Password not long enough") -> {
                                errorMessage("비밀번호는 8자 이상이어야 하며 특수 문자를 반드시 포함해야 합니다.")
                            }
                        }
                    }
                })
        }
    }

    override fun onBackPressed() {
        val i = Intent(this@JoinActivity, IntroActivity::class.java)
        startActivity(i)
        finish()
    }

    // 에러 메시지
    fun errorMessage(message: String?) {
        val mHandler = Handler(Looper.getMainLooper())
        mHandler.post { Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show() }
    }
}