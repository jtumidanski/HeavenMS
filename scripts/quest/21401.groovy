package quest

import scripting.event.EventManager
import scripting.quest.QuestActionManager
import server.life.MapleLifeFactory
import server.life.MapleMonster
import server.maps.MapleMap

import java.awt.Point

class Quest21401 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      status++
      if(mode == 0 && type == 0)
         status -= 2
      else if (mode != 1) {
         //if (mode == 0)
         qm.sendNext("#b(You need to think about this for a second...)#k")
         qm.dispose()
         return
      }

      if (status == 0) {
         qm.sendNext("Why do I look like this, you ask? I don't want to talk about it, but I suppose I can't hide from you since you're my master.")
      } else if (status == 1) {
         qm.sendNextPrev("While you were trapped inside ice for hundreds of years, I, too, was frozen. It was a long time to be away from you. That's when the seed of darkness was planted in my heart.")
      } else if (status == 2) {
         qm.sendNextPrev("But since you awoke, I thought the darkness had gone away. I thought things would return to the way they were. But I was mistaken...")
      } else if (status == 3) {
         qm.sendAcceptDecline("Please, Aran. Please stop me from becoming enraged. Only you can control me. It's getting out of my hands now. Please do whatever it takes to #rstop me from going berserk#k!")
      } else if (status == 4) {
         EventManager em = qm.getEventManager("MahaBattle")
         if (!em.startInstance(qm.getPlayer())) {
            qm.sendOk("There is currently someone in this map, come back later.")
         } else {
            qm.startQuest()
         }

         qm.dispose()
      }
   }

   def end(Byte mode, Byte type, Integer selection) {
      status++
      if(mode == 0 && type == 0)
         status -= 2
      else if (mode != 1) {
         //if (mode == 0)
         qm.sendNext("#b(You need to think about this for a second...)#k")
         qm.dispose()
         return
      }

      if (status == 0) {
         qm.sendNext("Thank you, Aran. If it weren't for you, I would have become enraged and who knows what could have happened. Thank you, NOT! It's only your duty as my master...")
      } else if(status == 1) {
         qm.sendYesNo("Anyway, I just noticed how high of a level you've reached. If you were able to control me in my state of rage, I think you're ready to handle more abilities.")
      } else if(status == 2) {
         if (!qm.isQuestCompleted(21401)) {
            if (!qm.canHold(1142132)) {
               qm.sendOk("Wow, your #bequip#k inventory is full. I need you to make at least 1 empty slot to complete this quest.")
               qm.dispose()
               return
            }
            if (!qm.canHold(2280003, 1)) {
               qm.sendOk("Hey, your #buse#k inventory is full. I need you to make at least 1 empty slot to complete this quest.")
               qm.dispose()
               return
            }

            qm.gainItem(1142132, true)
            qm.gainItem(2280003, (short) 1)
            qm.changeJobById(2112)

            qm.completeQuest()
         }
         qm.sendNext("Your skills have been restored. Those skills have been dormant for so long that you'll have to re-train yourself, but you'll be as good as new once you complete your training.")
      } else if (status == 3) {
         qm.dispose()
      }
   }

   static def spawnMob(x, y, int id, MapleMap map) {
      if(map.getMonsterById(id) != null)
         return

      MapleMonster mob = MapleLifeFactory.getMonster(id)
      map.spawnMonsterOnGroundBelow(mob, new Point(x, y))
   }
}

Quest21401 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21401(qm: qm))
   }
   return (Quest21401) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}