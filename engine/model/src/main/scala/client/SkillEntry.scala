package client

class SkillEntry(private var _skillLevel: Byte, private var _masterLevel: Int, private var _expiration: Long) {
  def skillLevel: Byte = _skillLevel

  def masterLevel: Int = _masterLevel

  def expiration: Long = _expiration

  override def toString: String = skillLevel + ":" + masterLevel
}
