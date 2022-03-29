package com.example.pizzashop

import android.util.Log
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pizzashop.adapters.OrderListAdapter
import com.example.pizzashop.adapters.PizzaListAdapter
import com.example.pizzashop.model.Order
import com.example.pizzashop.model.Pizza

@BindingAdapter("listPizzaData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Pizza>) {
    val adapter = recyclerView.adapter as PizzaListAdapter
    adapter.submitList(data)
}

@BindingAdapter("listOderData")
fun bindRecyclerViewOrder(recyclerView: RecyclerView, data: MutableList<Order>?) {
    val adapter = recyclerView.adapter as OrderListAdapter
    adapter.submitList(data)
}
