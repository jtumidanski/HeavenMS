package server.life

class MobAttackInfo(private var _mobId: Int, private var _attackId: Int, var deadlyAttack: Boolean,
                    var mpBurn: Int, var diseaseSkill: Int, var diseaseLevel: Int, var mpCon: Int) {
  def mobId: Int = _mobId

  def attackId: Int = _attackId
}
