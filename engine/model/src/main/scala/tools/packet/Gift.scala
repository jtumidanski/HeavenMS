package tools.packet

import client.inventory.Item

class Gift(private var _item: Item, private var _message: String) {
  def item: Item = _item

  def message: String = _message
}
