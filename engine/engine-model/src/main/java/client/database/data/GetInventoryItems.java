package client.database.data;

public record GetInventoryItems(Integer inventoryType, Integer itemId, Integer position, Integer quantity,
                                Integer petId, String owner,
                                Long expiration, String giftFrom, Integer flag, Integer acc, Integer avoid, Integer dex,
                                Integer hands,
                                Integer hp, Integer intelligence, Integer jump, Integer vicious, Integer luk,
                                Integer matk, Integer mdef, Integer mp,
                                Integer speed, Integer str, Integer watk, Integer wdef, Integer upgradeSlots,
                                Integer level, Float itemExp,
                                Integer itemLevel, Integer ringId, Integer characterId) {
}
