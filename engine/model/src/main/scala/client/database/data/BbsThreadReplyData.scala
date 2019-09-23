package client.database.data

class BbsThreadReplyData(private var _threadId: Int, private var _replyId: Int, private var _posterCharacterId: Int,
                         private var _timestamp: Long, private var _content: String) {
  def threadId: Int = _threadId

  def replyId: Int = _replyId

  def posterCharacterId: Int = _posterCharacterId

  def timestamp: Long = _timestamp

  def content: String = _content
}
