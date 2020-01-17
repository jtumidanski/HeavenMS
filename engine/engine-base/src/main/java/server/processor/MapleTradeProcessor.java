package server.processor;

import java.util.function.BiConsumer;

import client.MapleCharacter;
import client.inventory.Item;
import client.inventory.manipulator.MapleInventoryManipulator;
import client.inventory.manipulator.MapleKarmaManipulator;
import config.YamlConfig;
import database.AbstractQueryExecutor;
import net.server.coordinator.world.MapleInviteCoordinator;
import server.MapleTrade;
import server.MapleTradeResult;
import server.MapleTradeUtil;
import tools.LogHelper;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.I18nMessage;
import tools.packet.character.interaction.GetTradeResult;
import tools.packet.character.interaction.GetTradeStart;
import tools.packet.character.interaction.TradeChat;
import tools.packet.character.interaction.TradeInvite;
import tools.packet.character.interaction.TradePartnerAdd;

public class MapleTradeProcessor extends AbstractQueryExecutor {
   private static MapleTradeProcessor instance;

   public static MapleTradeProcessor getInstance() {
      if (instance == null) {
         instance = new MapleTradeProcessor();
      }
      return instance;
   }

   private MapleTradeProcessor() {
   }

   protected boolean checkCompleteHandshake(MapleTrade trade) {
      if (trade.getPartnerTrade().isEmpty() || trade.getOwner().getId() < trade.getPartnerTrade().get().getOwner().getId()) {
         return trade.checkTradeCompleteHandshake(true);
      } else {
         return trade.getPartnerTrade().get().checkTradeCompleteHandshake(false);
      }
   }

   public void completeTrade(MapleCharacter referenceCharacter) {
      if (referenceCharacter.getTrade().isEmpty()) {
         return;
      }
      MapleTrade referenceTrade = referenceCharacter.getTrade().get();
      if (referenceTrade.getPartnerTrade().isEmpty()) {
         return;
      }
      MapleTrade partnerTrade = referenceTrade.getPartnerTrade().get();

      if (checkCompleteHandshake(referenceTrade)) {
         referenceTrade.fetchExchangedItems();
         partnerTrade.fetchExchangedItems();

         if (!referenceTrade.fitsMeso()) {
            cancelTrade(referenceTrade.getOwner(), MapleTradeResult.UNSUCCESSFUL);
            MessageBroadcaster.getInstance().sendServerNotice(referenceCharacter, ServerNoticeType.PINK_TEXT, I18nMessage.from("NOT_ENOUGH_MESO_FOR_TRADE"));
            MessageBroadcaster.getInstance().sendServerNotice(partnerTrade.getOwner(), ServerNoticeType.PINK_TEXT, I18nMessage.from("NOT_ENOUGH_PARTNER_MESO_SPACE"));
            return;
         } else if (!partnerTrade.fitsMeso()) {
            cancelTrade(partnerTrade.getOwner(), MapleTradeResult.UNSUCCESSFUL);
            MessageBroadcaster.getInstance().sendServerNotice(referenceCharacter, ServerNoticeType.PINK_TEXT, I18nMessage.from("NOT_ENOUGH_PARTNER_MESO_SPACE"));
            MessageBroadcaster.getInstance().sendServerNotice(partnerTrade.getOwner(), ServerNoticeType.PINK_TEXT, I18nMessage.from("NOT_ENOUGH_MESO_FOR_TRADE"));
            return;
         }

         if (!referenceTrade.fitsInInventory()) {
            if (referenceTrade.fitsUniquesInInventory()) {
               cancelTrade(referenceTrade.getOwner(), MapleTradeResult.UNSUCCESSFUL);
               MessageBroadcaster.getInstance().sendServerNotice(referenceCharacter, ServerNoticeType.PINK_TEXT, I18nMessage.from("NOT_ENOUGH_INVENTORY_SPACE_FOR_TRADE"));
               MessageBroadcaster.getInstance().sendServerNotice(partnerTrade.getOwner(), ServerNoticeType.PINK_TEXT, I18nMessage.from("NOT_ENOUGH_PARTNER_INVENTORY_SPACE_FOR_TRADE"));
            } else {
               cancelTrade(referenceTrade.getOwner(), MapleTradeResult.UNSUCCESSFUL_UNIQUE_ITEM_LIMIT);
               MessageBroadcaster.getInstance().sendServerNotice(partnerTrade.getOwner(), ServerNoticeType.PINK_TEXT, I18nMessage.from("PARTNER_CANNOT_HOLD_MORE_THAN_ONE_OF_A_KIND"));
            }
            return;
         } else if (!partnerTrade.fitsInInventory()) {
            if (partnerTrade.fitsUniquesInInventory()) {
               cancelTrade(partnerTrade.getOwner(), MapleTradeResult.UNSUCCESSFUL);
               MessageBroadcaster.getInstance().sendServerNotice(referenceCharacter, ServerNoticeType.PINK_TEXT, I18nMessage.from("NOT_ENOUGH_PARTNER_INVENTORY_SPACE_FOR_TRADE"));
               MessageBroadcaster.getInstance().sendServerNotice(partnerTrade.getOwner(), ServerNoticeType.PINK_TEXT, I18nMessage.from("NOT_ENOUGH_INVENTORY_SPACE_FOR_TRADE"));
            } else {
               cancelTrade(partnerTrade.getOwner(), MapleTradeResult.UNSUCCESSFUL_UNIQUE_ITEM_LIMIT);
               MessageBroadcaster.getInstance().sendServerNotice(referenceCharacter, ServerNoticeType.PINK_TEXT, I18nMessage.from("PARTNER_CANNOT_HOLD_MORE_THAN_ONE_OF_A_KIND"));
            }
            return;
         }

         if (referenceTrade.getOwner().getLevel() < 15) {
            if (referenceTrade.getOwner().getMesosTraded() + referenceTrade.getExchangeMesos() > 1000000) {
               cancelTrade(referenceTrade.getOwner(), MapleTradeResult.NO_RESPONSE);
               MessageBroadcaster.getInstance().sendServerNotice(referenceTrade.getOwner(), ServerNoticeType.POP_UP, I18nMessage.from("LEVEL_TRADE_MESO_LIMIT"));
               return;
            } else {
               referenceTrade.getOwner().addMesosTraded(referenceTrade.getExchangeMesos());
            }
         } else if (partnerTrade.getOwner().getLevel() < 15) {
            if (partnerTrade.getOwner().getMesosTraded() + partnerTrade.getExchangeMesos() > 1000000) {
               cancelTrade(partnerTrade.getOwner(), MapleTradeResult.NO_RESPONSE);
               MessageBroadcaster.getInstance().sendServerNotice(partnerTrade.getOwner(), ServerNoticeType.POP_UP, I18nMessage.from("LEVEL_TRADE_MESO_LIMIT"));
               return;
            } else {
               partnerTrade.getOwner().addMesosTraded(partnerTrade.getExchangeMesos());
            }
         }

         LogHelper.logTrade(referenceTrade, partnerTrade);
         completeTrade(referenceTrade);
         completeTrade(partnerTrade);

         partnerTrade.getOwner().setTrade(null);
         referenceCharacter.setTrade(null);
      }
   }

   protected void completeTrade(MapleTrade referenceTrade) {
      byte result;
      boolean show = YamlConfig.config.server.USE_DEBUG;

      for (Item item : referenceTrade.getExchangeItems()) {
         MapleKarmaManipulator.toggleKarmaFlagToUntradeable(item);
         MapleInventoryManipulator.addFromDrop(referenceTrade.getOwner().getClient(), item, show);
      }

      if (referenceTrade.getExchangeMesos() > 0) {
         int fee = MapleTradeUtil.getFee(referenceTrade.getExchangeMesos());

         referenceTrade.getOwner().gainMeso(referenceTrade.getExchangeMesos() - fee, show, true, show);
         if (fee > 0) {
            MessageBroadcaster.getInstance().sendServerNotice(referenceTrade.getOwner(), ServerNoticeType.POP_UP, I18nMessage.from("TRADE_COMPLETE_MESO_WITH_FEE").with(referenceTrade.getExchangeMesos() - fee));
         } else {
            MessageBroadcaster.getInstance().sendServerNotice(referenceTrade.getOwner(), ServerNoticeType.POP_UP, I18nMessage.from("TRADE_COMPLETE").with(referenceTrade.getExchangeMesos()));
         }

         result = MapleTradeResult.NO_RESPONSE.getValue();
      } else {
         result = MapleTradeResult.SUCCESSFUL.getValue();
      }

      referenceTrade.clear();
      PacketCreator.announce(referenceTrade.getOwner(), new GetTradeResult(referenceTrade.getNumber(), result));
   }

   public void cancelTrade(MapleCharacter referenceCharacter, MapleTradeResult result) {
      referenceCharacter.getTrade().ifPresent(trade -> cancelHandshake(trade, result.getValue()));
   }

   protected void cancelHandshake(MapleTrade referenceTrade, byte result) {
      if (referenceTrade.getPartnerTrade().isEmpty() || referenceTrade.getOwner().getId() < referenceTrade.getPartnerTrade().get().getOwner().getId()) {
         referenceTrade.tradeCancelHandshake(true, result);
      } else {
         referenceTrade.getPartnerTrade().get().tradeCancelHandshake(false, result);
      }
   }

   public void startTrade(MapleCharacter referenceCharacter) {
      if (referenceCharacter.getTrade().isEmpty()) {
         referenceCharacter.setTrade(new MapleTrade((byte) 0, referenceCharacter));
      }
   }

   protected boolean hasTradeInviteBack(MapleCharacter character1, MapleCharacter character2) {
      return character2.getTrade()
            .flatMap(MapleTrade::getPartnerTrade)
            .map(otherPartner -> otherPartner.getOwner().getId() == character1.getId())
            .orElse(false);
   }

   public void inviteTrade(MapleCharacter character1, MapleCharacter character2) {
      if (MapleInviteCoordinator.hasInvite(MapleInviteCoordinator.InviteType.TRADE, character1.getId())) {
         if (hasTradeInviteBack(character1, character2)) {
            MessageBroadcaster.getInstance().sendServerNotice(character1, ServerNoticeType.PINK_TEXT, I18nMessage.from("HAS_TRADE_INVITE_BACK_FROM_PLAYER"));
         } else {
            MessageBroadcaster.getInstance().sendServerNotice(character1, ServerNoticeType.PINK_TEXT, I18nMessage.from("HAS_TRADE_INVITE_BACK"));
         }
         return;
      } else if (character1.getTrade().map(MapleTrade::isFullTrade).orElse(false)) {
         MessageBroadcaster.getInstance().sendServerNotice(character1, ServerNoticeType.PINK_TEXT, I18nMessage.from("ALREADY_IN_TRADE"));
         return;
      }

      if (MapleInviteCoordinator.createInvite(MapleInviteCoordinator.InviteType.TRADE, character1, character1.getId(), character2.getId())) {
         if (character2.getTrade().isEmpty()) {
            MapleTrade trade1 = character1.getTrade().orElseThrow();
            MapleTrade trade2 = new MapleTrade((byte) 1, character2);

            character2.setTrade(trade2);
            trade1.setPartnerTrade(trade2);
            trade2.setPartnerTrade(trade1);

            PacketCreator.announce(character1, new GetTradeStart(character1, trade1, (byte) 0));
            PacketCreator.announce(character2, new TradeInvite(character1));
         } else {
            MessageBroadcaster.getInstance().sendServerNotice(character1, ServerNoticeType.PINK_TEXT, I18nMessage.from("OTHER_PLAYER_ALREADY_IN_TRADE"));
            cancelTrade(character1, MapleTradeResult.NO_RESPONSE);
            MapleInviteCoordinator.answerInvite(MapleInviteCoordinator.InviteType.TRADE, character2.getId(), character1.getId(), false);
         }
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(character1, ServerNoticeType.PINK_TEXT, I18nMessage.from("OTHER_PLAYER_ALREADY_INVITED_TO_TRADE"));
         cancelTrade(character1, MapleTradeResult.NO_RESPONSE);
      }
   }

   protected void consistentTrade(MapleCharacter character1, MapleCharacter character2, BiConsumer<MapleTrade, MapleTrade> success, Runnable failure) {
      if (character1.getTrade().isEmpty() || character2.getTrade().isEmpty()) {
         failure.run();
         return;
      }
      MapleTrade trade1 = character1.getTrade().get();
      MapleTrade trade2 = character2.getTrade().get();

      if (trade1.getPartnerTrade().isEmpty() || trade1.getPartnerTrade().get() != trade2) {
         failure.run();
         return;
      } else if (trade2.getPartnerTrade().isEmpty() || trade2.getPartnerTrade().get() != trade1) {
         failure.run();
         return;
      }

      success.accept(trade1, trade2);
   }

   public void visitTrade(MapleCharacter character1, MapleCharacter character2) {
      MapleInviteCoordinator.MapleInviteResult inviteRes = MapleInviteCoordinator.answerInvite(MapleInviteCoordinator.InviteType.TRADE, character1.getId(), character2.getId(), true);

      MapleInviteCoordinator.InviteResult res = inviteRes.result;
      if (res != MapleInviteCoordinator.InviteResult.ACCEPTED) {
         MessageBroadcaster.getInstance().sendServerNotice(character1, ServerNoticeType.PINK_TEXT, I18nMessage.from("TRADE_ALREADY_RESCINDED"));
         cancelTrade(character1, MapleTradeResult.NO_RESPONSE);
         return;
      }

      consistentTrade(character1, character2, (trade1, trade2) -> {
         PacketCreator.announce(character2, new TradePartnerAdd(character1));
         PacketCreator.announce(character1, new GetTradeStart(character1, trade1, (byte) 1));
         trade1.setFullTrade(true);
         trade2.setFullTrade(true);
      }, () -> MessageBroadcaster.getInstance().sendServerNotice(character1, ServerNoticeType.PINK_TEXT, I18nMessage.from("TRADE_ALREADY_CLOSED_BY_OTHER_PLAYER")));
   }

   public void declineTrade(MapleCharacter referenceCharacter) {
      referenceCharacter.getTrade().ifPresent(trade -> {
         trade.getPartnerCharacter().ifPresent(partnerCharacter -> {
            if (MapleInviteCoordinator.answerInvite(MapleInviteCoordinator.InviteType.TRADE, referenceCharacter.getId(), partnerCharacter.getId(), false).result == MapleInviteCoordinator.InviteResult.DENIED) {
               MessageBroadcaster.getInstance().sendServerNotice(partnerCharacter, ServerNoticeType.PINK_TEXT, I18nMessage.from("TRADE_DECLINED_BY_PLAYER").with(referenceCharacter.getName()));
            }

            partnerCharacter.getTrade().ifPresent(otherTrade -> otherTrade.cancel(MapleTradeResult.PARTNER_CANCEL.getValue()));
            partnerCharacter.setTrade(null);
         });
         trade.cancel(MapleTradeResult.NO_RESPONSE.getValue());
         referenceCharacter.setTrade(null);
      });
   }

   public void chat(MapleTrade referenceTrade, String message) {
      PacketCreator.announce(referenceTrade.getOwner(), new TradeChat(referenceTrade.getOwner().getName(), message, true));
      referenceTrade.getPartnerTrade().ifPresent(partner -> PacketCreator.announce(partner.getOwner(), new TradeChat(referenceTrade.getOwner().getName(), message, false)));
   }
}