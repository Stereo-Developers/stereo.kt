package xyz.stereobot.lavaplayer

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import me.devoxin.flight.api.Context
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import xyz.stereobot.bot.extensions.truncate
import xyz.stereobot.bot.extensions.waiter
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

class ResultLoader(
  private val ctx: Context,
  private val manager: Manager,
) : AudioLoadResultHandler {
  override fun trackLoaded(track: AudioTrack?) {
    if (track != null) {
      return sendTrackEmbed(track)
    }
  }
  
  override fun playlistLoaded(playlist: AudioPlaylist?) {
    if (playlist!!.isSearchResult) {
      ctx.send {
        setColor(Integer.parseInt("3377de", 16))
        
        playlist.tracks
          .subList(0,
            if (playlist.tracks.size > 10)
              10
            else playlist.tracks.size
          ).mapIndexed { index, track ->
            appendDescription(
                "`#${
                (index + 1).toString().padStart(2, '0')
              }` | [${track.info.title.truncate(45)}](${track.info.uri})"
            )
            appendDescription("\n")
        }
      }
      
      ctx.waiter.waitForEvent(
        MessageReceivedEvent::class.java,
        { event -> event.author.id == ctx.author.id },
        {
          val content = it.message.contentRaw
          
          if (content.equals("cancel", ignoreCase = true)) {
            ctx.send {
              setColor(Integer.parseInt("3377de", 16))
              setDescription("Alright, I cancelled the prompt.")
            }
            
            return@waitForEvent
          }
          
          try {
            val track = playlist.tracks[Integer.parseInt(content) - 1]
            
            if (track == null) {
              ctx.send {
                setColor(Integer.parseInt("f55e53", 16))
                setDescription("You needed to provide a valid number!")
                appendDescription("\n")
                appendDescription("\n")
                appendDescription("Since you got it wrong, I've cancelled the prompt.")
              }
  
              return@waitForEvent
            } else {
              sendTrackEmbed(track).run {
                manager.queue(track, ctx.textChannel)
              }
            }
          } catch (e: Exception) {
            ctx.send {
              setColor(Integer.parseInt("f55e53", 16))
              setDescription("You needed to provide a valid number!")
              appendDescription("\n")
              appendDescription("\n")
              appendDescription("Since you got it wrong, I've cancelled the prompt.")
            }
  
            return@waitForEvent
          }
        },
        15L,
        TimeUnit.SECONDS,
        {
          ctx.send {
            setColor(Integer.parseInt("f55e53", 16))
            setDescription("You took too long to answer, so I'll be cancelling the prompt.")
          }
        }
      )
    }
  }
  
  override fun noMatches() {
    return ctx.send {
      setColor(Integer.parseInt("f55e53", 16))
      setDescription(
        "\uD83D\uDD0D I couldn't find anything for that. Check your spelling?"
      )
    }
  }
  
  override fun loadFailed(exception: FriendlyException?) {
    return ctx.send {
      setColor(Integer.parseInt("f55e53", 16))
      setDescription(
        "Oops, I ran into an exception! Sorry about this.\n```kt$${
          exception.toString().take(1950)
        }```"
      )
    }
  }
  
  private fun sendTrackEmbed(track: AudioTrack) {
    ctx.send {
      setColor(Integer.parseInt("3377de", 16))
      setThumbnail("https://i.ytimg.com/vi/${track.identifier}/hqdefault.jpg")
      setDescription("[${track.info.title}](${track.info.uri})")
      setFooter("Queued Up")
      setTimestamp(LocalDateTime.now())
    }
  }
}