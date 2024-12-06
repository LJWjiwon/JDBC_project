package gui;

import db.DB_Conn_Query;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.sql.*;

public class RestaurantApp extends JFrame {
    private JTabbedPane tabbedPane;
    private DefaultTableModel restaurantModel, menuModel, customerModel, deliveryModel, orderModel, reviewModel, orderDetailModel, allMenuModel;

    private final DB_Conn_Query dbConn;

    public RestaurantApp() {
        dbConn = new DB_Conn_Query(); // DB 연결 클래스 초기화
        setTitle("음식점 관리 프로그램");
        setSize(1000, 600); // 화면 크기 설정
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // TabbedPane 생성
        tabbedPane = new JTabbedPane();

        // 음식점 탭
        JPanel restaurantPanel = new JPanel(new BorderLayout());
        restaurantModel = new DefaultTableModel();
        restaurantModel.setColumnIdentifiers(new Object[]{"음식점 ID", "이름", "주소", "전화번호", "영업시간", "평점"});
        JTable restaurantTable = new JTable(restaurantModel);
        JScrollPane restaurantScrollPane = new JScrollPane(restaurantTable);
        restaurantPanel.add(restaurantScrollPane, BorderLayout.CENTER);
        restaurantPanel.add(addButtonsPanel("음식점"), BorderLayout.SOUTH);

        // 메뉴 탭
        JPanel menuPanel = new JPanel(new BorderLayout());
        menuModel = new DefaultTableModel();
        menuModel.setColumnIdentifiers(new Object[]{"선택", "메뉴 이름", "가격", "설명"});
        JTable menuTable = new JTable(menuModel) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return (columnIndex == 0) ? Boolean.class : super.getColumnClass(columnIndex);
            }
        };

        // 체크박스 열 설정
        TableColumn selectColumn = menuTable.getColumnModel().getColumn(0);
        selectColumn.setCellEditor(menuTable.getDefaultEditor(Boolean.class));
        selectColumn.setCellRenderer(menuTable.getDefaultRenderer(Boolean.class));

        JScrollPane menuScrollPane = new JScrollPane(menuTable);
        menuPanel.add(menuScrollPane, BorderLayout.CENTER);

        // 음식점 탭 오른쪽에 메뉴 탭 추가
        restaurantPanel.add(menuPanel, BorderLayout.EAST);

        // 고객 탭
        JPanel customerPanel = new JPanel(new BorderLayout());
        customerModel = new DefaultTableModel();
        customerModel.setColumnIdentifiers(new Object[]{"고객 ID", "이름", "전화번호", "주소", "주문 횟수", "회원 등급"});
        JTable customerTable = new JTable(customerModel);
        JScrollPane customerScrollPane = new JScrollPane(customerTable);
        customerPanel.add(customerScrollPane, BorderLayout.CENTER);
        customerPanel.add(addButtonsPanel("고객"), BorderLayout.SOUTH);

        // 배달원 탭
        JPanel deliveryPanel = new JPanel(new BorderLayout());
        deliveryModel = new DefaultTableModel();
        deliveryModel.setColumnIdentifiers(new Object[]{"배달원 ID", "이름", "전화번호", "평점"});
        JTable deliveryTable = new JTable(deliveryModel);
        JScrollPane deliveryScrollPane = new JScrollPane(deliveryTable);
        deliveryPanel.add(deliveryScrollPane, BorderLayout.CENTER);
        deliveryPanel.add(addButtonsPanel("배달원"), BorderLayout.SOUTH);

        // 주문 탭 추가
        JPanel orderPanel = new JPanel(new BorderLayout());
        orderModel = new DefaultTableModel();
        orderModel.setColumnIdentifiers(new Object[]{"주문 ID", "고객 ID", "음식점 ID", "주문 날짜", "총 가격","배달원 ID", "주문 상태"});
        JTable orderTable = new JTable(orderModel);
        JScrollPane orderScrollPane = new JScrollPane(orderTable);
        orderPanel.add(orderScrollPane, BorderLayout.CENTER);
        orderPanel.add(addButtonsPanel("주문"), BorderLayout.SOUTH);

        // 주문 상세 테이블 추가 (옆에 표시)
        JPanel orderDetailPanel = new JPanel(new BorderLayout());
        orderDetailModel = new DefaultTableModel();
        orderDetailModel.setColumnIdentifiers(new Object[]{"주문 상세 ID", "주문 ID", "메뉴 ID", "수량", "가격"});
        JTable orderDetailTable = new JTable(orderDetailModel);
        JScrollPane orderDetailScrollPane = new JScrollPane(orderDetailTable);
        orderDetailPanel.add(orderDetailScrollPane, BorderLayout.CENTER);
        orderPanel.add(orderDetailPanel, BorderLayout.EAST);

        tabbedPane.addTab("주문", orderPanel);

        // 리뷰 탭
        JPanel reviewPanel = new JPanel(new BorderLayout());
        reviewModel = new DefaultTableModel();
        reviewModel.setColumnIdentifiers(new Object[]{"리뷰 ID", "주문 ID", "고객 ID", "음식점 ID", "배달원 ID", "음식점 평점", "배달원 평점", "리뷰 내용"});
        JTable reviewTable = new JTable(reviewModel);
        JScrollPane reviewScrollPane = new JScrollPane(reviewTable);
        reviewPanel.add(reviewScrollPane, BorderLayout.CENTER);
        reviewPanel.add(addButtonsPanel("리뷰"), BorderLayout.SOUTH);

        // 전체 메뉴 탭
        JPanel allMenuPanel = new JPanel(new BorderLayout());
        allMenuModel = new DefaultTableModel();
        allMenuModel.setColumnIdentifiers(new Object[]{"메뉴 ID", "음식점 ID", "메뉴 이름", "가격", "설명"});
        JTable allMenuTable = new JTable(allMenuModel);
        JScrollPane allMenuScrollPane = new JScrollPane(allMenuTable);
        allMenuPanel.add(allMenuScrollPane, BorderLayout.CENTER);
        allMenuPanel.add(addButtonsPanel("전체 메뉴"), BorderLayout.SOUTH);

        // 탭 추가
        tabbedPane.addTab("음식점", restaurantPanel);
        tabbedPane.addTab("전체 메뉴", allMenuPanel);
        tabbedPane.addTab("고객", customerPanel);
        tabbedPane.addTab("배달원", deliveryPanel);
        tabbedPane.addTab("주문", orderPanel);
        tabbedPane.addTab("리뷰", reviewPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // 데이터 로드
        loadRestaurantData();
        loadMenuData();
        loadCustomerData();
        loadDeliveryData();
        loadOrderData();
        loadReviewData();
        loadAllMenuData(); // 전체 메뉴 데이터 로드

        // 음식점 선택 시 메뉴 데이터 로드
        restaurantTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = restaurantTable.getSelectedRow();
                if (selectedRow != -1) {
                    int restaurantId = (int) restaurantModel.getValueAt(selectedRow, 0);
                    loadMenuDataForRestaurant(restaurantId);
                }
            }
        });

        // 주문 테이블의 행 선택 이벤트 처리
        orderTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = orderTable.getSelectedRow();
                    if (selectedRow != -1) {
                        int orderId = (int) orderModel.getValueAt(selectedRow, 0);
                        loadOrderDetailData(orderId); // 선택한 주문 ID에 해당하는 주문 상세 데이터 로드
                    }
                }
            }
        });
    }

    // 전체 메뉴 데이터 로드
    private void loadAllMenuData() {
        String query = "SELECT * FROM menu";
        try (Connection con = dbConn.DB_Connect(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            allMenuModel.setRowCount(0);
            while (rs.next()) {
                allMenuModel.addRow(new Object[]{
                        rs.getInt("menu_id"),
                        rs.getInt("restaurant_id"),
                        rs.getString("name"),
                        rs.getFloat("price"),
                        rs.getString("description")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "전체 메뉴 데이터 로드 실패: " + e.getMessage());
        }
    }

    // 주문 상세 데이터 로드
    private void loadOrderDetailData(int orderId) {
        String query = "SELECT * FROM order_details WHERE order_id = " + orderId;
        try (Connection con = dbConn.DB_Connect(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
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
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "주문 상세 정보 로드 실패: " + e.getMessage());
        }
    }

    // 선택된 음식점에 맞는 메뉴 데이터 로드
    private void loadMenuDataForRestaurant(int restaurantId) {
        String query = "SELECT * FROM menu WHERE restaurant_id = " + restaurantId;
        try (Connection con = dbConn.DB_Connect(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            menuModel.setRowCount(0);  // 기존 메뉴 데이터 제거
            while (rs.next()) {
                menuModel.addRow(new Object[]{
                        false,  // 체크박스를 기본값으로 체크 안 되게 설정
                        rs.getString("name"),
                        rs.getFloat("price"),
                        rs.getString("description")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "메뉴 데이터 로드 실패: " + e.getMessage());
        }
    }

    // 음식점 데이터 로드
    private void loadRestaurantData() {
        String query = "SELECT * FROM restaurant";
        try (Connection con = dbConn.DB_Connect(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            restaurantModel.setRowCount(0);
            while (rs.next()) {
                restaurantModel.addRow(new Object[]{
                        rs.getInt("restaurant_id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("phone_number"),
                        rs.getString("business_hours"),
                        rs.getFloat("restaurant_rating")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "음식점 데이터 로드 실패: " + e.getMessage());
        }
    }

    // 메뉴 데이터 로드 (전체 메뉴)
    private void loadMenuData() {
        String query = "SELECT * FROM menu";
        try (Connection con = dbConn.DB_Connect(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            menuModel.setRowCount(0);
            while (rs.next()) {
                menuModel.addRow(new Object[]{
                        false,  // 체크박스를 기본값으로 체크 안 되게 설정
                        rs.getString("name"),
                        rs.getFloat("price"),
                        rs.getString("description")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "메뉴 데이터 로드 실패: " + e.getMessage());
        }
    }

    // 고객 데이터 로드
    private void loadCustomerData() {
        String query = "SELECT * FROM customer";
        try (Connection con = dbConn.DB_Connect(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            customerModel.setRowCount(0);
            while (rs.next()) {
                customerModel.addRow(new Object[]{
                        rs.getInt("customer_id"),
                        rs.getString("name"),
                        rs.getString("phone_number"),
                        rs.getString("address"),
                        rs.getInt("order_count"),
                        rs.getString("membership_grade")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "고객 데이터 로드 실패: " + e.getMessage());
        }
    }

    // 배달원 데이터 로드
    private void loadDeliveryData() {
        String query = "SELECT * FROM delivery_person";
        try (Connection con = dbConn.DB_Connect(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            deliveryModel.setRowCount(0);
            while (rs.next()) {
                deliveryModel.addRow(new Object[]{
                        rs.getInt("delivery_person_id"),
                        rs.getString("name"),
                        rs.getString("phone_number"),
                        rs.getFloat("delivery_person_rating")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "배달원 데이터 로드 실패: " + e.getMessage());
        }
    }

    // 주문 데이터 로드
    private void loadOrderData() {
        String query = "SELECT * FROM orders";
        try (Connection con = dbConn.DB_Connect(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            orderModel.setRowCount(0);
            while (rs.next()) {
                orderModel.addRow(new Object[]{
                        rs.getInt("order_id"),
                        rs.getInt("customer_id"),
                        rs.getInt("restaurant_id"),
                        rs.getString("order_date"),
                        rs.getFloat("total_price"),
                        rs.getInt("delivery_person_id"),
                        rs.getString("status")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "주문 데이터 로드 실패: " + e.getMessage());
        }
    }

    // 리뷰 데이터 로드
    private void loadReviewData() {
        String query = "SELECT * FROM review";
        try (Connection con = dbConn.DB_Connect(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            reviewModel.setRowCount(0);
            while (rs.next()) {
                reviewModel.addRow(new Object[]{
                        rs.getInt("review_id"),
                        rs.getInt("order_id"),
                        rs.getInt("customer_id"),
                        rs.getInt("restaurant_id"),
                        rs.getInt("delivery_person_id"),
                        rs.getFloat("restaurant_rating"),
                        rs.getFloat("delivery_person_rating"),
                        rs.getString("review_content")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "리뷰 데이터 로드 실패: " + e.getMessage());
        }
    }

    // 패널 하단에 버튼을 추가하기 위한 메서드 (수정)
    private JPanel addButtonsPanel(String tabName) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // 공통 버튼
        JButton addButton = new JButton("추가");
        JButton editButton = new JButton("수정");
        JButton deleteButton = new JButton("삭제");

        // 탭별 커스텀 버튼
        if ("음식점".equals(tabName)) {
            JButton orderButton = new JButton("주문");
            JButton topCustomerButton = new JButton("단골 고객 조회");
            JButton topRatingButton = new JButton("최고 평점 조회");

            // 숫자 입력 영역과 할인 버튼
            JTextField discountField = new JTextField(5); // 입력 필드(숫자 입력)
            JButton applyDiscountButton = new JButton("할인 적용");

            // 음식점 탭 전용 버튼 추가
            buttonPanel.add(orderButton);
            buttonPanel.add(topCustomerButton);
            buttonPanel.add(topRatingButton);
            buttonPanel.add(new JLabel("할인율 (%):")); // 입력 필드 앞에 레이블 추가
            buttonPanel.add(discountField);
            buttonPanel.add(applyDiscountButton);

            // 음식점 탭 버튼 클릭 이벤트 (구현 필요)
            orderButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "주문 버튼 클릭"));
            topCustomerButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "단골 고객 조회 버튼 클릭"));
            topRatingButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "최고 평점 조회 버튼 클릭"));
            applyDiscountButton.addActionListener(e -> {
                try {
                    int discount = Integer.parseInt(discountField.getText());
                    JOptionPane.showMessageDialog(this, "할인율 " + discount + "% 적용!");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "유효한 숫자를 입력하세요.");
                }
            });
        }else if ("고객".equals(tabName)) {
            JButton updateMembershipButton = new JButton("회원 등급 업데이트");

            // 고객 탭 전용 버튼 추가
            buttonPanel.add(updateMembershipButton);

            // 고객 탭 버튼 클릭 이벤트
            updateMembershipButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "회원 등급 업데이트 버튼 클릭"));
        }

        // 공통 버튼 추가
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        // 공통 버튼 클릭 이벤트 (구현 필요)
        addButton.addActionListener(e -> JOptionPane.showMessageDialog(this, tabName + " 추가 버튼 클릭"));
        editButton.addActionListener(e -> JOptionPane.showMessageDialog(this, tabName + " 수정 버튼 클릭"));
        deleteButton.addActionListener(e -> JOptionPane.showMessageDialog(this, tabName + " 삭제 버튼 클릭"));

        return buttonPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RestaurantApp app = new RestaurantApp();
            app.setVisible(true);
        });
    }
}
