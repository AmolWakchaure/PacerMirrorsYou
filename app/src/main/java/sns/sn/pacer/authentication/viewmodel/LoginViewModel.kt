package sns.sn.pacer.authentication.viewmodel

import android.app.Activity
import android.app.ProgressDialog
import android.arch.lifecycle.ViewModel
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import org.json.JSONObject
import org.json.JSONTokener
import sns.sn.pacer.authentication.model.LoginModel
import sns.sn.pacer.authentication.view.LoginActivity
import sns.sn.pacer.classes.Constants
import sns.sn.pacer.classes.MyApplication
import sns.sn.pacer.classes.T
import sns.sn.pacer.playvideo.F

class LoginViewModel (private val listner : LoginResultCallbacks,val context : LoginActivity) : ViewModel()
{
    private val loginModel : LoginModel

    init {
        this.loginModel = LoginModel("")

    }
    val loginIdTextwatcher : TextWatcher
        get() = object : TextWatcher
        {
            override fun afterTextChanged(loginId: Editable?)
            {

                loginModel.setUserId(loginId.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int)
            {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
            {

            }
        }

    fun onLoginClicked(view : View)
    {
        if(TextUtils.isEmpty(loginModel.getUserId()))
        {
            T.t("Warning ! please enter user id first.")

        }
        else
        {
            if(T.isNetworkAvailable())
            {
                authenticateLogin();
            }
            else
            {
                listner.displayMessage("Oops ! no internet connection")
            }
        }
    }
    private fun authenticateLogin()
    {


        try
        {
            var progressDialog = ProgressDialog(context);
            progressDialog.setMessage("Authenticating...")
            progressDialog.setCancelable(false)
            progressDialog.show()

            val stringRequest = object : StringRequest
                (

                Request.Method.POST,
                Constants.LOGIN,
                Response.Listener<String>
                {
                        response ->
                    progressDialog.dismiss()

                    parseLoginResponse(response)

                },
                object : Response.ErrorListener
                {
                    override fun onErrorResponse(volleyError: VolleyError)
                    {
                        progressDialog.dismiss()

                        listner.displayMessage("Volley : "+volleyError)

                    }
                }
            )
            {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String>
                {
                    val params = HashMap<String, String>()
                    params.put(Constants.DEVICE_ID, loginModel.getUserId().toString())
                    params.put(Constants.FCM_KEY, loginModel.getUserId().toString())
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

    private fun parseLoginResponse(response : String)
    {

        try
        {
            if(response != null || response.length > 0)
            {
                var json = JSONTokener(response).nextValue()

                if(json is JSONObject)
                {
                    var jsonObject = JSONObject(response)
                    var status = jsonObject.getString("status")
                    if(status.equals("1"))
                    {
                        listner.onLoginSuccess(loginModel.getUserId().toString().trim())
                    }
                    else if(status.equals("0"))
                    {
                        listner.onLoginSuccess("DST003")
                        //listner.onLoginFailure("Oops ! please check your device id")
                    }
                    else
                    {
                        listner.displayMessage("Oops ! Server failed")
                    }
                }
                else
                {
                    listner.displayMessage("Incorrect json format")
                }
            }
            else
            {
                listner.displayMessage("response != null || response.length > 0")
            }
        }
        catch (e : Exception)
        {
            listner.displayMessage("Exception : parseLoginResponse "+e)
        }
    }
}