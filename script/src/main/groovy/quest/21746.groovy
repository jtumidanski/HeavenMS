package quest


import scripting.quest.QuestActionManager
import server.maps.MapleMap

class Quest21746 {
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
            qm.sendNext("If you want to know more about the Seal Rock of Mu Lung, you will need to pass my test. Prove your valor overpowering me in melee combat, only then I shall recognize you as a worthy knight.")
         } else {
            MapleMap mapobj = qm.getWarpMap(925040001)
            if (mapobj.countPlayers() == 0) {
               mapobj.resetPQ(1)

               qm.warp(925040001, 0)
               qm.forceStartQuest()
            } else {
               qm.sendOk("Someone is already attempting a challenge. Wait for them to finish before you enter.")
            }


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
            qm.sendNext("Oh, you brought the ink. Now let me pour it, cautiously.... Almost there, almost. ... ..... Kyaaa! Th-the letter. It says: 'I'll be there to take your Seal Rock of Mu Lung.'")
         } else if (status == 1) {
            qm.gainItem(4032342, (short) -8)
            qm.gainItem(4220151, (short) -1)
            qm.gainExp(10000)

            qm.forceCompleteQuest()
            qm.dispose()
         }
      }
   }
}

Quest21746 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21746(qm: qm))
   }
   return (Quest21746) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}