package client;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;

import database.DatabaseConnection;
import database.administrator.MonsterBookAdministrator;
import client.database.data.MonsterBookData;
import database.provider.MonsterBookProvider;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.factory.MonitoredReentrantLockFactory;
import tools.MasterBroadcaster;
import tools.PacketCreator;
import tools.packet.foreigneffect.ShowForeignCardEffect;
import tools.packet.monster.book.SetCard;
import tools.packet.showitemgaininchat.ShowGainCard;

public final class MonsterBook {
   private static final Semaphore semaphore = new Semaphore(10);

   private int specialCard = 0;
   private int normalCard = 0;
   private int bookLevel = 1;
   private Map<Integer, Integer> cards = new LinkedHashMap<>();
   private Lock lock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.BOOK);

   public Set<Entry<Integer, Integer>> getCardSet() {
      lock.lock();
      try {
         return new HashSet<>(cards.entrySet());
      } finally {
         lock.unlock();
      }
   }

   public void addCard(final MapleClient c, final int cardId) {
      MasterBroadcaster.getInstance().sendToAllInMap(c.getPlayer().getMap(), new ShowForeignCardEffect(c.getPlayer().getId()), false, c.getPlayer());

      Integer qty;
      lock.lock();
      try {
         qty = cards.get(cardId);

         if (qty != null) {
            if (qty < 5) {
               cards.put(cardId, qty + 1);
            }
         } else {
            cards.put(cardId, 1);
            qty = 0;

            if (cardId / 1000 >= 2388) {
               specialCard++;
            } else {
               normalCard++;
            }
         }
      } finally {
         lock.unlock();
      }

      if (qty < 5) {
         if (qty == 0) {     // leveling system only accounts unique cards
            calculateLevel();
         }
         PacketCreator.announce(c, new SetCard(false, cardId, qty + 1));
         PacketCreator.announce(c, new ShowGainCard());
      } else {
         PacketCreator.announce(c, new SetCard(true, cardId, 5));
      }
   }

   private void calculateLevel() {
      lock.lock();
      try {
         int collectionExp = (normalCard + specialCard);

         int level = 0, expToNextLevel = 1;
         do {
            level++;
            expToNextLevel += level * 10;
         } while (collectionExp >= expToNextLevel);

         bookLevel = level;
      } finally {
         lock.unlock();
      }
   }

   public int getBookLevel() {
      lock.lock();
      try {
         return bookLevel;
      } finally {
         lock.unlock();
      }
   }

   public Map<Integer, Integer> getCards() {
      lock.lock();
      try {
         return Collections.unmodifiableMap(cards);
      } finally {
         lock.unlock();
      }
   }

   public int getTotalCards() {
      lock.lock();
      try {
         return specialCard + normalCard;
      } finally {
         lock.unlock();
      }
   }

   public int getNormalCard() {
      lock.lock();
      try {
         return normalCard;
      } finally {
         lock.unlock();
      }
   }

   public int getSpecialCard() {
      lock.lock();
      try {
         return specialCard;
      } finally {
         lock.unlock();
      }
   }

   public void loadCards(final int characterId) {
      lock.lock();
      try {
         List<MonsterBookData> monsterBookData = DatabaseConnection.getInstance().withConnectionResult(connection -> MonsterBookProvider.getInstance().getDataForCharacter(connection, characterId)).orElse(Collections.emptyList());
         for (MonsterBookData bookData : monsterBookData) {
            if (bookData.cardId() / 1000 >= 2388) {
               specialCard++;
            } else {
               normalCard++;
            }
            cards.put(bookData.cardId(), bookData.level());
         }
      } finally {
         lock.unlock();
      }

      calculateLevel();
   }

   public void saveCards(final int characterId) {
      Set<Entry<Integer, Integer>> cardSet = getCardSet();

      if (cardSet.isEmpty()) {
         return;
      }
      DatabaseConnection.getInstance().withConnection(connection -> {
         MonsterBookAdministrator.getInstance().deleteForCharacter(connection, characterId);

         try {
            semaphore.acquireUninterruptibly();
            MonsterBookAdministrator.getInstance().save(connection, characterId, cardSet);
         } finally {
            semaphore.release();
         }
      });
   }

   public static int[] getCardTierSize() {
      return DatabaseConnection.getInstance().withConnectionResult(entityManager -> MonsterBookProvider.getInstance().getCardTierSize(entityManager)).orElseThrow();
   }
}
