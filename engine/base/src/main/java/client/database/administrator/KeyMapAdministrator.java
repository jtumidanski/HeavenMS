package client.database.administrator;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import client.KeyBinding;
import client.database.AbstractQueryExecutor;
import client.database.DeleteForCharacter;
import entity.KeyMap;

public class KeyMapAdministrator extends AbstractQueryExecutor implements DeleteForCharacter {
   private static KeyMapAdministrator instance;

   public static KeyMapAdministrator getInstance() {
      if (instance == null) {
         instance = new KeyMapAdministrator();
      }
      return instance;
   }

   private KeyMapAdministrator() {
   }

   @Override
   public void deleteForCharacter(EntityManager entityManager, int characterId) {
      Query query = entityManager.createQuery("DELETE FROM KeyMap WHERE characterId = :characterId");
      query.setParameter("characterId", characterId);
      execute(entityManager, query);
   }

   public void create(EntityManager entityManager, int characterId, int key, int type, int action) {
      KeyMap keyMap = new KeyMap();
      keyMap.setCharacterId(characterId);
      keyMap.setKey(key);
      keyMap.setType(type);
      keyMap.setAction(action);
      insert(entityManager, keyMap);
   }

   public void create(EntityManager entityManager, int characterId, Set<Map.Entry<Integer, KeyBinding>> bindings) {
      List<KeyMap> keyMapList = bindings.stream().map(binding -> {
         KeyMap keyMap = new KeyMap();
         keyMap.setCharacterId(characterId);
         keyMap.setKey(binding.getKey());
         keyMap.setType(binding.getValue().theType());
         keyMap.setAction(binding.getValue().action());
         return keyMap;
      }).collect(Collectors.toList());
      insertBulk(entityManager, keyMapList);
   }
}