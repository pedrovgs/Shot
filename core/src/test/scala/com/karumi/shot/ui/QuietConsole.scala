package com.karumi.shot.ui

class QuietConsole extends Console {
  override def show(message: Message): Unit = {}

  override def showSuccess(message: Message): Unit = {}

  override def showError(message: Message): Unit = {}
}
