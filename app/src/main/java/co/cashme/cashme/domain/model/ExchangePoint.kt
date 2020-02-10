package co.cashme.cashme.domain.model

data class ExchangePoint(
    val id: String,
    val city: Int,
    val address: String,
    val name: String,
    val rates: List<Rate>,
    val isOpen: Boolean,
    val loadId: Int,
    val loadTimestamp: Int,
    val location: Pair<Double, Double>,
    val phone: String,
    val time: String
)