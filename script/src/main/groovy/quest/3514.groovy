package quest

import client.MapleBuffStat
import client.MapleCharacter
import scripting.quest.QuestActionManager

class Quest3514 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (qm.getPlayer().getMeso() >= 1000000) {
         if (qm.canHold(2022337, 1)) {
            qm.gainItem(2022337, (short) 1)
            qm.gainMeso(-1000000)

            //qm.sendOk("Nice doing business with you~~.");
            qm.startQuest(3514)
         } else {
            qm.sendOk("Check out for a slot on your USE inventory first.")
         }
      } else {
         qm.sendOk("Oy, you don't have the money. I charge #r1,000,000 mesos#k for the emotion potion. No money, no deal.")
      }

      qm.dispose()
   }

   def end(Byte mode, Byte type, Integer selection) {
      if (mode == 0 && type == 0) {
         status--
      } else if (mode == -1) {
         qm.dispose()
         return
      } else {
         status++
      }

      if (status == 0) {
         if (!usedPotion(qm.getPlayer())) {
            if (qm.haveItem(2022337)) {
               qm.sendOk("Are you scared to drink the potion? I can assure you it has only a minor #rside effect#k.")
            } else {
               if (qm.canHold(2022337)) {
                  qm.gainItem(2022337, (short) 1)
                  qm.sendOk("Lost it? Luckily for you I managed to recover it back. Take it.")
               } else {
                  qm.sendOk("Lost it? Luckily for you I managed to recover it back. Make a room to get it.")
               }
            }

            qm.dispose()
         } else {
            qm.sendOk("It seems the potion worked and your emotions are no longer frozen. And, oh, my... You're ailing bad, #bpurge#k that out quickly.")
         }
      } else if (status == 1) {
         qm.gainExp(891500)
         qm.completeQuest(3514)
         qm.dispose()
      }
   }

   static def usedPotion(MapleCharacter ch) {
      return ch.getBuffSource(MapleBuffStat.HP_RECOVERY) == 2022337
   }
}

Quest3514 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest3514(qm: qm))
   }
   return (Quest3514) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}