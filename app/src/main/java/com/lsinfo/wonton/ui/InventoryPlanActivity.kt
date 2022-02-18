package com.lsinfo.wonton.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.Menu
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import com.android.volley.VolleyError
import com.lsinfo.wonton.Config
import com.lsinfo.wonton.R
import com.lsinfo.wonton.adapter.InventoryPlanListAdapter
import com.lsinfo.wonton.db.AssetsDbManager
import com.lsinfo.wonton.db.InventoryErrorTypeDbManager
import com.lsinfo.wonton.model.*
import com.lsinfo.wonton.utils.HttpListenerInterface
import com.lsinfo.wonton.utils.VolleyRequestUtils
import kotlinx.android.synthetic.main.activity_inventory_plan.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*


class InventoryPlanActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory_plan)
        setSupportActionBar(toolbar)

        initView()
        refreshList()

        if (AssetsDbManager.isEmpty(applicationContext)){
            downloadAssets()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_inventory, menu)
        menu!!.findItem(R.id.action_sysn_assets).setOnMenuItemClickListener {
            AlertDialog.Builder(this).setTitle("提示").setMessage("是否确认下载资产信息到本地？").
                    setPositiveButton("确认") { dialog, id ->
                        downloadAssets()
                    }.
                    setNegativeButton("取消"){ dialog, id ->
                        dialog.cancel();
                    }.show()

            true
        }
        menu.findItem(R.id.action_reflash).setOnMenuItemClickListener {
            refreshList()
            true
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        //改写物理返回键的逻辑
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog.Builder(this).setTitle("提示").setMessage("是否返回登录界面？").
                    setPositiveButton("确认") { dialog, id ->
                        finish()
                    }.
                    setNegativeButton("取消"){ dialog, id ->
                        dialog.cancel();
                    }.show()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun initView() {
        list.emptyView = empty_text
        list.onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
            val intent = Intent(this@InventoryPlanActivity, InventoryActivity::class.java)
            val planId = (list.adapter.getItem(position) as InventoryPlanModel).id
            val planName = (list.adapter.getItem(position) as InventoryPlanModel).name
            val planCompany = (list.adapter.getItem(position) as InventoryPlanModel).company
            intent.putExtra("planId", planId)
            intent.putExtra("planName", planName)
            intent.putExtra("planCompany", planCompany)

            startActivity(intent)
        }
    }

    fun setTaskListAdapter(items: ArrayList<InventoryPlanModel>) {
        val adapter = InventoryPlanListAdapter(applicationContext, items)
        list.adapter = adapter
    }

    fun refreshList() {
        showProgress(true)
        downloadErrorType()
        val maps = HashMap<String, String>()
        VolleyRequestUtils.requestPost(applicationContext, Config.API_SYNC_INVENTORY_PLAN, Config.API_TAG_SYNC_INVENTORY_PLAN, maps,
                object : HttpListenerInterface(applicationContext) {

                    val items = arrayListOf<InventoryPlanModel>()

                    override fun onSuccess(obj: JSONObject) {
                        val taskObjs = if (obj.has("dataList")) obj.getJSONArray("dataList")  else JSONArray()
                        for (i in 0 until taskObjs.length()) {
                            val taskObj = taskObjs.get(i) as JSONObject
                            val item = InventoryPlanModel(
                                    id = taskObj.optString("planId"),
                                    company = taskObj.optString("company"),
                                    name = taskObj.optString("name")
                            )
                            items.add(item)
                        }
                        result.code = ResultCode.SUCCESS
                    }

                    override fun onFail(json: JSONObject) {
                        Toast.makeText(context, if (json.get("message") == null) "失败！" else json.getString("message"), Toast.LENGTH_SHORT).show()
                    }

                    override fun onError(error: VolleyError) {
                        Toast.makeText(this@InventoryPlanActivity, "刷新失败，请检查网络！", Toast.LENGTH_SHORT).show()
                        Handler().postDelayed({ showProgress(false) }, 500)
                    }
                    override fun onCallBack(result: ResultModel) {
                        if (!result.isSuccess()){
                            Toast.makeText(context, result.msg, Toast.LENGTH_SHORT).show()
                        }
                        setTaskListAdapter(items)
                        Handler().postDelayed({ showProgress(false) }, 500)
                    }
                }
        )
    }

    fun downloadAssets(){
        showProgress(true, downloadingText)
        VolleyRequestUtils.requestGet(applicationContext, Config.API_DOWNLOAD_ASSETS, Config.API_TAG_DOWNLOAD_ASSETS,
                object : HttpListenerInterface(applicationContext) {
                    override fun onSuccess(obj: JSONObject) {
                        val taskObjs = if (obj.has("dataList")) obj.getJSONArray("dataList")  else JSONArray()
                        var items = arrayListOf<AssetsModel>()
                        for (i in 0 until taskObjs.length()) {
                            val taskObj = taskObjs.get(i) as JSONObject
                            val item = AssetsModel(
                                    barcode = taskObj.optString("barcode"),
                                    assetsId = taskObj.optString("assetsId"),
                                    name = taskObj.optString("name"),
                                    deptId = taskObj.optString("deptId"),
                                    dept = taskObj.optString("dept"),
                                    place = taskObj.optString("place"),
                                    user = taskObj.optString("user"),
                                    model = taskObj.optString("model"),
                                    company = taskObj.optString("company"),
                                    status = taskObj.optInt("status")>0
                            )
                            items.add(item)
                        }
                        AssetsDbManager.deleteAll(applicationContext)
                        AssetsDbManager.batchInsert(applicationContext, items)
                        AssetsDbManager.insert(applicationContext, AssetsModel(
                                barcode = "9999",
                                assetsId = "999",
                                name = "未知资产",
                                deptId = "999",
                                dept = "未知部门",
                                place = "未知地点",
                                user = "未知人员",
                                model = "未知型号",
                                company = "集团公司",
                                status = true
                        ))
                        result.code = ResultCode.SUCCESS
                        Toast.makeText(this@InventoryPlanActivity, "资产信息下载成功！", Toast.LENGTH_SHORT).show()
                    }

                    override fun onFail(json: JSONObject) {
                        Toast.makeText(context, if (json.get("message") == null) "失败！" else json.getString("message"), Toast.LENGTH_SHORT).show()
                    }

                    override fun onError(error: VolleyError) {
                        Toast.makeText(this@InventoryPlanActivity, "获取失败，请检查网络！", Toast.LENGTH_SHORT).show()
                        Handler().postDelayed({ showProgress(false, downloadingText)}, 500)
                    }
                    override fun onCallBack(result: ResultModel) {
                        if (!result.isSuccess()){
                            Toast.makeText(context, result.msg, Toast.LENGTH_SHORT).show()
                        }
                        Handler().postDelayed({ showProgress(false, downloadingText)}, 500)
                    }
                }
        )
    }

    fun downloadErrorType(){
        VolleyRequestUtils.requestGet(applicationContext, Config.API_DOWNLOAD_INVENTORY_ERROR_TYPE, Config.API_TAG_DOWNLOAD_INVENTORY_ERROR_TYPE,
                object : HttpListenerInterface(applicationContext) {
                    override fun onSuccess(obj: JSONObject) {
                        val taskObjs = if (obj.has("dataList")) obj.getJSONArray("dataList")  else JSONArray()
                        InventoryErrorTypeDbManager.deleteAll(applicationContext)
                        for (i in 0 until taskObjs.length()) {
                            val taskObj = taskObjs.get(i) as JSONObject
                            val item = InventoryErrorTypeModel(
                                    id = taskObj.optString("id"),
                                    name = taskObj.optString("name")
                            )
                            item.save(applicationContext)
                        }
                        result.code = ResultCode.SUCCESS
                    }

                    override fun onFail(json: JSONObject) {
                        Toast.makeText(context, if (json.get("message") == null) "失败！" else json.getString("message"), Toast.LENGTH_SHORT).show()
                    }

                    override fun onError(error: VolleyError) {
                        Toast.makeText(this@InventoryPlanActivity, "获取失败，请检查网络！", Toast.LENGTH_SHORT).show()
                    }
                    override fun onCallBack(result: ResultModel) {
                        if (!result.isSuccess()){
                            Toast.makeText(context, result.msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        )
    }

    fun showProgress(show: Boolean, p: View = progressBar) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime)

            list.visibility = if (show) View.GONE else View.VISIBLE
            list.animate().setDuration(shortAnimTime.toLong()).alpha(
                    (if (show) 0 else 1).toFloat()).setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    list.visibility = if (show) View.GONE else View.VISIBLE
                }
            })

            p.visibility = if (show) View.VISIBLE else View.GONE
            p.animate().setDuration(shortAnimTime.toLong()).alpha(
                    (if (show) 1 else 0).toFloat()).setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    p.visibility = if (show) View.VISIBLE else View.GONE
                }
            })
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            p.visibility = if (show) View.VISIBLE else View.GONE
            p.visibility = if (show) View.GONE else View.VISIBLE
        }
    }

}
