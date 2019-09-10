package net.server.channel.packet.keymap

class KeyTypeAction(private var _key: Int, private var _type: Int, private var _action: Int) {
  def key: Int = _key

  def theType: Int = _type

  def action: Int = _action
}
