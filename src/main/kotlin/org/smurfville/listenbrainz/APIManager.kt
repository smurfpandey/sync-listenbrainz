package org.smurfville.listenbrainz

import com.github.kittinunf.fuel.*
import com.github.kittinunf.fuel.jackson.responseObject
import com.github.kittinunf.result.Result;

private const val API_BASE_URL = "https://api.listenbrainz.org"

fun getListens(userName: String, listenAfter: Long = 0L, listenBefore: Long = 0L): ListenBrainzListenList? {
    val requestUrl: String = "$API_BASE_URL/1/user/$userName/listens"

    val (_, res, result) = Fuel.get(requestUrl).responseObject<ListenBrainzListenList>()
    return result.component1()
}