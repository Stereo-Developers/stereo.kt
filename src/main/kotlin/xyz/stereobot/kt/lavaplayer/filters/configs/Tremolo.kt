/**
 * Taken from https://github.com/Frederikam/Lavalink/blob/dev/LavalinkServer/src/main/java/lavalink/server/player/filters/filterConfigs.kt#L71#L82
 * Commit 386c2f3bee435821041c8f81a22bfd511807ae13
 * Thanks Lavaplayer for having no documentation (that i can find) (jk still love you lavaplayer)
 */

package xyz.stereobot.kt.lavaplayer.filters.configs

import com.github.natanbc.lavadsp.natives.TimescaleNativeLibLoader
import com.github.natanbc.lavadsp.tremolo.TremoloPcmAudioFilter
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat

class Tremolo(
  private val frequency: Float = 2.0f,
  private val depth: Float = 0.5f
) : BaseConfig() {
  override fun build(format: AudioDataFormat, output: FloatPcmAudioFilter): FloatPcmAudioFilter {
    return TremoloPcmAudioFilter(output, format.channelCount, format.sampleRate)
      .setFrequency(frequency)
      .setDepth(depth)
  }
  
  override val isEnabled: Boolean get() = depth != 0.0f
}