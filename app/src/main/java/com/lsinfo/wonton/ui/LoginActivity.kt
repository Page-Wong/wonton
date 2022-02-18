package com.lsinfo.wonton.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.android.volley.VolleyError
import com.lsinfo.wonton.Config
import com.lsinfo.wonton.R
import com.lsinfo.wonton.model.ResultCode
import com.lsinfo.wonton.model.ResultModel
import com.lsinfo.wonton.utils.HttpListenerInterface
import com.lsinfo.wonton.utils.VolleyRequestUtils
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject
import java.util.*

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
//regin TODO G 测试
        /*userName.setText(("133607").toCharArray(), 0, ("133607").length)
        password.setText(("xwp145+2").toCharArray(), 0, ("xwp145+2").length)*/
//endregion
        Config.LOGIN_USER = ""
        sign_in_button.setOnClickListener {
            login(userName.text.toString(), password.text.toString())
        }
    }

    fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime)

            login_form.visibility = if (show) View.GONE else View.VISIBLE
            login_form.animate().setDuration(shortAnimTime.toLong()).alpha(
                    (if (show) 0 else 1).toFloat()).setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    login_form.visibility = if (show) View.GONE else View.VISIBLE
                }
            })

            login_progress.visibility = if (show) View.VISIBLE else View.GONE
            login_progress.animate().setDuration(shortAnimTime.toLong()).alpha(
                    (if (show) 1 else 0).toFloat()).setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    login_progress.visibility = if (show) View.VISIBLE else View.GONE
                }
            })
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            login_progress.visibility = if (show) View.VISIBLE else View.GONE
            login_progress.visibility = if (show) View.GONE else View.VISIBLE
        }
    }

    private fun login(userName: String, password: String) {
        showProgress(true)
        val maps = HashMap<String, String>()
        maps["pkCorpId"] = "2"
        maps["name"] = userName
        maps["password"] = password
        maps["createGroupCode"] = "1000"
        VolleyRequestUtils.requestPost(applicationContext, Config.API_LOGIN, Config.API_TAG_LOGIN, maps,
                object : HttpListenerInterface(applicationContext) {

                    override fun onSuccess(obj: JSONObject) {
                        Toast.makeText(this@LoginActivity, "登录成功！", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@LoginActivity, InventoryPlanActivity::class.java)
                        this@LoginActivity.startActivity(intent)
                        Config.LOGIN_USER = userName
                        result.code = ResultCode.SUCCESS
                    }

                    override fun onFail(json: JSONObject) {
                        Toast.makeText(context, if (json.get("message") == null) "失败！" else json.getString("message"), Toast.LENGTH_SHORT).show()
                    }

                    override fun onError(error: VolleyError) {
                        Toast.makeText(this@LoginActivity, "登录失败，请检查网络！", Toast.LENGTH_SHORT).show()
                        Handler().postDelayed({ showProgress(false) }, 500)
                    }
                    override fun onCallBack(result: ResultModel) {
                        if (!result.isSuccess()){
                            Toast.makeText(context, result.msg, Toast.LENGTH_SHORT).show()
                        }
                        Handler().postDelayed({ showProgress(false) }, 500)
                    }
                }
        )
    }

}
