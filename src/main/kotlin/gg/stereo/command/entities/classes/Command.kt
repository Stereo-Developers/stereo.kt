package gg.stereo.command.entities.classes

import gg.stereo.command.entities.annotations.Init
import gg.stereo.command.entities.annotations.Description
import gg.stereo.command.entities.annotations.Restrictions

abstract class Command : ICommand {
  override fun getInfo(): Init? {
    return try {
      this::class.java.getAnnotation(Init::class.java)
    } catch(e: Exception) {
      null
    }
  }
  
  override fun getDescription(): Description? {
    return try {
      this::class.java.getAnnotation(Description::class.java)
    } catch(e: Exception) {
      null
    }
  }
  
  override fun getRestrictions(): Restrictions? {
    return try {
      this::class.java.getAnnotation(Restrictions::class.java)
    } catch(e: Exception) {
      null
    }
  }
}