package com.amwebexpert.app.pokerplanning.service

import com.amwebexpert.app.pokerplanning.service.model.UserEstimate
import com.amwebexpert.app.pokerplanning.service.model.UserEstimateMessage
import com.amwebexpert.app.pokerplanning.service.model.UserMessageType
import java.util.*

class PokerPlanningService {
    companion object {
        val instance = PokerPlanningService()
    }

    val votesCategories: Map<String, VoteChoices> = hashMapOf(
        "fibonnacy" to VoteChoices(listOf("?", "0", "1", "2", "3", "5", "8", "13", "20", "40", "100")),
        "fibonnacy-variant-1" to VoteChoices(listOf("?", "0", "0.5", "1", "1.5", "2", "2.5", "3", "3.5", "4", "4.5", "5", "8", "13", "20", "40", "100")),
        "t-shirt" to VoteChoices(listOf("?", "S", "M", "L", "XL")),
        "t-shirt-variant-1" to VoteChoices(listOf("?", "XS", "S", "M", "L", "XL", "XXL")),
    )

    fun buildEstimateMessage(username: String, estimate: String): UserEstimateMessage {
        val estimatedAtISO8601 = String.format("%tFT%<tT.%<tLZ", Calendar.getInstance(TimeZone.getTimeZone("Z")))
        val userEstimate = UserEstimate(username = username, estimate = estimate, estimatedAtISO8601 = estimatedAtISO8601)
        return UserEstimateMessage(type = UserMessageType.VOTE, payload = userEstimate)
    }
}