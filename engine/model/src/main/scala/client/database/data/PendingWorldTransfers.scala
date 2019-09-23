package client.database.data

class PendingWorldTransfers(private var _id: Int, private var _characterId: Int, private var _from: Int,
                            private var _to: Int) {
  def id: Int = _id

  def characterId: Int = _characterId

  def from: Int = _from

  def to: Int = _to
}
