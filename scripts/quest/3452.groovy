package quest

import client.inventory.MapleInventoryType
import scripting.quest.QuestActionManager

class Quest3452 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {

   }

   def end(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         qm.dispose()
      } else {
         if (status == 0) {
            qm.sendNext("Take these #bMana Elixir Pills#k as a token of my gratitude.")
         } else if (status == 1) {
            if (qm.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() >= 1) {
               qm.gainItem(4000099, (short) -1)
               qm.gainItem(2000011, (short) 50)
               qm.gainExp(8000)
               qm.forceCompleteQuest()
            } else {
               qm.sendNext("Hm? It looks like your inventory is full.")
            }

            qm.dispose()
         }
      }
   }
}

Quest3452 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest3452(qm: qm))
   }
   return (Quest3452) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}