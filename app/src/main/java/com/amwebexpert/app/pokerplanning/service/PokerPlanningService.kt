package com.amwebexpert.app.pokerplanning.service

// singleton service
object PokerPlanningService {
    val votesCategories: Map<String, VoteChoices> = hashMapOf(
        "fibonnacy" to VoteChoices(listOf("?", "0", "1", "2", "3", "5", "8", "13", "20", "40", "100")),
        "fibonnacy-variant-1" to VoteChoices(listOf("?", "0", "0.5", "1", "1.5", "2", "2.5", "3", "3.5", "4", "4.5", "5", "8", "13", "20", "40", "100")),
        "t-shirt" to VoteChoices(listOf("?", "S", "M", "L", "XL")),
        "t-shirt-variant-1" to VoteChoices(listOf("?", "XS", "S", "M", "L", "XL", "XXL")),
    )
}