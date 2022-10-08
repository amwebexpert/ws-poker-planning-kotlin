package com.amwebexpert.app.pokerplanning.service

data class VoteChoices(val choices: List<String>) {
    override fun toString(): String = this.choices.joinToString(", ")
}
