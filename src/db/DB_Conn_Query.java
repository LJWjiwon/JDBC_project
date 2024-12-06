package db;//import oracle.jdbc.internal.OracleTypes;
import java.sql.*;

public class DB_Conn_Query {
    Connection con = null;
    String url = "jdbc:oracle:thin:@localhost:1521:XE";
    String id = "Food_Delivery";
    String password = "1234";

    public DB_Conn_Query() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            System.out.println("드라이버 적재 성공");
        } catch (ClassNotFoundException var2) {
            System.out.println("No Driver.");
        }

    }

    public Connection DB_Connect() {
        try {
            this.con = DriverManager.getConnection(this.url, this.id, this.password);
            System.out.println("DB 연결 성공");
        } catch (SQLException var2) {
            System.out.println("Connection Fail");
        }

        return con;
    }

    private void sqlRun() throws SQLException {
        String query = "select restaurant.restaurant_ID, restaurant.name as restaurant_name, menu.name as menu_name from restaurant, menu where restaurant.restaurant_id = menu.restaurant_id";

        try {
            this.DB_Connect();
            Statement stmt = this.con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while(rs.next()) {
                System.out.print("\t" + rs.getInt(1));
                System.out.print("\t" + rs.getString("restaurant_name"));
                System.out.print("\t" + rs.getString("menu_name") + "\n");
            }

            stmt.close();
            rs.close();
        } catch (SQLException var7) {
            SQLException e;
            e = var7;
            e.printStackTrace();
        } finally {
            this.con.close();
        }

    }

//    private void sqlRun2_callable() {
//        try {
//            this.DB_Connect();
//            CallableStatement cstmt = this.con.prepareCall("{call SP_우수고객(?, ?)}");
//            cstmt.setInt(1, 2000);
//            cstmt.registerOutParameter(2, 4);
//            cstmt.executeQuery();
//            System.out.println(cstmt.getInt(2));
//            cstmt.close();
//            this.con.close();
//        } catch (SQLException var2) {
//            SQLException e = var2;
//            e.printStackTrace();
//        }
//
//    }
//
//    private void sqlRun3_callable() {
//        try {
//            CallableStatement cstmt = this.con.prepareCall("{call SP_잠재고객(?)}");
//            cstmt.registerOutParameter(1, -10);
//            cstmt.executeQuery();
//            ResultSet rs = (ResultSet)cstmt.getObject(1);
//
//            while(rs.next()) {
//                PrintStream var10000 = System.out;
//                String var10001 = rs.getString(1);
//                var10000.println(var10001 + ",\t" + rs.getString(2));
//            }
//
//            cstmt.close();
//            rs.close();
//            this.con.close();
//        } catch (SQLException var3) {
//            SQLException e = var3;
//            e.printStackTrace();
//        }
//
//    }

//    public static void main(String[] arg) throws SQLException {
//        DB_Conn_Query dbconquery = new DB_Conn_Query();
//        dbconquery.sqlRun();
//        dbconquery.DB_Connect();
//    }

}
