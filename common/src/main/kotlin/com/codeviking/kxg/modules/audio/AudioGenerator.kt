package com.codeviking.kxg.modules.audio

import com.codeviking.kxg.KxgContext
import com.codeviking.kxg.util.Float32Buffer

/**
 * AudioGenerator plays audio from the given sample generator function. The generator
 * function is provided with the current time step in seconds (i.e. 1 / sample-rate) and is expected to compute one
 * audio sample at a time. The returned sample should be in the range -1f .. 1f.
 *
 * @author fabmax
 */
expect class AudioGenerator(ctx: KxgContext, generatorFun: AudioGenerator.(Float) -> Float) {

    val sampleRate: Float
    var isPaused: Boolean

    fun stop()

    fun enableFftComputation(nSamples: Int)

    fun getPowerSpectrum(): Float32Buffer

}
