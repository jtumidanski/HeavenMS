package server.life

class MobAttackInfoHolder(private var _attackPos: Int, private var _mpCon: Int, private var _coolTime: Int,
                          private var _animationTime: Int) {
  def attackPos: Int = _attackPos

  def mpCon: Int = _mpCon

  def coolTime: Int = _coolTime

  def animationTime: Int = _animationTime
}
