package co.cashme.cashme.domain.model

data class Rate(
    val currency: Currency,
    val sellCost: Cost,
    val buyCost: Cost
) {
    class Cost(
        val value: Float,
        val profit: Profit
    )

    enum class Profit {
        BEST, MEDIUM, WORST
    }
}