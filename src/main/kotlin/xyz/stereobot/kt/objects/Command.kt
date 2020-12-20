package xyz.stereobot.kt.objects

import net.dv8tion.jda.api.Permission

abstract class Command : ICommand {
  var name: String = ""
  var aliases: List<String> = listOf()
  var info: String = "no description"
  var use: String? = null
  var usages: List<String>? = listOf()
  var group: String = "Util"
  
  var owner: Boolean = false
  var guild: Boolean = false
  var botPerms: List<Permission> = listOf()
  var userPerms: List<Permission> = listOf()
  
  var ratelimit: Int? = null
  
  fun getColor(color: String): Int {
    return Integer.parseInt(
      color.replace("#", ""),
      16
    )
  }
  
  override fun getTrigger(): String {
    return name
  }
  
  override fun getTriggers(): List<String>? {
    return aliases
  }
  
  override fun getOwnerOnly(): Boolean {
    return owner
  }
  
  override fun getGuildOnly(): Boolean {
    return guild
  }
  
  override fun getDescription(): String {
    return info
  }
  
  override fun getUsage(): String? {
    return this.use
  }
  
  override fun getExamples(): List<String>? {
    return this.usages
  }
  
  override fun getBotPermissions(): List<Permission> {
    return this.botPerms
  }
  
  override fun getUserPermissions(): List<Permission> {
    return this.userPerms
  }
  
  override fun getCategory(): String {
    return group
  }
  
  override fun getCooldown(): Int? {
    return this.ratelimit
  }
}