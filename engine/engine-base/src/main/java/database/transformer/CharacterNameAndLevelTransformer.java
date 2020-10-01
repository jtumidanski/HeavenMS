package database.transformer;

import client.CharacterNameAndLevel;
import entity.Character;
import transformer.SqlTransformer;

public class CharacterNameAndLevelTransformer implements SqlTransformer<CharacterNameAndLevel, Character> {
   @Override
   public CharacterNameAndLevel transform(Character character) {
      return new CharacterNameAndLevel(character.getName(), character.getLevel());
   }
}
