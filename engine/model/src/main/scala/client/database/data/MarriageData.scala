package client.database.data

class MarriageData(private var _id: Int, private var _spouse1: Int, private var _spouse2: Int) {
  def id: Int = _id

  def spouse1: Int = _spouse1

  def spouse2: Int = _spouse2
}
