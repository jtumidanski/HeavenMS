package database.transformer;

import client.CharacterWorldNameLevel;
import entity.Character;
import transformer.SqlTransformer;

public class CharacterWorldNameLevelTransformer implements SqlTransformer<CharacterWorldNameLevel, Character> {
   @Override
   public CharacterWorldNameLevel transform(Character character) {
      return new CharacterWorldNameLevel(character.getWorld(), character.getName(), character.getLevel());
   }
}
