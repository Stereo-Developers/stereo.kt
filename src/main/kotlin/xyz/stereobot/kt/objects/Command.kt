package xyz.stereobot.kt.objects

abstract class Command : ICommand {
  var name: String = ""
  var aliases: List<String> = listOf()
  var owner: Boolean = false
  var guild: Boolean = false
  var info: String = "no description"
  var usages: List<String>? = listOf()
  var use: String? = null
  var group: String = "Util"
  
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
  
  override fun getBotPermissions(): List<String>? {
    return null
  }
  
  override fun getUserPermissions(): List<String>? {
    return null
  }
  
  override fun getCategory(): String {
    return group
  }
}