package xyz.stereobot.kt.commands.util

import net.dv8tion.jda.api.EmbedBuilder
import xyz.stereobot.kt.commands.Context
import xyz.stereobot.kt.database.DatabaseManager
import xyz.stereobot.kt.objects.Command
import xyz.stereobot.kt.utils.PrologBuilder

class PingCommand(val database: DatabaseManager) : Command() {
  init {
    this.name = "ping"
    this.aliases = listOf("pong")
    this.info = "Displays the client's latency"
  }
  
  override fun invoke(ctx: Context, args: List<String>) {
    ctx.bot.restPing.queue {
      ctx.channel
        .sendMessage(
          EmbedBuilder()
            .setColor(getColor("#3377de"))
            .setDescription(
              PrologBuilder().apply {
                addLine("Shard", "${ctx.bot.gatewayPing}ms")
                addLine("Rest", "${it}ms")
                addLine("Database", "${database.monitor.latency}ms")
              }.build()
            )
            .build()
        )
        .queue()
    }
  }
}