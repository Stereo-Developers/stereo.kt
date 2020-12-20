package xyz.stereobot.kt.lavaplayer

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.TextChannel
import java.util.*

class Scheduler(val player: AudioPlayer) : AudioEventAdapter() {
  val queue: Queue<AudioTrack> = LinkedList()
  var channel: TextChannel? = null
  
  val filterManager = FilterManager(player)
  var repeat = "nothing"
  
  override fun onTrackStart(player: AudioPlayer, track: AudioTrack) {
    val embed = EmbedBuilder()
      .setColor(3373022)
      .setTitle(track.info.author)
      .setThumbnail("https://i.ytimg.com/vi/" + track.info.identifier + "/hqdefault.jpg")
      .setDescription(String.format(
        "[%s](%s)",
        track.info.title,
        track.info.uri
      ))
      .setFooter("Now playing")
    
    channel!!.sendMessage(embed.build()).queue()
  }
  
  fun add(track: AudioTrack, channel: TextChannel?) {
    if (channel != null) {
      this.channel = channel
    }
    
    if (!player.startTrack(track, true)) {
      queue.offer(track)
    }
  }
  
  fun shuffle() {
    Collections.shuffle(queue as List<*>)
  }
  
  fun startNext() {
    player.startTrack(queue.poll(), false)
  }
  
  override fun onTrackEnd(player: AudioPlayer, track: AudioTrack, endReason: AudioTrackEndReason) {
    if (channel?.guild?.selfMember?.voiceState?.channel?.members?.size?.minus(1) == 0) {
      channel
        ?.sendMessage(
          EmbedBuilder()
            .setColor(7506394)
            .setDescription("I was left alone in a voice channel. I am now clearing the queue")
            .build()
        )
        ?.queue()
      
      PlayerManager.getInstance().destroyMusicManager(channel!!.guild)
      
      val manager = channel!!.guild.audioManager
      manager.closeAudioConnection()
      return
    }
    
    if (endReason.mayStartNext) {
      if (repeat == "track") {
        this.player.startTrack(track.makeClone(), false)
        return
      } else if (repeat == "queue") {
        add(track.makeClone(), null)
        return
      }
      
      if (queue.isEmpty()) {
        channel
          ?.sendMessage(
            EmbedBuilder()
              .setColor(3373022)
              .setDescription("The queue has finished. Goodbye \uD83D\uDC4B")
              .build()
          )
          ?.queue()
        
        PlayerManager.getInstance().destroyMusicManager(channel!!.guild)
        
        val manager = channel!!.guild.audioManager
        manager.closeAudioConnection()
        
        return
      }
      
      startNext()
    }
  }
}