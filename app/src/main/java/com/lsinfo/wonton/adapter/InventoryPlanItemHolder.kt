package com.lsinfo.wonton.adapter

import android.content.Context
import android.view.View
import android.widget.TextView
import com.lsinfo.wonton.R
import com.lsinfo.wonton.model.InventoryPlanModel
import kotlinx.android.synthetic.main.item_inventory_plan.*

/**
 * Created by G on 2018-05-07.
 */
class InventoryPlanItemHolder {
    private var mContext: Context
    private var txId: TextView
    private var txName: TextView


    constructor(convertView: View, mContext: Context) {
        this.mContext = mContext
        if (convertView.tag == null) {
            txId = convertView.findViewById(R.id.id)
            txName = convertView.findViewById(R.id.name)
            convertView.tag = this
        } else {
            val holder = convertView.tag as InventoryPlanItemHolder
            txId = holder.txId
            txName = holder.txName
        }

        txId.keyListener = null
        txName.keyListener = null
    }

    fun setItem(item: InventoryPlanModel) {
        txId.text = item.id
        txName.text = "${item.company} ${item.name}"
    }
}