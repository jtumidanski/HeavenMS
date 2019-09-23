package client.database.data

class SkillMacroData(private var _position: Int, private var _skill1Id: Int, private var _skill2Id: Int,
                     private var _skill3Id: Int, private var _name: String, private var _shout: Int) {
  def position: Int = _position

  def skill1Id: Int = _skill1Id

  def skill2Id: Int = _skill2Id

  def skill3Id: Int = _skill3Id

  def name: String = _name

  def shout: Int = _shout
}
