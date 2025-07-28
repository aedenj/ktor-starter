package com.example

enum class Environment(
    private val env: String,
) {
    LOCAL("local"),
    TEST("test"), ;

    override fun toString(): String = env
}
