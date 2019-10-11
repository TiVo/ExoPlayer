package com.google.android.exoplayer2.trickplay;// Copyright 2010 TiVo Inc.  All rights reserved.

import android.content.Context;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.util.MediaClock;

/**
 * Control interface for entering/leaving high speed playback mode using the {{@link #setTrickMode(TrickMode)}}
 * call.
 *
 * To use trick play, create the implementation using the {@link TrickPlayControlFactory#createTrickPlayControl(DefaultTrackSelector)}
 * call.
 */
public interface TrickPlayControl {

    /**
     * Create factory for Renderers that are suitable for use with trick-play.
     *
     * The TrickPlayControl must create the Renderers for ExoPlayer when using TrickPlayControl
     * this allows control of frame-rate during trick playback.
     *
     * @param context
     * @return RenderersFactory you can pass to {@link ExoPlayerFactory#newSimpleInstance(Context, RenderersFactory, TrackSelector)}
     */
    RenderersFactory createRenderersFactory(Context context);

    /**
     * Create a LoadControl instance that wraps the 'delegate' LoadControl by adding support
     * for behaviors required for trick play
     *
     * @param delegate
     * @return
     */
    LoadControl createLoadControl(LoadControl delegate);

    /**
     * Before the trickplay control can be used it must be bound to a player instance.
     *
     * @param player the current player to bind to the control, any previously set player is released.
     */
    void setPlayer(SimpleExoPlayer player);

    /**
     * Get the current playing mode.
     *
     * @return TrickMode that is currently playing.
     */
    TrickMode getCurrentTrickMode();

    /**
     * If the trickplay event has signaled metadata is valid or {@link #isMetadataValid()} is
     * true then this value is valid and indicates if high speed trickplay (I-Frames) are supported
     *
     * @return true if the current playing {@link com.google.android.exoplayer2.source.MediaSource} has
     *         an i-frame only playlist
     */
    boolean isSmoothPlayAvailable();

    /**
     * This value indicates the current speeds for the trickplay mode ({@link #getSpeedFor(TrickMode)}
     * and the {@link #isSmoothPlayAvailable()} flags are valid.
     *
     * Clients can poll for this or listen for the event {@link TrickPlayEventListener#playlistMetadataValid(boolean)}
     *
     * @return true if the metadata has been determined
     */
    boolean isMetadataValid();

    /**
     * Set the TrickMode to "newMode", this will immediately change the playback speed.
     *
     * When the transition exits trick-play (newMode is {@see TrickMode.NORMAL}) the player
     * returns the number of recorded trick-play frame times
     *
     * @param newMode
     * @return number of saved trick-play frame times
     */
    int setTrickMode(TrickMode newMode);

    /**
     * Seeks to the last Nth played trick-play frame.  Only valid after a transition form
     * a trick-play mode to normal ({@see TrickMode.NORMAL}) playback mode.
     *
     * This API is intended to allow selecting N frames back from the stop point for overshoot
     * correction, or for a keyevent selected skip back feature
     *
     * @param frameNumber
     * @return true if the seek was possible (saved frames, and at least frameNumber frames were saved)
     */
    boolean seekToNthPlayedTrickFrame(int frameNumber);

    /**
     * Get the acutal playback speed represented by the {@see TrickMode}, mode.
     *
     * This will reflect the availability of high speed playback support (eg via IFrame only playlist)
     * once the {@see TrickPlayEventListener} signals that high speed support was enabled.  Prior
     * to this default speeds are returned.
     *
     *
     * @param mode
     * @return
     */
    Float getSpeedFor(TrickMode mode);

    /**
     * Add event listener for changes to trickplay state.  Called back with Handler for the
     * application thread (that started the Player, {@link ExoPlayer#getApplicationLooper()})
     *
     * @param eventListener - listener to call back.
     */
    void addEventListener(TrickPlayEventListener eventListener);

    /**
     * Remove previously added event listener.  Note, setting a new player ({@link #setPlayer(SimpleExoPlayer)} clears
     * all previously added listeners automatically.
     *
     * @param eventListener - listener instance previously added.
     */
    void removeEventListener(TrickPlayEventListener eventListener);

    /**
     * Trick modes include normal playback and one of 3 fast modes forward and reverse.   The first fast-modes
     * are within the range to allow for playback with changing the source of {@link MediaClock}
     */
    enum TrickMode {
        FF1, FF2, FF3, NORMAL, FR1, FR2, FR3
    }
}
