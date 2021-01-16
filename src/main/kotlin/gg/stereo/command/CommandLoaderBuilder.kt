package gg.stereo.command

import com.jagrosh.jdautilities.commons.waiter.EventWaiter

class CommandLoaderBuilder {
  private var owners = listOf<String>()
  private lateinit var packageName: String
  private var prefixes = listOf<String>()
  private lateinit var waiter: EventWaiter
  
  fun setWaiter(waiter: EventWaiter): CommandLoaderBuilder {
    this.waiter = waiter
    
    return this
  }
  
  fun setOwners(owners: List<String>): CommandLoaderBuilder {
    this.owners = owners
    
    return this
  }
  
  fun setPrefixes(prefixes: List<String>): CommandLoaderBuilder {
    this.prefixes = prefixes
    
    return this
  }
  
  fun setCommandPackage(pkg: String): CommandLoaderBuilder {
    packageName = pkg
    
    return this
  }
  
  fun build(): CommandLoader {
    return CommandLoader(owners, packageName, prefixes, waiter)
  }
}