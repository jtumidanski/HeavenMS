package rest.master.maker

import com.fasterxml.jackson.annotation.{JsonCreator, JsonGetter, JsonProperty}

class MakerCreateRequirement @JsonCreator()(@JsonProperty("id") _id: Integer, @JsonProperty("item") _item: Integer,
                                            @JsonProperty("required-level") _requiredLevel: Integer,
                                            @JsonProperty("required-maker-level") _requiredMakerLevel: Integer,
                                            @JsonProperty("required-meso") _requiredMeso: Integer,
                                            @JsonProperty("required-item") _requiredItem: Integer,
                                            @JsonProperty("required-equip") _requiredEquip: Integer,
                                            @JsonProperty("catalyst") _catalyst: Integer,
                                            @JsonProperty("quantity") _quantity: Integer,
                                            @JsonProperty("tuc") _tuc: Integer) {
  @JsonGetter("id")
  def id: Integer = _id

  @JsonGetter("item")
  def item: Integer = _item

  @JsonGetter("required-level")
  def requiredLevel: Integer = _requiredLevel

  @JsonGetter("required-maker-level")
  def requiredMakerLevel: Integer = _requiredMakerLevel

  @JsonGetter("required-meso")
  def requiredMeso: Integer = _requiredMeso

  @JsonGetter("required-item")
  def requiredItem: Integer = _requiredItem

  @JsonGetter("required-equip")
  def requiredEquip: Integer = _requiredEquip

  @JsonGetter("catalyst")
  def catalyst: Integer = _catalyst

  @JsonGetter("quantity")
  def quantity: Integer = _quantity

  @JsonGetter("tuc")
  def tuc: Integer = _tuc
}
