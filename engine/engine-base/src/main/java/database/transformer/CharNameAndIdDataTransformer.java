package database.transformer;

import client.database.data.CharNameAndIdData;
import entity.Character;
import transformer.SqlTransformer;

public class CharNameAndIdDataTransformer implements SqlTransformer<CharNameAndIdData, Character> {
   @Override
   public CharNameAndIdData transform(Character character) {
      return new CharNameAndIdData(character.getName(), character.getId());
   }
}
