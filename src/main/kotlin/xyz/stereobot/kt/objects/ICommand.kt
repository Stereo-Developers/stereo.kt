package xyz.stereobot.kt.objects

import net.dv8tion.jda.api.Permission
import xyz.stereobot.kt.commands.Context

interface ICommand {
  fun invoke(ctx: Context, args: List<String>)
  
  fun getTrigger(): String
  fun getTriggers(): List<String>?
  fun getDescription(): String
  fun getUsage(): String?
  fun getExamples(): List<String>?
  fun getOwnerOnly(): Boolean
  fun getGuildOnly(): Boolean
  fun getBotPermissions(): List<Permission>?
  fun getUserPermissions(): List<Permission>?
  fun getCategory(): String
  fun getCooldown(): Int?
}