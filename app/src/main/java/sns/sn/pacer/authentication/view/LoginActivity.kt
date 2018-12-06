package sns.sn.pacer.authentication.view

import android.Manifest
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import sns.sn.pacer.R
import sns.sn.pacer.authentication.viewmodel.LoginResultCallbacks
import sns.sn.pacer.authentication.viewmodel.LoginViewModel
import sns.sn.pacer.authentication.viewmodel.LoginViewModelFactory
import sns.sn.pacer.classes.Constants
import sns.sn.pacer.classes.MyApplication
import sns.sn.pacer.classes.T
import sns.sn.pacer.databinding.ActivityLoginBinding
import sns.sn.pacer.playvideo.F
import sns.sn.pacer.playvideo.view.DownloadVideoActivity

class LoginActivity : AppCompatActivity(),LoginResultCallbacks {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_login)
        val activityLogin = DataBindingUtil.setContentView<ActivityLoginBinding>(this,R.layout.activity_login)
        //insatantiate new ViewModels
        activityLogin.loginViewModel = ViewModelProviders.of(this, LoginViewModelFactory(this,this)).get(LoginViewModel::class.java)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        supportActionBar!!.hide()
        requestPermission()

    }

    override fun onLoginSuccess(deviceId: String)
    {

        MyApplication.editor.putString(Constants.DEVICE_ID,deviceId).commit()
        val intent = Intent(this, DownloadVideoActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onLoginFailure(messageFail: String)
    {
        T.t(messageFail)
    }
    override fun displayMessage(messageFail: String)
    {
        T.t(messageFail)
    }
    private fun requestPermission()
    {

        if (F.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
        {

        }
        else
        {
            F.askPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

    }
}
