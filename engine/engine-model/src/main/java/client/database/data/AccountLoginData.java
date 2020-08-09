package client.database.data;

import java.sql.Date;
import java.sql.Timestamp;

public record AccountLoginData(Integer loggedIn, Timestamp lastLogin, Date birthday) {
}
