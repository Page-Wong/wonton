package com.lsinfo.wonton.ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.support.v4.app.ListFragment
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.AdapterView
import android.widget.Toast
import com.android.volley.VolleyError
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.lsinfo.wonton.Config
import com.lsinfo.wonton.R
import com.lsinfo.wonton.adapter.InventoryAssetsListAdapter
import com.lsinfo.wonton.db.InventoryAssetsDbManager
import com.lsinfo.wonton.db.InventoryErrorDbManager
import com.lsinfo.wonton.model.InventoryAssetsModel
import com.lsinfo.wonton.model.InventoryErrorModel
import com.lsinfo.wonton.model.ResultCode
import com.lsinfo.wonton.model.ResultModel
import com.lsinfo.wonton.utils.ConvertHelper
import com.lsinfo.wonton.utils.HttpListenerInterface
import com.lsinfo.wonton.utils.VolleyRequestUtils
import kotlinx.android.synthetic.main.fragment_inventory_assets_list.*
import kotlinx.android.synthetic.main.item_assets.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat

class InventoryAssetsListFragment : ListFragment() {
    var planId: String? = null
    var planName: String? = null
    var planCompany: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (context != null){
           val adapter = object :InventoryAssetsListAdapter(context!!){
                override fun onClickDeleteListener(position: Int): View.OnClickListener {
                    return View.OnClickListener() {
                        if (count >= position && planId != null){
                            (activity as InventoryActivity).delete(getItem(position))
                        }
                    }
                }
            }
            listAdapter = adapter
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (activity != null && activity is InventoryActivity) {
            planId = (activity as InventoryActivity).planId
            planName = (activity as InventoryActivity).planName
            planCompany = (activity as InventoryActivity).planCompany
        }
        return inflater.inflate(R.layout.fragment_inventory_assets_list, container, false)
    }

    override fun onResume() {
        super.onResume()
        initToolBar()
        refreshList()

        list.emptyView = empty_text
        list.onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
            val fragment = activity!!.supportFragmentManager.findFragmentByTag(getString(R.string.tab_title_info))
            if (fragment != null){
                (fragment as InventoryAssetsInfoFragment).searchById((list.adapter.getItem(position) as InventoryAssetsModel).id)
                (activity as InventoryActivity).changeCurrentTabByTag(getString(R.string.tab_title_info))
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity!!.menuInflater.inflate(R.menu.menu_inventory_assets_list, menu)
        initUploadMenu(menu)
        initDownloadInventoryAssetsMenu(menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    private fun initUploadMenu(menu: Menu){
        val btnUpload = menu.findItem(R.id.action_upload)
        btnUpload.setOnMenuItemClickListener {
            (activity as InventoryActivity).showProgress(true)
            var list : MutableList<HashMap<String, String?>> = (listAdapter as InventoryAssetsListAdapter).listItems.filter { it.isLocal }.map {
                val str = GsonBuilder().setDateFormat(Config.DATE_TIME_FORMAT_PATTERN).create().toJson(it)
                var map = GsonBuilder().setDateFormat(Config.DATE_TIME_FORMAT_PATTERN).create().fromJson(str, HashMap<String, String?>()::class.java)
                map["errors"] = Gson().toJson(InventoryErrorDbManager.queryByInventoryAssetsId(context!!, it.id))
                map
            }.toMutableList()

            val maps = HashMap<String, String>()
            maps["planId"] = planId?:""
            maps["deviceId"] = Settings.Secure.getString(activity!!.contentResolver, Settings.Secure.ANDROID_ID)
            maps["dataList"] = GsonBuilder().setDateFormat(Config.DATE_TIME_FORMAT_PATTERN).create().toJson(list)
            /*maps["assetsList"] = GsonBuilder().setDateFormat(Config.DATE_TIME_FORMAT_PATTERN).create().toJson((listAdapter as InventoryAssetsListAdapter).listItems)
            maps["errorList"] = GsonBuilder().setDateFormat(Config.DATE_TIME_FORMAT_PATTERN).create().toJson((listAdapter as InventoryAssetsListAdapter).listItems.map { Gson().toJson(InventoryErrorDbManager.queryByInventoryAssetsId(context!!, it.id).filter { a -> !a.correctInfo.isNullOrEmpty() }) })*/
            VolleyRequestUtils.requestPost(context!!, Config.API_UPLOAD_INVENTORY_ASSETS, Config.API_TAG_UPLOAD_INVENTORY_ASSETS, maps,
                    object : HttpListenerInterface(context!!) {

                        override fun onSuccess(obj: JSONObject) {
                            (listAdapter as InventoryAssetsListAdapter).listItems.filter { it.isLocal }.forEach {
                                it.isLocal=false
                                it.save(context)
                            }
                            android.support.v7.app.AlertDialog.Builder(context!!).setTitle("上传成功")
                                    .setMessage("${if (obj.get("message") != "") "提示：\n${obj.get("message")}" else ""}").setPositiveButton("确定", null).show();
                            refreshList()
                            result.code = ResultCode.SUCCESS
                        }

                        override fun onFail(json: JSONObject) {
                            Toast.makeText(context, if (json.get("message") == null) "上传失败！" else json.getString("message"), Toast.LENGTH_SHORT).show()
                        }

                        override fun onError(error: VolleyError) {
                            Toast.makeText(context, "上传失败，请检查网络！", Toast.LENGTH_SHORT).show()
                            Handler().postDelayed({ (activity as InventoryActivity).showProgress(false) }, 500)
                        }
                        override fun onCallBack(result: ResultModel) {
                            if (!result.isSuccess()){
                                Toast.makeText(context, result.msg, Toast.LENGTH_SHORT).show()
                            }
                            Handler().postDelayed({ (activity as InventoryActivity).showProgress(false) }, 500)
                        }
                    }
            )
            true
        }

    }

    private fun initDownloadInventoryAssetsMenu(menu: Menu){
        val btn = menu.findItem(R.id.action_downloadInventoryAssets)
        btn.setOnMenuItemClickListener {
            (activity as InventoryActivity).showProgress(true)
            val maps = HashMap<String, String>()
            maps["planId"] = planId?:""
            VolleyRequestUtils.requestPost(context!!, Config.API_DOWNLOAD_INVENTORY_ASSETS, Config.API_TAG_DOWNLOAD_INVENTORY_ASSETS, maps,
                    object : HttpListenerInterface(context!!) {

                        override fun onSuccess(obj: JSONObject) {
                            var localInventoryAssets = InventoryAssetsDbManager.queryByPlan(context, this@InventoryAssetsListFragment.planId!!)
                            localInventoryAssets.filter { !it.isLocal }.forEach {
                                val errors = InventoryErrorDbManager.queryByInventoryAssetsId(context, it.id)
                                errors.forEach { it.delete(context) }
                                it.delete(context)
                            }
                            val taskObjs = if (obj.has("dataList")) obj.getJSONArray("dataList")  else JSONArray()
                            for (i in 0 until taskObjs.length()) {
                                val taskObj = taskObjs.get(i) as JSONObject
                                if (localInventoryAssets.none { it.isLocal && (it.assetsId== taskObj.optString("assetsId") || it.id == taskObj.optString("id"))}){
                                    val item = InventoryAssetsModel(
                                            id = taskObj.optString("id"),
                                            isLocal = false,
                                            planId = taskObj.optString("planId"),
                                            assetsId = taskObj.optString("assetsId"),
                                            name = taskObj.optString("name"),
                                            deptId = taskObj.optString("deptId"),
                                            dept = taskObj.optString("dept"),
                                            place = taskObj.optString("place"),
                                            user = taskObj.optString("user"),
                                            model = taskObj.optString("model"),
                                            company = taskObj.optString("company"),
                                            memo = taskObj.optString("memo"),
                                            createUser = taskObj.optString("createUser"),
                                            createTime = SimpleDateFormat(Config.DATE_TIME_FORMAT_PATTERN).parse(taskObj.optString("createTime")),
                                            status = taskObj.optInt("status")>0
                                    )
                                    item.save(context)
                                    var errors = ConvertHelper.gson.fromJson(taskObj.optString("errors"), arrayOf<InventoryErrorModel>()::class.java)

                                    errors.forEach {
                                        it.save(context)
                                    }
                                }
                            }
                            refreshList()
                            result.code = ResultCode.SUCCESS
                        }

                        override fun onFail(json: JSONObject) {
                            Toast.makeText(context, if (json.get("message") == null) "下载失败！" else json.getString("message"), Toast.LENGTH_SHORT).show()
                        }

                        override fun onError(error: VolleyError) {
                            Toast.makeText(context, "下载失败，请检查网络！", Toast.LENGTH_SHORT).show()
                            Handler().postDelayed({ (activity as InventoryActivity).showProgress(false) }, 500)
                        }
                        override fun onCallBack(result: ResultModel) {
                            if (!result.isSuccess()){
                                Toast.makeText(context, result.msg, Toast.LENGTH_SHORT).show()
                            }
                            Handler().postDelayed({ (activity as InventoryActivity).showProgress(false) }, 500)
                        }
                    }
            )
            true
        }

    }

    private fun initToolBar(){
        setHasOptionsMenu(true)
        toolbar.title = planCompany?:getString(R.string.title_activity_inventory_info)
        toolbar.subtitle = planName?:""
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        //监听 Navigatio
        toolbar.setNavigationOnClickListener {  }
        //监听菜单点击
        toolbar.setOnMenuItemClickListener { false }
    }

    fun refreshList() {
        if (context == null || planId == null) return
        var items = InventoryAssetsDbManager.queryByPlan(context!!, planId!!)
        (listAdapter as InventoryAssetsListAdapter).listItems = items

    }
}
