package sns.sn.pacer.authentication.model

import android.databinding.BaseObservable

class LoginModel (private var userId : String) : BaseObservable()
{


    fun setUserId(userId : String)
    {
        this.userId = userId

    }
    fun getUserId(): String?
    {
        return userId

    }
}