package client.database.data

class QuestProgressData(private var _questStatusId: Int, private var _progressId: Int, private var _progress: String) {
  def questStatusId: Int = _questStatusId

  def progressId: Int = _progressId

  def progress: String = _progress
}
