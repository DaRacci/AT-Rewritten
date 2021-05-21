package io.github.niestrat99.advancedteleport.sql;

import io.github.niestrat99.advancedteleport.CoreClass;
import io.github.niestrat99.advancedteleport.config.CustomMessages;
import io.github.niestrat99.advancedteleport.config.NewConfig;
import org.bukkit.command.CommandSender;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static io.github.niestrat99.advancedteleport.CoreClass.debug;

public abstract class SQLManager {

    protected static Connection connection;
    protected static String tablePrefix;
    protected static boolean usingSqlite;

    public SQLManager() {
        if (connection == null) {
            tablePrefix = NewConfig.get().TABLE_PREFIX.get();
            if (!tablePrefix.matches("^[_A-Za-z0-9]+$")) {
                CoreClass.getInstance().getLogger().warning("Table prefix " + tablePrefix + " is not alphanumeric. Using advancedtp...");
                tablePrefix = "advancedtp";
            }
            if (NewConfig.get().USE_MYSQL.get()) {
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    connection = DriverManager.getConnection("jdbc:mysql://"
                            + NewConfig.get().MYSQL_HOST.get() + ":"
                            + NewConfig.get().MYSQL_PORT.get() + "/"
                            + NewConfig.get().MYSQL_DATABASE.get() + "?useSSL=false&autoReconnect=true",
                            NewConfig.get().USERNAME.get(),
                            NewConfig.get().PASSWORD.get());
                    usingSqlite = false;
                } catch (ClassNotFoundException | SQLException e) {
                    e.printStackTrace();
                    loadSqlite();
                }


            } else {
                loadSqlite();
            }
        }
        createTable();
    }

    private void loadSqlite() {
        // Load JDBC
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + CoreClass.getInstance().getDataFolder() + "/data.db");
            usingSqlite = true;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void closeConnection() {
        debug("Checking for a connection...");
        if (connection != null) {
            debug("There is a connecting, attempting to close it...");
            try {
                connection.close();
                debug("Closed the connection.");
            } catch (SQLException exception) {
                exception.printStackTrace();
                debug("Failed to close the connection.");
            }
        }
        debug("Should be done with SQL connection now.");
    }

    public abstract void createTable();

    public abstract void transferOldData();

    public String getStupidAutoIncrementThing() {
        return usingSqlite ? "AUTOINCREMENT" : "AUTO_INCREMENT";
    }

    public interface SQLCallback<D> {
        void onSuccess(D data);

        default void onSuccess() {}

        default void onFail() {}

        static SQLCallback<Boolean> getDefaultCallback(CommandSender sender, String success, String fail, String... placeholders) {
            return new SQLCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    CustomMessages.sendMessage(sender, success, placeholders);
                }

                @Override
                public void onFail() {
                    CustomMessages.sendMessage(sender, fail, placeholders);
                }
            };
        }
    }


}
