package sns.sn.pacer.playvideo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import sns.sn.pacer.classes.Constants;
import sns.sn.pacer.classes.MyApplication;
import sns.sn.pacer.classes.T;
import sns.sn.pacer.playvideo.view.DownloadVideoActivity;

import java.io.*;

public class F
{



    public static Boolean downloadAndSaveFile(String contentName,String contentType, File localFile) throws IOException
    {
        FTPClient ftp = null;


        try {
            ftp = new FTPClient();
            ftp.connect(Constants.Companion.getSERVER(), Constants.Companion.getPORT_NUMBER());
            Log.d("PACER", "Connected. Reply: " + ftp.getReplyString());

            ftp.login(Constants.Companion.getUSERNAME(), Constants.Companion.getPASSWORD());
            Log.d("PACER", "Logged in");
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            Log.d("PACER", "Downloading");
            ftp.enterLocalPassiveMode();

            OutputStream outputStream = null;
            boolean success = false;
            try
            {
                outputStream = new BufferedOutputStream(new FileOutputStream(localFile));
                success = ftp.retrieveFile(Constants.Companion.getSERVER_STORAGE_PATH()+""+contentName, outputStream);

                Log.d("PACER", "success : "+success);
            }
            catch (Exception e)
            {
                Log.d("PACER", "One : "+e);
            }
            finally
            {
                if (outputStream != null)
                {
                    outputStream.close();
                }
            }

            return success;
        }
        catch (Exception e)
        {
            Log.d("PACER", "Two : "+e);
        }
        finally
        {
            if (ftp != null) {
                //ftp.logout();
                ftp.disconnect();
            }
        }
        return null;
    }


    public static boolean checkPermission(Context context, String permission)
    {

        // Ask for permission if it wasn't granted yet
        return (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED );

    }
    public static void askPermission(Context context,String permission)
    {

        ActivityCompat.requestPermissions((Activity) context,new String[]{permission},12);
    }
}
