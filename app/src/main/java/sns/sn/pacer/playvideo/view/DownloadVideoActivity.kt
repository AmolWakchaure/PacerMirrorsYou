package sns.sn.pacer.playvideo.view

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.*
import android.support.v7.app.AppCompatActivity
import android.provider.ContactsContract
import android.util.Log
import android.widget.MediaController
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import kotlinx.android.synthetic.main.activity_download_video.*
import org.json.JSONObject
import org.json.JSONTokener
import sns.sn.pacer.R
import sns.sn.pacer.classes.Constants
import sns.sn.pacer.classes.MyApplication
import sns.sn.pacer.classes.T
import sns.sn.pacer.database.TABLE_ADD_LIST
import sns.sn.pacer.playvideo.F
import java.io.File
import java.io.IOException
import sns.sn.pacer.playvideo.FTPDownloader
import sns.sn.pacer.playvideo.model.AddDetails
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.view.View
import java.util.concurrent.TimeUnit


class DownloadVideoActivity : AppCompatActivity() {

    val HOST = "192.168.0.19"
    val USERNAME = "sandip"
    val PASSWORD = "Finix"
    val SERVER_PATH = "/home/sandip/Downloads/FTP/Best_Advertisement_ever-Winner_of_Best_Ad_2014.mp4"


    var CONTENT_DATA = ArrayList<AddDetails>()
    var cnt : Int = 0
    var contentName : String? = null
    var contentType : String? = null
    var mediaController : MediaController? = null

   // private lateinit var mdelayHandler : Handler
   // private var splashDelay : Long = 5000//3 sec


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download_video)




        supportActionBar!!.hide()
        mediaController = MediaController(this)


        //first download all content data and store into local
        var DEVICE_ID = MyApplication.prefs.getString(Constants.DEVICE_ID,"0");

        if(T.isNetworkAvailable())
        {
            CONTENT_DATA = TABLE_ADD_LIST.selectContent()
            if(CONTENT_DATA.isEmpty())
            {
                fetchAddList(DEVICE_ID)
            }
            else
            {
                playVieo(mediaController!!)
            }
        }
        else
        {
            CONTENT_DATA = TABLE_ADD_LIST.selectContent()
            if(CONTENT_DATA.isEmpty())
            {
                T.t("Oops ! no internet connection")
            }
            else
            {
                playVieo(mediaController!!)
            }

        }


    }

    private fun fetchAddList(DEVICE_ID : String)
    {
        try
        {
            var progressDialog = ProgressDialog(this);
            progressDialog.setMessage("Downloading Video...")
            progressDialog.setCancelable(false)
            progressDialog.show()
            Log.e("DDDDDDDDDDDD","DEVICE_ID : "+DEVICE_ID)
            val stringRequest = object : StringRequest
                (

                Request.Method.POST,
                Constants.GET_VIDEO_LIST,
                Response.Listener<String>
                {
                        response ->
                    progressDialog.dismiss()

                    Log.e("DDDDDDDDDDDD",""+response)
                    parseResponse(response)

                },
                object : Response.ErrorListener
                {
                    override fun onErrorResponse(volleyError: VolleyError)
                    {
                        progressDialog.dismiss()
                        T.t("Volley : "+volleyError)

                    }
                }
            )
            {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String>
                {
                    val params = HashMap<String, String>()
                    params.put(Constants.DEVICE_ID, DEVICE_ID)

                    return params
                }
            }
            //adding request to queue
            MyApplication.instance?.addToRequestQueue(stringRequest)

        }
        catch (c : Exception)
        {
            //M.t(""+c)
        }
    }

    private fun parseResponse(response: String?) {


        var id = Constants.NA
        var content_name = Constants.NA
        var type = Constants.NA
        try
        {
            if(response != null || response!!.length > 0)
            {
                var json = JSONTokener(response).nextValue()

                if(json is JSONObject)
                {
                    var jsonObject = JSONObject(response)
                    var status = jsonObject.getString("status")
                    if(status.equals("1"))
                    {
                         var jsonArray = jsonObject.getJSONArray("data")

                        for (i in 0 until jsonArray.length())
                        {
                            var jsonObject = jsonArray.getJSONObject(i)

                            if(jsonObject.has("id") && !jsonObject.isNull("id"))
                            {
                                id = jsonObject.getString("id")
                            }

                            if(jsonObject.has("content_name") && !jsonObject.isNull("content_name"))
                            {
                                content_name = jsonObject.getString("content_name")
                            }

                            if(jsonObject.has("type") && !jsonObject.isNull("type"))
                            {
                                type = jsonObject.getString("type")
                            }
                            //check duplicate add
                            var status = TABLE_ADD_LIST.checkDuplicateAdd(id)
                            if(status.equals("0"))
                            {
                                TABLE_ADD_LIST.insertAddList(id,content_name,type)
                            }
                        }
                        T.t("Add successfully fetched")
                        //ready to play video
                        CONTENT_DATA = TABLE_ADD_LIST.selectContent()
                        readyToDownloadVideo()

                    }
                    else if(status.equals("0"))
                    {
                        T.t("Oops ! add not found")
                    }
                    else
                    {
                        T.t("Oops ! Server failed")
                    }
                }
                else
                {
                    T.t("Incorrect json format")
                }

            }
            else
            {
                T.t("response != null || response.length > 0")
            }
        }
        catch (e : Exception)
        {
            T.t("Exception : parseLoginResponse "+e)
        }
    }

    var cntDownload : Int = 0
    private fun readyToDownloadVideo()
    {



        if(cntDownload < CONTENT_DATA.size)
        {
            val rootFolder = File(Environment.getExternalStorageDirectory(), "Pacer")
            val root = File(Environment.getExternalStorageDirectory(), "Pacer/"+CONTENT_DATA[cntDownload].contentName)
            Log.e("PATHDATA",""+Environment.getExternalStorageDirectory())
            if (!rootFolder.exists())
            {
                rootFolder.mkdirs()
            }
            if (!root.exists())
            {
                root.createNewFile()
            }
            DownloadData(CONTENT_DATA[cntDownload].contentName,CONTENT_DATA[cntDownload].contentType,root).execute()
        }
        else
        {
            playVieo(mediaController!!)
        }

    }

    fun playVieo(mediaController : MediaController)
    {
        if (cnt == CONTENT_DATA.size)
        {
            cnt = 0
            CONTENT_DATA.clear()
            CONTENT_DATA = TABLE_ADD_LIST.selectContent()
            playVieo(mediaController)
        }
        else
        {
            contentName = CONTENT_DATA[cnt].contentName
            // contentName = "Best_Advertisement_ever-Winner_of_Best_Ad_2014.mp4"
            contentType = CONTENT_DATA[cnt].contentType
            //contentType = "v"

            if(contentType.equals("v"))
            {
                addImageView.setVisibility(View.GONE)
                videoViewPlay.setVisibility(View.VISIBLE)
                mediaController!!.setAnchorView(videoViewPlay)
                videoViewPlay.setMediaController(MediaController(this))
                handler.sendEmptyMessage(1)
                //playVieo(mediaController!!)
            }
            /*else if (contentType.equals("i"))
            {
                //set image
                setImageAdd(CONTENT_DATA[cnt].contentName)
                T.e("contentName : "+CONTENT_DATA[cnt].contentName)
                T.e("cnt : "+cnt)

            }*/
            else
            {

                cnt++
                playVieo(mediaController)
            }

        }

    }

   // var imageNameDisp : String? = null
    fun setImageAdd(imageName : String)
    {
        object : CountDownTimer(5000, 1000) { // 1000 = 1 sec

            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish()
            {
                videoViewPlay.setVisibility(View.GONE)
                addImageView.setVisibility(View.VISIBLE)
                var imagePath = Environment.getExternalStorageDirectory().toString() + "/Pacer/"+imageName
                val bitmap = BitmapFactory.decodeFile(imagePath)
                addImageView.setImageBitmap(bitmap)
                cnt++
                playVieo(mediaController!!)
            }
        }.start()


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
    inner class DownloadData(internal var contentName: String,internal var contentType: String,internal var localFile: File) : AsyncTask<Void, Void, Void>()
    {
        val progressDialog = ProgressDialog(this@DownloadVideoActivity)
        override fun onPreExecute() {
            super.onPreExecute()


            progressDialog.setMessage("Downloading... "+(cntDownload + 1)+"/"+CONTENT_DATA.size)
            progressDialog.show()
        }


        override fun doInBackground(vararg urls: Void): Void?
        {
            try
            {
                F.downloadAndSaveFile(contentName,contentType,localFile)
            }
            catch (e: IOException)
            {
                Log.d("PACER", "doInBackground Exception : $e")
                e.printStackTrace()
            }

            return null
        }
        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            progressDialog.dismiss()
            cntDownload++
            readyToDownloadVideo()

        }
    }

}
