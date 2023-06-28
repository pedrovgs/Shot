package com.karumi.shot.ui
import scala.Console.{CYAN, GREEN, RED, RESET, YELLOW}

class Console {

  type Message = String

  def show(message: Message): Unit =
    print(CYAN + message + RESET)

  def showSuccess(message: Message): Unit =
    print(GREEN + message + RESET)

  def showWarning(message: Message): Unit =
    print(YELLOW + message + RESET)

  def showError(message: Message): Unit =
    print(RED + message + RESET)

  def lineBreak(): Unit = show("\n")

  private def print(message: Message): Unit =
    if (!message.isEmpty) {
      println(message)
    }

}
