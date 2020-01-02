package client

class AbnormalStatusValueHolder(private var _startTime: Long, private var _length: Long) {
  def startTime: Long = _startTime

  def length: Long = _length
}
