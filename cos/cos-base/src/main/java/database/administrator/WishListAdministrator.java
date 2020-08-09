package database.administrator;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import accessor.AbstractQueryExecutor;
import database.DeleteForCharacter;
import entity.Wishlist;

public class WishListAdministrator extends AbstractQueryExecutor implements DeleteForCharacter {
   private static WishListAdministrator instance;

   private WishListAdministrator() {
   }

   public static WishListAdministrator getInstance() {
      if (instance == null) {
         instance = new WishListAdministrator();
      }
      return instance;
   }

   public void addForCharacter(EntityManager entityManager, int characterId, List<Integer> snList) {
      List<Wishlist> wishLists = snList.stream().map(id -> {
         Wishlist wishlist = new Wishlist();
         wishlist.setCharacterId(characterId);
         wishlist.setSn(id);
         return wishlist;
      }).collect(Collectors.toList());
      insertBulk(entityManager, wishLists);
   }

   @Override
   public void deleteForCharacter(EntityManager entityManager, int characterId) {
      Query query = entityManager.createQuery("DELETE FROM Wishlist WHERE characterId = :characterId");
      query.setParameter("characterId", characterId);
      execute(entityManager, query);
   }
}
