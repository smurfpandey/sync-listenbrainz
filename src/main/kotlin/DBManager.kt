import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object Listens : Table() {
    val id = uuid("id").primaryKey()
    val track = varchar("track_name", length = 150) // Column<String>
    val artist = varchar("track_artist", length = 150) // Column<String>
    val album = varchar("track_album", length = 150) // Column<String>
}

object SyncStatus : Table() {
    val last_sync_on = long("last_sync_on").default(0)
}

fun initDB() {
    Database.connect("jdbc:mysql://localhost:3306/music_habits", driver = "com.mysql.jdbc.Driver", user = "root", password = "server@123")

    transaction {
        SchemaUtils.create(Listens, SyncStatus)
    }
}

fun getLastSyncTime(): Long {
    return SyncStatus.select { }.single()[SyncStatus.last_sync_on]
}