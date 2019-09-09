package quest


import scripting.quest.QuestActionManager
import tools.MessageBroadcaster
import tools.ServerNoticeType

class QuestunidentifiedQuest {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      MessageBroadcaster.getInstance().sendServerNotice(qm.getPlayer(), ServerNoticeType.NOTICE, "Quest: " + qm.getQuest() + " is not found, please report this.")
      qm.dispose()
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

QuestunidentifiedQuest getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new QuestunidentifiedQuest(qm: qm))
   }
   return (QuestunidentifiedQuest) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}