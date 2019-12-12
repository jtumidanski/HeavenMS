package rest.cashshop

import com.fasterxml.jackson.annotation.{JsonCreator, JsonGetter, JsonProperty}

class WishListResponse @JsonCreator()(@JsonProperty("items") _items: java.util.List[WishListItem]) {
  @JsonGetter("items")
  def items: java.util.List[WishListItem] = _items
}
