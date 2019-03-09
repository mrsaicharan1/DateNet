package tech.freeflo.datingapp.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import kotlinx.android.synthetic.main.fragment_home.*

import org.jetbrains.anko.doAsync
import org.json.JSONObject
import tech.freeflo.datingapp.R
import tech.freeflo.datingapp.utils.Constants
import tech.freeflo.datingapp.utils.MyPreferences
import tech.freeflo.datingapp.utils.User


class HomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        displayDetails()

//        if (User.isFirstLaunch) {
//            loadUserDetails()
//        } else {
//            displayReportCard()
//        }


    }

    private fun displayDetails() {
//        userNameTextView.text = User.userName
//        greetingsTextView.text = "Hey, ${User.Name}"
//        collegeNameTextView.text = "Glad to have our campus ambassador in ${User.collegeName} :)"
    }

    private fun displayReportCard() {
    }

    private fun loadUserDetails() {

        AndroidNetworking.get(Constants.REGULAR_USER_URL)
            .addHeaders(Constants.AUTHORIZATION_KEY, Constants.TOKEN_STRING + User.Token)
            .setTag("userRequest")
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    User.points = response.getInt("total_points")
                    User.rank = response.getInt("rank")
                    doAsync {
                        val prefs = MyPreferences.customPrefs(context!!, Constants.MY_SHARED_PREFERENCE)

                        displayDetails()
                    }

                    User.isFirstLaunch = false
                    displayReportCard()
                }

                override fun onError(error: ANError) {

                }
            })
    }

}
