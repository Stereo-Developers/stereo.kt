package gg.stereo.bot.commands.music

import gg.stereo.bot.Launcher
import gg.stereo.command.entities.annotations.Description
import gg.stereo.command.entities.annotations.Init
import gg.stereo.command.entities.annotations.Restrictions
import gg.stereo.command.entities.classes.Command
import gg.stereo.command.entities.classes.Context
import java.net.URL
import java.net.URLEncoder

@Init(trigger = "play", triggers = ["pl", "p"], group = "Music")
@Description(info = "Plays music", usage = "<song | url>")
@Restrictions(guild = true, inVc = true, vcJoinCheck = true)
class Play : Command() {
  override fun executor(ctx: Context, args: List<String>) {
    if (args.isEmpty()) {
      ctx.sendError("Argument **song** is required, but empty.")
      return
    }
    
    if (!ctx.guild.selfMember.voiceState?.inVoiceChannel()!!) {
      ctx.join(ctx.member?.voiceState?.channel!!)
    }
    
    val search = args.joinToString(" ").replace("<(.+)>", "$1")
    
    ctx.registry.load(ctx, search)
  }
}