package com.lsinfo.wonton.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.lsinfo.wonton.R
import com.lsinfo.wonton.adapter.Impl.CustomListAdapter
import com.lsinfo.wonton.model.InventoryAssetsModel

/**
 * Created by G on 2018-05-07.
 */
abstract class InventoryAssetsListAdapter: CustomListAdapter<InventoryAssetsModel> {


    /*fun initItems() {
        var items = ArrayList<InventoryAssetsModel>()
        items.add(InventoryAssetsModel("1","aaa" ))
        items.add(InventoryAssetsModel("2","bbb" ))
        items.add(InventoryAssetsModel("3","ccc" ))
        items.add(InventoryAssetsModel("4","ddd" ))
        listItems = items
    }*/

    constructor(c: Context, items: MutableList<InventoryAssetsModel> = arrayListOf()): super(c) {
        listItems = items
    }

    // create a new ImageView for each item referenced by the Adapter
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val holder: InventoryAssetsItemHolder
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_assets, parent, false)
            holder = object : InventoryAssetsItemHolder(convertView, mContext){
                override fun onClickDeleteListener(): View.OnClickListener {
                    return this@InventoryAssetsListAdapter.onClickDeleteListener(position)
                }
            }
            //convertView!!.tag = holder
        } else {
            holder = convertView.tag as InventoryAssetsItemHolder
        }
        holder.item = getItem(position)

        if(!getItem(position).isLocal){
            convertView?.setBackgroundResource(R.color.gainsboro)
        }
        else{
            convertView?.setBackgroundResource(R.color.white)
        }
        return convertView!!
    }

    abstract fun onClickDeleteListener(position: Int): View.OnClickListener
}