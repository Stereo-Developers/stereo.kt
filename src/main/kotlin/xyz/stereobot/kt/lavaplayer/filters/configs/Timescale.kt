/**
 * Taken from https://github.com/Frederikam/Lavalink/blob/dev/LavalinkServer/src/main/java/lavalink/server/player/filters/filterConfigs.kt#L55#L69
 * Commit 386c2f3bee435821041c8f81a22bfd511807ae13
 * Thanks Lavaplayer for having no documentation (that i can find) (jk still love you lavaplayer)
 */

package xyz.stereobot.kt.lavaplayer.filters.configs

import com.github.natanbc.lavadsp.timescale.TimescalePcmAudioFilter
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat

class Timescale(
  private val speed: Double = 1.0,
  private val pitch: Double = 1.0,
  private val rate: Double = 1.0
) : BaseConfig() {
  override fun build(format: AudioDataFormat, output: FloatPcmAudioFilter): FloatPcmAudioFilter {
    return TimescalePcmAudioFilter(output, format.channelCount, format.sampleRate)
      .setSpeed(speed)
      .setPitch(pitch)
      .setRate(rate)
  }
  
  override val isEnabled: Boolean get() = speed != 1.0 || pitch != 1.0 || rate != 1.0
}