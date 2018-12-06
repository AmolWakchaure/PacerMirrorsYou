package sns.sn.pacer.classes

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import android.widget.Toast

class T
{
    companion object {

        fun t(message : String)
        {
            Toast.makeText(MyApplication.context,message, Toast.LENGTH_LONG).show()
        }

        fun e(message: String)
        {
            Log.e("PACER_LOG",message)
        }

        fun isNetworkAvailable(): Boolean
        {
            val connectivityManager = MyApplication.context.getSystemService(Context.CONNECTIVITY_SERVICE)
            return if (connectivityManager is ConnectivityManager) {
                val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
                networkInfo?.isConnected ?: false
            } else false
        }

    }
}