package server.quest.actions

class ItemData(private var _map: Int, private var _id: Int, private var _count: Int, private var _prop: Integer, private var _job: Int, private var _gender: Int, private var _period: Int) {
  def map: Int = _map

  def id: Int = _id

  def count: Int = _count

  def prop: Integer = _prop

  def job: Int = _job

  def gender: Int = _gender

  def period: Int = _period
}
