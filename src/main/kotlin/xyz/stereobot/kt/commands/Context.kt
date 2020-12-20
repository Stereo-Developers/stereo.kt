package xyz.stereobot.kt.commands

import com.jagrosh.jdautilities.commons.waiter.EventWaiter
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.VoiceChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import xyz.stereobot.kt.Configuration
import xyz.stereobot.kt.lavaplayer.PlayerManager
import java.util.concurrent.TimeUnit
import kotlin.math.ceil

class Context(val event: MessageReceivedEvent, val waiter: EventWaiter) {
  // Guild Related
  val guild = event.guild
  
  // Information Related
  val member = event.member
  val author = event.author
  val message = event.message
  val channel = event.channel
  
  // Client shit lol
  val bot = event.jda
  val config = Configuration()
  
  // Audio
  val manager = PlayerManager.getInstance().getMusicManager(guild)
  val scheduler = manager.scheduler
  val player = manager.player
  val audio = guild.audioManager
  val vc = guild.selfMember.voiceState!!.channel
  
  fun join(channel: VoiceChannel) {
    audio.openAudioConnection(channel)
    audio.isSelfDeafened = true
  }
  
  fun leave() {
    audio.closeAudioConnection()
  }
  
  fun destroy() {
    PlayerManager.getInstance().destroyMusicManager(guild)
    this.leave()
  }
  
  fun <T> paged(list: List<T>, p: Int, maxPerPage: Int): PaginatedResponse<T> {
    val response = object : PaginatedResponse<T> {
      override var list: List<T> = listOf()
      override var max = 0
      override var page = p
    }
    
    val max = ceil((list.size / maxPerPage).toDouble())
    response.max = max.toInt()
    
    if (response.page > max || response.page < 1) {
      response.page = 1
    }
    
    try {
      response.list = list.subList((response.page - 1) * maxPerPage, response.page * maxPerPage)
    } catch (e: Exception) {
      response.list = list.subList((response.page - 1) * maxPerPage, list.size)
    }
    
    return response
  }
}

interface PaginatedResponse<T> {
  var list: List<T>
  var page: Int
  var max: Int
}