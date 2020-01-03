package quest

import client.MapleCharacter
import constants.game.ExpTable
import scripting.quest.QuestActionManager
import server.MapleItemInformationProvider
import server.QuestConsItem

class Quest20514 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {

   }

   def end(Byte mode, Byte type, Integer selection) {

   }

   def raiseOpen() {
      MapleCharacter chr = qm.getPlayer()
      byte questStatus = chr.getQuestStatus(qm.getQuest())

      if (questStatus == (byte) 0) {
         qm.setQuestProgress(20515, 0, chr.getLevel())
         qm.setQuestProgress(20515, 1, chr.getExp())
      } else if (questStatus == (byte) 1) {  // update mimiana progress...
         int diffExp = chr.getExp() - qm.getQuestProgressInt(20515, 1)

         int initLevel = qm.getQuestProgressInt(20515, 0)
         for (int i = initLevel; i < chr.getLevel(); i++) {
            diffExp += ExpTable.getExpNeededForLevel(i)
         }

         if (diffExp > 0) {
            QuestConsItem consItem = MapleItemInformationProvider.getInstance().getQuestConsumablesInfo(4220137)
            int exp = consItem.exp()
            int grade = consItem.grade()
            qm.setQuestProgress(20514, 0, Math.min(diffExp, exp * grade))
         }
      }

      qm.dispose()
   }
}

Quest20514 getQuest() {
   QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
   getBinding().setVariable("quest", new Quest20514(qm: qm))
   return (Quest20514) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}

def raiseOpen() {
   getQuest().raiseOpen()
}