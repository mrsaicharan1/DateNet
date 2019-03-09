package tech.freeflo.datingapp.fragments

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
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
import android.os.Build
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.widget.Toast
import android.support.v4.app.ActivityCompat





class HomeFragment : Fragment() {

    private val CAMERA_REQUEST = 2

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        displayDetails()
        avatar.setOnClickListener { openCamera() }
        btnAnalyse.setOnClickListener {
            val progressDialog = ProgressDialog(context)
            progressDialog.isIndeterminate = true
            progressDialog.setMessage("Analysing..")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()

            val handler = Handler()
            handler.postDelayed({
                progressDialog.dismiss()
                val x = totalProgress.progress
                totalProgress.progress = x + 2
                pointsText.text = "${(x + 2)} / 100"
            }, 3000) // 3000 milliseconds delay

        }

//        if (User.isFirstLaunch) {
//            loadUserDetails()
//        } else {
//            displayReportCard()
//        }


    }

    private fun openCamera() {
        val cameraIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQUEST)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            // Handle intent
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((context as Activity?)!!, Manifest.permission.CAMERA)) {

            } else {
                ActivityCompat.requestPermissions((context as Activity?)!!, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST)
            }

        }
    }


}
