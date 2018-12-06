package sns.sn.pacer.classes

interface Constants
{
    companion object {

        var NA : String = "NA"


        //shared pref
        var PREF_KEY : String = "prefs_pacer"


        //web api parmas
        var DEVICE_ID : String = "device_id"
        var FCM_KEY : String = "reg_no"

        //web api
        var WEB_URL : String = "http://192.168.0.19/pacer/webservice/"
        var LOGIN : String = WEB_URL+"setDeviceReg"
        var GET_VIDEO_LIST : String = WEB_URL+"getConfigData"

        //FTP details
        var SERVER : String = "192.168.0.19"
        var PORT_NUMBER : Int = 21
        var USERNAME : String = "sandip"
        var PASSWORD : String = "Finix"
        var SERVER_STORAGE_PATH : String = "/home/sandip/Downloads/FTP/"



    }

}