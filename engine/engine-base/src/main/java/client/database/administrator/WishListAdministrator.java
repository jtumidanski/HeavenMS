package client.database.administrator;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import client.database.AbstractQueryExecutor;
import client.database.DeleteForCharacter;
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
      List<Wishlist> wishlists = snList.stream().map(id -> {
         Wishlist wishlist = new Wishlist();
         wishlist.setCharacterId(characterId);
         wishlist.setSn(id);
         return wishlist;
      }).collect(Collectors.toList());
      insertBulk(entityManager, wishlists);
   }

   @Override
   public void deleteForCharacter(EntityManager entityManager, int characterId) {
      Query query = entityManager.createQuery("DELETE FROM Wishlist WHERE characterId = :characterId");
      query.setParameter("characterId", characterId);
      execute(entityManager, query);
   }
}
