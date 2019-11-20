package client

class KeyBinding(private var _type: Int, private var _action: Int) {
  def theType: Int = _type

  def action: Int = _action
}
