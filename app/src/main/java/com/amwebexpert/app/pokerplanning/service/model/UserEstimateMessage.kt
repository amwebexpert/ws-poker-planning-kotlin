package com.amwebexpert.app.pokerplanning.service.model

import kotlinx.serialization.Serializable

@Serializable
data class UserEstimateMessage(val type: UserMessageType, val payload: UserEstimate)
