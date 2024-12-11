package db;//import oracle.jdbc.internal.OracleTypes;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class DB_Conn_Query extends Component {
    Connection con = null;
    String url = "jdbc:oracle:thin:@localhost:1521:XE";
    String id = "Food_Delivery";
//    String id = "Hmart";
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
        // 기존 테이블 데이터를 모두 삭제(음식점)
        int rowCount = restaurantModel.getRowCount();
        for (int i = rowCount - 1; i >= 0; i--) {
            restaurantModel.removeRow(i);
        }

        String query = "SELECT * FROM restaurant ORDER BY restaurant_id"; // 음식점 데이터를 가져오는 SQL 쿼리
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
        // 기존 테이블 데이터를 모두 삭제(고객)
        int rowCount = customerModel.getRowCount();
        for (int i = rowCount - 1; i >= 0; i--) {
            customerModel.removeRow(i);
        }

        String query = "SELECT * FROM customer ORDER BY customer_id"; // 고객 데이터를 가져오는 SQL 쿼리
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
        // 기존 테이블 데이터를 모두 삭제(배달원)
        int rowCount = deliveryModel.getRowCount();
        for (int i = rowCount - 1; i >= 0; i--) {
            deliveryModel.removeRow(i);
        }

        String query = "SELECT * FROM delivery_person ORDER BY delivery_person_id"; // 배달원 데이터를 가져오는 SQL 쿼리
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
        // 기존 테이블 데이터를 모두 삭제(주문)
        int rowCount = orderModel.getRowCount();
        for (int i = rowCount - 1; i >= 0; i--) {
            orderModel.removeRow(i);
        }

        String query = "SELECT * FROM orders ORDER BY order_id"; // 주문 데이터를 가져오는 SQL 쿼리
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
        // 기존 테이블 데이터를 모두 삭제(리뷰)
        int rowCount = reviewModel.getRowCount();
        for (int i = rowCount - 1; i >= 0; i--) {
            reviewModel.removeRow(i);
        }

        String query = "SELECT * FROM review ORDER BY review_id"; // 리뷰 데이터를 가져오는 SQL 쿼리
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
        // 기존 테이블 데이터를 모두 삭제(전체 메뉴)
        int rowCount = allMenuModel.getRowCount();
        for (int i = rowCount - 1; i >= 0; i--) {
            allMenuModel.removeRow(i);
        }

        String query = "SELECT * FROM menu ORDER BY menu_id"; // 모든 메뉴 데이터를 가져오는 SQL 쿼리
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

    // 프로시저 호출하여 best_menu 업데이트
    public void updateBestMenu(int restaurantId) {
        CallableStatement stmt = null;
        try {
            Connection con = this.DB_Connect();
            // 프로시저 호출
            String sql = "{ call update_best_menu(?) }";
            stmt = con.prepareCall(sql);

            // 파라미터 설정
            stmt.setLong(1, restaurantId);  // setInt 대신 setLong 사용

            // 프로시저 실행
            stmt.execute();
            System.out.println("Best menu for restaurant ID " + restaurantId + " has been updated.");

        } catch (SQLException e) {
            // 예외 메시지 출력
            System.out.println("Error occurred while updating best menu: " + e.getMessage());
            e.printStackTrace();  // 스택 트레이스를 출력하여 오류 원인 파악
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e) {
                System.out.println("Error while closing CallableStatement: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // 해당 음식점의 가장 인기 있는 메뉴 반환
    public String getBestMenuForRestaurant(int restaurantId) {
        String bestMenu = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT best_menu FROM restaurant WHERE restaurant_ID = ?";
            stmt = con.prepareStatement(sql);
            stmt.setInt(1, restaurantId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                bestMenu = rs.getString("best_menu");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return bestMenu;
    }



    // 단골 고객 조회하는 메소드
    public String findLoyalCustomers(int restaurantId, int orderCountThreshold) {
        StringBuilder loyalCustomers = new StringBuilder();
        ResultSet rs = null;

        try {
            Connection con = this.DB_Connect();

            // 단골 고객 조회 쿼리
            String query = "SELECT c.customer_ID, c.name, COUNT(o.order_ID) AS order_count " +
                    "FROM orders o " +
                    "JOIN customer c ON o.customer_ID = c.customer_ID " +
                    "WHERE o.restaurant_ID = ? " +
                    "GROUP BY c.customer_ID, c.name " +
                    "HAVING COUNT(o.order_ID) >= ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, restaurantId);
            pstmt.setInt(2, orderCountThreshold);
            rs = pstmt.executeQuery();

            // 결과 처리
            boolean foundCustomer = false;
            while (rs.next()) {
                int customerId = rs.getInt("customer_ID");
                String customerName = rs.getString("name");
                int orderCount = rs.getInt("order_count");

                loyalCustomers.append("단골 고객: " + customerName + " (고객 ID: " + customerId + ")\n");
                loyalCustomers.append("주문 횟수: " + orderCount + " 회\n");
                loyalCustomers.append("-----------------------------------\n");
                foundCustomer = true;
            }

            // 고객이 없으면 메시지 출력
            if (!foundCustomer) {
                loyalCustomers.append("해당 조건에 맞는 단골 고객이 없습니다.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            loyalCustomers.append("알 수 없는 오류가 발생했습니다.");
        } finally {
            try {
                if (rs != null) rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return loyalCustomers.toString();  // 결과 반환
    }


    public void saveReview(int orderId, int customerId, int restaurantId, int deliveryPersonId, double restaurantRating, double deliveryRating, String reviewContent) {
        String sql = "SELECT MAX(review_id) FROM review";  // 최대 review_id 값을 가져오는 쿼리
        int newReviewId = 1;  // 기본 값 설정

        try (Connection conn = this.DB_Connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                // 기존 review_id 중 가장 큰 값을 가져와서 +1을 하여 새로운 review_id를 생성
                newReviewId = rs.getInt(1) + 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 리뷰를 데이터베이스에 삽입
        String insertSql = "INSERT INTO review (review_id, order_id, customer_id, restaurant_id, delivery_person_id, restaurant_rating, delivery_person_rating, review_content) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = this.DB_Connect();
             PreparedStatement pstmt = conn.prepareStatement(insertSql)) {

            // 값 설정
            pstmt.setInt(1, newReviewId);  // 새로운 review_id
            pstmt.setInt(2, orderId);      // 주문 ID
            pstmt.setInt(3, customerId);   // 고객 ID
            pstmt.setInt(4, restaurantId); // 식당 ID
            pstmt.setInt(5, deliveryPersonId); // 배달원 ID
            pstmt.setDouble(6, restaurantRating); // 식당 평점
            pstmt.setDouble(7, deliveryRating); // 배달원 평점
            pstmt.setString(8, reviewContent);  // 리뷰 내용

            // 리뷰 삽입
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("리뷰가 성공적으로 저장되었습니다.");
            } else {
                System.out.println("리뷰 저장에 실패했습니다.");
            }

            // 리뷰가 성공적으로 삽입되면 평균 평점 계산
            updateRestaurantRating(restaurantId);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void updateRestaurantRating(int restaurantId) {
        String sql = "SELECT AVG(restaurant_rating) FROM review WHERE restaurant_id = ?";
        double avgRating = 0;

        try (Connection conn = this.DB_Connect()) {
            // 자동 커밋을 비활성화하여 트랜잭션을 시작합니다.
            conn.setAutoCommit(false);

            // 1. 평균 평점 계산
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, restaurantId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    avgRating = rs.getDouble(1);
                }

                // 2. 음식점의 평균 평점 업데이트
                String updateAvgRatingSql = "UPDATE restaurant SET restaurant_rating = ? WHERE restaurant_id = ?";
                try (PreparedStatement updatePstmt = conn.prepareStatement(updateAvgRatingSql)) {
                    updatePstmt.setDouble(1, avgRating);
                    updatePstmt.setInt(2, restaurantId);
                    updatePstmt.executeUpdate();
                }

                // 3. 전체 음식점의 'top_rated_restaurant' 플래그 업데이트
                String updateTopRatedFlagSql =
                        "UPDATE restaurant " +
                                "SET top_rated_restaurant = CASE " +
                                "    WHEN restaurant_rating = (SELECT MAX(restaurant_rating) FROM restaurant) THEN 'Y' " +
                                "    ELSE 'N' " +
                                "END";

                try (PreparedStatement updateTopRatedPstmt = conn.prepareStatement(updateTopRatedFlagSql)) {
                    updateTopRatedPstmt.executeUpdate();
                }


                // 5. 트랜잭션 커밋
                conn.commit();
                System.out.println("평점 업데이트 및 가장 높은 평점 음식점 플래그 설정 완료.");

            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 회원 등급을 업데이트하는 메서드
    public void updateMembershipGrade(int customerId) {
        // 프로시저 호출 SQL
        String sql = "{ CALL update_membership_grade(?) }";

        try (Connection conn = this.DB_Connect();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            // 프로시저에 고객 ID 전달
            cstmt.setInt(1, customerId);

            // 프로시저 실행
            cstmt.execute();

            System.out.println("회원 등급이 성공적으로 업데이트되었습니다.");

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("회원 등급 업데이트 중 오류가 발생했습니다.");
        }
    }

    public void deleteCustomer(int customerId) {
        String sql = "DELETE FROM customer WHERE customer_id = ?";

        try (Connection conn = this.DB_Connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 삭제할 고객 ID 설정
            pstmt.setInt(1, customerId);

            // 삭제 쿼리 실행
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Customer deleted successfully.");
            } else {
                System.out.println("No customer found with the given ID.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error deleting customer: " + e.getMessage());
        }
    }

    // 고객 데이터를 데이터베이스에 저장하는 메서드
    public void addCustomerToDatabase(String customerId,String customerName, String customerPhone, String customerAddress, String customer_order_count, String customermembership_grad) {
        String query = "INSERT INTO customer (customer_id, name, phone_number, address, order_count,membership_grade) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = this.DB_Connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // 입력값 설정
            pstmt.setString(1, customerId);
            pstmt.setString(2, customerName);
            pstmt.setString(3, customerPhone);
            pstmt.setString(4, customerAddress);
            pstmt.setString(5, customer_order_count);
            pstmt.setString(6, customermembership_grad);


            // 실행
            pstmt.executeUpdate();
            System.out.println("고객이 성공적으로 추가되었습니다.");

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("고객 추가 중 오류가 발생했습니다.");
        }
    }
    // 주문 데이터를 `orders` 테이블에 추가하는 메서드
    public void addOrder(int restaurantId, float totalPrice) {
        String insertOrderQuery = "INSERT INTO orders (order_id, customer_id, restaurant_id, order_date, total_price, delivery_person_id, status) " +
                "VALUES (order_seq_new.NEXTVAL, ?, ?, SYSDATE, ?, ?, '주문 접수')"; // '주문 접수' 상태로 설정

        try (Connection conn = this.DB_Connect();
             PreparedStatement stmt = conn.prepareStatement(insertOrderQuery)) {

            // 고객 ID는 실제로 선택된 고객의 ID로 처리되어야 합니다.
            // 예시로 1번 고객 ID를 사용 (이 부분은 실제로 구현 시, 로그인된 고객의 ID로 바꿔야 합니다)
            int customerId = 3;  // 예시로 1번 고객 ID 설정
            int deliveryPersonId = 3;   // 예시로 1번 배달원 ID 설정

            stmt.setInt(1, customerId); // 고객 ID
            stmt.setInt(2, restaurantId); // 음식점 ID
            stmt.setFloat(3, totalPrice); // 총 가격
            stmt.setInt(4, deliveryPersonId);  // 배달 담당자 ID (기본값 또는 선택된 값)
            // 주문 추가 실행
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("주문 추가 중 오류 발생: " + e.getMessage());
        }

    }

    // 데이터베이스에서 메뉴 가격을 업데이트하는 메서드
    public boolean updateMenuPriceByName(String menuName, float discountedPrice) {
        String updateQuery = "UPDATE menu SET price = ? WHERE name = ?";
        try (Connection conn = this.DB_Connect();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

            stmt.setFloat(1, discountedPrice); // 할인된 가격
            stmt.setString(2, menuName); // 메뉴 이름

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // 업데이트 성공 여부 반환
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void updateCustomerInDatabase(String customerId, String customerName, String customerPhone, String customerAddress, String customer_order_count, String customermembership_grad) {
        String query = "UPDATE customer SET name = ?, phone_number = ?, address = ?, order_count = ?, membership_grade = ? WHERE customer_id = ?";

        try (Connection conn = this.DB_Connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // 수정할 값 설정
            pstmt.setString(1, customerName); // 이름
            pstmt.setString(2, customerPhone); // 전화번호
            pstmt.setString(3, customerAddress); // 주소
            pstmt.setString(4, customer_order_count); // 주문 횟수
            pstmt.setString(5, customermembership_grad); // 멤버십 등급
            pstmt.setString(6, customerId); // 고객 ID (수정하려는 고객)

            // 실행
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("고객 정보가 성공적으로 수정되었습니다.");
            } else {
                System.out.println("해당 고객 ID에 해당하는 정보가 없습니다.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("고객 정보 수정 중 오류가 발생했습니다.");
        }
    }

    public int addDeliveryPersonToDatabase(String name, String phone, float rating) {
        String query = "INSERT INTO delivery_person (delivery_person_id, name, phone_number, delivery_person_rating) " +
                "VALUES (delivery_person_seq.NEXTVAL, ?, ?, ?)";

        try (Connection conn = this.DB_Connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // 입력값 설정
            pstmt.setString(1, name); // 배달원 이름
            pstmt.setString(2, phone); // 배달원 전화번호
            pstmt.setFloat(3, rating); // 배달원 평점

            // 실행
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                // 성공적으로 추가된 경우
                System.out.println("배달원이 성공적으로 추가되었습니다.");

                // 최근에 추가된 배달원의 ID를 가져오기
                String getIdQuery = "SELECT delivery_person_seq.CURRVAL FROM dual";
                try (PreparedStatement stmt = conn.prepareStatement(getIdQuery);
                     ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        int generatedId = rs.getInt(1);  // 생성된 배달원 ID 가져오기
                        System.out.println("생성된 배달원 ID: " + generatedId);
                        return generatedId;  // 생성된 배달원 ID 반환
                    }
                }
            } else {
                System.out.println("배달원 추가에 실패했습니다.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("배달원 추가 중 오류가 발생했습니다.");
        }
        return -1;  // 실패한 경우 -1 반환
    }





    public void updateDeliveryPersonInDatabase(String deliveryPersonId, String name, String phone, String rating) {
        String query = "UPDATE delivery_person SET name = ?, phone_number = ?, delivery_person_rating = ? WHERE delivery_person_id = ?";

        try (Connection conn = this.DB_Connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, name);
            pstmt.setString(2, phone);
            pstmt.setString(3, rating);
            pstmt.setString(4, deliveryPersonId);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("배달원 정보가 성공적으로 수정되었습니다.");
            } else {
                System.out.println("해당 배달원 ID를 찾을 수 없습니다.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("배달원 정보 수정 중 오류가 발생했습니다.");
        }
    }

    public void deleteDeliveryPersonInDatabase(String deliveryPersonId) {
        String query = "DELETE FROM delivery_person WHERE delivery_person_id = ?";

        try (Connection conn = this.DB_Connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, deliveryPersonId);

            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("배달원이 성공적으로 삭제되었습니다.");
            } else {
                System.out.println("해당 배달원 ID를 찾을 수 없습니다.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("배달원 삭제 중 오류가 발생했습니다.");
        }
    }

    public void updateReviewInDatabase(String reviewId, String orderId, String customerId, String restaurantId,
                                      String deliveryPersonId, String restaurantRating, String deliveryPersonRating,
                                      String reviewContent) {
        String query = "UPDATE review SET order_id = ?, customer_id = ?, restaurant_id = ?, delivery_person_id = ?, " +
                "restaurant_rating = ?, delivery_person_rating = ?, review_content = ? WHERE review_id = ?";

        try (Connection conn = this.DB_Connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // 쿼리 파라미터 설정
            pstmt.setString(1, orderId);
            pstmt.setString(2, customerId);
            pstmt.setString(3, restaurantId);
            pstmt.setString(4, deliveryPersonId);
            pstmt.setString(5, restaurantRating);
            pstmt.setString(6, deliveryPersonRating);
            pstmt.setString(7, reviewContent);
            pstmt.setString(8, reviewId);

            // 쿼리 실행
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("리뷰 수정 중 오류가 발생했습니다.");
        }
    }

    public void deleteReviewFromDatabase(String reviewId) {
        String query = "DELETE FROM review WHERE review_id = ?";

        try (Connection conn = this.DB_Connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // 쿼리 파라미터 설정
            pstmt.setString(1, reviewId);

            // 쿼리 실행
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("리뷰 삭제 중 오류가 발생했습니다.");
        }
    }


    // 최고 평점을 가진 음식점 정보를 하나의 문자열로 반환
    public String getTopRatedRestaurantInfo() {
        StringBuilder result = new StringBuilder();
        String query = "SELECT name, restaurant_rating " +
                "FROM restaurant " +
                "WHERE restaurant_rating = (SELECT MAX(restaurant_rating) FROM restaurant)";

        try (Connection conn = this.DB_Connect();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("name");
                double rating = rs.getDouble("restaurant_rating");
                result.append(String.format("- %s (평점: %.2f)\n", name, rating));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return result.toString().isEmpty() ? null : result.toString();
    }


}