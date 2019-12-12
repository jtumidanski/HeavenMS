package rest.master

import com.fasterxml.jackson.annotation.{JsonCreator, JsonGetter, JsonProperty}

class SpecialCashItem @JsonCreator()(@JsonProperty("sn") _sn: Integer, @JsonProperty("modifier") _modifier: Integer, @JsonProperty("info") _info: Integer) {
  private var _id: Integer = _

  def id(value: Integer): Unit = _id = value

  @JsonGetter("id")
  def id: Integer = _id

  @JsonGetter("sn")
  def sn: Integer = _sn

  @JsonGetter("modifier")
  def modifier: Integer = _modifier

  @JsonGetter("info")
  def info: Integer = _info
}
