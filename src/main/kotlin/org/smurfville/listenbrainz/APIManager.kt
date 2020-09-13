package org.smurfville.listenbrainz

import com.github.kittinunf.fuel.*
import com.github.kittinunf.fuel.jackson.responseObject

private const val API_BASE_URL = "https://api.listenbrainz.org"
private const val MAX_RESULT = "100"

/*
args:
    username: User to get listen history of.
    notListenBefore(max_ts): History before this timestamp will not be returned. DO NOT USE WITH max_ts.
    notListenAfter(min_ts): History after this timestamp will not be returned. DO NOT USE WITH min_ts.
    count: How many listens to return. If not specified, uses a default from the server.
 */
fun getListens(userName: String, notListenBefore: Int = 0, notListenAfter: Int = 0): ListenBrainzListenList? {
    // validate
    if(userName.trim().isEmpty()) {
        throw Exception("userName is required")
    }
    if(notListenBefore > 0 && notListenAfter > 0) {
        throw Exception("Only 1 timestamp is allowed.")
    }

    val requestUrl: String = "$API_BASE_URL/1/user/${userName.trim()}/listens"
    var requestParam: List<Pair<String, String>>? = null
    if(notListenBefore > 0) {
        requestParam = listOf("min_ts" to notListenBefore.toString(), "count" to MAX_RESULT)
    }
    if(notListenAfter > 0) {
        requestParam = listOf("max_ts" to notListenAfter.toString(), "count" to MAX_RESULT)
    }

    if(requestParam == null) {
        requestParam = listOf("count" to MAX_RESULT)
    }

    val (_, res, result) = Fuel.get(requestUrl, requestParam).responseObject<ListenBrainzListenList>()
    return result.component1()
}