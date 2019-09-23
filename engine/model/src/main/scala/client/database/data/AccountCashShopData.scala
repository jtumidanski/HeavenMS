package client.database.data

class AccountCashShopData(private var _nxCredit: Int, private var _maplePoint: Int, private var _nxPrepaid: Int) {
  def nxCredit: Int = _nxCredit

  def maplePoint: Int = _maplePoint

  def nxPrepaid: Int = _nxPrepaid
}
