package quest

import constants.ServerConstants
import scripting.quest.QuestActionManager

class Quest21302 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {

   }

   def end(Byte mode, Byte type, Integer selection) {
      status++
      if (mode != 1) {
         if (mode == 0 && type == 1) {
            qm.sendNext("Hey! At least say you tried!")
         }
         qm.dispose()
         return
      }
      if (status == 0) {
         qm.sendNext("Wait.. Isn't that.. Did you remember how to make Red Jade?\r\nWow... you may be stupid and prone to amnesia, but this is why I can't abandon you. Now give me the jade!")
         //Giant Polearm
      } else if (status == 1) {
         qm.sendNextPrev("Okay, now that I have the Red Jade back on, let me work on reawakening more of your abilities. I mean, your level's gone much higher since the last time we met, so I am sure I can work my magic a bit more this time!")
      } else if (status == 2) {
         if (!qm.isQuestCompleted(21302)) {
            if (!qm.canHold(1142131)) {
               qm.sendOk("Wow, your #bequip#k inventory is full. I need you to make at least 1 empty slot to complete this quest.")
               qm.dispose()
               return
            }

            if (qm.haveItem(4032312, 1)) {
               qm.gainItem(4032312, (short) -1)
            }

            qm.gainItem(1142131, true)
            qm.changeJobById(2111)

            if (ServerConstants.USE_FULL_ARAN_SKILLSET) {
               qm.teachSkill(21110002, (byte) 0, (byte) 20, -1)   //full swing
            }

            qm.completeQuest()
         }

         qm.sendNext("Come on, keep training so you can get all your abilities back, and that way we can explore together once more!")
      } else if (status == 3) {
         qm.dispose()
      }
   }
}

Quest21302 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest21302(qm: qm))
   }
   return (Quest21302) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}