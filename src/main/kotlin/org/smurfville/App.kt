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

    println("Last Sync: ${appSyncData.last_sync_on}, Sync After: ${appSyncData.sync_after}, Sync Before: ${appSyncData.sync_before}")

    // if first call, pass min_ts
    // for subsequent calls, pass max_ts taken from last element of previous result
    // if max_ts <= appSynData.sync_after. Stop

    val listenData: ListenBrainzListenList? = getListens("smurfpandey")
    if(listenData == null) {
        println("Something went wrong")
        return
    }

    val syncAfter = listenData.payload.listens.first().listened_at
    val syncBefore = listenData.payload.listens.last().listened_at

    var areWeDone: Boolean = false
    run saveData@{
        listenData.payload.listens.forEach {
            if (it.listened_at <= appSyncData.sync_after) {
                // we have seen this data
                areWeDone = true
                return@saveData
            }
            var releaseName = it.track_metadata.release_name
            if (releaseName == null) {
                releaseName = ""
            }
            saveListenData(it.track_metadata.track_name, it.track_metadata.artist_name, releaseName, it.listened_at)
        }
    }

    if(areWeDone) {
        updateLastSyncTime()
    } else {
        //hungry for more
    }

}
