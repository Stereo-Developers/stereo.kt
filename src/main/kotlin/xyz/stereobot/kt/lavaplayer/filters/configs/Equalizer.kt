/**
 * Taken from https://github.com/Frederikam/Lavalink/blob/dev/LavalinkServer/src/main/java/lavalink/server/player/filters/filterConfigs.kt#L24#L37
 * Commit 386c2f3bee435821041c8f81a22bfd511807ae13
 * Thanks Lavaplayer for having no documentation (that i can find) (jk still love you lavaplayer)
 */

package xyz.stereobot.kt.lavaplayer.filters.configs

import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter
import com.sedmelluq.discord.lavaplayer.filter.equalizer.Equalizer
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat

class Equalizer(bands: List<Band>) : BaseConfig() {
  private val array = FloatArray(Equalizer.BAND_COUNT) { 0.0f }
  
  init {
    bands.forEach { array[it.band] = it.gain }
  }
  
  override fun build(format: AudioDataFormat, output: FloatPcmAudioFilter): FloatPcmAudioFilter {
    return Equalizer(format.channelCount, output, array)
  }
  
  override val isEnabled: Boolean get() = array.any { it != 0.0f }
}

data class Band(val band: Int, val gain: Float)