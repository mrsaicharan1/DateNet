package tech.freeflo.datingapp.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import kotlinx.android.synthetic.main.fragment_signup.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.json.JSONException
import org.json.JSONObject
import tech.freeflo.datingapp.utils.Constants
import tech.freeflo.datingapp.R
import tech.freeflo.datingapp.activities.HomeActivity

class SignupFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_signup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnSignup.setOnClickListener { signup() }
        tvLinkLogin.setOnClickListener { showLoginFragment() }
    }

    private fun signup() {
        val firstName = inputFirstnameSignup.text.toString()
        val lastName = inputLastnameSignup.text.toString()
        val age = inputAgeSignup.text.toString()
        val email = inputEmailSignup.text.toString()
        val password = inputPasswordSignup.text.toString()

        if (!validate(firstName, lastName, email, password)) {
            onSignupFailed()
            return
        }

        btnSignup.isEnabled = false

        val progressDialog = ProgressDialog(context)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage("Creating Account...")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()

        val data = JSONObject()
        try {
            data.put("first_name", firstName)
            data.put("last_name", lastName)
            data.put("age", age)
            data.put("email", email)
            data.put("password", password)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        AndroidNetworking.post(Constants.SIGNUP_URL)
            .addJSONObjectBody(data)
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    onSignupSuccess()
                    progressDialog.dismiss()
                }

                override fun onError(error: ANError) {
                    if (error.errorCode != 0) {
                        val errorResponse = JSONObject(error.errorBody)
                        Log.e("ANError:", error.errorBody)
                    }
                    // handle error
                    onSignupFailed()
                    progressDialog.dismiss()
                }
            })
    }

    private fun showLoginFragment() {
        val transaction = activity!!.supportFragmentManager.beginTransaction()
        val loginFragment = LoginFragment()
        transaction.replace(R.id.login_signup_fragment_holder, loginFragment)
        transaction.commit()
    }

//    fun showUserDetailsInputFragment() {
//        val transaction = activity!!.supportFragmentManager.beginTransaction()
//        val userDetailsInputFragment = UserDetailsInputFragment()
//        transaction.replace(R.id.login_signup_fragment_holder, userDetailsInputFragment)
//        transaction.commit()
//    }

    fun onSignupSuccess() {
        activity?.toast("Registration Successful")
        activity?.startActivity<HomeActivity>()
        btnSignup.isEnabled = true
    }

    fun onSignupFailed() {
        activity?.toast("Sign up failed")
        btnSignup.isEnabled = true
    }

    private fun validate(firstname: String, lastname: String, email: String, password: String): Boolean {
        var valid = true

        if (firstname.isEmpty() || firstname.length < 3) {
            inputFirstnameSignupLayout.error = "at least 3 characters"
            valid = false
        } else {
            inputFirstnameSignupLayout.error = null
        }

        if (lastname.isEmpty() || lastname.length < 3) {
            inputLastnameSignupLayout.error = "at least 3 characters"
            valid = false
        } else {
            inputLastnameSignupLayout.error = null
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmailSignupLayout.error = "enter a valid email address"
            valid = false
        } else {
            inputEmailSignupLayout.error = null
        }

        if (password.isEmpty() || password.length < 8) {
            inputPasswordSignupLayout.error = "Password must be at least 8 characters long"
            valid = false
        } else {
            inputPasswordSignupLayout.error = null
        }

        return valid
    }

}
