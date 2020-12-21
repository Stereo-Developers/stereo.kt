package xyz.stereobot.kt.commands.settings

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import xyz.stereobot.kt.commands.Context
import xyz.stereobot.kt.handlers.CommandHandler
import xyz.stereobot.kt.objects.Command

class PrefixCommand() : Command() {
  init {
    this.name = "prefix"
    this.aliases = listOf("prefixes", "pfx")
    this.info = "Changes the guild's prefix"
    this.use = "<add, +|remove, -|list, all> <arg>"
    this.usages = listOf(
      "",
      "add s!!",
      "+ `se `",
      "remove s!!",
      "list",
      "all"
    )
    this.guild = true
    this.group = "Settings"
    this.userPerms = listOf(Permission.MESSAGE_MANAGE)
  }
  
  override fun invoke(ctx: Context, args: List<String>) {
    if (args.isEmpty()) {
      ctx.channel
        .sendMessage(
          EmbedBuilder()
            .setColor(getColor("#f55e53"))
            .setDescription("Please provide a method")
            .build()
        )
        .queue()
      
      return
    }
    
    val prefix = args.minus(args[0]).joinToString(" ")
    
    when (args[0]) {
      "add" -> this.add(ctx, prefix)
      "+" -> this.add(ctx, prefix)
      "remove" -> this.remove(ctx, prefix)
      "-" -> this.remove(ctx, prefix)
      "list" -> this.all(ctx)
      "all" -> this.all(ctx)
    }
  }
  
  private fun add(ctx: Context, prefix: String) {
    val newPrefix = prefix.replace("`", "")
    
    var prefixes = ctx.settings.get<List<String>>(
      ctx.guild.id,
      "prefixes",
      ctx.config.get("bot.prefixes")
    )
    
    if (prefixes.contains(newPrefix.toLowerCase())) {
      ctx.channel
        .sendMessage(
          EmbedBuilder()
            .setColor(getColor("#f55e53"))
            .setDescription("That prefix already exists.")
            .build()
        )
        .queue()
      
      return
    }
    
    prefixes = prefixes.plus(newPrefix)
    ctx.settings.update(ctx.guild.id, "prefixes", prefixes)
    
    ctx.channel
      .sendMessage(
        EmbedBuilder()
          .setColor(getColor("#3377de"))
          .setDescription("Added the prefix successfully!")
          .build()
      )
      .queue()
  }
  
  private fun remove(ctx: Context, prefix: String) {
    val newPrefix = prefix.replace("`", "")
  
    var prefixes = ctx.settings.get<List<String>>(
      ctx.guild.id,
      "prefixes",
      ctx.config.get("bot.prefixes")
    )
  
    if (!prefixes.contains(newPrefix.toLowerCase())) {
      ctx.channel
        .sendMessage(
          EmbedBuilder()
            .setColor(getColor("#f55e53"))
            .setDescription("That prefix doesn't already exist.")
            .build()
        )
        .queue()
    
      return
    }
  
    prefixes = prefixes.minus(newPrefix)
    ctx.settings.update(ctx.guild.id, "prefixes", prefixes)
  
    ctx.channel
      .sendMessage(
        EmbedBuilder()
          .setColor(getColor("#3377de"))
          .setDescription("Removed the prefix successfully!")
          .build()
      )
      .queue()
  }
  
  private fun all(ctx: Context) {
    val prefixes = ctx.settings.get<List<String>>(
      ctx.guild.id,
      "prefixes",
      ctx.config.get("bot.prefixes")
    )
    
    ctx.channel
      .sendMessage(
        EmbedBuilder()
          .setColor(getColor("#3377de"))
          .setDescription(
            "You have added ${prefixes.size} prefix${
              if (prefixes.size > 1)
                "es"
              else ""
            }:\n\n${prefixes
              .mapIndexed {
                index, prefix ->
                "`#${(index + 1).toString().padStart(2, '0')}` | **${prefix}**"
              }.joinToString(",\n")
            }"
          )
          .build()
      )
      .queue()
  }
}