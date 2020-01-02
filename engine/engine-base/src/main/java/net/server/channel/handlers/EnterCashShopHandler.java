package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import client.processor.CashShopProcessor;
import net.server.packet.NoOpPacket;
import net.server.packet.reader.NoOpReader;
import server.CashShop;
import tools.PacketCreator;
import tools.packet.SetCashShop;
import tools.packet.cashshop.ShowCash;
import tools.packet.cashshop.operation.ShowCashInventory;
import tools.packet.cashshop.operation.ShowWishList;

public class EnterCashShopHandler extends AbstractShopSystem<NoOpPacket> {
   @Override
   public Class<NoOpReader> getReaderClass() {
      return NoOpReader.class;
   }

   @Override
   protected boolean featureDisabled(MapleClient client) {
      return client.getPlayer().cannotEnterCashShop();
   }

   @Override
   protected boolean failsShopSpecificValidation(MapleClient client) {
      return client.getPlayer().getCashShop().isOpened();
   }

   @Override
   protected void openShop(MapleClient client) {
      MapleCharacter character = client.getPlayer();
      CashShop cashShop = character.getCashShop();
      PacketCreator.announce(client, new SetCashShop(client));
      PacketCreator.announce(client, new ShowCashInventory(client.getAccID(), cashShop.getInventory(), character.getStorage().getSlots(), client.getCharacterSlots()));
      CashShopProcessor.getInstance().showGifts(character, cashShop);
      PacketCreator.announce(client, new ShowWishList(cashShop.getWishList(), false));
      PacketCreator.announce(client, new ShowCash(character.getCashShop().getCash(1), character.getCashShop().getCash(2), character.getCashShop().getCash(4)));
      character.getCashShop().open(true);
   }

   @Override
   public void handlePacket(NoOpPacket packet, MapleClient client) {
      genericHandle(client);
   }
}
