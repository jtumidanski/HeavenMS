package client.database.data

import scala.jdk.CollectionConverters._

class BbsThreadData(private var _posterCharacterId: Int, private var _timestamp: Long, private var _name: String,
                    private var _startPost: String, private var _icon: Int, private var _replyCount: Int,
                    private var _threadId: Int) {
  def posterCharacterId: Int = _posterCharacterId

  def timestamp: Long = _timestamp

  def name: String = _name

  def startPost: String = _startPost

  def icon: Int = _icon

  def replyCount: Int = _replyCount

  def threadId: Int = _threadId

  private var replyDataList: List[BbsThreadReplyData] = List[BbsThreadReplyData]()

  def addReply(bbsThreadReplyData: BbsThreadReplyData): Unit = {
    replyDataList = replyDataList.appended(bbsThreadReplyData)
  }

  def getReplyData: java.util.List[BbsThreadReplyData] = {
    replyDataList.asJava
  }
}
