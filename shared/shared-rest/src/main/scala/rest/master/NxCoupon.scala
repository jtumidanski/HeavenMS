package rest.master

import com.fasterxml.jackson.annotation.{JsonCreator, JsonGetter, JsonProperty}

class NxCoupon @JsonCreator()(@JsonProperty("coupon") _coupon: Integer, @JsonProperty("rate") _rate: Integer,
                              @JsonProperty("active-day") _activeDay: Integer,
                              @JsonProperty("start-hour") _startHour: Integer, @JsonProperty("end-hour") _endHour: Integer) {
  private var _id: Integer = _

  def id(value: Integer): Unit = _id = value

  @JsonGetter("id")
  def id: Integer = _id

  @JsonGetter("coupon")
  def coupon: Integer = _coupon

  @JsonGetter("rate")
  def rate: Integer = _rate

  @JsonGetter("active-day")
  def activeDay: Integer = _activeDay

  @JsonGetter("start-hour")
  def startHour: Integer = _startHour

  @JsonGetter("end-hour")
  def endHour: Integer = _endHour
}
