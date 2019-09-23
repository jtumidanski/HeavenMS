package client.database.data

class FamilyData(private var _characterId: Int, private var _familyId: Int, private var _seniorId: Int,
                 private var _reputation: Int, private var _todaysReputation: Int, private var _totalReputation: Int,
                 private var _reputationToSenior: Int, private var _precepts: String) {
  def characterId: Int = _characterId

  def familyId: Int = _familyId

  def seniorId: Int = _seniorId

  def reputation: Int = _reputation

  def todaysReputation: Int = _todaysReputation

  def totalReputation: Int = _totalReputation

  def reputationToSenior: Int = _reputationToSenior

  def precepts: String = _precepts
}
