package xyz.stereobot.bot.commands.general

import me.devoxin.flight.api.Context
import me.devoxin.flight.api.annotations.Command
import me.devoxin.flight.api.entities.Cog

class General : Cog {
  @Command(
    aliases = ["pong", "latency"],
    description = "Displays the clients latency"
  )
  fun ping(ctx: Context) {
    ctx.jda.restPing.queue {
      ctx.send {
        setColor(Integer.parseInt("3377de", 16))
        appendDescription("**\uD83D\uDC93 Heartbeat**: ${ctx.jda.gatewayPing}ms")
        appendDescription("\n")
        appendDescription("**⏱️ Roundtrip**: ${it}ms")
      }
    }
  }
}