package client.database.data;

public record FamilyData(Integer characterId, Integer familyId, Integer seniorId, Integer reputation,
                         Integer todaysReputation, Integer totalReputation, Integer reputationToSenior,
                         String precepts) {
}
