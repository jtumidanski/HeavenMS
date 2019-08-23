package quest


import scripting.quest.QuestActionManager

class Quest2126 {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {

   }

   def end(Byte mode, Byte type, Integer selection) {
      if (!qm.haveItem(4031619, 1)) {
         qm.sendOk("Please bring me the box with the supplies that lies with #b#p2012019##k...")
      } else {
         qm.gainItem(4031619, (short) -1)
         qm.sendOk("Oh, you brought #p2012019#'s box! Thank you.")
         qm.forceCompleteQuest()
      }

      qm.dispose()
   }
}

Quest2126 getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new Quest2126(qm: qm))
   }
   return (Quest2126) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}