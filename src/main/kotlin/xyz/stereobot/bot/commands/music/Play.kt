package xyz.stereobot.bot.commands.music

import me.devoxin.flight.api.Context
import me.devoxin.flight.api.annotations.Command
import me.devoxin.flight.api.annotations.Greedy
import me.devoxin.flight.api.entities.Cog
import net.dv8tion.jda.api.Permission
import xyz.stereobot.bot.utils.extensions.playerRegistry

class Play : Cog {
  @Command(
    aliases = ["p", "pp"],
    description = "Plays music in your voice channel",
    guildOnly = true
  )
  fun play(ctx: Context, @Greedy search: String) {
    val memberVoiceState = ctx.member!!.voiceState
  
    if (!ctx.guild!!.selfMember.voiceState?.inVoiceChannel()!!) {
      if (!memberVoiceState?.inVoiceChannel()!!) {
        return ctx.send {
          setColor(Integer.parseInt("f55e53", 16))
          setDescription("Please join a voice channel")
        }
      }
      
      if (
        !ctx.guild!!.selfMember.hasPermission(memberVoiceState.channel!!,
          Permission.VOICE_CONNECT,
          Permission.VOICE_SPEAK
        )
      ) {
        return ctx.send {
          setColor(Integer.parseInt("f55e53", 16))
          setDescription("I cannot join your voice channel due to incorrect permissions.")
        }
      }
      
      ctx.guild!!.audioManager.openAudioConnection(memberVoiceState.channel)
    } else {
      if (
        memberVoiceState?.channel?.id != ctx.guild!!.selfMember.voiceState?.channel?.id
      ) {
        return ctx.send {
          setColor(Integer.parseInt("f55e53", 16))
          setDescription("Please join my voice channel")
        }
      }
    }
    
    ctx.playerRegistry.load(ctx, search)
  }
}