package com.lsinfo.wonton.adapter.Impl

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlin.properties.Delegates

/**
 * Created by G on 2018-05-07.
 */
abstract class CustomListAdapter<T>(var mContext: Context) : BaseAdapter() {
    var inflater: LayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    var listItems by Delegates.observable(mutableListOf<T>()){prop, old, new ->
        notifyDataSetChanged()
    }

    fun addItem(item: T) {
        if (!listItems.contains(item)) {
            listItems.add(0, item)
            notifyDataSetChanged()
        }
    }

    fun addItems(items: MutableList<T>) {
        for (item in items) {
            if (!listItems.contains(item)) {
                listItems.add(0, item)
            }
        }
        notifyDataSetChanged()
    }

    fun removeItem(item: T) {
        listItems.remove(item)
        notifyDataSetChanged()
    }

    fun removeItemAt(position: Int) {
        listItems.removeAt(position)
        notifyDataSetChanged()
    }

    fun removeItems(items: MutableList<T>) {
        listItems.removeAll(items)
        notifyDataSetChanged()
    }

    fun removeAllItems() {
        listItems.clear()
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return listItems.size
    }

    fun getAllItems():MutableList<T> {
        return listItems
    }

    override fun getItem(position: Int): T {
        return listItems[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    // create a new ImageView for each item referenced by the Adapter
    abstract override fun getView(position: Int, convertView: View?, parent: ViewGroup): View

}