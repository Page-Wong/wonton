package com.lsinfo.wonton.adapter

import android.content.Context
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.lsinfo.wonton.Config
import com.lsinfo.wonton.R
import com.lsinfo.wonton.model.InventoryAssetsModel
import java.text.SimpleDateFormat

/**
 * Created by G on 2018-05-07.
 */
abstract class InventoryAssetsItemHolder {
    private var mContext: Context
    private var txId: TextView
    private var txName: TextView
    private var txAssetsId: TextView
    private var txModel: TextView
    private var txDept: TextView
    private var txUser: TextView
    private var txPlace: TextView
    private var txStatus: TextView
    private var txCompanyp: TextView
    private var txCreateUser: TextView
    private var txCreateTime: TextView
    private var txMemo: TextView
    private var btnDelete: ImageButton
    var item: InventoryAssetsModel? = null
        set(value) {
            field=value
            if (value != null){
                txId.text = value.assetsId
                txName.text = if (value.name.isNullOrEmpty()) "(无资产名称)" else value.name
                txAssetsId.text = value.assetsId
                txModel.text = "型号：${value.model}"
                txDept.text = if (value.dept.isNullOrEmpty()) "(无使用部门)" else value.dept
                txUser.text = if (value.user.isNullOrEmpty()) "(无使用人)" else value.user
                txPlace.text = "使用地点：${value.place}"
                txCompanyp.text = if (value.company.isNullOrEmpty()) "(无账套)" else value.company
                txCreateUser.text = value.createUser
                txCreateTime.text = SimpleDateFormat(Config.DATE_TIME_FORMAT_PATTERN).format(value.createTime)
                txMemo.text = "备注：${value.memo}"
                txStatus.visibility = if (value.status) View.VISIBLE else View.GONE
            }
        }

    constructor(convertView: View, mContext: Context) {
        this.mContext = mContext
        if (convertView.tag == null) {
            txId = convertView.findViewById(R.id.id)
            txName = convertView.findViewById(R.id.name)
            txAssetsId = convertView.findViewById(R.id.assetsId)
            txModel = convertView.findViewById(R.id.model)
            txDept = convertView.findViewById(R.id.dept)
            txUser = convertView.findViewById(R.id.user)
            txPlace = convertView.findViewById(R.id.place)
            txStatus = convertView.findViewById(R.id.status)
            txCompanyp = convertView.findViewById(R.id.company)
            txCreateUser = convertView.findViewById(R.id.createUser)
            txCreateTime = convertView.findViewById(R.id.createTime)
            txMemo = convertView.findViewById(R.id.memo)
            btnDelete = convertView.findViewById(R.id.btnDelete)
            convertView.tag = this
        } else {
            val holder = convertView.tag as InventoryAssetsItemHolder
            txId = holder.txId
            txName = holder.txName
            txAssetsId = holder.txAssetsId
            txModel = holder.txModel
            txDept = holder.txDept
            txUser = holder.txUser
            txPlace = holder.txPlace
            txStatus = holder.txStatus
            txCompanyp = holder.txCompanyp
            txCreateUser = holder.txCreateUser
            txCreateTime = holder.txCreateTime
            txMemo = holder.txMemo
            btnDelete = holder.btnDelete
        }
        btnDelete.setOnClickListener(onClickDeleteListener())
    }

    abstract fun onClickDeleteListener(): View.OnClickListener
}