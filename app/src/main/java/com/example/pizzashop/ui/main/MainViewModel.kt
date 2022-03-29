package com.example.pizzashop.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pizzashop.model.Order
import com.example.pizzashop.model.Pizza
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.Socket
import java.nio.charset.Charset
import java.text.DecimalFormat

class MainViewModel : ViewModel() {

    private val _pizzas = MutableLiveData<List<Pizza>>()
    val pizzas: LiveData<List<Pizza>> = _pizzas

    private val _orders = MutableLiveData<List<Order>>(listOf())
    val orders: LiveData<List<Order>> = _orders

    private val _codeWord = MutableLiveData<String>()
    val codeWord: LiveData<String> = _codeWord

    private val _serverIp = MutableLiveData<String>()
    val serverIp: LiveData<String> = _serverIp

    private val _portServer = MutableLiveData<Int>()
    val portServer: LiveData<Int> = _portServer

    fun submitPizzaList(pizza: MutableList<Pizza>){
        val test = pizza.toList()
        _pizzas.value = test
    }

    // The method adds pizza to the order
    fun addItemToOrder(item: Pizza, quantity: Int){
        var hasPizza = false
        var orderIndex: Int = 0

        _orders.value!!.forEachIndexed { index, order ->
            if(item.id == order.pizza.id) {
                hasPizza = true
                orderIndex = index
            }
        }

        // If the pizza has already been added to the cart,
        // the next click will increase its quantity
        if(hasPizza){
            _orders.value!![orderIndex].count += quantity
        }
        //If that pizza is not in the cart, then add it to the cart
        else {
            val mass: MutableList<Order>

            mass = _orders.value!!.toMutableList()
            mass.add(Order(item,quantity))

            _orders.value = mass.toList()
        }
    }

    // In the shopping cart adds a unit of this product
    fun plusOneItemVm(item: Order){
        Log.d("MainViewModel","PlusOneItemVm")

        _orders.value!!.forEach { if (it.pizza.id == item.pizza.id) it.count++ }

        val mutableList = _orders.value!!.toMutableList()
        _orders.value = mutableList

    }

    // In the shopping cart, it takes away a unit of this product
    fun minusOneItemVm(item: Order){
        Log.d("MainViewModel","MinusOn....")
        // The number of products cannot be negative
        if(item.count != 1){
            _orders.value!!.forEach { if (it.pizza.id == item.pizza.id) it.count-- }

            val mutableList = _orders.value!!.toMutableList()
            _orders.value = mutableList
        }
        else{

            _orders.value!!.forEach { if (it.pizza.id == item.pizza.id) {Log.d("MainViewModel","item ${item} and it ${it}")
                val mass = mutableListOf<Order>()
                orders.value!!.forEach() { mass.add(it) }
                mass.remove(item)
                _orders.value = mass.toList()
            } }
        }
    }

    fun getSubTotalPrice(): Double {
        var st = 0.0

        orders.value!!.forEach { st += it.pizza.price * it.count }

        val f = DecimalFormat("##.00")

        return st
    }

    fun getItemCount(): Int{
        var counter = 0

        orders.value!!.forEach { counter += it.count }

        return counter
    }

    fun setInformationAboutServer(inputIp: String, inputPort: Int, inputWord: String){
        _serverIp.value = inputIp
        _portServer.value = inputPort
        _codeWord.value = inputWord
    }

    fun isDataOrderFilled(name: String, number: String, address: String, countPizza: Int): Boolean{
        return name != "" && number != "" && address != "" && countPizza != 0 && number.length == 13
    }

    fun sendOrder(name: String, number: String, address: String){
        var message = "addOrder"

        message += "%$name"
        message += "%$number"
        message += "%$address"

        orders.value!!.forEach { message += "%${it.pizza.name}/${it.count}" }

        Log.d("MainViewModel","Message:$message")

        CoroutineScope(Dispatchers.IO).launch {
            sendOrderOnServer(message)
        }
    }

    private suspend fun sendOrderOnServer(message: String){
        val client = Socket(serverIp.value, portServer.value!!)
        val writer = client.getOutputStream()

        writer.write(message.toByteArray())

        writer.close()
        client.close()
    }

    fun clearAllData(){
        _pizzas.value = listOf<Pizza>()
        _orders.value = listOf<Order>()
        _serverIp.value = ""
        _portServer.value = 0
    }
}