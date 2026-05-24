package mino.dx.curseletcraft.database;

@SuppressWarnings("unused")
public enum DatabaseType {
    MYSQL, SQLITE;

    public String getName() {
        return this.toString();
    }

    public boolean isMySQL() {
        return this.toString().equalsIgnoreCase("mysql");
    }

    @Override
    public String toString() {
        return this == MYSQL ? "MySQL" : "SQLite";
    }
}
