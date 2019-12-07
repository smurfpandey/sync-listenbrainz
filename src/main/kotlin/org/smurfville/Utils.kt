package org.smurfville

fun getNowInUnix(): Long {
    return System.currentTimeMillis() / 1000L
}