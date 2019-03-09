package tech.freeflo.datingapp.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.content_home.*
import tech.freeflo.datingapp.R
import tech.freeflo.datingapp.fragments.HomeFragment

class HomeActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        title = "Home"

        val transaction = supportFragmentManager.beginTransaction()
        val homeFragment = HomeFragment()
        transaction.replace(R.id.home_fragment_holder, homeFragment)
        transaction.commit()
    }
}
