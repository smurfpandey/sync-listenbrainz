package org.smurfville.listenbrainz

import com.github.kittinunf.fuel.*
import com.github.kittinunf.fuel.jackson.responseObject
import com.github.kittinunf.result.Result;

private const val API_BASE_URL = "https://api.listenbrainz.org"

/*
args:
    username: User to get listen history of.
    listenBefore(max_ts): History before this timestamp will not be returned. DO NOT USE WITH max_ts.
    listenAfter(min_ts): History after this timestamp will not be returned. DO NOT USE WITH min_ts.
    count: How many listens to return. If not specified, uses a default from the server.
 */
fun getListens(userName: String, listenAfter: Long = 0L, listenBefore: Long = 0L): ListenBrainzListenList? {
    // validate
    if(userName.trim().isEmpty()) {
        throw Exception("userName is required")
    }
    if(listenAfter > 0 && listenBefore > 0) {
        throw Exception("Only 1 timestamp is allowed.")
    }

    val requestUrl: String = "$API_BASE_URL/1/user/${userName.trim()}/listens"
    var requestParam: List<Pair<String, String>>? = null
    if(listenBefore > 0) {
        requestParam = listOf("min_ts" to listenBefore.toString(), "count" to "75")
    }
    if(listenAfter > 0) {
        requestParam = listOf("max_ts" to listenAfter.toString(), "count" to "75")
    }

    if(requestParam == null) {
        requestParam = listOf("count" to "75")
    }

    val (_, res, result) = Fuel.get(requestUrl, requestParam).responseObject<ListenBrainzListenList>()
    return result.component1()
}