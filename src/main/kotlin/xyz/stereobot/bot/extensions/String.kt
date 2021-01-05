package xyz.stereobot.bot.extensions

val String.truncate: Function1<Int, *>
  get() = fun (amount: Int): String {
    return if (this.length > amount) {
      this.substring(0, amount - 3) + "..."
    } else {
      this
    }
  }