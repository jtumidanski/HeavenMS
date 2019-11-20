package client.database.data

class MakerCreateData(private var _requiredLevel: Int, private var _requiredMakerLevel: Int,
                      private var _requiredMeso: Int, private var _quantity: Int) {
  def requiredLevel: Int = _requiredLevel

  def requiredMakerLevel: Int = _requiredMakerLevel

  def requiredMeso: Int = _requiredMeso

  def quantity: Int = _quantity
}
