package org.smurfville.listenbrainz

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

data class ListenBrainzListenList(
    val payload: Payload
)

data class Payload(
    val count: Int,
    val latest_listen_ts: Int,
    val listens: List<Listens>,
    val user_id: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Listens(
    val listened_at: Int,
    val recording_msid: String,
    val track_metadata: TrackMetadata,
    val user_name: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TrackMetadata(
    val artist_name: String,
    val release_name: String?,
    val track_name: String
)