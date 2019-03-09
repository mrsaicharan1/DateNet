package tech.freeflo.datingapp.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import tech.freeflo.datingapp.fragments.LoginFragment
import tech.freeflo.datingapp.R

class MainActivity : AppCompatActivity() {

    val manager = supportFragmentManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        showLoginFragment()

    }

    private fun showLoginFragment() {
        val transaction = manager.beginTransaction()
        val loginFragment = LoginFragment()
        transaction.replace(R.id.login_signup_fragment_holder, loginFragment)
        transaction.commit()
    }


}
