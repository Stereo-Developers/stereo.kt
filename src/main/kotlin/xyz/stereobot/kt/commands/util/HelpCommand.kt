package xyz.stereobot.kt.commands.util

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.MessageEmbed
import xyz.stereobot.kt.commands.Context
import xyz.stereobot.kt.handlers.CommandHandler
import xyz.stereobot.kt.objects.Command
import xyz.stereobot.kt.utils.PrologBuilder

class HelpCommand(val commands: CommandHandler) : Command() {
  init {
    this.name = "help"
    this.aliases = listOf("halp", "h", "commands", "cmds")
    this.info = "Displays all commands, or info on one."
    this.usages = listOf("", "ping")
    this.use = "<command>"
    this.ratelimit = 5000
  }
  
  override fun invoke(ctx: Context, args: List<String>) {
    val embed = EmbedBuilder().setColor(getColor("#3377de"))
    
    if (args.isEmpty()) {
      ctx.channel.sendMessage(helpEmbed(ctx, embed)).queue()
      return
    }
    
    val command = this.commands.findCommand(args[0].toLowerCase())
    if (
      command == null ||
      this.filteredCategories(ctx).contains(command.group)
    ) {
      ctx.channel.sendMessage(helpEmbed(ctx, embed)).queue()
      return
    }
  
    embed
      .setAuthor(
        "Help for ${command.getTrigger()}",
        null,
        ctx.author.effectiveAvatarUrl
      )
      .setDescription(
        PrologBuilder().apply {
          addLine("Trigger", command.getTrigger())
          addLine(
            "Triggers",
            if (
              command.getTriggers().isNullOrEmpty()
            ) "None"
            else command.getTriggers()!!.joinToString(", "))
          addLine("Info", command.getDescription())
          addLine("Group", command.getCategory())
          
          if (command.getCooldown() != null) {
            addLine("Cooldown", "${command.getCooldown()!! / 1000}s")
          }
        }.build()
      )
    
    if (!command.getExamples().isNullOrEmpty()) {
      embed
        .appendDescription("\n\n")
        .appendDescription(
          "**› Other Information**"
        )
        .appendDescription("\n")
        .appendDescription(
          PrologBuilder().apply {
            addLine("Usage", command.getUsage() ?: "No usage")
            addLine(
              "Examples",
              "\n${
                command
                  .getExamples()!!
                  .joinToString("\n")
                  { "\u3000 ${command.name} $it" }
              }"
            )
          }.build()
        )
    }
    
    ctx.channel
      .sendMessage(embed.build())
      .queue()
  }
  
  private fun helpEmbed(ctx: Context, embed: EmbedBuilder): MessageEmbed {
    val categories = HashSet<String>()
    this.commands.forEach { categories.add(it.getCategory()) }
    
    for (id in categories.filter { !this.filteredCategories(ctx).contains(it) }) {
      val commands = this.commands.filter { it.getCategory().equals(id, true) }
      
      embed.addField(
        "› $id (${commands.size})",
        commands.joinToString(", ") { "`${it.getTrigger()}`" },
        false
      )
    }
    
    embed.setAuthor(
      "Available commands for ${ctx.author.name}",
      null,
      ctx.author.effectiveAvatarUrl
    )
    
    return embed.build()
  }
  
  private fun filteredCategories(ctx: Context): List<String?> {
    return listOf(
      if (
        !ctx.config.get<List<String>>("bot.owners")
          .contains(ctx.author.id)
      ) "Owner"
      else null,
      if (
        !(
            ctx.event.isFromGuild &&
            commands.missing(
              null,
              ctx.member,
              Permission.getRaw(
                Permission.MESSAGE_MANAGE,
              )
            ).isEmpty()
          )
      ) "Settings" else null
    )
  }
}