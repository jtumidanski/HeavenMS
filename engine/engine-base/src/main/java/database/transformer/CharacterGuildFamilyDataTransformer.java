package database.transformer;

import client.database.data.CharacterGuildFamilyData;
import entity.Character;
import transformer.SqlTransformer;

public class CharacterGuildFamilyDataTransformer implements SqlTransformer<CharacterGuildFamilyData, Character> {
   @Override
   public CharacterGuildFamilyData transform(Character character) {
      return new CharacterGuildFamilyData(character.getWorld(), character.getGuildId(), character.getGuildRank(), character.getFamilyId());
   }
}
