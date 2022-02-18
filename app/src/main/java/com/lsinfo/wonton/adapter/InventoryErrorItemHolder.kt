package com.lsinfo.wonton.adapter

import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.lsinfo.wonton.R
import com.lsinfo.wonton.model.InventoryErrorModel

/**
 * Created by G on 2018-05-07.
 */
class InventoryErrorItemHolder {
    private var mContext: Context
    private var txTypeName: TextView
    private var txCorrectInfo: EditText
    var item: InventoryErrorModel? = null
        set(value) {
            field=value
            if (value != null){
                txTypeName.text = value.getErrorType(mContext)?.name
                txCorrectInfo.setText((value.correctInfo?:"").toCharArray(), 0, (value.correctInfo?:"").length)
            }
        }

    @RequiresApi(Build.VERSION_CODES.M)
    constructor(convertView: View, mContext: Context) {
        this.mContext = mContext
        if (convertView.tag == null) {
            txTypeName = convertView.findViewById(R.id.typeName)
            txCorrectInfo = convertView.findViewById(R.id.edtErrorMsg)
            convertView.tag = this
        } else {
            val holder = convertView.tag as InventoryErrorItemHolder
            txTypeName = holder.txTypeName
            txCorrectInfo = holder.txCorrectInfo
        }

        txTypeName.keyListener = null
        txCorrectInfo.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(str: Editable?) {
                if (str.isNullOrEmpty()){
                    txTypeName.setTextColor(ContextCompat.getColor(mContext, R.color.black))
                }
                else{
                    txTypeName.setTextColor(ContextCompat.getColor(mContext, R.color.red))
                }
                item?.correctInfo = str?.toString()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })
    }

}