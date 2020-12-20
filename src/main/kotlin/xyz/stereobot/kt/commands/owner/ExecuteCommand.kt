package xyz.stereobot.kt.commands.owner

import net.dv8tion.jda.api.EmbedBuilder
import xyz.stereobot.kt.commands.Context
import xyz.stereobot.kt.objects.Command
import java.io.BufferedReader
import java.io.InputStreamReader

class ExecuteCommand : Command() {
  init {
    this.name = "execute"
    this.aliases = listOf("exec", "run", "command", "cmd")
    this.use = "[command]"
    this.info = "Executes a command"
    this.group = "Owner"
    this.usages = listOf(
      "ls",
      "screen -ls"
    )
    this.owner = true
  }
  
  override fun invoke(ctx: Context, args: List<String>) {
    if (args.isEmpty()) {
      ctx.channel
        .sendMessage(
          EmbedBuilder()
            .setColor(getColor("#f55e53"))
            .setDescription("Provide something to execute")
            .build()
        )
        .queue()
  
      return
    }
    
    try {
      var time = System.currentTimeMillis()
      val output = Runtime.getRuntime().exec(args.joinToString(" "))
      time = System.currentTimeMillis() - time
      
      val lines = InputStreamReader(
        output.inputStream
      ).readLines()
        .joinToString("\n")
        .replace("`", "\\u0027'")
        .replace("\u001B\\[[;\\d]*m", "")
      
      ctx.channel
        .sendMessage(
          "Executed in ${time}ms\n```bash\n${
            if (lines.length > 1950) lines.substring(0, 1950) else lines
          }```"
        ).queue()
    } catch (error: Exception) {
      ctx.channel
        .sendMessage(
          "Error:\n```bash\n${error}```"
        ).queue()
    }
  }
}