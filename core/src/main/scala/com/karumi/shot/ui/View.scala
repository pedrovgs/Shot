package com.karumi.shot.ui

class View {

  type Message = String

  def show(message: Message): Unit =
    print(Console.CYAN + message + Console.RESET)

  def showSuccess(message: Message): Unit =
    print(Console.GREEN + message + Console.RESET)

  def showError(message: Message): Unit =
    print(Console.RED + message + Console.RESET)

  private def print(message: Message): Unit =
    if (!message.isEmpty) {
      println(message)
    }

}
