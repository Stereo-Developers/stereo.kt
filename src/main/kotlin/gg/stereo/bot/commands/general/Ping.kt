package gg.stereo.bot.commands.general

import gg.stereo.command.entities.annotations.Description
import gg.stereo.command.entities.annotations.Init
import gg.stereo.command.entities.classes.Command
import gg.stereo.command.entities.classes.Context

@Init(trigger = "ping", triggers = ["pong"], group = "General")
@Description(info = "Displays the clients latency")
class Ping : Command() {
  override fun executor(ctx: Context, args: List<String>) {
    ctx.event.jda.restPing.queue {
      ctx.sendEmbedded {
        setColor("3377de".toInt(16))
        appendDescription("⏱️ REST: **${ctx.event.jda.gatewayPing}ms**")
        appendDescription("\n")
        appendDescription("\uD83D\uDC93 Gateway: **${it}ms**")
      }
    }
  }
}