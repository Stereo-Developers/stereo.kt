package xyz.stereobot.kt.commands.owner

import net.dv8tion.jda.api.EmbedBuilder
import xyz.stereobot.kt.commands.Context
import xyz.stereobot.kt.objects.Command
import javax.script.ScriptEngineManager

class EvalCommand : Command() {
  init {
    this.name = "eval"
    this.aliases = listOf("evaluate", "ev")
    this.info = "Evaluates code"
    this.use = "[code]"
    this.usages = listOf("ctx.bot.gatewayPing", "2 + 2")
    this.group = "Owner"
    this.owner = true
  }
  
  override fun invoke(ctx: Context, args: List<String>) {
    if (args.isEmpty()) {
      ctx.channel
        .sendMessage(
          EmbedBuilder()
            .setColor(getColor("#f55e53"))
            .setDescription("Please provide something to evaluate, cunt")
            .build()
        )
        .queue()
      
      return
    }
    
    try {
      val engine = ScriptEngineManager().getEngineByName("nashorn")
  
      try {
        engine.put("ctx", ctx)
        engine.put("args", args)
        engine.put("engine", engine)
    
        var before = System.currentTimeMillis()
        val evaluated = engine.eval(args.joinToString(" ")).toString()
        before = System.currentTimeMillis() - before
    
        ctx.channel
          .sendMessage("Evaluated in ")
          .append(before.toString())
          .append("ms")
          .append("\n")
          .append("```java\n")
          .append(if (evaluated.length > 1900) evaluated.substring(0, 1900) else evaluated)
          .append("```")
          .queue()
      } catch (error: Exception) {
        ctx.channel
          .sendMessage("Exception:")
          .append("\n")
          .append("```java")
          .append("\n")
          .append(if (error.toString().length > 1900) error.toString().substring(0, 1900) else error.toString())
          .append("```")
          .queue()
      }
    } catch (e: Exception) {
    
    }
  }
}