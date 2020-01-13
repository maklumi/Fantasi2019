package com.maklumi.ui

data class InventoryItemLocation(
        var locationIndex: Int = 0,
        var itemTypeAtLocation: String = "",
        var numberItemsAtLocation: Int = 0,
        var itemNameProperty: String = ""
)