package com.example.pizzashop.adapters

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import com.example.pizzashop.databinding.ItemOrderListBinding
import com.example.pizzashop.model.Order
import android.view.animation.LinearInterpolator

import android.R.attr.scaleX
import android.content.ClipData


class OrderListAdapter(
    private val onItemAdded: (Order) -> Unit,
    private val onItemRemoved: (Order) -> Unit
) : ListAdapter<Order, OrderListAdapter.OrderViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderViewHolder {

        return OrderViewHolder(
            ItemOrderListBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: OrderListAdapter.OrderViewHolder, position: Int) {
        Log.d("OrderListAdapter", "onBindViewHolder ${getItem(position)}")
        val orderInstance = getItem(position)
        holder.bind(orderInstance, onItemAdded, onItemRemoved)
    }

    class OrderViewHolder(private var binding: ItemOrderListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            orderInstance: Order,
            onItemAdded: (Order) -> Unit,
            onItemRemoved: (Order) -> Unit
        ) {
            binding.buttonAdd.setOnClickListener {
                animateText(binding.countPizza)
                animateButton(it)
                onItemAdded(orderInstance)
            }

            binding.buttonRemove.setOnClickListener {
                animateText(binding.countPizza)
                animateButton(it)
                onItemRemoved(orderInstance)
            }

            binding.order = orderInstance
            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements
            binding.executePendingBindings()
        }

        // Animation
        private fun animateButton(view: View) {

            ObjectAnimator.ofFloat(view, View.ROTATION, 0f, 360f).apply {
                duration = 500
                interpolator = AccelerateDecelerateInterpolator()
            }.start()

        }

        // Animation
        private fun animateText(view: View) {
            val scaleX = PropertyValuesHolder.ofFloat(
                View.SCALE_X, 1f, 1.2f, 1f
            )

            val scaleY = PropertyValuesHolder.ofFloat(
                View.SCALE_Y, 1f, 1.2f, 1f
            )

            ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY).apply {
                duration = 200
                interpolator = AccelerateDecelerateInterpolator()
            }.start()
        }
    }

    fun changeCount(id: Int, currentCount: Int) {
        currentList[id].count = currentCount
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    /*override fun submitList(list: List<Order>?) {
        super.submitList(list?.let { ArrayList(it) })
    }*/

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Order>() {
            override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
                //Log.d("OrderListAdapter","areItemsTheSame: oldItem:${oldItem} ${oldItem == newItem} newItem:${newItem}")

                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
                //Log.d("OrderListAdapter","areContentsTheSame: oldItem:${oldItem.count} ${oldItem.count == newItem.count} newItem:${newItem.count}")

                return oldItem.count == newItem.count
            }
        }
    }
}