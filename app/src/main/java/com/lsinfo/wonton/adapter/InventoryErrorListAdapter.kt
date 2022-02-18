package com.lsinfo.wonton.adapter

import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.view.View
import android.view.ViewGroup
import com.lsinfo.wonton.R
import com.lsinfo.wonton.adapter.Impl.CustomListAdapter
import com.lsinfo.wonton.model.InventoryErrorModel
import java.util.*

/**
 * Created by G on 2018-05-07.
 */
class InventoryErrorListAdapter: CustomListAdapter<InventoryErrorModel> {


    /*fun initItems() {
        var items = ArrayList<InventoryErrorModel>()
        items.add(InventoryErrorModel("1","aaa" ,typeName = "aaa", typeId = "11"))
        items.add(InventoryErrorModel("2","bbb" ,typeName = "bbb", typeId = "22" ))
        items.add(InventoryErrorModel("3","ccc" ,typeName = "ccc", typeId = "33" ))
        items.add(InventoryErrorModel("4","ddd" ,typeName = "ddd", typeId = "44" ))
        listItems = items
    }*/

    constructor(c: Context, items: MutableList<InventoryErrorModel> = arrayListOf()): super(c) {
        //initItems()
        listItems = items
    }

    // create a new ImageView for each item referenced by the Adapter
    @RequiresApi(Build.VERSION_CODES.M)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val holder: InventoryErrorItemHolder
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_inventory_error, parent, false)
            holder = InventoryErrorItemHolder(convertView, mContext)
        } else {
            holder = convertView.tag as InventoryErrorItemHolder
        }
        holder.item = getItem(position)
        return convertView!!
    }

}