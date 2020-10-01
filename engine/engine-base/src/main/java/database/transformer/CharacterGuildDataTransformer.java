package database.transformer;

import client.database.data.CharacterGuildData;
import entity.Character;
import transformer.SqlTransformer;

public class CharacterGuildDataTransformer implements SqlTransformer<CharacterGuildData, Character> {
   @Override
   public CharacterGuildData transform(Character character) {
      return new CharacterGuildData(character.getId(), character.getGuildId(), character.getGuildRank(),
            character.getName(), character.getAllianceRank(), character.getLevel(), character.getJob());
   }
}
