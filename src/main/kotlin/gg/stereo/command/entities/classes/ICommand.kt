package gg.stereo.command.entities.classes

import gg.stereo.command.entities.annotations.Init
import gg.stereo.command.entities.annotations.Description
import gg.stereo.command.entities.annotations.Restrictions

interface ICommand {
  fun getInfo(): Init?
  fun getDescription(): Description?
  fun getRestrictions(): Restrictions?
  
  fun executor(ctx: Context, args: List<String>)
}