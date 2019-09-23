package client.database.data

class SkillData(private var _skillId: Int, private var _skillLevel: Byte, private var _masterLevel: Int,
                private var _expiration: Long) {
  def skillId: Int = _skillId

  def skillLevel: Byte = _skillLevel

  def masterLevel: Int = _masterLevel

  def expiration: Long = _expiration
}
