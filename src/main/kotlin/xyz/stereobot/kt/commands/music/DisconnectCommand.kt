package xyz.stereobot.kt.commands.music

import net.dv8tion.jda.api.EmbedBuilder
import xyz.stereobot.kt.commands.Context
import xyz.stereobot.kt.objects.Command

class DisconnectCommand : Command() {
  init {
    this.name = "disconnect"
    this.aliases = listOf("leave", "fuckoff", "goodbye")
    this.info = "Disconnects the bot from the voice channel"
    this.group = "Music"
    this.guild = true
  }
  
  override fun invoke(ctx: Context, args: List<String>) {
    if (ctx.player.playingTrack == null) {
      ctx.channel
        .sendMessage(
          EmbedBuilder()
            .setColor(getColor("#f55e53"))
            .setDescription("There is no player spawned for this guild.")
            .build()
        )
        .queue()
    
      return
    }
  
    if (
      !ctx.member!!.voiceState!!.inVoiceChannel() ||
      ctx.vc!!.id != ctx.member.voiceState!!.channel!!.id
    ) {
      ctx.channel
        .sendMessage(
          EmbedBuilder()
            .setColor(getColor("#f55e53"))
            .setDescription("Please join my voice channel")
            .build()
        )
        .queue()
    
      return
    }
    
    ctx.destroy()
    
    ctx.channel
      .sendMessage(
        EmbedBuilder()
          .setColor(getColor("#3377de"))
          .setDescription("I've left your voice channel. Goodbye!")
          .build()
      )
      .queue()
  }
}