package client.database.data

class NxCodeData(private var _retriever: String, private var _expiration: Long, private var _id: Int) {
  def retriever: String = _retriever

  def expiration: Long = _expiration

  def id: Int = _id
}
