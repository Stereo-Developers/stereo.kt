package xyz.stereobot.kt.objects

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
  fun getBotPermissions(): List<String>?
  fun getUserPermissions(): List<String>?
  fun getCategory(): String
}