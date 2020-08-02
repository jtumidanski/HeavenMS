package npc
import tools.I18nMessage


import scripting.AbstractPlayerInteraction
import scripting.event.EventInstanceManager
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC9201007 {
   NPCConversationManager cm
   int status = -1
   int sel = -1

   EventInstanceManager eim
   boolean hasEngage
   boolean hasRing

   def start() {
      eim = cm.getEventInstance()
      if (eim == null) {
         cm.warp(680000000, 0)
         cm.dispose()
         return
      }

      if (cm.getMapId() == 680000200) {
         if (eim.getIntProperty("weddingStage") == 0) {
            cm.sendNext(I18nMessage.from("9201007_GUESTS_ARE_GATHERING"))
         } else {
            cm.warp(680000210, "sp")
            cm.sendNext(I18nMessage.from("9201007_PICK_YOUR_SEAT"))
         }

         cm.dispose()
      } else {
         if (cm.getPlayer().getId() != eim.getIntProperty("groomId") && cm.getPlayer().getId() != eim.getIntProperty("brideId")) {
            cm.sendNext(I18nMessage.from("9201007_NO_TALKING"))
            cm.dispose()
            return
         }

         hasEngage = false
         for (int i = 4031357; i <= 4031364; i++) {
            if (cm.haveItem(i)) {
               hasEngage = true
               break
            }
         }

         int[] rings = [1112806, 1112803, 1112807, 1112809]
         hasRing = false
         for (int i = 0; i < rings.length; i++) {
            if (cm.getPlayer().haveItemWithId(rings[i], true)) {
               hasRing = true
            }
         }

         status = -1
         action((byte) 1, (byte) 0, 0)
      }
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == -1 || mode == 0) {
         cm.sendOk(I18nMessage.from("9201007_GOOD_BYE"))
         cm.dispose()
         return
      } else if (mode == 1) {
         status++
      } else {
         status--
      }

      if (status == 0) {
         boolean hasGoldenLeaf = cm.haveItem(4000313)

         if (hasGoldenLeaf && hasEngage) {
            cm.sendOk(I18nMessage.from("9201007_YOU_CANNOT_LEAVE_YET"))
            cm.dispose()
         } else if (hasGoldenLeaf && hasRing) {
            String[] choice = ["Go to the after party", "What should I be doing"]
            String msg = "What can I help you with?#b"
            for (int i = 0; i < choice.length; i++) {
               msg += "\r\n#L" + i + "#" + choice[i] + "#l"
            }
            cm.sendSimple(msg)
         } else {
            cm.sendNext(I18nMessage.from("9201007_MUST_NOT_BELONG"))
         }
      } else if (status == 1) {
         AbstractPlayerInteraction cmPartner = cm.getMap().getCharacterById(cm.getPlayer().getPartnerId()).getAbstractPlayerInteraction()

         switch (selection) {
            case 0:
               if (eim.getIntProperty("isPremium") == 1) {
                  eim.warpEventTeam(680000300)
                  cm.sendOk(I18nMessage.from("9201007_CHERISH_YOUR_PHOTOS"))
                  if (cmPartner != null) {
                     cmPartner.npcTalk(cm.getNpc(), "Enjoy! Cherish your Photos Forever!")
                  }
               } else {    // skip the party-time (premium only)
                  eim.warpEventTeam(680000500)
                  cm.sendOk(I18nMessage.from("9201007_CONGRATULATIONS"))
                  if (cmPartner != null) {
                     cmPartner.npcTalk(cm.getNpc(), "Congratulations for the newly-wed! I will escort you to the exit.")
                  }
               }

               cm.dispose()
               break

            case 1:
               cm.sendOk(I18nMessage.from("9201007_RECEIVE_THE_BLESSINGS"))
               cm.dispose()
               break

            default:
               cm.warp(680000000, 0)
               cm.dispose()
               break
         }
      }
   }
}

NPC9201007 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC9201007(cm: cm))
   }
   return (NPC9201007) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }