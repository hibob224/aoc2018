package utils

import kotlin.coroutines.experimental.buildSequence

infix fun <T> Boolean.then(param: T): T? = if (this) param else null

fun Int?.orZero(): Int = this ?: 0

fun Int.isMultipleOf(x: Int): Boolean = this % x == 0

fun Long?.orZero(): Long = this ?: 0L

fun Long.isMultipleOf(x: Long): Boolean = this % x == 0L

fun <T> Sequence<T>.repeat() = buildSequence {
    while (true) {
        yieldAll(this@repeat)
    }
}