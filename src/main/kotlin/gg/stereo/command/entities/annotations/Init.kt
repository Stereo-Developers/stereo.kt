package gg.stereo.command.entities.annotations

annotation class Init(
  val trigger: String,
  val triggers: Array<String> = [],
  val group: String = "Ungrouped"
)
