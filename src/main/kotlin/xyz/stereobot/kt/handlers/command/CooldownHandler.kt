package xyz.stereobot.kt.handlers.command

import java.util.*
import kotlin.collections.HashMap

class CooldownHandler : HashMap<String, Long>() {
  fun getCooldown(id: String): Long? {
    return this[id]
  }
}