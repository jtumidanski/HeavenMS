package database.transformer;

import client.database.data.CharacterIdNameAccountId;
import entity.Character;
import transformer.SqlTransformer;

public class CharacterIdNameAccountIdTransformer implements SqlTransformer<CharacterIdNameAccountId, Character> {
   @Override
   public CharacterIdNameAccountId transform(Character character) {
      return new CharacterIdNameAccountId(character.getId(), character.getAccountId(), character.getName());
   }
}
