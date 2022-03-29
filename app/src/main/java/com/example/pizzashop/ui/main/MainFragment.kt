package com.example.pizzashop.ui.main

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pizzashop.adapters.PizzaListAdapter
import com.example.pizzashop.databinding.MainFragmentBinding
import com.example.pizzashop.model.Pizza
import java.lang.Exception
import androidx.recyclerview.widget.RecyclerView


class MainFragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModels()

    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("MainFragment","${activity?.intent?.getStringExtra("pizzaListLine")}")


        val finishedList = mutableListOf<Pizza>()
        activity?.intent?.let {
            val lineList = it!!.getStringExtra("pizzaListLine")

            mainViewModel.setInformationAboutServer(
                it!!.getStringExtra("ip").toString(),
                it!!.getIntExtra("portCon", 0).toInt(),
                it!!.getStringExtra("secreteCode").toString()
            )

            Log.d("MainFragment", "lineList: ${lineList}")
            val rawList = lineList!!.split('%')

            for (line in rawList) {
                val instancePizza = line.split("[cut]")

                finishedList.add(
                    Pizza(
                        instancePizza[0].toInt(),
                        instancePizza[1],
                        instancePizza[2],
                        instancePizza[3].toDouble()
                    )
                )
            }
        }

        mainViewModel.submitPizzaList(finishedList)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = PizzaListAdapter {
            mainViewModel.addItemToOrder(it, 1)
        }

        Log.d("MainFragment", "1")

        binding?.apply {
            viewModel = mainViewModel
            lifecycleOwner = viewLifecycleOwner
            recyclerView.layoutManager = LinearLayoutManager(context)
            binding.recyclerView.adapter = adapter
        }

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy < 0 && !binding.floatingActionButton.isShown()) binding.floatingActionButton.show() else if (dy > 0 && binding.floatingActionButton.isShown()) binding.floatingActionButton.hide()
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }
        })

        // Attach an observer on the pizzas list to update the UI automatically when the data
        // changes.
        mainViewModel.pizzas.observe(viewLifecycleOwner) { pizza ->
            pizza.let {
                adapter.submitList(it)
            }
        }

        binding.floatingActionButton.setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToOrderListFragment2()

            findNavController().navigate(action)
        }

        Log.d("MainFragment", "3")

    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
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