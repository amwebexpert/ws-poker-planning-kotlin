package com.amwebexpert.app.pokerplanning.service.model

import kotlinx.serialization.Serializable

@Serializable
data class PokerPlanningSession(val version: String, val lastUpdateISO8601: String, val estimates: List<UserEstimate>)
