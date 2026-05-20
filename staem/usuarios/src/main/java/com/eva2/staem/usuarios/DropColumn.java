package com.eva2.staem.usuarios;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DropColumn {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://aws-1-us-west-1.pooler.supabase.com:6543/postgres?sslmode=require";
        String user = "postgres.bsqmacefyronbhcmjlri";
        String password = "2zWED9Au8Kz76S7X";

        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("ALTER TABLE usuarios DROP COLUMN IF EXISTS contrasena");
            System.out.println("Columna eliminada con exito.");
            stmt.close();
            conn.close();
        } catch (Exception e) {
             e.printStackTrace();
        }
    }
}
