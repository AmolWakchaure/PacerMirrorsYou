package sns.sn.pacer.splash

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import sns.sn.pacer.R
import sns.sn.pacer.authentication.view.LoginActivity
import sns.sn.pacer.classes.Constants
import sns.sn.pacer.classes.MyApplication
import sns.sn.pacer.playvideo.view.DownloadVideoActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar!!.hide()

        var USER_ID = MyApplication.prefs.getString(Constants.DEVICE_ID,"0")

        if(USER_ID.equals("0"))
        {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        else
        {
            val intent = Intent(this, DownloadVideoActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
