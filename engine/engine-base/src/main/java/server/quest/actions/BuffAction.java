package server.quest.actions;

import client.MapleCharacter;
import provider.MapleData;
import provider.MapleDataTool;
import server.MapleItemInformationProvider;
import server.quest.MapleQuest;
import server.quest.MapleQuestActionType;

public class BuffAction extends MapleQuestAction {
   int itemEffect;

   public BuffAction(MapleQuest quest, MapleData data) {
      super(MapleQuestActionType.BUFF, quest);
      processData(data);
   }

   @Override
   public boolean check(MapleCharacter chr, Integer extSelection) {
      return true;
   }

   @Override
   public void processData(MapleData data) {
      itemEffect = MapleDataTool.getInt(data);
   }

   @Override
   public void run(MapleCharacter chr, Integer extSelection) {
      MapleItemInformationProvider.getInstance().getItemEffect(itemEffect).applyTo(chr);
   }
} 
