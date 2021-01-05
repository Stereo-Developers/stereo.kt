package xyz.stereobot.bot.commands.owner

import me.devoxin.flight.api.Context
import me.devoxin.flight.api.annotations.Command
import me.devoxin.flight.api.annotations.Greedy
import me.devoxin.flight.api.entities.Cog
import xyz.stereobot.bot.Launcher
import javax.script.ScriptEngineManager

class Eval : Cog {
  private val engine = ScriptEngineManager().getEngineByExtension("kts")
  
  @Command(
    aliases = ["evaluate", "ev"],
    description = "Evaluates kotlin code",
    developerOnly = true
  )
  fun eval(ctx: Context, @Greedy code: String) {
    try {
      val bindings = engine.createBindings()
      val binds = mapOf(
        "ctx" to ctx,
        "bot" to ctx.jda,
        "launcher" to Launcher,
      )
  
      bindings.putAll(binds)
  
      val vars = binds.map {
        "val ${it.key} = bindings[\"${it.key}\"] as ${it.value::class.java.kotlin.qualifiedName}"
      }.joinToString("\n")
  
      var before = System.currentTimeMillis()
      val evaluated = engine.eval("${vars}\n${code}", bindings).toString()
      before = System.currentTimeMillis() - before
      
      return ctx.send("*Evaluated in ${before}ms*\n```kt\n${evaluated.take(1950)}```")
    } catch (e: Exception) {
      return ctx.send("Exception:\n```kt\n${e.toString().take(1950)}```")
    }
  }
}