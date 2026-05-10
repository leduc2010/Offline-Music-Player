package com.duc.offlinemusicplayer.ads

private const val TEST_INTER = "ca-app-pub-3940256099942544/1033173712"
private const val TEST_NATIVE = "ca-app-pub-3940256099942544/2247696110"
private const val TEST_OPEN = "ca-app-pub-3940256099942544/3419835294"
private const val TEST_BANNER = "ca-app-pub-3940256099942544/6300978111"

enum class AdPlacement(val key: String, val id: String) {
    INTER_RESUME("inter_resume", TEST_INTER),
    INTER_BACK("inter_back", TEST_INTER),
    NATIVE_FULL("native_full", TEST_NATIVE),

    INTER_SPLASH("inter_splash", TEST_INTER),
    NATIVE_FULL_SPLASH("native_full_splash", TEST_NATIVE),
    INTER_SPLASH_BACKUP("inter_splash_backup", TEST_INTER),
    BANNER_SPLASH("banner_splash", TEST_BANNER),

    NATIVE_LANGUAGE("native_language", TEST_NATIVE),
    NATIVE_CLICK("native_click", TEST_NATIVE),

    NATIVE_OBD_1("native_obd_1", TEST_NATIVE),
    NATIVE_OBD_FULL_1("native_obd_full_1", TEST_NATIVE),
    NATIVE_OBD_2("native_obd_2", TEST_NATIVE),
    NATIVE_OBD_FULL_2("native_obd_full_2", TEST_NATIVE),
    NATIVE_OBD_3("native_obd_3", TEST_NATIVE),

    NATIVE_COLLAP_HOME("native_collap_home", TEST_NATIVE),
    INTER_SCAN_SONG("inter_scan_song", TEST_INTER),
    INTER_SCAN_DONE("inter_scan_done", TEST_INTER),
    INTER_PLAYLIST("inter_playlist", TEST_INTER),
    
    NATIVE_COLLAP_SONGS_CHANGECOVER_1("native_collap_songs_changecover_1", TEST_NATIVE),
    NATIVE_COLLAP_SONGS_CHANGECOVER_2("native_collap_songs_changecover_2", TEST_NATIVE),
    INTER_CHANGE_COVER_DONE("inter_change_cover_done", TEST_INTER),

    NATIVE_COLLAP_SEARCH("native_collap_search", TEST_NATIVE),
    NATIVE_COLLAP_QUEUE("native_collap_queue", TEST_NATIVE),

    NATIVE_COLLAP_LYRICS("native_collap_lyrics", TEST_NATIVE),
    NATIVE_COLLAP_ENTER_LYRICS("native_collap_enter_lyrics", TEST_NATIVE),
    INTER_LYRICS_SEARCH("inter_lyrics_search", TEST_INTER),
    INTER_LYRICS_IMPORT("inter_lyrics_import", TEST_INTER),
    INTER_LYRICS_ENTER("inter_lyrics_enter", TEST_INTER),

    NATIVE_COLLAP_PLAYING_NOW("native_collap_playing_now", TEST_NATIVE),
    INTER_SET_RINGTONE_DONE("inter_set_ringtone_done", TEST_INTER),

    NATIVE_COLLAP_PLAYLIST_EDIT_NAME("native_collap_playlist_edit_name", TEST_NATIVE),
    NATIVE_COLLAP_PLAYLIST_ADD_SONG("native_collap_playlist_add_song", TEST_NATIVE),
    NATIVE_COLLAP_VISUALIZER_LIST("native_collap_visualizer_list", TEST_NATIVE),
    NATIVE_COLLAP_VISUALIZER_COLLECTION("native_collap_visualizer_collection", TEST_NATIVE),
    NATIVE_COLLAP_SCAN("native_collap_scan", TEST_NATIVE),
    NATIVE_COLLAP_EQUALIZER("native_collap_equalizer", TEST_NATIVE),
    INTER_EQUALIZER_USE("inter_equalizer_use", TEST_INTER),

    NATIVE_UNINSTALL("native_uninstall", TEST_NATIVE);

    companion object {
        fun fromKey(key: String): AdPlacement? = entries.find { it.key == key }
    }
}
