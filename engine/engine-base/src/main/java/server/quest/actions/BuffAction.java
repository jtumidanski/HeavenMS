package server.quest.actions;

import client.MapleCharacter;
import provider.MapleData;
import provider.MapleDataTool;
import server.MapleItemInformationProvider;
import server.quest.MapleQuestActionType;

public class BuffAction extends MapleQuestAction {
   private int itemEffect;

   public BuffAction(int questId, MapleData data) {
      super(questId, MapleQuestActionType.BUFF);
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
