package client.database.data

class GetInventoryItems(private var _inventoryType: Int, private var _itemId: Int, private var _position: Int,
                        private var _quantity: Int, private var _petId: Int, private var _owner: String,
                        private var _expiration: Long, private var _giftFrom: String, private var _flag: Int,
                        private var _acc: Int, private var _avoid: Int, private var _dex: Int, private var _hands: Int,
                        private var _hp: Int, private var _intelligence: Int, private var _jump: Int,
                        private var _vicious: Int, private var _luk: Int, private var _matk: Int,
                        private var _mdef: Int, private var _mp: Int, private var _speed: Int, private var _str: Int,
                        private var _watk: Int, private var _wdef: Int, private var _upgradeSlots: Int,
                        private var _level: Int, private var _itemExp: Float, private var _itemLevel: Int,
                        private var _ringId: Int, private var _characterId: Int) {
  def inventoryType: Int = _inventoryType

  def itemId: Int = _itemId

  def position: Int = _position

  def quantity: Int = _quantity

  def petId: Int = _petId

  def owner: String = _owner

  def expiration: Long = _expiration

  def giftFrom: String = _giftFrom

  def flag: Int = _flag

  def acc: Int = _acc

  def avoid: Int = _avoid

  def dex: Int = _dex

  def hands: Int = _hands

  def hp: Int = _hp

  def intelligence: Int = _intelligence

  def jump: Int = _jump

  def vicious: Int = _vicious

  def luk: Int = _luk

  def matk: Int = _matk

  def mdef: Int = _mdef

  def mp: Int = _mp

  def speed: Int = _speed

  def str: Int = _str

  def watk: Int = _watk

  def wdef: Int = _wdef

  def upgradeSlots: Int = _upgradeSlots

  def level: Int = _level

  def itemExp: Float = _itemExp

  def itemLevel: Int = _itemLevel

  def ringId: Int = _ringId

  def characterId: Int = _characterId
}
