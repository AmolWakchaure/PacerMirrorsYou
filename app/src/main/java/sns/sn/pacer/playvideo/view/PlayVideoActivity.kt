package sns.sn.pacer.playvideo.view

import android.media.MediaPlayer
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.MediaController
import sns.sn.pacer.R
import android.widget.VideoView
import sns.sn.pacer.classes.T
import android.R.attr.path
import android.os.Handler
import android.os.Message
import android.util.Log
import kotlinx.android.synthetic.main.activity_play_video.*
import sns.sn.pacer.playvideo.F
import java.io.IOException
import android.media.MediaPlayer.OnCompletionListener
import sns.sn.pacer.database.TABLE_ADD_LIST
import sns.sn.pacer.playvideo.model.AddDetails
import android.os.CountDownTimer




class PlayVideoActivity : AppCompatActivity() {


    var mediaController : MediaController? = null
    var CONTENT_DATA = ArrayList<AddDetails>()
    var contentName : String? = null
    var contentType : String? = null
    var cnt : Int = 0
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_video)

        supportActionBar!!.hide()
        mediaController = MediaController(this)

        CONTENT_DATA = TABLE_ADD_LIST.selectContent()
        playVieo(mediaController!!)

    }


    fun playVieo(mediaController : MediaController)
    {
        if (cnt == CONTENT_DATA.size)
        {
            finish()
        }
        else
        {
            contentName = CONTENT_DATA[cnt].contentName
           // contentName = "Best_Advertisement_ever-Winner_of_Best_Ad_2014.mp4"
            contentType = CONTENT_DATA[cnt].contentType
            //contentType = "v"

           // T.t("contentName : "+contentName)
           // T.t("contentType : "+contentType)

            if(contentType.equals("v"))
            {
                mediaController!!.setAnchorView(videoViewPlay)
                videoViewPlay.setMediaController(MediaController(this))
                handler.sendEmptyMessage(1)
                //playVieo(mediaController!!)
            }
            else

                cnt++
                playVieo(mediaController)
            }
            /*mediaController!!.setAnchorView(videoViewPlay)
            videoViewPlay.setMediaController(MediaController(this))
            handler.sendEmptyMessage(1)*/


        }

    }

    var handler: Handler = object : Handler()
    {

        override fun handleMessage(msg: Message)
        {

            val pos = msg.what
            if (pos == 1) {

                videoViewPlay.setVideoPath(Environment.getExternalStorageDirectory().toString() + "/Pacer/"+contentName)
                videoViewPlay.requestFocus()
                videoViewPlay.start()

                Log.d("Before Video Finish", "i m in before video finish")
                videoViewPlay.setOnCompletionListener {

                    cnt++
                    playVieo(mediaController!!)

                }
                /*videoViewPlay.setOnCompletionListener(OnCompletionListener { finish()
                })*/
            }
        }
    }


}
