package com.lsinfo.wonton.ui

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.posapi.PosApi
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.lsinfo.wonton.R
import com.lsinfo.wonton.db.AssetsDbManager
import com.lsinfo.wonton.db.InventoryAssetsDbManager
import com.lsinfo.wonton.db.InventoryErrorDbManager
import com.lsinfo.wonton.db.InventoryErrorTypeDbManager
import com.lsinfo.wonton.model.*
import com.lsinfo.wonton.receiver.Pda3506ScanReceiver
import com.lsinfo.wonton.service.Pda3506ScanService
import kotlinx.android.synthetic.main.activity_inventory.*
import java.util.ArrayList
import android.content.BroadcastReceiver
import android.content.Context
import android.content.pm.PackageManager
import android.device.ScanManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.acker.simplezxing.activity.CaptureActivity
import com.lsinfo.wonton.receiver.PdaI6200SScanReceiver


class InventoryActivity : AppCompatActivity() {

    private var tabs = arrayListOf<TabModel>()
    lateinit var planId: String
    lateinit var planName: String
    lateinit var planCompany: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)

        val model = android.os.Build.MODEL
        if(android.os.Build.MODEL == "3506") {
            val newIntent = Intent(this@InventoryActivity, Pda3506ScanService::class.java)
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startService(newIntent)

            val mFilter = IntentFilter()
            mFilter.addAction(PosApi.ACTION_POS_COMM_STATUS)
            registerReceiver(object : Pda3506ScanReceiver() {
                override fun callBack(str: String) {
                    scan(str)
                }
            }, mFilter)
        }
        if (android.os.Build.MODEL == "i6200S"){
            val mScanManager = ScanManager()
            mScanManager.openScanner()

            mScanManager.switchOutputMode(0)

            val filter = IntentFilter()
            filter.addAction("urovo.rcv.message")
            registerReceiver(object : PdaI6200SScanReceiver(){
                override fun callBack(str: String) {
                    scan(str)
                }
            }, filter)
        }



        planId = if (intent.hasExtra("planId")) intent.getStringExtra("planId") else ""
        planName = if (intent.hasExtra("planName")) intent.getStringExtra("planName") else ""
        planCompany = if (intent.hasExtra("planCompany")) intent.getStringExtra("planCompany") else ""
        initTab()

        showProgress(false)
        // TODO G 测试数据
        initTestData()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data);
        when (requestCode) {
            CaptureActivity.REQ_CODE ->
                when (resultCode) {
                    RESULT_OK ->
                        scan(data!!.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT))
                }
        }
    }

    private val REQ_CODE_PERMISSION = 0x1111
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQ_CODE_PERMISSION-> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // User agree the permission
                    startCaptureActivityForResult();
                } else {
                    // User disagree the permission
                    Toast.makeText(this, "扫描需要使用相机权限", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    fun startCaptureActivityForResult() {
        // Open Scan Activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Do not have the permission of camera, request it.
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQ_CODE_PERMISSION);
        } else {
            // Have gotten the permission
            val intent = Intent(this, CaptureActivity::class.java)
            var bundle = Bundle()
            bundle.putBoolean(CaptureActivity.KEY_NEED_BEEP, CaptureActivity.VALUE_BEEP);
            bundle.putBoolean(CaptureActivity.KEY_NEED_VIBRATION, CaptureActivity.VALUE_VIBRATION);
            bundle.putBoolean(CaptureActivity.KEY_NEED_EXPOSURE, CaptureActivity.VALUE_NO_EXPOSURE);
            bundle.putByte(CaptureActivity.KEY_FLASHLIGHT_MODE, CaptureActivity.VALUE_FLASHLIGHT_OFF);
            bundle.putByte(CaptureActivity.KEY_ORIENTATION_MODE, CaptureActivity.VALUE_ORIENTATION_AUTO);
            bundle.putBoolean(CaptureActivity.KEY_SCAN_AREA_FULL_SCREEN, CaptureActivity.VALUE_SCAN_AREA_FULL_SCREEN);
            bundle.putBoolean(CaptureActivity.KEY_NEED_SCAN_HINT_TEXT, CaptureActivity.VALUE_SCAN_HINT_TEXT);
            intent.putExtra(CaptureActivity.EXTRA_SETTING_BUNDLE, bundle);
            startActivityForResult(intent, CaptureActivity.REQ_CODE);
        }
    }

    fun changeCurrentTabByTag(tag: String){
        val fragment = supportFragmentManager.findFragmentByTag(tag)
        if (fragment != null){
            tabhost.setCurrentTabByTag(tag)
        }
    }

    private fun getTabSpecView(tab: TabModel): View {
        val view = layoutInflater.inflate(R.layout.indicator_tabspec, null)
        val tabIv = view.findViewById(R.id.tab_iv) as ImageView
        val tabTv = view.findViewById(R.id.tab_tv) as TextView
        tabIv.setImageResource(tab.iconResId)
        tabTv.setText(tab.textResId)
        return view
    }

    private fun initTab() {
        tabhost.setup(this, supportFragmentManager, R.id.realtabcontent)
        val tabInfo = TabModel(R.drawable.tab_info, R.string.tab_title_info, InventoryAssetsInfoFragment::class.java)
        val tabList = TabModel(R.drawable.tab_list, R.string.tab_title_list, InventoryAssetsListFragment::class.java)
        tabs.add(tabInfo)
        tabs.add(tabList)

        for (tab in tabs) {
            val tabSpec = tabhost.newTabSpec(getString(tab.textResId))
            tabSpec.setIndicator(getTabSpecView(tab))
            tabhost.addTab(tabSpec, tab.tabFragment, null)
        }
        tabhost.setOnTabChangedListener {
            when (it){
                getString(R.string.tab_title_info) ->{

                }
                getString(R.string.tab_title_list) ->{
                    val fragment = supportFragmentManager.findFragmentByTag(it)
                    if (fragment != null){
                        (fragment as InventoryAssetsListFragment).refreshList()
                    }
                }
            }
        }
        tabhost.tabWidget.showDividers = LinearLayout.SHOW_DIVIDER_NONE //去掉每个tab之间的分割线
    }

    fun scan(str: String){
        var assetsId = str
        if (str.split(",").size>1) assetsId = str.split(",")[1]
        val fragment = supportFragmentManager.findFragmentByTag(getString(R.string.tab_title_info))
        if (fragment != null){
            (fragment as InventoryAssetsInfoFragment).search(assetsId)
            changeCurrentTabByTag(getString(R.string.tab_title_info))
        }
    }

    fun delete(inventoryAssets: InventoryAssetsModel): Boolean{

        try {
            val inventoryAssetsListFragment = supportFragmentManager.findFragmentByTag(getString(R.string.tab_title_list))
            val inventoryAssetsInfoFragment = supportFragmentManager.findFragmentByTag(getString(R.string.tab_title_info))
            AlertDialog.Builder(this).setTitle("提示").setMessage("是否确认删除？").
                    setPositiveButton("确认") { dialog, id ->
                        try{
                            InventoryErrorDbManager.queryByInventoryAssetsId(applicationContext, inventoryAssets.id).forEach {
                                it.delete(applicationContext)
                            }
                            inventoryAssets.delete(applicationContext)
                            if (inventoryAssetsListFragment != null){
                                (inventoryAssetsListFragment as InventoryAssetsListFragment).refreshList()
                            }
                            if (inventoryAssetsInfoFragment != null && (inventoryAssetsInfoFragment as InventoryAssetsInfoFragment).inventoryAssets?.assetsId == inventoryAssets.assetsId){
                                inventoryAssetsInfoFragment.inventoryAssets = null
                                inventoryAssetsInfoFragment.refreshInfo()
                            }
                            Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show()
                        }
                        catch (e:Exception){
                            Toast.makeText(this, "删除出错，错误信息：${e.toString()}", Toast.LENGTH_SHORT).show()
                        }

                    }.
                    setNegativeButton("取消"){ dialog, id ->
                        dialog.cancel();
                    }.show()
            return true
        }
        catch (e:Exception){
            Toast.makeText(this, "删除失败,错误信息：${e.toString()}", Toast.LENGTH_SHORT).show()
        }
        return false

    }

    fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime)

            activity_main.visibility = if (show) View.GONE else View.VISIBLE
            activity_main.animate().setDuration(shortAnimTime.toLong()).alpha(
                    (if (show) 0 else 1).toFloat()).setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    activity_main.visibility = if (show) View.GONE else View.VISIBLE
                }
            })

            progressBar.visibility = if (show) View.VISIBLE else View.GONE
            progressBar.animate().setDuration(shortAnimTime.toLong()).alpha(
                    (if (show) 1 else 0).toFloat()).setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    progressBar.visibility = if (show) View.VISIBLE else View.GONE
                }
            })
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressBar.visibility = if (show) View.VISIBLE else View.GONE
            progressBar.visibility = if (show) View.GONE else View.VISIBLE
        }
    }

    private fun initTestData(){

        /*var inventoryErrorTypeItems = ArrayList<InventoryErrorTypeModel>()
        inventoryErrorTypeItems.add(InventoryErrorTypeModel("type1","部门问题" ))
        inventoryErrorTypeItems.add(InventoryErrorTypeModel("type2","人问题" ))
        inventoryErrorTypeItems.add(InventoryErrorTypeModel("type3","脑子问题" ))
        inventoryErrorTypeItems.add(InventoryErrorTypeModel("type4","奶子问题" ))
        InventoryErrorTypeDbManager.deleteAll(applicationContext)
        inventoryErrorTypeItems.forEach { it.save(applicationContext) }

        var assetsItems = ArrayList<AssetsModel>()
        assetsItems.add(AssetsModel("assetsId1","name1" ,"model1","dept1","user1","place1","1001",true))
        assetsItems.add(AssetsModel("assetsId2","name2" ,"model2","dept2","user2","place2","1001",false))
        assetsItems.add(AssetsModel("assetsId3","name3" ,"model3","dept3","user3","place3","1002",false))
        assetsItems.add(AssetsModel("assetsId4","name4" ,"model4","dept4","user4","place4","1002",true))
        AssetsDbManager.deleteAll(applicationContext)
        assetsItems.forEach { it.save(applicationContext) }

        var inventoryAssetsItems = ArrayList<InventoryAssetsModel>()
        assetsItems.forEach { inventoryAssetsItems.add(InventoryAssetsModel(planId, it.assetsId, it.name, it.model, it.dept, it.user, it.place,it.company, it.status,"aaaaa")) }
        InventoryAssetsDbManager.deleteAll(applicationContext)
        inventoryAssetsItems.forEach { it.save(applicationContext)}

        var inventoryErrorItems = ArrayList<InventoryErrorModel>()
        inventoryAssetsItems.forEach { inventoryErrorItems.add(InventoryErrorModel(it.id, "type1", "问题问题")) }
        InventoryErrorDbManager.deleteAll(applicationContext)
        inventoryErrorItems.forEach { it.save(applicationContext)}*/

    }

}
