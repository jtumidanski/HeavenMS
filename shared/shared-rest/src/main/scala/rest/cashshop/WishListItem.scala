package rest.cashshop

import com.fasterxml.jackson.annotation.{JsonCreator, JsonGetter, JsonProperty}

class WishListItem @JsonCreator()(@JsonProperty("id") _id: Integer) {
  @JsonGetter("id")
  def id: Int = _id
}
