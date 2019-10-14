package client.newyear

import java.util.concurrent.ScheduledFuture

class NewYearCardRecord(private var _senderId: Int, private var _senderName: String, private var _receiverId: Int,
                        private var _receiverName: String, private var _message: String) {
  var id: Int = -1

  private var _dateSent: Long = System.currentTimeMillis()

  var dateReceived: Long = 0

  private var _sendTask: Option[ScheduledFuture[_]] = Option.empty

  var senderDiscardCard: Boolean = false

  var receiverDiscardCard: Boolean = false

  var receiverReceivedCard: Boolean = false

  def senderId: Int = _senderId

  def senderName: String = _senderName

  def receiverId: Int = _receiverId

  def receiverName: String = _receiverName

  def message: String = _message

  def dateSent: Long = _dateSent

  def hasSendTask: Boolean = {
    _sendTask.isDefined
  }

  def setNewYearCardTask(task: ScheduledFuture[_]): Unit = {
    _sendTask = Option.apply(task)
  }

  def stopNewYearCardTask(): Unit = {
    if (_sendTask.isDefined) {
      _sendTask.get.cancel(false)
      _sendTask = Option.empty
    }
  }

  def setExtraNewYearCardRecord(id: Int, senderDiscardCard: Boolean, receiverDiscardCard: Boolean,
                                receiverReceivedCard: Boolean, dateSent: Long, dateReceived: Long) = {
    this.id = id
    this.senderDiscardCard = senderDiscardCard
    this.receiverDiscardCard = receiverDiscardCard
    this.receiverReceivedCard = receiverReceivedCard
    this._dateSent = dateSent
    this.dateReceived = dateReceived
  }
}
