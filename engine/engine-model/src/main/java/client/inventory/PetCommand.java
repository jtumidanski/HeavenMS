package client.inventory;

public record PetCommand(Integer petId, Integer skillId, Integer probability, Integer increase) {
}
