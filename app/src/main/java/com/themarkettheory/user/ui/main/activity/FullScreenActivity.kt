package com.themarkettheory.user.ui.main.activity

import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.themarkettheory.user.R
import kotlinx.android.synthetic.main.activity_full_screen.*


class FullScreenActivity : BaseActivity() {
    private var isFullscreen = false
    var videoUrl: String? = ""
    private var player: SimpleExoPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        setContentView(R.layout.activity_full_screen)

        if (intent != null) {
            videoUrl = intent.getStringExtra("videoUrl")
        }
        val mp4VideoUri: Uri = Uri.parse(videoUrl)
        player = SimpleExoPlayer.Builder(this).build()
        simpleExoPlayerView.useController = true;//set to true or false to see controllers
        simpleExoPlayerView.setShowFastForwardButton(false)
        simpleExoPlayerView.setShowPreviousButton(false)
        simpleExoPlayerView.setShowNextButton(false)
        simpleExoPlayerView.setShowRewindButton(false)
        simpleExoPlayerView.setShowRewindButton(false)
        simpleExoPlayerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
        simpleExoPlayerView.requestFocus()
        // Bind the player to the view.
        simpleExoPlayerView.player = player
        val mediaItem = MediaItem.fromUri(mp4VideoUri)
        player?.apply {
            setMediaItem(mediaItem)
            playWhenReady = true
            /*seekTo(0)*/
            prepare()
            play()
        }

        ivClose.setOnClickListener {
            onBackPressed()
        }

        player!!.addListener(object : Player.EventListener {
            override fun onTimelineChanged(timeline: Timeline, manifest: Any?, reason: Int) {}
            override fun onTracksChanged(
                trackGroups: TrackGroupArray,
                trackSelections: TrackSelectionArray
            ) {
            }

            override fun onLoadingChanged(isLoading: Boolean) {}
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playWhenReady && playbackState == Player.STATE_READY) {
                    // Active playback.
                } else if (playbackState == Player.STATE_ENDED) {
                    //The player finished playing all media

                    //Add your code here
                    player!!.setPlayWhenReady(false);
                    player!!.stop();
                    finish()
                } else if (playWhenReady) {
                    // Not playing because playback ended, the player is buffering, stopped or
                    // failed. Check playbackState and player.getPlaybackError for details.
                } else {
                    // Paused by app.
                }
            }

            override fun onRepeatModeChanged(repeatMode: Int) {}
            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {}
            override fun onPlayerError(error: ExoPlaybackException) {}
            override fun onPositionDiscontinuity(reason: Int) {
                //THIS METHOD GETS CALLED FOR EVERY NEW SOURCE THAT IS PLAYED
                //  int latestWindowIndex = player.getCurrentWindowIndex();
            }

            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {}
            override fun onSeekProcessed() {}
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        player!!.stop();
        finish()
    }
}