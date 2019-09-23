package client.database.data

class CoolDownData(private var _skillId: Int, private var _length: Long, private var _startTime: Long) {
  def skillId: Int = _skillId

  def length: Long = _length

  def startTime: Long = _startTime
}
