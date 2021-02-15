package me.arifbanai.idLogger.utils;

public class Config {

    public boolean usingSQLite;
    public Database db;

    public boolean isUsingSQLite() {
        return usingSQLite;
    }

    public void setUsingSQLite(boolean usingSQLite) {
        this.usingSQLite = usingSQLite;
    }

    public Database getDb() {
        return db;
    }

    public void setDb(Database db) {
        this.db = db;
    }
}
