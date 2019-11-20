package client

class CoolDownValueHolder(private var _skillId: Int, private var _startTime: Long, private var _length: Long) {
  def skillId: Int = _skillId

  def startTime: Long = _startTime

  def length: Long = _length
}
