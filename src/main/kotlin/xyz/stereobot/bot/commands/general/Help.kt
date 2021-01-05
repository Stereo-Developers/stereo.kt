package xyz.stereobot.bot.commands.general

import me.devoxin.flight.api.Context
import me.devoxin.flight.api.annotations.Command
import me.devoxin.flight.api.entities.Cog

class Help : Cog {
  @Command(
    aliases = ["h", "halp", "cmds", "commands"],
    description = ""
  )
  fun help(ctx: Context, command: String?) {
    if (command.isNullOrEmpty()) {
      this.sendGeneralHelp(ctx)
      return
    }
    
    val cmd = ctx.commandClient.commands.findCommandByAlias(command) ?:
      ctx.commandClient.commands.findCommandByName(command)
    
    if (cmd == null) {
      this.sendGeneralHelp(ctx)
      return
    }
    
    val filter = categoryFilter(ctx)
    if (filter.contains(cmd.category.toLowerCase())) {
      ctx.send {
        setColor(Integer.parseInt("f55e53", 16))
        setDescription("You are not allowed to view this command.")
      }
      
      return
    }
    
    ctx.send {
      setColor(Integer.parseInt("3377de", 16))
      setAuthor("Help for ${cmd.name}", null, ctx.author.effectiveAvatarUrl)
      setDescription(
        if (cmd.properties.description.isNotEmpty())
          cmd.properties.description
        else "No command description provided"
      )
      appendDescription("\n")
      appendDescription("\n")
      appendDescription(
        "**Aliases**: ${
          if (cmd.properties.aliases.isNotEmpty())
            cmd.properties.aliases.joinToString(", ") { "`$it`" }
          else "No aliases"
        }"
      )
      setFooter("<> = Optional, [] = Strict")
    }
  }
  
  private fun sendGeneralHelp(ctx: Context) {
    val categories = ctx.commandClient.commands.values
      .groupBy { it.category }
      .filter { !categoryFilter(ctx).contains(it.key.toLowerCase()) }
    
    ctx.send {
      setColor(Integer.parseInt("3377de", 16))
      setAuthor(
        "Available commands for ${ctx.author.name}",
        null,
        ctx.author.effectiveAvatarUrl
      )
      
      for ((id, commands) in categories) {
        addField(
          "› $id (${commands.size})",
          commands.joinToString { "`${it.name}`" },
          false
        )
      }
      
      setFooter("Psst, you can run !!help <command> to see more information")
    }
  }
  
  private fun categoryFilter(ctx: Context): ArrayList<String> {
    val list = arrayListOf<String>()
    
    if (!ctx.commandClient.ownerIds.contains(ctx.author.idLong)) {
      list.add("owner")
    }
    
    return list
  }
}