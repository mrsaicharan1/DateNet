package tech.freeflo.datingapp.fragments

import android.app.ProgressDialog
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import kotlinx.android.synthetic.main.fragment_login.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONException
import tech.freeflo.datingapp.utils.Constants
import tech.freeflo.datingapp.R
import tech.freeflo.datingapp.activities.HomeActivity

class LoginFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inputPasswordLogin.typeface = Typeface.DEFAULT
        inputPasswordLogin.transformationMethod = PasswordTransformationMethod()

        btnLogin.setOnClickListener { login() }
        tvLinkSignup.setOnClickListener { showSignupFragment() }
    }

    fun login() {
        val username = inputUsernameLogin.text.toString()
        val password = inputPasswordLogin.text.toString()

        if(!validate(username, password)){
            onLoginFailed()
            return
        }

        val progressDialog = ProgressDialog(context)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage("Authenticating..")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()

        val jsonObject = JSONObject()
        try {
            jsonObject.put("username", username)
            jsonObject.put("password", password)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        context?.toast(jsonObject.toString())

        AndroidNetworking.post(Constants.LOGIN_URL)
            .addJSONObjectBody(jsonObject)
            .setPriority(Priority.IMMEDIATE)
            .build()
            .getAsJSONArray(object : JSONArrayRequestListener {
                override fun onResponse(response: JSONArray?) {
                    onLoginSuccess()
                    progressDialog.dismiss()
                }

                override fun onError(anError: ANError) {
                    if (anError.errorCode != 0) {
                        val errorResponse = JSONObject(anError.errorBody)

                        if (errorResponse.has("username")) {
                            inputUsernameLoginLayout.error = errorResponse.getJSONArray("username").getString(0)
                        }
                        if (errorResponse.has("non_field_errors")) {
                            inputUsernameLoginLayout.error = "Either the username or the password is incorrect"
                        }
                    }
                    onLoginFailed()
                    Log.e("ANError:", anError.errorBody)
                    progressDialog.dismiss()
                }

            })
        progressDialog.dismiss()
    }

    private fun showSignupFragment() {
        val transaction = activity!!.supportFragmentManager.beginTransaction()
        val signupFragment = SignupFragment()
        transaction.replace(R.id.login_signup_fragment_holder, signupFragment)
        transaction.commit()
    }


    private fun validate(username: String, password: String): Boolean {
        var valid = true

        if (username.isEmpty() || username.length < 3) {
            inputUsernameLoginLayout.error = "Wrong Username"
            valid = false
        } else {
            inputUsernameLoginLayout.error = null
        }

        if (password.isEmpty() ) {
            inputPasswordLoginLayout.error = "Wrong Password"
            valid = false
        } else {
            inputPasswordLoginLayout.error = null
        }

        return valid
    }

    fun onLoginSuccess() {
        btnLogin.isEnabled = true
        activity?.startActivity<HomeActivity>()
        activity?.finish()
    }

    fun onLoginFailed() {
        Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show()
        btnLogin.isEnabled = true
    }
}
