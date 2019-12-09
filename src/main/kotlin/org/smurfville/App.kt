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

    var lastSyncTime: Int = syncFromListenBrainz(appSyncData, 0)

    while (lastSyncTime > 0) {
        lastSyncTime = syncFromListenBrainz(appSyncData, lastSyncTime)
    }

    updateLastSyncTime()

}

fun syncFromListenBrainz(appSyncData: AppSyncStatus, notListenAfter: Int): Int {
    val listenData: ListenBrainzListenList? = getListens("smurfpandey", notListenAfter = notListenAfter)
    if(listenData == null) {
        println("Something went wrong")
        return 0
    }

    if(listenData.payload.listens.isEmpty()) {
        println("All data synced!")
        return 0
    }

    var lastListenTime: Int = listenData.payload.listens.last().listened_at

    run saveData@{
        listenData.payload.listens.forEach {
            if (it.listened_at <= appSyncData.sync_after) {
                // we have seen this data
                // and we are done
                lastListenTime = 0
                return@saveData
            }
            var releaseName = it.track_metadata.release_name
            if (releaseName == null) {
                releaseName = ""
            }
            println("Saving: ${it.track_metadata.track_name} By: ${it.track_metadata.artist_name} Listened at: ${it.listened_at}")
            saveListenData(it.track_metadata.track_name, it.track_metadata.artist_name, releaseName, it.listened_at)
        }
    }

    return lastListenTime
}
