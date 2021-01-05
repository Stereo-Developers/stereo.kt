package xyz.stereobot.lavaplayer

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.audio.AudioSendHandler
import net.dv8tion.jda.api.entities.TextChannel
import java.nio.ByteBuffer
import java.time.LocalDateTime
import java.util.*

class Manager(val player: AudioPlayer) : AudioEventAdapter(), AudioSendHandler {
  private val buffer = ByteBuffer.allocate(1024)
  private val frame = MutableAudioFrame()
  
  private var channel: TextChannel? = null
  private var messageId: Long? = null
  val queue = LinkedList<AudioTrack>()
  
  // AudioSendHandler shit
  override fun canProvide(): Boolean {
    return player.provide(frame)
  }
  
  override fun provide20MsAudio(): ByteBuffer {
    return buffer.flip()
  }
  
  override fun isOpus(): Boolean {
    return true
  }
  
  // fucking events and shit
  override fun onTrackStart(player: AudioPlayer?, track: AudioTrack?) {
    if (track != null) {
      this.announce(track)
    }
  }
  
  override fun onTrackException(player: AudioPlayer?, track: AudioTrack?, exception: FriendlyException?) {
    channel?.sendMessage(
      EmbedBuilder()
        .setColor(Integer.parseInt("f55e53", 16))
        .setDescription(
          "Oops, I ran into an exception! Sorry about this.\n```kt$${
            exception.toString().take(1950)
          }```"
        ).build()
    )?.queue { next() }
  }
  
  // just general shit and shit
  fun announce(track: AudioTrack): Unit? {
    val thumbnailUrl = "https://i.ytimg.com/vi/${track.identifier}/hqdefault.jpg"
    
    return if (this.messageId == null) {
      channel?.sendMessage(
        EmbedBuilder()
          .setColor(Integer.parseInt("3377de", 16))
          .setThumbnail(thumbnailUrl)
          .setTitle(track.info.author)
          .setDescription("[${track.info.title}](${track.info.uri})")
          .setFooter("Playing")
          .setTimestamp(LocalDateTime.now())
          .build()
      )?.queue { this.messageId = it.idLong }
    } else {
      try {
        channel?.editMessageById(
          messageId!!,
          EmbedBuilder()
            .setColor(Integer.parseInt("3377de", 16))
            .setThumbnail(thumbnailUrl)
            .setTitle(track.info.author)
            .setDescription("[${track.info.title}](${track.info.uri})")
            .setFooter("Playing")
            .setTimestamp(LocalDateTime.now())
            .build()
        )?.queue()
      } catch (e: Exception) {
        this.messageId = null
        this.announce(track)
      }
    }
  }
  
  fun queue(track: AudioTrack, channel: TextChannel?) {
    if (channel != null) {
      this.channel = channel
    }
    
    if (!player.startTrack(track, true)) {
      queue.offer(track)
    }
  }
  
  fun next() {
    player.startTrack(queue.poll(), false)
  }
  
  init {
    frame.setBuffer(buffer)
  }
}