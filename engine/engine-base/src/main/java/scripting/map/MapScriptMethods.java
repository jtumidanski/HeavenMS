/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation version 3 as published by
the Free Software Foundation. You may not use, modify or distribute
this program under any other version of the GNU Affero General Public
License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package scripting.map;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleQuestStatus;
import scripting.AbstractPlayerInteraction;
import server.quest.MapleQuest;
import tools.PacketCreator;
import tools.packet.foreigneffect.ShowTitleEarned;
import tools.packet.quest.ShowQuestComplete;
import tools.packet.showitemgaininchat.ShowInfo;
import tools.packet.showitemgaininchat.ShowIntro;

public class MapScriptMethods extends AbstractPlayerInteraction {

   private String rewardstring = " title has been rewarded. Please see NPC Dalair to receive your Medal.";

   public MapScriptMethods(MapleClient c) {
      super(c);
   }

   public void displayCygnusIntro() {
      switch (c.getPlayer().getMapId()) {
         case 913040100:
            lockUI();
            PacketCreator.announce(c, new ShowInfo("Effect/Direction.img/cygnusJobTutorial/Scene0"));
            break;
         case 913040101:
            PacketCreator.announce(c, new ShowInfo("Effect/Direction.img/cygnusJobTutorial/Scene1"));
            break;
         case 913040102:
            PacketCreator.announce(c, new ShowInfo("Effect/Direction.img/cygnusJobTutorial/Scene2"));
            break;
         case 913040103:
            PacketCreator.announce(c, new ShowInfo("Effect/Direction.img/cygnusJobTutorial/Scene3"));
            break;
         case 913040104:
            PacketCreator.announce(c, new ShowInfo("Effect/Direction.img/cygnusJobTutorial/Scene4"));
            break;
         case 913040105:
            PacketCreator.announce(c, new ShowInfo("Effect/Direction.img/cygnusJobTutorial/Scene5"));
            break;
         case 913040106:
            lockUI();
            PacketCreator.announce(c, new ShowInfo("Effect/Direction.img/cygnusJobTutorial/Scene6"));
            break;
      }
   }

   public void displayAranIntro() {
      switch (c.getPlayer().getMapId()) {
         case 914090010:
            lockUI();
            PacketCreator.announce(c, new ShowIntro("Effect/Direction1.img/aranTutorial/Scene0"));
            break;
         case 914090011:
            PacketCreator.announce(c, new ShowIntro("Effect/Direction1.img/aranTutorial/Scene1" + c.getPlayer().getGender()));
            break;
         case 914090012:
            PacketCreator.announce(c, new ShowIntro("Effect/Direction1.img/aranTutorial/Scene2" + c.getPlayer().getGender()));
            break;
         case 914090013:
            PacketCreator.announce(c, new ShowIntro("Effect/Direction1.img/aranTutorial/Scene3"));
            break;
         case 914090100:
            lockUI();
            PacketCreator.announce(c, new ShowIntro("Effect/Direction1.img/aranTutorial/HandedPoleArm" + c.getPlayer().getGender()));
            break;
      }
   }

   public void startExplorerExperience() {
      if (c.getPlayer().getMapId() == 1020100) //Swordman
      {
         PacketCreator.announce(c, new ShowIntro("Effect/Direction3.img/swordman/Scene" + c.getPlayer().getGender()));
      } else if (c.getPlayer().getMapId() == 1020200) //Magician
      {
         PacketCreator.announce(c, new ShowIntro("Effect/Direction3.img/magician/Scene" + c.getPlayer().getGender()));
      } else if (c.getPlayer().getMapId() == 1020300) //Archer
      {
         PacketCreator.announce(c, new ShowIntro("Effect/Direction3.img/archer/Scene" + c.getPlayer().getGender()));
      } else if (c.getPlayer().getMapId() == 1020400) //Rogue
      {
         PacketCreator.announce(c, new ShowIntro("Effect/Direction3.img/rogue/Scene" + c.getPlayer().getGender()));
      } else if (c.getPlayer().getMapId() == 1020500) //Pirate
      {
         PacketCreator.announce(c, new ShowIntro("Effect/Direction3.img/pirate/Scene" + c.getPlayer().getGender()));
      }
   }

   public void goAdventure() {
      lockUI();
      PacketCreator.announce(c, new ShowIntro("Effect/Direction3.img/goAdventure/Scene" + c.getPlayer().getGender()));
   }

   public void goLith() {
      lockUI();
      PacketCreator.announce(c, new ShowIntro("Effect/Direction3.img/goLith/Scene" + c.getPlayer().getGender()));
   }

   public void explorerQuest(short questid, String questName) {
      MapleQuest quest = MapleQuest.getInstance(questid);
      if (!isQuestStarted(questid)) {
         if (!quest.forceStart(getPlayer(), 9000066)) {
            return;
         }
      }
      MapleQuestStatus q = getPlayer().getQuest(quest);
      if (!q.addMedalMap(getPlayer().getMapId())) {
         return;
      }
      String status = Integer.toString(q.getMedalProgress());
      String infoex = q.getInfoEx(0);
      getPlayer().announceUpdateQuest(MapleCharacter.DelayedQuestUpdate.UPDATE, q, true);
      StringBuilder smp = new StringBuilder();
      StringBuilder etm = new StringBuilder();
      if (status.equals(infoex)) {
         etm.append("Earned the ").append(questName).append(" title!");
         smp.append("You have earned the <").append(questName).append(">").append(rewardstring);
         PacketCreator.announce(getPlayer(), new ShowQuestComplete(quest.getId()));
      } else {
         PacketCreator.announce(getPlayer(), new ShowTitleEarned(status + "/" + infoex + " regions explored."));
         etm.append("Trying for the ").append(questName).append(" title.");
         smp.append("You made progress on the ").append(questName).append(" title. ").append(status).append("/").append(infoex);
      }
      PacketCreator.announce(getPlayer(), new ShowTitleEarned(etm.toString()));
      showInfoText(smp.toString());
   }

   public void touchTheSky() { //29004
      MapleQuest quest = MapleQuest.getInstance(29004);
      if (!isQuestStarted(29004)) {
         if (!quest.forceStart(getPlayer(), 9000066)) {
            return;
         }
      }
      MapleQuestStatus q = getPlayer().getQuest(quest);
      if (!q.addMedalMap(getPlayer().getMapId())) {
         return;
      }
      String status = Integer.toString(q.getMedalProgress());
      getPlayer().announceUpdateQuest(MapleCharacter.DelayedQuestUpdate.UPDATE, q, true);
      PacketCreator.announce(getPlayer(), new ShowTitleEarned(status + "/5 Completed"));
      PacketCreator.announce(getPlayer(), new ShowTitleEarned("The One Who's Touched the Sky title in progress."));
      if (Integer.toString(q.getMedalProgress()).equals(q.getInfoEx(0))) {
         showInfoText("The One Who's Touched the Sky" + rewardstring);
         PacketCreator.announce(getPlayer(), new ShowQuestComplete(quest.getId()));
      } else {
         showInfoText("The One Who's Touched the Sky title in progress. " + status + "/5 Completed");
      }
   }
}
