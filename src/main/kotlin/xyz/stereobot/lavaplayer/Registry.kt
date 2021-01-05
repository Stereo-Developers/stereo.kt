package xyz.stereobot.lavaplayer

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import me.devoxin.flight.api.Context
import java.net.URL
import java.net.URLEncoder

class Registry {
  val players = HashMap<Long, Manager>()
  val manager = DefaultAudioPlayerManager()
  
  fun create(id: Long) =
    players.computeIfAbsent(id) { Manager(manager.createPlayer()) }
 
  fun exists(id: Long) =
    players.containsKey(id)
  
  fun get(id: Long): Manager {
    return if (exists(id)) {
      players[id]!!
    } else {
      create(id)
      players[id]!!
    }
  }
  
  fun destroy(id: Long) {
    if (exists(id)) {
      val player = get(id)
      player.player.destroy()
    }
  }
  
  fun load(ctx: Context, query: String) {
    val manager = get(ctx.guild!!.idLong)
    ctx.guild?.audioManager?.sendingHandler = manager
    
    var search = query
    
    if (!isUrl(query)) {
      search = "ytsearch:${URLEncoder.encode(query, Charsets.UTF_8)}"
    }
    
    this.manager.loadItemOrdered(manager, search, ResultLoader(ctx, manager))
  }
  
  fun isUrl(value: String): Boolean {
    return try {
      URL(value)
      true
    } catch (e: Exception) {
      false
    }
  }
  
  init {
    AudioSourceManagers.registerRemoteSources(manager)
    AudioSourceManagers.registerLocalSource(manager)
  }
}