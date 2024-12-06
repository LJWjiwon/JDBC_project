package db;//import oracle.jdbc.internal.OracleTypes;
import javax.swing.table.DefaultTableModel;
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

    // 음식점 데이터 로드
    public void loadRestaurantData(DefaultTableModel restaurantModel) {
        String query = "SELECT * FROM restaurant"; // 음식점 데이터를 가져오는 SQL 쿼리
        try (Connection conn = this.DB_Connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("restaurant_id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("phone_number"),
                        rs.getString("business_hours"),
                        rs.getFloat("restaurant_rating")
                };
                restaurantModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

//    // 메뉴 데이터 로드
//    public void loadMenuData(DefaultTableModel menuModel) {
//        String query = "SELECT * FROM menu"; // 메뉴 데이터를 가져오는 SQL 쿼리
//        try (Connection conn = this.DB_Connect();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(query)) {
//
//            while (rs.next()) {
//                Object[] row = {
//                        false, // 기본값, 체크박스를 위한 값
//                        rs.getString("name"),
//                        rs.getFloat("price"),
//                        rs.getString("description")
//                };
//                menuModel.addRow(row);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

    // 특정 음식점의 메뉴 데이터 로드
    public void loadMenuDataForRestaurant(int restaurantId, DefaultTableModel menuModel) {
        String query = "SELECT * FROM menu WHERE restaurant_id = ?";
        try (Connection conn = this.DB_Connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, restaurantId);
            try (ResultSet rs = stmt.executeQuery()) {
                menuModel.setRowCount(0); // 기존 데이터를 지운다
                while (rs.next()) {
                    Object[] row = {
                            false, // 기본값, 체크박스를 위한 값
                            rs.getString("name"),
                            rs.getFloat("price"),
                            rs.getString("description")
                    };
                    menuModel.addRow(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 주문 상세 데이터 로드
    public void loadOrderDetailData(int orderId, DefaultTableModel orderDetailModel) {
        String query = "SELECT * FROM order_details WHERE order_id = ?";

        try (Connection con = this.DB_Connect();
             PreparedStatement stmt = con.prepareStatement(query)) {

            // orderId를 쿼리 파라미터로 안전하게 설정
            stmt.setInt(1, orderId);

            try (ResultSet rs = stmt.executeQuery()) {
                orderDetailModel.setRowCount(0);  // 이전 데이터 제거
                while (rs.next()) {
                    orderDetailModel.addRow(new Object[]{
                            rs.getInt("order_detail_id"),
                            rs.getInt("order_id"),
                            rs.getInt("menu_id"),
                            rs.getInt("quantity"),
                            rs.getFloat("subtotal")
                    });
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 고객 데이터 로드
    public void loadCustomerData(DefaultTableModel customerModel) {
        String query = "SELECT * FROM customer"; // 고객 데이터를 가져오는 SQL 쿼리
        try (Connection conn = this.DB_Connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("customer_id"),
                        rs.getString("name"),
                        rs.getString("phone_number"),
                        rs.getString("address"),
                        rs.getInt("order_count"),
                        rs.getString("membership_grade")
                };
                customerModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 배달원 데이터 로드
    public void loadDeliveryData(DefaultTableModel deliveryModel) {
        String query = "SELECT * FROM delivery_person"; // 배달원 데이터를 가져오는 SQL 쿼리
        try (Connection conn = this.DB_Connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("delivery_person_id"),
                        rs.getString("name"),
                        rs.getString("phone_number"),
                        rs.getFloat("delivery_person_rating")
                };
                deliveryModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 주문 데이터 로드
    public void loadOrderData(DefaultTableModel orderModel) {
        String query = "SELECT * FROM orders"; // 주문 데이터를 가져오는 SQL 쿼리
        try (Connection conn = this.DB_Connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("order_id"),
                        rs.getInt("customer_id"),
                        rs.getInt("restaurant_id"),
                        rs.getString("order_date"),
                        rs.getFloat("total_price"),
                        rs.getInt("delivery_person_id"),
                        rs.getString("status")
                };
                orderModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 리뷰 데이터 로드
    public void loadReviewData(DefaultTableModel reviewModel) {
        String query = "SELECT * FROM review"; // 리뷰 데이터를 가져오는 SQL 쿼리
        try (Connection conn = this.DB_Connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("review_id"),
                        rs.getInt("order_id"),
                        rs.getInt("customer_id"),
                        rs.getInt("restaurant_id"),
                        rs.getInt("delivery_person_id"),
                        rs.getFloat("restaurant_rating"),
                        rs.getFloat("delivery_person_rating"),
                        rs.getString("review_content")
                };
                reviewModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 전체 메뉴 데이터 로드
    public void loadAllMenuData(DefaultTableModel allMenuModel) {
        String query = "SELECT * FROM menu"; // 모든 메뉴 데이터를 가져오는 SQL 쿼리
        try (Connection conn = this.DB_Connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("menu_id"),
                        rs.getInt("restaurant_id"),
                        rs.getString("name"),
                        rs.getFloat("price"),
                        rs.getString("description")
                };
                allMenuModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



//    private void sqlRun() throws SQLException {
//        String query = "select restaurant.restaurant_ID, restaurant.name as restaurant_name, menu.name as menu_name from restaurant, menu where restaurant.restaurant_id = menu.restaurant_id";
//
//        try {
//            this.DB_Connect();
//            Statement stmt = this.con.createStatement();
//            ResultSet rs = stmt.executeQuery(query);
//
//            while(rs.next()) {
//                System.out.print("\t" + rs.getInt(1));
//                System.out.print("\t" + rs.getString("restaurant_name"));
//                System.out.print("\t" + rs.getString("menu_name") + "\n");
//            }
//
//            stmt.close();
//            rs.close();
//        } catch (SQLException var7) {
//            SQLException e;
//            e = var7;
//            e.printStackTrace();
//        } finally {
//            this.con.close();
//        }
//
//    }

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
