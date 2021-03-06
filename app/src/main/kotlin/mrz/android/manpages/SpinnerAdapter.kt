package mrz.android.manpages

import android.widget.BaseAdapter
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.content.Context
import android.widget.TextView
import java.util.ArrayList

public open class SpinnerAdapter(
        val context: Context,
        val spinnerLayout: Int = android.R.layout.simple_spinner_item,
        val itemLayout: Int = android.R.layout.simple_spinner_dropdown_item) : BaseAdapter(), android.widget.SpinnerAdapter {

    private var mItems: ArrayList<CharSequence>? = ArrayList()

    override fun getCount(): Int {
        return mItems?.size() ?: 0
    }

    override fun getItem(position: Int): Any? {
        return mItems?.get(position)
    }

    fun clear() {
        mItems?.clear()
    }

    fun setItems(items: List<CharSequence>?) {
        clear()
        mItems?.addAll(items)

        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        return populateView(position,
                convertView ?: LayoutInflater.from(context).inflate(spinnerLayout, parent, false))
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        return populateView(position,
                convertView ?: LayoutInflater.from(context).inflate(itemLayout, parent, false))
    }

    private fun populateView(position: Int, view: View?): View? {
        var viewHolder = view?.getTag() as ViewHolder? ?: ViewHolder(view?.findViewById(android.R.id.text1) as TextView?)

        viewHolder.textView?.setText(mItems?.get(position))

        return view
    }

    class ViewHolder(val textView: TextView?)
}

