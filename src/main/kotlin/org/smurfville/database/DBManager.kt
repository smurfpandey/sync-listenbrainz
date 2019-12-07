package org.smurfville.database

import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.smurfville.database.Listens.index
import org.smurfville.getNowInUnix
import java.util.*

object Listens : Table() {
    val id = varchar("id", 64).clientDefault { UUID.randomUUID().toString() }.primaryKey()
    val track = varchar("track_name", length = 150).index() // Column<String>
    val artist = varchar("track_artist", length = 150).index() // Column<String>
    val album = varchar("track_album", length = 150).index() // Column<String>
    val listened_at = integer("listened_at").uniqueIndex()
}

object SyncStatus : IntIdTable() {
    val last_sync_on = long("last_sync_on").default(0)  // timestamp when last sync happened
    val sync_after = long("sync_after").default(0)  // timestamp of latest data we have
    val sync_before = long("sync_before").default(0) // timestamp of oldest data we have
}

class AppSyncStatus(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<AppSyncStatus>(SyncStatus)

    var last_sync_on by SyncStatus.last_sync_on
    var sync_after   by SyncStatus.sync_after
    var sync_before  by SyncStatus.sync_before
}

fun initDB() {
    Database.connect("jdbc:mysql://localhost:3306/music_habits", driver = "com.mysql.cj.jdbc.Driver", user = "root", password = "server@123")

    transaction {
        SchemaUtils.create(Listens, SyncStatus)
    }
}

fun saveListenData(trackName: String, artistName: String, albumName: String, listendOn: Int) {
    transaction {
        Listens.insert {
            it[track] = trackName
            it[artist] = artistName
            it[album] = albumName
            it[listened_at] = listendOn
        }
    }
}

fun initAppSync(): AppSyncStatus {
    return transaction {
        AppSyncStatus.new {
            last_sync_on = 0
            sync_after = 0
            sync_before = 0
        }
    }
}

fun getSyncStatus(): AppSyncStatus? {
    var appSyncData: AppSyncStatus? = null
    transaction {
        val queryResult =  AppSyncStatus.all()
        if (queryResult.count() > 0) {
            appSyncData = queryResult.firstOrNull()
        }
    }

    return appSyncData
}

fun updateLastSyncTime() {
    val currentTimestamp: Long = getNowInUnix()
    updateLastSyncTime(currentTimestamp)
}

fun updateLastSyncTime(syncTime: Long) {
    transaction {
        val queryResult: Query =  SyncStatus.selectAll()
        if (queryResult.count() > 0) {
            // update
            SyncStatus.update ({ SyncStatus.last_sync_on.greater(0L) }) {
                it[SyncStatus.last_sync_on] = syncTime
            }
        } else {
            // insert
            SyncStatus.insert {
                it[last_sync_on] = syncTime
            }
        }
    }
}

fun updateSyncStatus(lastSyncOn: Long, syncAfter: Long, syncBefore: Long) {
    transaction {
        val queryResult: Query =  SyncStatus.selectAll()
        if (queryResult.count() > 0) {
            // update
            SyncStatus.update ({ SyncStatus.last_sync_on.greater(0L) }) {
                it[SyncStatus.last_sync_on] = lastSyncOn
            }
        } else {
            // insert
            SyncStatus.insert {
                it[last_sync_on] = lastSyncOn
            }
        }
    }
}