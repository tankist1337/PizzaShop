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
import com.example.pizzashop.databinding.ItemListBinding
import com.example.pizzashop.model.Pizza

class PizzaListAdapter(private val onItemClicked: (Pizza) -> Unit) :
    ListAdapter<Pizza, PizzaListAdapter.PizzaViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PizzaViewHolder {

        Log.d("PizzaListAdapter","OnCreateViewHolder: я работаю")

        return PizzaViewHolder(
            ItemListBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: PizzaViewHolder, position: Int) {
        val pizzaInstance = getItem(position)
        holder.bind(pizzaInstance, onItemClicked)
    }

    class PizzaViewHolder(
        private var binding: ItemListBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(pizzaInstance: Pizza, onItemClicked: (Pizza) -> Unit) {
            binding.button.setOnClickListener{
                Log.d("PizzaListAdapter","binding.button.setOnClickListener: я работаю")
                animateButton(it)
                onItemClicked(pizzaInstance)}
            binding.pizza = pizzaInstance
            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements
            binding.executePendingBindings()
        }

        // Animation
        private fun animateButton(view: View) {
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

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Pizza>() {
            override fun areItemsTheSame(oldItem: Pizza, newItem: Pizza): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Pizza, newItem: Pizza): Boolean {
                return oldItem.name == newItem.name
            }
        }
    }
}