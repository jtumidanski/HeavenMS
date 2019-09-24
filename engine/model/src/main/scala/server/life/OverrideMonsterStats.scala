package server.life

class OverrideMonsterStats(var hp: Int, var mp: Int, var exp: Int, private var _change: Boolean) {
  def this(hp: Int, mp: Int, exp: Int) = {
    this(hp, mp, exp, true)
  }

  def this() = {
    this(1, 0, 0, true)
  }
}
