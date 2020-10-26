package quest


import scripting.quest.QuestActionManager
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

class UnidentifiedQuest {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      MessageBroadcaster.getInstance().sendServerNotice(qm.getPlayer(), ServerNoticeType.NOTICE, I18nMessage.from("QUEST_NOT_FOUND").with(qm.getQuestId()))
      qm.dispose()
   }

   def end(Byte mode, Byte type, Integer selection) {

   }
}

UnidentifiedQuest getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new UnidentifiedQuest(qm: qm))
   }
   return (UnidentifiedQuest) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}