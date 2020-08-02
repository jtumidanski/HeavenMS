package npc
import tools.I18nMessage

import net.server.world.MapleParty
import net.server.world.MaplePartyCharacter
import scripting.npc.NPCConversationManager

/*
	NPC Name: 		
	Map(s): 		
	Description: 	
*/


class NPC2103013 {
   NPCConversationManager cm
   int status = 0
   int selected = -1

   int party = 0

   def start() {
      cm.sendOk(I18nMessage.from("2103013_PYRAMID_PQ_IS_UNAVAILABLE"))
      cm.dispose()
   }

   def action(Byte mode, Byte type, Integer selection) {
      if (mode == 0 && type == 0) {
         status--
      } else if (mode < 0 || (type == 4 && mode == 0)) {
         cm.dispose()
         return
      } else {
         status++
      }

      if (cm.getMapId() == 926010000) {
         if (status == 0) {
            if (selection > -1) {
               selected = selection
            }
            if (selection == 0 || selected == 0) {
               cm.sendNext(I18nMessage.from("2103013_THIS_IS_THE_PYRAMID_OF_NETT"))
            } else if (selection == 1) {
               cm.sendSimple(I18nMessage.from("2103013_YOU_FOOLS"))
            } else if (selection == 2) {
               cm.openUI((byte) 0x16)
               cm.showInfoText("Use the Party Search (Hotkey O) window to search for a party to join anytime and anywhere!")
               cm.dispose()
            } else if (selection == 3) {
               cm.sendSimple(I18nMessage.from("2103013_WHAT_GEM"))
            } else if (selection == 4) {
               cm.sendNext(I18nMessage.from("2103013_INSIDE"))
            } else if (selection == 5) {
               int progress = cm.getQuestProgressInt(29932)
               if (progress >= 50000) {
                  cm.dispose()
               } else {
                  cm.sendNext("")
               }

            }
         } else if (status == 1) {
            if (selected == 0) {
               cm.sendNextPrev(I18nMessage.from("2103013_ONCE_YOU_ENTER"))
            } else if (selected == 1) {
               party = selection
               cm.sendSimple(I18nMessage.from("2103013_YOU_WHO_LACK_FEAR"))
            } else if (selected == 3) {
               if (selection == 0) {
                  if (cm.haveItem(4001322)) {
                     return
                  }
               } else if (selection == 1) {
                  if (cm.haveItem(4001323)) {
                     return
                  }
               } else if (selection == 2) {
                  if (cm.haveItem(4001324)) {
                     return
                  }
               } else if (selection == 3) {
                  if (cm.haveItem(4001325)) {
                     return
                  }
               }
               cm.sendOk(I18nMessage.from("2103013_ARE_YOU_SURE_YOU_HAVE_ONE"))
               cm.dispose()
            } else {
               if (!(selected == 5)) {
                  cm.dispose()
               }
            }
         } else if (status == 2) {
            if (selected == 0) {
               cm.sendNextPrev(I18nMessage.from("2103013_THE_REST_IS_IN_YOUR_HANDS"))
            } else if (selected == 1) {
               String pqMode = "EASY"
               //Finish this
               Optional<MapleParty> optionalParty = cm.getPlayer().getParty()
               if (party == 1) {
                  if (optionalParty.isEmpty()) {
                     cm.sendOk(I18nMessage.from("2103013_CREATE_A_PARTY"))
                     cm.dispose()
                     return
                  } else {
                     if (optionalParty.get().getMembers().size() < 2) {
                        cm.sendOk(I18nMessage.from("2103013_GET_MORE_MEMBERS"))
                        cm.dispose()
                        return
                     } else {
                        int i = 0
                        for (MaplePartyCharacter partyCharacter : optionalParty.get().getMembers()) {
                           if (i > 1) {
                              break
                           }
                           if (partyCharacter != null && partyCharacter.getMapId() == 926010000) {
                              i++
                           }
                        }
                        if (i < 2) {
                           cm.sendOk(I18nMessage.from("2103013_2_OR_MORE_PARTY_MEMBERS_IN_YOUR_MAP"))
                           cm.dispose()
                           return
                        }
                     }
                  }
               }

               if (cm.getPlayer().getLevel() < 40) {
                  cm.sendOk(I18nMessage.from("2103013_MUST_BE_LEVEL_40"))
                  cm.dispose()
                  return
               }
               if (selection < 3 && cm.getPlayer().getLevel() > 60) {
                  cm.sendOk(I18nMessage.from("2103013_NEED_TO_BE_LEVEL_60"))
                  cm.dispose()
                  return
               }
               if (selection == 1) {
                  pqMode = "NORMAL"
               } else if (selection == 2) {
                  pqMode = "HARD"
               } else if (selection == 3) {
                  pqMode = "HELL"
               }

               if (!cm.createPyramid(pqMode, party == 1)) {
                  cm.sendOk(I18nMessage.from("2103013_ALL_ROOMS_FULL"))
               }
               cm.dispose()
            }
         } else if (status == 3) {
            cm.dispose()
         }
      } else if (cm.getMapId() == 926020001) {
         if (status == 0) {
            if (selection == 0) {
               cm.dispose()
            }//:(
            else if (selection == 1) {
               cm.sendNext(I18nMessage.from("2103013_CHECK_TO_SEE_IF_YOU_HAVE_ETC_SPACE"))
            }

         } else if (status == 1) {
            int itemId = 4001325
            if (cm.getPlayer().getLevel() >= 60) {
               itemId = 4001325
            }
            if (cm.canHold(itemId)) {
               cm.gainItem(itemId)
               cm.warp(926010000)
            } else {
               cm.showInfoText("You must have at least 1 empty slot in your Etc window to receive the reward.")
            }

            cm.dispose()
         }
      } else {
         cm.warp(926010000)
         cm.getPlayer().setPartyQuest(null)
         cm.dispose()
      }
   }
}

NPC2103013 getNPC() {
   if (!getBinding().hasVariable("npc")) {
      NPCConversationManager cm = (NPCConversationManager) getBinding().getVariable("cm")
      getBinding().setVariable("npc", new NPC2103013(cm: cm))
   }
   return (NPC2103013) getBinding().getVariable("npc")
}

def start() {
   getNPC().start()
}

def action(Byte mode, Byte type, Integer selection) { getNPC().action(mode, type, selection) }