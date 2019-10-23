/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
		       Matthias Butz <matze@odinms.de>
		       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;

import client.database.administrator.MonsterBookAdministrator;
import client.database.data.MonsterBookData;
import client.database.provider.MonsterBookProvider;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.factory.MonitoredReentrantLockFactory;
import tools.DatabaseConnection;
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

   private Set<Entry<Integer, Integer>> getCardSet() {
      lock.lock();
      try {
         return Collections.unmodifiableSet(cards.entrySet());
      } finally {
         lock.unlock();
      }
   }

   public void addCard(final MapleClient c, final int cardid) {
      MasterBroadcaster.getInstance().sendToAllInMap(c.getPlayer().getMap(), new ShowForeignCardEffect(c.getPlayer().getId()), false, c.getPlayer());

      Integer qty;
      lock.lock();
      try {
         qty = cards.get(cardid);

         if (qty != null) {
            if (qty < 5) {
               cards.put(cardid, qty + 1);
            }
         } else {
            cards.put(cardid, 1);
            qty = 0;

            if (cardid / 1000 >= 2388) {
               specialCard++;
            } else {
               normalCard++;
            }
         }
      } finally {
         lock.unlock();
      }

      if (qty < 5) {
         calculateLevel();   // current leveling system only accounts unique cards...
         PacketCreator.announce(c, new SetCard(false, cardid, qty + 1));
         PacketCreator.announce(c, new ShowGainCard());
      } else {
         PacketCreator.announce(c, new SetCard(true, cardid, 5));
      }
   }

   private void calculateLevel() {
      lock.lock();
      try {
         bookLevel = (int) Math.max(1, Math.sqrt((normalCard + specialCard) / 5));
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

   public void loadCards(final int charid) {
      lock.lock();
      try {
         List<MonsterBookData> monsterBookData = DatabaseConnection.getInstance().withConnectionResult(connection -> MonsterBookProvider.getInstance().getDataForCharacter(connection, charid)).orElse(Collections.emptyList());
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

   public void saveCards(final int charid) {
      Set<Entry<Integer, Integer>> cardSet = getCardSet();

      if (cardSet.isEmpty()) {
         return;
      }
      DatabaseConnection.getInstance().withConnection(connection -> {
         MonsterBookAdministrator.getInstance().deleteForCharacter(connection, charid);

         try {
            semaphore.acquireUninterruptibly();
            MonsterBookAdministrator.getInstance().save(connection, charid, cardSet);
         } finally {
            semaphore.release();
         }
      });
   }
}
