package quest


import scripting.quest.QuestActionManager
import tools.MessageBroadcaster
import tools.ServerNoticeType

class QuestmedalQuest {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      qm.forceStartQuest()
      qm.forceCompleteQuest()

      String medalname = qm.getMedalName()
      MessageBroadcaster.getInstance().sendServerNotice(qm.getPlayer(), ServerNoticeType.PINK_TEXT, "<" + medalname + "> is not coded.")
      qm.earnTitle("<" + medalname + "> has been awarded.")
      qm.dispose()
   }

   def end(Byte mode, Byte type, Integer selection) {
      qm.forceCompleteQuest()

      String medalname = qm.getMedalName()
      MessageBroadcaster.getInstance().sendServerNotice(qm.getPlayer(), ServerNoticeType.PINK_TEXT, "<" + medalname + "> is not coded.")
      qm.earnTitle("<" + medalname + "> has been awarded.")
      qm.dispose()
   }
}

QuestmedalQuest getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new QuestmedalQuest(qm: qm))
   }
   return (QuestmedalQuest) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}