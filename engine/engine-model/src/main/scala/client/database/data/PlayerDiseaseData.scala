package client.database.data

class PlayerDiseaseData(private var _disease: Int, private var _mobSkillId: Int, private var _mobSkillLevel: Int,
                        private var _length: Int) {
  def disease: Int = _disease

  def mobSkillId: Int = _mobSkillId

  def mobSkillLevel: Int = _mobSkillLevel

  def length: Int = _length
}
