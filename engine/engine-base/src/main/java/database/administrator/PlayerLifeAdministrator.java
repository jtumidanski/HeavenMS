package database.administrator;


import javax.persistence.EntityManager;

import database.AbstractQueryExecutor;
import entity.PLife;

public class PlayerLifeAdministrator extends AbstractQueryExecutor {
   private static PlayerLifeAdministrator instance;

   public static PlayerLifeAdministrator getInstance() {
      if (instance == null) {
         instance = new PlayerLifeAdministrator();
      }
      return instance;
   }

   private PlayerLifeAdministrator() {
   }

   public void create(EntityManager entityManager, int lifeId, int f, int fh, int cy, int rx0, int rx1, String type,
                      int xpos, int ypos, int worldId, int mapId, int mobTime, int hide) {
      PLife pLife = new PLife();
      pLife.setLife(lifeId);
      pLife.setF(f);
      pLife.setFh(fh);
      pLife.setCy(cy);
      pLife.setRx0(rx0);
      pLife.setRx1(rx1);
      pLife.setType(type);
      pLife.setX(xpos);
      pLife.setY(ypos);
      pLife.setWorld(worldId);
      pLife.setMap(mapId);
      pLife.setMobTime(mobTime);
      pLife.setHide(hide);
      insert(entityManager, pLife);
   }
}