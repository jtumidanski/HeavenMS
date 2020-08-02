package npc

import net.server.world.MapleParty
import net.server.world.MaplePartyCharacter
import scripting.event.EventManager
import scripting.npc.NPCConversationManager
import tools.I18nMessage

class NPC1061012 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   def start() {
      if (cm.getQuestStatus(6107) == 1 || cm.getQuestStatus(6108) == 1) {
         int ret = checkJob()
         if (ret == -1) {
            cm.sendOk(I18nMessage.from("1061012_FORM_A_PARTY"))
         } else if (ret == 0) {
            cm.sendOk(I18nMessage.from("1061012_PARTY_SIZE_REQUIREMENT"))
         } else if (ret == 1) {
            cm.sendOk(I18nMessage.from("1061012_INELLIGIBLE_JOB"))
         } else if (ret == 2) {
            cm.sendOk(I18nMessage.from("1061012_MEMBER_LEVEL_REQUIREMENT"))
         } else {
            EventManager em = cm.getEventManager("s4aWorld")
            if (em == null) {
               cm.sendOk(I18nMessage.from("1061012_UNKNOWN_REASON"))
            } else if (em.getProperty("started") == "true") {
               cm.sendOk(I18nMessage.from("1061012_SOMEONE_ELSE_ALREADY_ATTEMPTING"))
            } else {
               MaplePartyCharacter[] eli = em.getEligibleParty(cm.getParty().orElseThrow())
               if (eli.size() > 0) {
                  if (!em.startInstance(cm.getParty().orElseThrow(), cm.getPlayer().getMap(), 1)) {
                     cm.sendOk(I18nMessage.from("1061012_PARTY_MEMBER_ALREADY_REGISTERED"))
                  }
               } else {
                  cm.sendOk(I18nMessage.from("1061012_INVALID_PARTY_REQUIREMENT"))
               }
            }
         }
      } else {
         cm.sendOk(I18nMessage.from("1061012_NOT_ALLOWED_TO_ENTER_UNKNOWN"))
      }

      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {
   }

   def checkJob() {
      Optional<MapleParty> party = cm.getParty()

      if (party.isEmpty()) {
         return -1
      }
      //    if (party.getMembers().size() != 2) {
      //	return 0;
      //    }
      Iterator it = party.get().getMembers().iterator()

      while (it.hasNext()) {
         MaplePartyCharacter cPlayer = it.next()

         if (cPlayer.getJobId() == 312 || cPlayer.getJobId() == 322 || cPlayer.getJobId() == 900) {
            if (cPlayer.getLevel() < 120) {
               return 2
            }
         } else {
            return 1
         }
      }
      return 3
   }
}

NPC1061012 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC1061012(cm: cm))
   }
   return (NPC1061012) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }