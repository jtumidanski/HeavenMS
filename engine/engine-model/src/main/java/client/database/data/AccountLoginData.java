package client.database.data;


import java.sql.Timestamp;
import java.util.Date;

public record AccountLoginData(int loggedIn, Timestamp lastLogin, Date birthday) {
}
