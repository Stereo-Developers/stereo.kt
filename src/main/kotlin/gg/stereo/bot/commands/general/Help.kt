package gg.stereo.bot.commands.general

import gg.stereo.bot.utils.Line
import gg.stereo.bot.utils.PrologBuilder
import gg.stereo.bot.utils.extensions.truncate
import gg.stereo.command.entities.annotations.Description
import gg.stereo.command.entities.annotations.Init
import gg.stereo.command.entities.classes.Command
import gg.stereo.command.entities.classes.Context
import net.dv8tion.jda.api.EmbedBuilder

@Init(trigger = "help", triggers = ["halp", "h", "commands", "cmds"], group = "General")
@Description(info = "Displays all the commands or information on just one", usage = "<cmd>")
class Help : Command() {
  override fun executor(ctx: Context, args: List<String>) {
    if (args.isEmpty()) {
      return helpEmbed(ctx)
    }
  
    val command = ctx.commandLoader.getCommand(args[0]) ?: return helpEmbed(ctx)
    
    val categories = categoryFilter(ctx)
    if (categories.contains(command.getInfo()!!.group)) {
      return ctx.sendError("You may not view this commands information.")
    }
    
    return ctx.sendEmbedded {
      setColor("3377de".toInt(16))
      setAuthor(
        "Command information for ${command.getInfo()!!.trigger}",
        null,
        ctx.author.effectiveAvatarUrl
      )
      
      addField(
        "› Basic Usages",
        PrologBuilder().apply {
          val info = command.getInfo()!!
          
          addLine(Line("Trigger", info.trigger))
          
          if (info.triggers.isNotEmpty()) {
            addLine(Line("Triggers", info.triggers.joinToString(", ")))
          }
          
          addLine(Line("Group", info.group))
          
        }.build(),
        false
      )
    }
  }
  
  private fun categoryFilter(ctx: Context): ArrayList<String> {
    val categories = arrayListOf<String>()
    
    if (!ctx.commandLoader.owners.contains(ctx.author.id)) {
      categories.add("Owner")
    }
    
    return categories
  }
  
  private fun helpEmbed(ctx: Context) {
    val embed = EmbedBuilder()
      .setAuthor(
        "Available commands for ${ctx.author.name.truncate(25)}",
        null, ctx.author.effectiveAvatarUrl
      ).setColor("3377de".toInt(16))
      .setFooter("Run !!help <command> for more information on a command")
  
    val categories = ctx.commandLoader.commands.groupBy {
      it.getInfo()!!.group
    }.filter { !categoryFilter(ctx).contains(it.key.toLowerCase()) }
  
    for ((category, commands) in categories) {
      embed.addField(
        "› $category (${commands.size})",
        commands.joinToString(", ") { "`${it.getInfo()!!.trigger}`" },
        false
      )
    }
  
    ctx.textChannel.sendMessage(embed.build()).submit()
    return
  }
}