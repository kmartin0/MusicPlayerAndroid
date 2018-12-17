package com.kevin.musicplayer.model

data class Track(
		var album: Album?,
		var artist: Artist?,
		var bookmark: String?,
		var trackNumber: String?,
		var year: String?,
		var data: String?,
		var dateAdded: String?,
		var dateModified: String?,
		var displayName: String?,
		var mimeType: String?,
		var size: String?,
		var title: String?
)
