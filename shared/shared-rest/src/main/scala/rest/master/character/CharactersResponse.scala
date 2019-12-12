package rest.master.character

import com.fasterxml.jackson.annotation.{JsonCreator, JsonGetter, JsonProperty}

class CharactersResponse @JsonCreator()(@JsonProperty("characters") _characters: java.util.List[Character]) {
  @JsonGetter("characters")
  def characters: java.util.List[Character] = _characters
}
