package quest

import client.MapleBuffStat
import client.MapleCharacter
import scripting.quest.QuestActionManager

class Quest3941 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         qm.dispose()
      } else {
         if (mode == 0 && type > 0) {
            qm.dispose()
            return
         }

         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            if (!isTigunMorphed(qm.getPlayer())) {
               qm.sendNext("What's this? I can't simply give the Queen's silk to anyone, claiming they will hand it at once to the queen. Get out of my sights.")
               status = 1
               return
            }

            qm.sendNext("Tigun, what are you doing here?")
         } else if (status == 1) {
            qm.sendNext("The Queen wants her silk right now? Alright, I have them here. Hold on a moment.")
            qm.forceStartQuest()
         } else if (status == 2) {
            qm.dispose()
         }
      }
   }

   def end(Byte mode, Byte type, Integer selection) {
      if (mode == -1) {
         qm.dispose()
      } else {
         if (mode == 0 && type > 0) {
            qm.dispose()
            return
         }

         if (mode == 1) {
            status++
         } else {
            status--
         }

         if (status == 0) {
            if (!isTigunMorphed(qm.getPlayer())) {
               qm.sendNext("What's this? I can't simply give the Queen's silk to anyone, claiming they will hand it at once to the queen. Get out of my sights.")
               qm.dispose()
               return
            }

            if (qm.canHold(4031571, 1)) {
               qm.gainItem(4031571)

               qm.sendNext("There you go. Please deliver to the queen as soon as possible, Tigun, she gets really mad if things get delayed.")
               qm.forceCompleteQuest()
            } else {
               qm.sendNext("Hey, you're lacking space to hold this, man. I will stay with it while you arrange your backpack...")
            }
         } else if (status == 1) {
            qm.dispose()
         }
      }
   }

   static def isTigunMorphed(MapleCharacter ch) {
      return ch.getBuffSource(MapleBuffStat.MORPH) == 2210005
   }
}

Quest3941 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest3941(qm: qm))
   }
   return (Quest3941) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}