package database.transformer;

import client.CharacterIdAndAccount;
import entity.Character;
import transformer.SqlTransformer;

public class CharacterIdAndAccountTransformer implements SqlTransformer<CharacterIdAndAccount, Character> {
   @Override
   public CharacterIdAndAccount transform(Character character) {
      return new CharacterIdAndAccount(character.getId(), character.getAccountId());
   }
}
