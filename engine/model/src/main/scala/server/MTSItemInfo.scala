package server

import java.util.Calendar

import client.inventory.Item

class MTSItemInfo(private var _item: Item, private var _price: Int, private var _id: Int, private var _characterId: Int, private var _seller: String, private var _date: String) {
  def item: Item = _item

  def price: Int = _price

  def id: Int = _id

  def characterId: Int = _characterId

  def seller: String = _seller

  def year: Int = Integer.parseInt(_date.substring(0, 4))

  def month: Int = Integer.parseInt(_date.substring(5, 7))

  def day: Int = Integer.parseInt(_date.substring(8, 10))

  def taxes: Int = 100 + price / 10

  def endingDate: Long = {
    val now: Calendar = Calendar.getInstance()
    now.set(year, month - 1, day)
    now.getTimeInMillis
  }
}
