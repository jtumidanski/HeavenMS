package client.database.data

class AllianceData(private var _capacity: Int, private var _name: String, private var _notice: String,
                   private var _rank1: String, private var _rank2: String, private var _rank3: String,
                   private var _rank4: String, private var _rank5: String) {
  def capacity: Int = _capacity

  def name: String = _name

  def notice: String = _notice

  def rank1: String = _rank1

  def rank2: String = _rank2

  def rank3: String = _rank3

  def rank4: String = _rank4

  def rank5: String = _rank5
}
