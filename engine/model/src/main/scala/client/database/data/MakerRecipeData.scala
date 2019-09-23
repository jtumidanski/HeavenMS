package client.database.data

class MakerRecipeData(private var _requiredItem: Int, private var _count: Int) {
  def requiredItem: Int = _requiredItem

  def count: Int = _count
}
