package com.lsinfo.wonton.ui

import android.os.Bundle
import android.support.v4.app.ListFragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import com.lsinfo.wonton.R
import com.lsinfo.wonton.adapter.InventoryErrorListAdapter
import com.lsinfo.wonton.db.AssetsDbManager
import com.lsinfo.wonton.db.InventoryErrorTypeDbManager
import com.lsinfo.wonton.model.InventoryErrorModel
import com.lsinfo.wonton.utils.ConvertHelper
import kotlinx.android.synthetic.main.fragment_inventory_assets_info.*
import android.view.LayoutInflater
import com.lsinfo.wonton.db.InventoryAssetsDbManager
import com.lsinfo.wonton.db.InventoryErrorDbManager
import com.lsinfo.wonton.model.InventoryAssetsModel
import com.lsinfo.wonton.utils.PrintUtils


class InventoryAssetsInfoFragment : ListFragment() {
    var planId: String? = null
    var planName: String? = null
    var planCompany: String? = null

    var inventoryAssets :InventoryAssetsModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (activity != null && activity is InventoryActivity) {
            planId = (activity as InventoryActivity).planId
            planName = (activity as InventoryActivity).planName
            planCompany = (activity as InventoryActivity).planCompany
        }
        return inflater.inflate(R.layout.fragment_inventory_assets_info, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (activity != null && context != null){

            val adapter = InventoryErrorListAdapter(context!!)
            listAdapter = adapter

        }
    }

    override fun onResume() {
        super.onResume()
        initToolBar()

        btnEditMemo.setOnClickListener{
            memo.visibility = if (memo.visibility == View.VISIBLE){
                btnEditMemo.setImageResource(R.drawable.edit)
                View.GONE
            }
            else{
                btnEditMemo.setImageResource(R.drawable.confirm)
                View.VISIBLE
            }
        }
    }

    fun search(str: String): Boolean{
        if (planId == null) {
            Toast.makeText(activity, "计划信息有误", Toast.LENGTH_SHORT).show()
            return false
        }

        if (str == "999"){
            inventoryAssets = null;
        }
        else {
            inventoryAssets = InventoryAssetsDbManager.queryByPlan(context!!,planId!!).find { it.assetsId == str }
        }
        if (inventoryAssets == null){
            val assets = AssetsDbManager.search(context!!,str)
            if (assets == null){
                Toast.makeText(activity, "资产搜索结果为空，请尝试同步最新资产数据后再搜索", Toast.LENGTH_SHORT).show()
                return false
            }
            inventoryAssets = ConvertHelper.assets2InventoryAssets(assets, planId!!)

            memo.setText("")
            status.isChecked = false
            save()
        }
        refreshInfo()
        return true
    }

    fun searchById(str: String): Boolean{
        if (planId == null) {
            Toast.makeText(activity, "计划信息有误", Toast.LENGTH_SHORT).show()
            return false
        }
        inventoryAssets = InventoryAssetsDbManager.queryByPlan(context!!,planId!!).find { it.id == str }
        refreshInfo()
        return true
    }

    fun save(): Boolean{
        if (inventoryAssets == null){
            Toast.makeText(context!!, "没有可保存的信息", Toast.LENGTH_SHORT).show()
            return false
        }
        try {
            inventoryAssets!!.memo = memo.text.toString()
            inventoryAssets!!.status = status.isChecked
            inventoryAssets!!.isLocal = true
            inventoryAssets!!.save(context!!)
            (listAdapter as InventoryErrorListAdapter).listItems.forEach {
                it.save(context!!)
            }
            Toast.makeText(context!!, "保存成功", Toast.LENGTH_SHORT).show()
            return true
        }
        catch (e:Exception){
            Toast.makeText(context!!, "保存失败,错误信息：${e.toString()}", Toast.LENGTH_SHORT).show()
        }
        return false
    }

    fun refreshInfo(){
        if (inventoryAssets == null){
            name.text = getString(R.string.assets_name)
            model.text = getString(R.string.model)
            dept.text = getString(R.string.department)
            user.text = getString(R.string.user)
            place.text = getString(R.string.place)
            company.text = getString(R.string.pkCorp)
            status.isChecked = false
            memo.setText("".toCharArray(), 0, "".length)
            (listAdapter as InventoryErrorListAdapter).removeAllItems()
            btnEditMemo.visibility = View.GONE
        }
        else{
            name.text = inventoryAssets!!.name
            model.text = inventoryAssets!!.model
            dept.text = inventoryAssets!!.dept
            user.text = inventoryAssets!!.user
            place.text = inventoryAssets!!.place
            company.text = inventoryAssets!!.company
            status.isChecked = inventoryAssets!!.status
            memo.setText(inventoryAssets!!.memo.toCharArray(), 0, inventoryAssets!!.memo.length)
            val errors = InventoryErrorDbManager.queryByInventoryAssetsId(context!!, inventoryAssets!!.id)

            val types = InventoryErrorTypeDbManager.getAll(context!!)
            (listAdapter as InventoryErrorListAdapter).removeAllItems()
            (listAdapter as InventoryErrorListAdapter).listItems =types.map { InventoryErrorModel(
                    inventoryAssetsId = inventoryAssets!!.id,
                    typeId = it.id,
                    correctInfo = errors.find { error -> error.typeId == it.id }?.correctInfo?:""
            ) }.toMutableList()
            btnEditMemo.visibility = View.VISIBLE
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

    private fun initSearchViewMenu(menu: Menu){
        val menuItem = menu.findItem(R.id.action_search)
        //v14 之前版本
        //        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        //v14 之后版本
        val searchView = menuItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            //文字输入完成   提交
            override fun onQueryTextSubmit(query: String): Boolean {
                val success = search(query)
                if (success){
                    searchView.onActionViewCollapsed()
                }

                return !success
            }

            //当文字发生改变
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
        //当用户点击   input
        searchView.setOnSearchClickListener(View.OnClickListener {

        })

        //一进来就是 输入框，不隐藏
        //searchView.isIconified = false
        //设置 searchView 点击 × 的时候不关闭，只进行清空
        //searchView.setIconifiedByDefault(false)
        searchView.clearFocus()

        //设置右边的图标
        val imageView = searchView.findViewById(R.id.search_go_btn) as ImageView
        imageView.setImageResource(R.drawable.scan_barcode)
        imageView.visibility = View.VISIBLE
        imageView.setOnClickListener {
            (activity as InventoryActivity).startCaptureActivityForResult()
        }
        searchView.isSubmitButtonEnabled = true
    }

    private fun initSaveMenu(menu: Menu){
        val btnSave = menu.findItem(R.id.action_save)
        btnSave.setOnMenuItemClickListener {
            if (context != null){
                save()
            }
            true
        }
    }

    private fun initDeleteMenu(menu: Menu){
        val btnDelete = menu.findItem(R.id.action_delete)
        btnDelete.setOnMenuItemClickListener {
            if (activity != null && inventoryAssets != null){
                (activity as InventoryActivity).delete(inventoryAssets!!)
            }
            true
        }
    }

    private fun initPrintMenu(menu: Menu){
        val btn = menu.findItem(R.id.action_print)
        btn.setOnMenuItemClickListener {
            if (context != null){
                save()
                PrintUtils.printSimpleTag(context!!, inventoryAssets)
            }
            true
        }
    }

    private fun initPrintSpaceMenu(menu: Menu){
        val btn = menu.findItem(R.id.action_print_space)
        btn.setOnMenuItemClickListener {
            if (context != null){
                PrintUtils.printSpace(context!!)
            }
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater:MenuInflater) {

        activity!!.menuInflater.inflate(R.menu.menu_inventory_assets_info, menu)

        initSearchViewMenu(menu)
        initSaveMenu(menu)
        initDeleteMenu(menu)
        initPrintMenu(menu)
        initPrintSpaceMenu(menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


}// Required empty public constructor
