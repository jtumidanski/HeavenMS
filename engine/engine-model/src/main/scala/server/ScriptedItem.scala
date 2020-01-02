package server

class ScriptedItem(private var _npc: Int, private var _script: String, private var _runOnPickup: Boolean) {
  def npc: Int = _npc

  def script: String = _script

  def runOnPickup: Boolean = _runOnPickup
}
