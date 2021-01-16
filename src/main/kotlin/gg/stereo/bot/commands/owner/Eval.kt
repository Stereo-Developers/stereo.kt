package gg.stereo.bot.commands.owner

import gg.stereo.bot.Launcher
import gg.stereo.command.entities.annotations.Description
import gg.stereo.command.entities.annotations.Init
import gg.stereo.command.entities.annotations.Restrictions
import gg.stereo.command.entities.classes.Command
import gg.stereo.command.entities.classes.Context
import javax.script.ScriptEngineManager

@Init(trigger = "eval", triggers = ["ev", "evaluate"], group = "Owner")
@Description(info = "Evaluates Kotlin code", usage = "[...code]")
@Restrictions(owner = true)
class Eval : Command() {
  private val engine = ScriptEngineManager().getEngineByExtension("kts")
  
  override fun executor(ctx: Context, args: List<String>) {
    if (args.isEmpty()) {
      ctx.sendError("Please provide something to evaluate")
      return
    }
    
    val code = args.joinToString(" ")
      .removePrefix("```kt")
      .removeSuffix("```")
  
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