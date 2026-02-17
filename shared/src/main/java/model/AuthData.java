package model;

import java.util.UUID;
import model.AuthData;

public record AuthData(String authToken, String username) {
}
