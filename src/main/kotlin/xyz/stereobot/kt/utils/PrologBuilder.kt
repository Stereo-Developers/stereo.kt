package xyz.stereobot.kt.utils

class PrologBuilder {
  private var lines: Array<Prolog> = arrayOf()
  private var header: String? = null
  
  fun addLine(name: String, value: String): PrologBuilder {
    lines = lines.plus(
      object : Prolog {
        override val name = name
        override val value = value
      }
    )
    
    return this
  }
  
  fun addHeader(header: String): PrologBuilder {
    this.header = header
    return this
  }
  
  fun build(): String {
    val padding = fun(): Int {
      var padding = 0
    
      for (line in this.lines) {
        if (line.name.length > padding) {
          padding = line.name.length
        }
      }
    
      return padding
    }
  
    var str = "```prolog\n"
  
    if (!this.header.isNullOrEmpty()) {
      str += this.header
    }
  
    for (line in lines) {
      if (line.name.isNotEmpty() && line.value.isNotEmpty()) {
        str += "${line.name.padStart(padding(), ' ')} : ${line.value}\n"
      }
    }
  
    str += "```"
  
    return str
  }
}

interface Prolog {
  val name: String
  val value: String
}