package com.lsinfo.wonton.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.lsinfo.wonton.R
import com.lsinfo.wonton.adapter.Impl.CustomListAdapter
import com.lsinfo.wonton.model.InventoryPlanModel
import java.util.ArrayList

/**
 * Created by G on 2018-05-07.
 */
class InventoryPlanListAdapter: CustomListAdapter<InventoryPlanModel> {


    fun initItems() {

        /*var items = ArrayList<InventoryPlanModel>()
        items.add(InventoryPlanModel("1","aaa" ))
        items.add(InventoryPlanModel("2","bbb" ))
        items.add(InventoryPlanModel("3","ccc" ))
        items.add(InventoryPlanModel("4","ddd" ))

        listItems = items*/
    }

    constructor(c: Context, items: ArrayList<InventoryPlanModel>): super(c) {
        //initItems();
        listItems = items
    }

    // create a new ImageView for each item referenced by the Adapter
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val holder: InventoryPlanItemHolder
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_inventory_plan, parent, false)
            holder = InventoryPlanItemHolder(convertView, mContext)
            //convertView!!.tag = holder
        } else {
            holder = convertView.tag as InventoryPlanItemHolder
        }
        holder.setItem(getItem(position))
        return convertView!!
    }
}