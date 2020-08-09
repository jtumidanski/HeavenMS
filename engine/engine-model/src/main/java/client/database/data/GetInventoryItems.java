package client.database.data;

public record GetInventoryItems(int inventoryType, int itemId, int position, int quantity, int petId, String owner,
                                long expiration, String giftFrom, int flag, int acc, int avoid, int dex, int hands,
                                int hp, int intelligence, int jump, int vicious, int luk, int matk, int mdef, int mp,
                                int speed, int str, int watk, int wdef, int upgradeSlots, int level, int itemExp,
                                int itemLevel, int ringId, int characterId) {
}
