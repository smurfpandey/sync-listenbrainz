package org.smurfville

import org.smurfville.database.*
import org.smurfville.listenbrainz.*

fun main() {
    initDB()

    // check last sync status
    var appSyncData = getSyncStatus()

    if ( appSyncData == null ) {
        appSyncData = initAppSync()
    }

    println("Agar tum saath ho! for $appSyncData")
    val listenData: ListenBrainzListenList? = getListens("smurfpandey", listenAfter = appSyncData.sync_after)
    if(listenData == null) {
        println("Something went wrong")
        return
    }

    val syncAfter = listenData.payload.listens.first().listened_at
    val syncBefore = listenData.payload.listens.last().listened_at

    listenData.payload.listens.forEach {
        println(it)
        saveListenData(it.track_metadata.track_name, it.track_metadata.artist_name, it.track_metadata.release_name, it.listened_at)
    }

    updateLastSyncTime()
}
