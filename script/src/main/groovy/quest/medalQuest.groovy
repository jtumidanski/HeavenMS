package quest


import scripting.quest.QuestActionManager
import tools.I18nMessage
import tools.MessageBroadcaster
import tools.ServerNoticeType

class MedalQuest {
   QuestActionManager qm
   int status = -1

   def start(Byte mode, Byte type, Integer selection) {
      qm.forceStartQuest()
      qm.forceCompleteQuest()

      String medalName = qm.getMedalName()
      MessageBroadcaster.getInstance().sendServerNotice(qm.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("MEDAL_NOT_CODED").with(medalName))
      qm.earnTitle("<" + medalName + "> has been awarded.")
      qm.dispose()
   }

   def end(Byte mode, Byte type, Integer selection) {
      qm.forceCompleteQuest()

      String medalName = qm.getMedalName()
      MessageBroadcaster.getInstance().sendServerNotice(qm.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("MEDAL_NOT_CODED").with(medalName))
      qm.earnTitle("<" + medalName + "> has been awarded.")
      qm.dispose()
   }
}

MedalQuest getQuest() {
   if (!getBinding().hasVariable("quest")) {
      QuestActionManager qm = (QuestActionManager) getBinding().getVariable("qm")
      getBinding().setVariable("quest", new MedalQuest(qm: qm))
   }
   return (MedalQuest) getBinding().getVariable("quest")
}

def start(Byte mode, Byte type, Integer selection) {
   getQuest().start(mode, type, selection)
}

def end(Byte mode, Byte type, Integer selection) {
   getQuest().end(mode, type, selection)
}