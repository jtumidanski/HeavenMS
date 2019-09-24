package server

import java.sql.Timestamp
import java.util.Calendar

import client.inventory.Item

class DueyPackage(private var _packageId: Int, private var _item: Option[Item]) {
  def packageId: Int = _packageId

  def item: Option[Item] = _item

  var sender: String = ""

  var mesos: Int = 0

  var message: String = ""

  private var timestamp: Calendar = _

  def sentTimeInMilliseconds: Long = {
    val ts: Calendar = timestamp
    if (ts != null) {
      val cal: Calendar = Calendar.getInstance()
      cal.setTime(ts.getTime)
      cal.add(Calendar.MONTH, 1)
      cal.getTimeInMillis
    } else {
      0
    }
  }

  def isDeliveringTime: Boolean = {
    val ts: Calendar = timestamp
    if (ts != null) {
      ts.getTimeInMillis >= System.currentTimeMillis()
    } else {
      false
    }
  }

  def setSentTime(ts: Timestamp, quick: Boolean): Unit = {
    val cal: Calendar = Calendar.getInstance()
    cal.setTimeInMillis(ts.getTime)
    if (quick) {
      if (System.currentTimeMillis() - ts.getTime < 24 * 60 * 60 * 1000) {
        cal.add(Calendar.DATE, -1)
      }
    }
    timestamp = cal
  }
}
