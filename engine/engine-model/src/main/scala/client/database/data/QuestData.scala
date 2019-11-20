package client.database.data

class QuestData(private var _questId: Short, private var _status: Int, private var _time: Long,
                private var _expires: Long, private var _forfeited: Int, private var _completed: Int,
                private var _questStatusId: Int) {
  def questId: Short = _questId

  def status: Int = _status

  def time: Long = _time

  def expires: Long = _expires

  def forfeited: Int = _forfeited

  def completed: Int = _completed

  def questStatusId: Int = _questStatusId
}
