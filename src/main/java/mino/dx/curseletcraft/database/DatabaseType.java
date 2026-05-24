package mino.dx.curseletcraft.database;

@SuppressWarnings("unused")
public enum DatabaseType {
    MYSQL(new TypeString("mysql", false)),
    SQLITE(new TypeString("sqlite", false));

    private record TypeString(String databaseType, boolean isAsync) {}
    private final TypeString typeString;

    DatabaseType(TypeString typeString) {
        this.typeString = typeString;
    }

    public String getName() {
        return typeString.databaseType();
    }

    public boolean isAsync() {
        return typeString.isAsync();
    }

    public boolean isMySQL() {
        return typeString.databaseType.equalsIgnoreCase("mysql");
    }

    /**
     * Parse enum dựa trên 2 biến boolean trong config
     * @param isAsync true nếu bật chế độ bất đồng bộ
     * @param isMySQL true nếu sử dụng MySQL, false nếu SQLite
     * @return DatabaseType tương ứng
     */
    public static DatabaseType parseType(boolean isAsync, boolean isMySQL) {
        if (isAsync) {
            return isMySQL ? MYSQL_ASYNC : SQLITE_ASYNC;
        } else {
            return isMySQL ? MYSQL : SQLITE;
        }
    }

    @Override
    public String toString() {
        return (isAsync() ? "Async " : "Sync ") + getName().toUpperCase();
    }
}
