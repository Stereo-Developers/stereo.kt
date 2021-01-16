package gg.stereo.bot.commands.music

import gg.stereo.bot.utils.extensions.truncate
import gg.stereo.command.entities.annotations.Description
import gg.stereo.command.entities.annotations.Init
import gg.stereo.command.entities.annotations.Restrictions
import gg.stereo.command.entities.classes.Command
import gg.stereo.command.entities.classes.Context

@Init(trigger = "join", triggers = ["j"], group = "Music")
@Description(info = "Joins me into your voice channel")
@Restrictions(guild = true, inVc = true, vcJoinCheck = true)
class Join : Command() {
  override fun executor(ctx: Context, args: List<String>) {
    val vc = ctx.member?.voiceState?.channel

    ctx.join(vc!!)
    
    ctx.sendEmbedded {
      setColor("3377de".toInt(16))
      setDescription("Connected to `${vc.name.truncate.let { it(25) }}`")
    }
  }
}
