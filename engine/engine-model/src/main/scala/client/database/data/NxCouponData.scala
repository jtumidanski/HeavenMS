package client.database.data

class NxCouponData(private var _couponId: Int, private var _rate: Int) {
  def couponId: Int = _couponId

  def rate: Int = _rate
}
