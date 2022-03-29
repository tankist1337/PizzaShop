package com.example.pizzashop

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.ClipData
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.Contacts.PhonesColumns.NUMBER_KEY
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import androidx.annotation.Nullable
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pizzashop.adapters.OrderListAdapter
import com.example.pizzashop.adapters.PizzaListAdapter
import com.example.pizzashop.databinding.FragmentOrderListBinding
import com.example.pizzashop.databinding.MainFragmentBinding
import com.example.pizzashop.model.Order
import com.example.pizzashop.ui.main.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class OrderListFragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModels()

    private var _binding: FragmentOrderListBinding? = null
    private val binding get() = _binding!!

    private lateinit var layoutBottomSheet: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOrderListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = OrderListAdapter(
            { mainViewModel.plusOneItemVm(it) },
            { mainViewModel.minusOneItemVm(it) }
        )

        // Просто привязка
        binding.apply {
            viewModel = mainViewModel
            lifecycleOwner = this@OrderListFragment
            orderListRecyclerView.layoutManager = LinearLayoutManager(context)
            binding.orderListRecyclerView.adapter = adapter
        }

        // Проверка заказа, а после и его отправка
        binding.include.placeOrderButton.setOnClickListener {
            if (mainViewModel.isDataOrderFilled(
                    binding.include.nameTextInputEdit.text.toString(),
                    binding.include.numberTextInputEdit.text.toString(),
                    binding.include.addressTextInputEdit.text.toString(),
                    mainViewModel.getItemCount()
                )
            ) {
                binding.include.nameTextField.error = null
                binding.include.numberTextField.error = null
                binding.include.addressTextField.error = null

                animateButton(binding.include.placeOrderButton)
                mainViewModel.sendOrder(
                    binding.include.nameTextInputEdit.text.toString(),
                    binding.include.numberTextInputEdit.text.toString(),
                    binding.include.addressTextInputEdit.text.toString()
                )

                MaterialAlertDialogBuilder(requireContext()).setTitle("Спасибо вам, что вы с нами!")
                    .setMessage("Ваш заказ оформлен, приятного Вам аппетита.")
                    .setOnCancelListener {
                        val i = Intent(requireContext(), LoginActivity::class.java)
                        startActivity(i)
                        activity?.finish()
                    }
                    .show()
            } else {
                if (binding.include.nameTextInputEdit.text.toString() == "") {
                    binding.include.nameTextField.error = getString(R.string.error_name)
                } else {
                    binding.include.nameTextField.error = null
                }
                if (binding.include.numberTextInputEdit.text.toString() == "") {
                    binding.include.numberTextField.error = getString(R.string.error_number)
                } else if (binding.include.numberTextInputEdit.text!!.length != 13) {
                    binding.include.numberTextField.error = getString(R.string.error_number_length)
                } else {
                    binding.include.numberTextField.error = null
                }
                if (binding.include.addressTextInputEdit.text.toString() == "") {
                    binding.include.addressTextField.error = getString(R.string.error_address)
                } else {
                    binding.include.addressTextField.error = null
                }
                if (mainViewModel.getItemCount() == 0) {
                    MaterialAlertDialogBuilder(requireContext()).setMessage("Добавте товар в корзину!")
                        .setPositiveButton(
                            "Ок, закажу минимум на 300$",
                            DialogInterface.OnClickListener { dialog, id ->
                            })
                        .show()
                }
            }
        }

        // Установка клика на bottomSheet, для его расширения
        binding.include.bottomSheetLayout.setOnClickListener {
            val bottomSheet = BottomSheetBehavior.from(it)
            if (bottomSheet.state != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
            }

        }

        // Для обновлений данных на экране с помощью наблюдателя
        val oldList = mutableListOf<Int>()
        mainViewModel.orders.value!!.forEach { oldList.add(it.count) }
        mainViewModel.orders.observe(this.viewLifecycleOwner) { order ->
            order.let {
                Log.d("OrderListFragment", "observer is working")

                adapter.submitList(order)

                if (oldList.size == order.size) {
                    order.forEachIndexed { index, order ->
                        if (order.count != oldList[index]) {
                            adapter.notifyItemChanged(index, Unit)
                        }
                    }
                    oldList.clear()
                    order.forEach { oldList.add(it.count) }
                } else {
                    oldList.clear()
                    order.forEach { oldList.add(it.count) }
                }

                binding.include.subtTextView.text =
                    getString(R.string.total_cost, mainViewModel.getItemCount())
                binding.include.totalCostTextView.text =
                    getString(R.string.sum, mainViewModel.getSubTotalPrice())
            }
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
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