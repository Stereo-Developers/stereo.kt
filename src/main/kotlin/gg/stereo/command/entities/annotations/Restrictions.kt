package gg.stereo.command.entities.annotations

import net.dv8tion.jda.api.Permission

annotation class Restrictions(
  val owner: Boolean = false,
  val guild: Boolean = false,
  val inVc: Boolean = false,
  val vcJoinCheck: Boolean = false,
  val user: Array<Permission> = [],
  val bot: Array<Permission> = [],
)
