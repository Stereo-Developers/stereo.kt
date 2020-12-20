package xyz.stereobot.kt.lavaplayer

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import xyz.stereobot.kt.commands.Context
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap

class PlayerManager() {
  private val musicManagers: MutableMap<Long, Manager>
  private val audioPlayerManager: AudioPlayerManager
  private var INSTANCE: PlayerManager? = null
  
  fun getMusicManager(guild: Guild): Manager {
    return musicManagers.computeIfAbsent(guild.idLong) { _ ->
      val manager = Manager(audioPlayerManager)
      guild.audioManager.sendingHandler = manager.handler
      manager
    }
  }
  
  fun destroyMusicManager(guild: Guild): Boolean {
    val manager = musicManagers[guild.idLong]
    
    return if (manager == null) {
      false
    } else {
      musicManagers.remove(guild.idLong)
      true
    }
  }
  
  fun loadAndPlay(ctx: Context, track: String?) {
    val manager = getMusicManager(ctx.guild)
    
    audioPlayerManager.loadItemOrdered(manager, track, object : AudioLoadResultHandler {
      override fun trackLoaded(track: AudioTrack) {
        manager.scheduler.add(track, ctx.event.textChannel)
        
        val embed = EmbedBuilder()
          .setColor(3373022)
          .setTitle(track.info.author)
          .setThumbnail("https://i.ytimg.com/vi/" + track.info.identifier + "/hqdefault.jpg")
          .setDescription(String.format(
            "[%s](%s)",
            track.info.title,
            track.info.uri
          ))
          .setFooter("Enqueued Track")
  
        ctx.event.textChannel.sendMessage(embed.build()).queue()
      }
      
      override fun playlistLoaded(playlist: AudioPlaylist) {
        if (playlist.isSearchResult) {
          ctx.channel
            .sendMessage(
              EmbedBuilder()
                .setColor(3373022)
                .setDescription(
                  playlist.tracks
                    .subList(0, 5)
                    .joinToString("\n") {
                      "`#${playlist.tracks.indexOf(it) + 1}` | [${it.info.title}](${it.info.uri})"
                    }
                )
                .build()
            )
            .queue()
          
          ctx.waiter.waitForEvent(
            MessageReceivedEvent::class.java,
            { event -> event.author.id == ctx.author.id },
            { event ->
              val content = event.message.contentRaw
              
              if (content.toLowerCase() == "cancel") {
                ctx.channel
                  .sendMessage(
                    EmbedBuilder()
                      .setColor(Integer.parseInt("f55e53", 16))
                      .setDescription("Alright, cancelled the selection.")
                      .build()
                  )
                  .queue()
                
                return@waitForEvent
              }
              
              val s: Int? = try {
                Integer.parseInt(content)
              } catch (e: Exception) {
                ctx.channel
                  .sendMessage(
                    EmbedBuilder()
                      .setColor(Integer.parseInt("f55e53", 16))
                      .setDescription("You did not provide a valid number. I have cancelled the collector now.")
                      .build()
                  )
                  .queue()
                
                return@waitForEvent
              }
              
              if (s == null) {
                return@waitForEvent
              }
  
              if (s > 5 || s < 1) {
                ctx.channel
                  .sendMessage(
                    EmbedBuilder()
                      .setColor(Integer.parseInt("f55e53", 16))
                      .setDescription("You must provide a valid selection! I've cancelled the collector, now.")
                      .build()
                  )
                  .queue()
    
                return@waitForEvent
              }
              
              val song = playlist.tracks[s - 1]
              manager.scheduler.add(song, ctx.event.textChannel)
              
              ctx.channel
                .sendMessage(
                  EmbedBuilder()
                    .setColor(3373022)
                    .setTitle(song.info.title)
                    .setThumbnail("https://i.ytimg.com/vi/${song.identifier}/hqdefault.jpg")
                    .setDescription("[${song.info.title}](${song.info.uri})")
                    .setFooter("Enqueued Track")
                    .build()
                )
                .queue()
              
              return@waitForEvent
            },
            15L,
            TimeUnit.SECONDS,
            {
              ctx.channel
                .sendMessage(
                  "You took too long to respond, so I've cancelled the song selection"
                )
                .queue()
            }
          )
          
          return
        }
        
        for (track: AudioTrack? in playlist.tracks) {
          manager.scheduler.add(track!!, ctx.event.textChannel)
        }
        
        val embed = EmbedBuilder()
          .setColor(3373022)
          .setThumbnail(
            "https://i.ytimg.com/vi" +
              playlist.tracks[0].identifier +
              "/hqdefault.jpg"
          )
          .setDescription(String.format(
            "%s",
            playlist.name
          ))
          .setFooter(String.format(
            "Enqueued Playlist • %s Songs",
            playlist.tracks.size
          ))
  
        ctx.event.textChannel.sendMessage(embed.build()).queue()
      }
      
      override fun noMatches() {
        ctx.event.textChannel
          .sendMessage(
            EmbedBuilder()
              .setColor(16080467)
              .setDescription("Hmm, I couldn't find anything for that query. Try again?")
              .build()
          )
          .queue()
      }
      
      override fun loadFailed(exception: FriendlyException) {
        ctx.event.textChannel
          .sendMessage(
            EmbedBuilder()
              .setColor(16080467)
              .setDescription(
                "I had some problems loading the track. Here is the exception:\n```java\n${exception.toString()}```"
              )
              .build()
          )
          .queue()
        
        exception.printStackTrace()
      }
    })
  }
  
  companion object {
    private var INSTANCE: PlayerManager? = null
    
    fun getInstance(): PlayerManager {
      if (this.INSTANCE == null) {
        this.INSTANCE = PlayerManager()
      }
      
      return this.INSTANCE!!
    }
  }
  
  init {
    musicManagers = HashMap()
    audioPlayerManager = DefaultAudioPlayerManager()
    AudioSourceManagers.registerRemoteSources(audioPlayerManager)
    AudioSourceManagers.registerLocalSource(audioPlayerManager)
//
//    YoutubeIpRotatorSetup(
//      NanoIpRoutePlanner(
//        Collections.singletonList(
//          Ipv6Block("<your_ipv6_block>/64")
//        ) as List<IpBlock<InetAddress>>, true
//      )
//    )
//    .forSource(YoutubeAudioSourceManager())
//      .setup();
  }
}