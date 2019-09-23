package client.inventory

class PetCommand(private var _petId: Int, private var _skillId: Int, private var _probability: Int, private var _increase: Int) {
  def petId: Int = _petId

  def skillId: Int = _skillId

  def probability: Int = _probability

  def increase: Int = _increase
}
