package utils

import kotlin.coroutines.experimental.buildSequence

infix fun <T> Boolean.then(param: T): T? = if (this) param else null

fun <T> Sequence<T>.repeat() = buildSequence {
    while (true) {
        yieldAll(this@repeat)
    }
}