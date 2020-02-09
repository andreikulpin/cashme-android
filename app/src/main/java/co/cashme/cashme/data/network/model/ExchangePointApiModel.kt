package co.cashme.cashme.data.network.model

class ExchangePointApiModel(
    val address: String,
    val city: Int,
    val isOpen: Boolean,
    val loadId: Int,
    val loadTimestamp: Int,
    val location: List<Double>,
    val name: String,
    val phone: String,
    val currency: Int,
    val buy: Float,
    val sell: Float,
    val time: String
)