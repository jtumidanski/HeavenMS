package client.processor;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;

import client.MapleCharacter;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.ItemFactory;
import client.inventory.MapleInventoryType;
import config.YamlConfig;
import constants.inventory.ItemConstants;
import database.DatabaseConnection;
import database.administrator.AccountAdministrator;
import database.provider.AccountProvider;
import rest.RestService;
import rest.UriBuilder;
import rest.cashshop.GiftsResponse;
import rest.cashshop.WishListItem;
import rest.cashshop.WishListResponse;
import server.CashShop;
import tools.LoggerOriginator;
import tools.LoggerUtil;
import tools.PacketCreator;
import tools.Pair;
import tools.packet.Gift;
import tools.packet.cashshop.operation.ShowGifts;

public class CashShopProcessor {
   private static CashShopProcessor instance;

   public static CashShopProcessor getInstance() {
      if (instance == null) {
         instance = new CashShopProcessor();
      }
      return instance;
   }

   private CashShopProcessor() {
   }

   /**
    * Gets the appropriate cash shop item factory given a characters job.
    *
    * @param jobType the type of job
    * @return the item factory
    */
   protected ItemFactory getItemFactoryForJob(int jobType) {
      ItemFactory factory = null;
      if (!YamlConfig.config.server.USE_JOINT_CASHSHOP_INVENTORY) {
         if (jobType == 0) {
            factory = ItemFactory.CASH_EXPLORER;
         } else if (jobType == 1) {
            factory = ItemFactory.CASH_CYGNUS;
         } else if (jobType == 2) {
            factory = ItemFactory.CASH_ARAN;
         }
      } else {
         factory = ItemFactory.CASH_OVERALL;
      }
      return factory;
   }

   public CashShop initializeCashShop(int accountId, int characterId, int jobType) {
      CashShop cashShop = new CashShop(accountId, characterId, jobType);
      ItemFactory factory = getItemFactoryForJob(jobType);

      DatabaseConnection.getInstance().withConnection(connection -> {
         AccountProvider.getInstance().getAccountCashShopData(connection, accountId).ifPresent(cashShopData -> {
            cashShop.gainCash(1, cashShopData.nxCredit());
            cashShop.gainCash(2, cashShopData.maplePoint());
            cashShop.gainCash(4, cashShopData.nxPrepaid());
         });
         factory.loadItems(accountId, false).forEach(item -> cashShop.addToInventory(item.getLeft()));
         getWishList(characterId).forEach(cashShop::addToWishList);
      });

      return cashShop;
   }

   protected List<Integer> getWishList(int characterId) {
      List<Integer> results = new ArrayList<>();
      UriBuilder.service(RestService.CASH_SHOP).path("characters").path(characterId).path("wish-list").path("items").getRestClient(WishListResponse.class)
            .success((responseCode, result) -> results.addAll(result.items().parallelStream().map(WishListItem::id).collect(Collectors.toList())))
            .failure(responseCode -> LoggerUtil.printError(LoggerOriginator.CASH_SHOP_ORCHESTRATOR, "Failed to get wish list for character " + characterId))
            .get();

      return results;
   }

   public void save(EntityManager entityManager, CashShop cashShop) {
      AccountAdministrator.getInstance().saveNxInformation(entityManager, cashShop.getAccountId(), cashShop.getNxCredit(), cashShop.getMaplePoint(), cashShop.getNxPrepaid());

      List<Pair<Item, MapleInventoryType>> itemsWithType = cashShop.getInventory().stream()
            .map(item -> new Pair<>(item, item.inventoryType()))
            .collect(Collectors.toList());

      ItemFactory factory = getItemFactoryForJob(cashShop.getJobType());
      factory.saveItems(itemsWithType, cashShop.getAccountId(), entityManager);

      setWishListItems(cashShop.getCharacterId(), cashShop.getWishList());
   }

   /**
    * Sets the items on a characters wish list
    *
    * @param characterId the character id
    * @param ids         the set of item ids
    */
   public void setWishListItems(int characterId, List<Integer> ids) {
      deleteWishListForCharacter(characterId);
      addWishListItems(characterId, ids);
   }

   /**
    * Adds a set of items to a characters wish list.
    *
    * @param characterId the character id
    * @param ids         the set of item ids
    */
   protected void addWishListItems(int characterId, List<Integer> ids) {
      ids.parallelStream().forEach(id -> addWishListItem(characterId, id));
   }

   /**
    * Adds an item to a characters wish list.
    *
    * @param characterId the character id
    * @param id          the item id
    */
   protected void addWishListItem(int characterId, int id) {
      UriBuilder.service(RestService.CASH_SHOP).path("characters").path(characterId).path("wish-list").path("items").getRestClient()
            .failure(responseCode -> LoggerUtil.printError(LoggerOriginator.CASH_SHOP_ORCHESTRATOR, "Failed to add wish list item " + id + " for character " + characterId))
            .create(new WishListItem(id));
   }

   /**
    * Deletes a characters wish list.
    *
    * @param characterId the character id
    */
   public void deleteWishListForCharacter(int characterId) {
      UriBuilder.service(RestService.CASH_SHOP).path("characters").path(characterId).path("wish-list").path("items").getRestClient()
            .failure(responseCode -> LoggerUtil.printError(LoggerOriginator.CASH_SHOP_ORCHESTRATOR, "Failed to delete wish list for character " + characterId))
            .delete();
   }


   public void gift(int recipient, String from, String message, int sn) {
      gift(recipient, from, message, sn, -1);
   }

   public void gift(int recipient, String from, String message, int sn, int ringId) {
      UriBuilder.service(RestService.CASH_SHOP).path("characters").path(recipient).path("gifts").getRestClient()
            .failure(responseCode -> LoggerUtil.printError(LoggerOriginator.CASH_SHOP_ORCHESTRATOR, "Failed to create gift for character " + recipient))
            .create(new rest.cashshop.Gift(from, message, sn, ringId));
   }

   public void showGifts(MapleCharacter character, CashShop cashShop) {
      List<Gift> gifts = loadGifts(character.getId(), cashShop);
      PacketCreator.announce(character, new ShowGifts(gifts));
   }

   protected List<Gift> loadGifts(int characterId, CashShop cashShop) {
      List<Gift> gifts = new ArrayList<>();

      UriBuilder.service(RestService.CASH_SHOP).path("characters").path(characterId).path("gifts").getRestClient(GiftsResponse.class)
            .success((responseCode, result) -> gifts.addAll(result.gifts().stream().map(gift -> loadGift(cashShop, gift)).collect(Collectors.toList())))
            .failure(responseCode -> LoggerUtil.printError(LoggerOriginator.CASH_SHOP_ORCHESTRATOR, "Failed to get gifts for character " + characterId))
            .get();
      UriBuilder.service(RestService.CASH_SHOP).path("characters").path(characterId).path("gifts").getRestClient()
            .failure(responseCode -> LoggerUtil.printError(LoggerOriginator.CASH_SHOP_ORCHESTRATOR, "Failed to delete gifts for character " + characterId))
            .delete();
      return gifts;
   }

   protected Gift loadGift(CashShop cashShop, rest.cashshop.Gift gift) {
      Gift result;

      cashShop.addNote();
      CashShop.CashItem cItem = CashShop.CashItemFactory.getItem(gift.sn());
      Item item = Item.newBuilder(cItem.toItem()).setGiftFrom(gift.from()).build();
      if (item.inventoryType().equals(MapleInventoryType.EQUIP)) {
         item = Equip.newBuilder((Equip) item).setRingId(gift.ringId()).build();
      }
      result = new Gift(item, gift.message());

      if (CashShop.CashItemFactory.isPackage(cItem.getItemId())) { //Packages never contains a ring
         for (Item packageItem : CashShop.CashItemFactory.getPackage(cItem.getItemId())) {
            Item updatedItem = Item.newBuilder(packageItem).setGiftFrom(gift.from()).build();
            cashShop.addToInventory(updatedItem);
         }
      } else {
         cashShop.addToInventory(item);
      }
      return result;
   }

   public Item generateCouponItem(int itemId, short quantity) {
      CashShop.CashItem it = new CashShop.CashItem(77777777, itemId, 7777, ItemConstants.isPet(itemId) ? 30 : 0, quantity, true);
      return it.toItem();
   }
}