package client

class BuddyListStats(private var _buddies: Long, private var _capacity: Int) {
  def buddies: Long = _buddies

  def capacity: Int = _capacity
}
