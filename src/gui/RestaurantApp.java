package gui;

import db.DB_Conn_Query;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class RestaurantApp extends JFrame {
    private JTabbedPane tabbedPane;
    private JTable restaurantTable, allMenuTable;
    private DefaultTableModel restaurantModel, menuModel;
    private DefaultTableModel customerModel, deliveryModel, orderModel, reviewModel, orderDetailModel, allMenuModel;

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
        restaurantTable = new JTable(restaurantModel);
        JScrollPane restaurantScrollPane = new JScrollPane(restaurantTable);
        restaurantPanel.add(restaurantScrollPane, BorderLayout.CENTER);
        restaurantPanel.add(addButtonsPanel("음식점"), BorderLayout.SOUTH);    //밑에 버튼


        // 'Best Menu' 레이블 초기화
        JLabel bestMenuLabel = new JLabel("Best Menu: ");
        bestMenuLabel.setFont(new Font("나눔고딕", Font.BOLD, 16));
        bestMenuLabel.setForeground(Color.RED);

        // bestMenuLabel을 포함할 JPanel 생성
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));  // 가운데 정렬
        labelPanel.add(bestMenuLabel);
        labelPanel.add(bestMenuLabel);
        restaurantPanel.add(labelPanel, BorderLayout.NORTH);

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

        // 버튼 추가 영역
        JPanel buttonPanel_customer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btn1 = new JButton("회원등급 업데이트");
        JButton btn2 = new JButton("추가");
        JButton btn3 = new JButton("수정");
        JButton btn4 = new JButton("삭제");

        buttonPanel_customer.add(btn1);
        buttonPanel_customer.add(btn2);
        buttonPanel_customer.add(btn3);
        buttonPanel_customer.add(btn4);

        // 버튼 패널을 orderPanel의 아래쪽에 추가
        customerPanel.add(buttonPanel_customer, BorderLayout.SOUTH);  // 버튼이 아래쪽에 보이게



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

        // 버튼 추가 영역
        JPanel buttonPanel_review = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton button_a = new JButton("리뷰 작성");
        JButton button_b = new JButton("수정");

        buttonPanel_review.add(button_a);
        buttonPanel_review.add(button_b);

        // 버튼 패널을 orderPanel의 아래쪽에 추가
        orderPanel.add(buttonPanel_review, BorderLayout.SOUTH);  // 버튼이 아래쪽에 보이게




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

        // 리뷰 내용 표시 영역 (JTextArea)
        JPanel reviewDetailPanel = new JPanel();
        reviewDetailPanel.setLayout(new BorderLayout());

        // 텍스트 영역 생성 (길어도 내용 표시 가능)
        JTextArea reviewDetailsArea = new JTextArea(10, 30);
        reviewDetailsArea.setEditable(false); // 내용 수정 불가
        reviewDetailsArea.setLineWrap(true); // 줄 바꿈 허용
        reviewDetailsArea.setWrapStyleWord(true); // 단어 단위로 줄 바꿈

        // 세로 스크롤을 위한 JScrollPane 추가
        JScrollPane reviewDetailsScrollPane = new JScrollPane(reviewDetailsArea);
        reviewDetailsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);  // 세로 스크롤 항상 표시

        // 리뷰 내용 텍스트 영역을 패널에 추가
        reviewDetailPanel.add(reviewDetailsScrollPane, BorderLayout.CENTER);
        reviewPanel.add(reviewDetailPanel, BorderLayout.EAST);


        // 버튼 추가 영역
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton button1 = new JButton("수정");
        JButton button2 = new JButton("삭제");
        buttonPanel.add(button1);
        buttonPanel.add(button2);

        // 버튼 패널을 reviewPanel의 아래쪽에 추가
        reviewPanel.add(buttonPanel, BorderLayout.SOUTH);  // 버튼이 아래쪽에 보이게


        // 전체 메뉴 탭
        JPanel allMenuPanel = new JPanel(new BorderLayout());
        allMenuModel = new DefaultTableModel();
        allMenuModel.setColumnIdentifiers(new Object[]{"메뉴 ID", "음식점 ID", "메뉴 이름", "가격", "설명"});
        allMenuTable = new JTable(allMenuModel);
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
        dbConn.loadRestaurantData(restaurantModel);
        dbConn.loadCustomerData(customerModel);
        dbConn.loadDeliveryData(deliveryModel);
        dbConn.loadOrderData(orderModel);
        dbConn.loadReviewData(reviewModel);
        dbConn.loadAllMenuData(allMenuModel); // 전체 메뉴 데이터 로드

        // 음식점 선택 시 메뉴 데이터 로드
        restaurantTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = restaurantTable.getSelectedRow();
                if (selectedRow != -1) {
                    int restaurantId = (int) restaurantModel.getValueAt(selectedRow, 0);
                    dbConn.loadMenuDataForRestaurant(restaurantId, menuModel);

                    // 1. DB에서 best_menu 업데이트
                    dbConn.updateBestMenu(restaurantId);

                    // 2. 해당 음식점에 대한 가장 인기 있는 메뉴 가져오기
                    String bestMenu = dbConn.getBestMenuForRestaurant(restaurantId);

                    // "Best Menu" 레이블에 텍스트 업데이트
                    if (bestMenu != null && !bestMenu.isEmpty()) {
                        bestMenuLabel.setText("Best Menu: " + bestMenu);
                    } else {
                        bestMenuLabel.setText("Best Menu: No best menu found.");
                    }
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
                        dbConn.loadOrderDetailData(orderId, orderDetailModel); // 선택한 주문 ID에 해당하는 주문 상세 데이터 로드
                    }
                }
            }
        });

        // 리뷰 테이블의 선택 이벤트 추가
        reviewTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int selectedRow = reviewTable.getSelectedRow();
                if (selectedRow != -1) {
                    // 선택된 리뷰의 ID와 내용을 JTextArea에 표시
                    String reviewId = reviewModel.getValueAt(selectedRow, 0).toString();
                    String reviewContent = reviewModel.getValueAt(selectedRow, 7).toString();
                    reviewDetailsArea.setText("리뷰 ID: " + reviewId + "\n\n리뷰 내용: " + reviewContent);
                }
            }
        });

        button_a.addActionListener(e -> {
            // 주문 테이블에서 선택된 행을 가져옵니다.
            int selectedRow = orderTable.getSelectedRow();

            // 주문 테이블에서 선택된 행이 없을 경우 메시지 출력
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "리뷰를 작성할 주문을 선택하세요.");
                return;
            }

            // 주문 ID, 고객 ID, 음식점 ID, 배달원 ID 가져오기
            int orderId = (int) orderModel.getValueAt(selectedRow, 0); // 주문 ID
            int customerId = (int) orderModel.getValueAt(selectedRow, 1); // 고객 ID
            int restaurantId = (int) orderModel.getValueAt(selectedRow, 2); // 음식점 ID
            int deliveryPersonId = (int) orderModel.getValueAt(selectedRow, 5); // 배달원 ID

            // 리뷰 입력을 위한 입력 창 구성
            JTextField restaurantRatingField = new JTextField();
            restaurantRatingField.setPreferredSize(new Dimension(80, 30));  // 평점 입력 칸의 크기를 작게 설정
            JTextField deliveryRatingField = new JTextField();
            deliveryRatingField.setPreferredSize(new Dimension(80, 30));  // 평점 입력 칸의 크기를 작게 설정

            // 리뷰 내용 입력 창
            JTextArea reviewContentArea = new JTextArea(5, 20);
            reviewContentArea.setLineWrap(true);
            reviewContentArea.setWrapStyleWord(true);

            JScrollPane reviewScrollPane_write = new JScrollPane(reviewContentArea);
            reviewScrollPane_write.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));  // FlowLayout 사용
            panel.add(new JLabel("음식점 평점 (1~5):"));
            panel.add(restaurantRatingField);
            panel.add(new JLabel("배달원 평점 (1~5):"));
            panel.add(deliveryRatingField);
            panel.add(new JLabel("리뷰 내용:"));
            panel.add(reviewContentArea);

            // 레이아웃을 새로 고침
            panel.revalidate();
            panel.repaint();

            int result = JOptionPane.showConfirmDialog(this, panel, "리뷰 작성", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                try {
                    double restaurantRating = Double.parseDouble(restaurantRatingField.getText());
                    double deliveryRating = Double.parseDouble(deliveryRatingField.getText());
                    String reviewContent = reviewContentArea.getText();

                    // 평점 유효성 검사
                    if (restaurantRating < 1 || restaurantRating > 5 || deliveryRating < 1 || deliveryRating > 5) {
                        JOptionPane.showMessageDialog(this, "평점은 1에서 5 사이의 값이어야 합니다.");
                        return;
                    }

                    // DB에 리뷰 저장 (written_date는 sysdate로 자동 처리됨)
                    dbConn.saveReview(orderId, customerId, restaurantId, deliveryPersonId, restaurantRating, deliveryRating, reviewContent);

                    JOptionPane.showMessageDialog(this, "리뷰가 성공적으로 저장되었습니다.");

                    // 기존 테이블 데이터를 모두 삭제
                    int rowCount = reviewModel.getRowCount();
                    for (int i = rowCount - 1; i >= 0; i--) {
                        reviewModel.removeRow(i);
                    }

                    // 리뷰 데이터 테이블 새로고침
                    dbConn.loadReviewData(reviewModel);

                    // 기존 테이블 데이터를 모두 삭제(음식점)
                    rowCount = restaurantModel.getRowCount();
                    for (int i = rowCount - 1; i >= 0; i--) {
                        restaurantModel.removeRow(i);
                    }

                    // 음식점 데이터 테이블 새로고침
                    dbConn.loadRestaurantData(restaurantModel);

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "평점은 숫자로 입력하세요.");
                    ex.printStackTrace(); // 예외 메시지 출력
                }
            }
        });

        btn1.addActionListener(e -> {
            // 선택된 행의 인덱스를 가져옴
            int selectedRow = customerTable.getSelectedRow(); // JTable에서 선택된 행 인덱스 가져오기

            if (selectedRow != -1) { // 선택된 행이 있을 경우
                try {
                    // 선택된 행의 고객 ID를 가져옴
                    int customerId = (Integer) customerModel.getValueAt(selectedRow, 0);

                    // 회원 등급 업데이트
                    DB_Conn_Query customerService = new DB_Conn_Query();
                    customerService.updateMembershipGrade(customerId);

                    // 성공 메시지 창 표시
                    JOptionPane.showMessageDialog(null, "Membership grade updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);

                    // 기존 테이블 데이터를 모두 삭제(고객)
                    int rowCount = customerModel.getRowCount();
                    for (int i = rowCount - 1; i >= 0; i--) {
                        customerModel.removeRow(i);
                    }

                    // 음식점 데이터 테이블 새로고침
                    dbConn.loadCustomerData(customerModel);

                } catch (Exception ex) {
                    // 오류 발생 시 메시지 창 표시
                    JOptionPane.showMessageDialog(null, "An error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // 선택된 고객이 없을 경우 메시지 창 표시
                JOptionPane.showMessageDialog(null, "Please select a customer.", "Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        btn2.addActionListener(e ->  {
            // 고객 추가 버튼 클릭 이벤트
            // 팝업 창을 띄워서 고객 정보를 입력받기 위한 JTextField 등 사용
            JTextField idField = new JTextField(20);
            JTextField nameField = new JTextField(20);
            JTextField phoneField = new JTextField(20);
            JTextField addressField = new JTextField(20);
            JTextField order_countField = new JTextField(20);
            JTextField Membership_gradField = new JTextField(20);

            // 팝업 창 생성
            JPanel panel = new JPanel();
            panel.add(new JLabel("고객ID:"));
            panel.add(idField);
            panel.add(new JLabel("고객명:"));
            panel.add(nameField);
            panel.add(new JLabel("전화번호:"));
            panel.add(phoneField);
            panel.add(new JLabel("주소:"));
            panel.add(addressField);
            panel.add(new JLabel("주문횟수:"));
            panel.add(order_countField);
            panel.add(new JLabel("등급:"));
            panel.add(Membership_gradField);

            int option = JOptionPane.showConfirmDialog(null, panel, "새 고객 추가", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (option == JOptionPane.OK_OPTION) {
                String customerId = idField.getText();
                String customerName = nameField.getText();
                String customerPhone = phoneField.getText();
                String customerAddress = addressField.getText();
                String customer_order_count = order_countField.getText();
                String customermembership_grad = Membership_gradField.getText();


                // 유효성 검사 (예시)
                if (customerName.isEmpty() || customerPhone.isEmpty() || customerAddress.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "모든 필드를 입력해주세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 테이블에 추가
                DefaultTableModel customerModel = (DefaultTableModel) customerTable.getModel();
                customerModel.addRow(new Object[]{customerId,customerName, customerPhone, customerAddress, customer_order_count, customermembership_grad});


                // 데이터베이스에 추가
                dbConn.addCustomerToDatabase(customerId,customerName, customerPhone, customerAddress, customer_order_count, customermembership_grad);

                // 성공 메시지
                JOptionPane.showMessageDialog(null, "고객이 성공적으로 추가되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        btn3.addActionListener(e -> {
            // 고객 수정 버튼 클릭 이벤트
            // 선택된 고객 정보를 테이블에서 가져오기
            int selectedRow = customerTable.getSelectedRow();

            // 고객이 선택되지 않은 경우
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "수정할 고객을 선택해주세요.", "선택 오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 선택된 고객의 기존 정보 가져오기
            String customerId = customerTable.getValueAt(selectedRow, 0).toString();
            String customerName = customerTable.getValueAt(selectedRow, 1).toString();
            String customerPhone = customerTable.getValueAt(selectedRow, 2).toString();
            String customerAddress = customerTable.getValueAt(selectedRow, 3).toString();
            String customerOrderCount = customerTable.getValueAt(selectedRow, 4).toString();
            String customerMembershipGrade = customerTable.getValueAt(selectedRow, 5).toString();

            // 팝업 창을 띄워서 고객 정보를 수정하기 위한 JTextField 등 사용
            JTextField idField = new JTextField(customerId, 20);
            JTextField nameField = new JTextField(customerName, 20);
            JTextField phoneField = new JTextField(customerPhone, 20);
            JTextField addressField = new JTextField(customerAddress, 20);
            JTextField orderCountField = new JTextField(customerOrderCount, 20);
            JTextField membershipGradeField = new JTextField(customerMembershipGrade, 20);

            // 팝업 창 생성
            JPanel panel = new JPanel();
            panel.add(new JLabel("고객ID:"));
            panel.add(idField);
            panel.add(new JLabel("고객명:"));
            panel.add(nameField);
            panel.add(new JLabel("전화번호:"));
            panel.add(phoneField);
            panel.add(new JLabel("주소:"));
            panel.add(addressField);
            panel.add(new JLabel("주문횟수:"));
            panel.add(orderCountField);
            panel.add(new JLabel("등급:"));
            panel.add(membershipGradeField);

            int option = JOptionPane.showConfirmDialog(null, panel, "고객 정보 수정", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (option == JOptionPane.OK_OPTION) {
                // 수정된 고객 정보 가져오기
                String newCustomerId = idField.getText();
                String newCustomerName = nameField.getText();
                String newCustomerPhone = phoneField.getText();
                String newCustomerAddress = addressField.getText();
                String newCustomerOrderCount = orderCountField.getText();
                String newCustomerMembershipGrade = membershipGradeField.getText();

                // 유효성 검사 (예시)
                if (newCustomerName.isEmpty() || newCustomerPhone.isEmpty() || newCustomerAddress.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "모든 필드를 입력해주세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 테이블의 데이터 수정
                customerTable.setValueAt(newCustomerId, selectedRow, 0);
                customerTable.setValueAt(newCustomerName, selectedRow, 1);
                customerTable.setValueAt(newCustomerPhone, selectedRow, 2);
                customerTable.setValueAt(newCustomerAddress, selectedRow, 3);
                customerTable.setValueAt(newCustomerOrderCount, selectedRow, 4);
                customerTable.setValueAt(newCustomerMembershipGrade, selectedRow, 5);

                // 데이터베이스에 수정된 내용 반영
                dbConn.updateCustomerInDatabase(newCustomerId, newCustomerName, newCustomerPhone, newCustomerAddress, newCustomerOrderCount, newCustomerMembershipGrade);

                // 성공 메시지
                JOptionPane.showMessageDialog(null, "고객 정보가 성공적으로 수정되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // 배달원 전용 버튼 패널 추가
        JPanel deliveryButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addButton = new JButton("추가");
        JButton editButton = new JButton("수정");
        JButton deleteButton = new JButton("삭제");

        deliveryButtonPanel.add(addButton);
        deliveryButtonPanel.add(editButton);
        deliveryButtonPanel.add(deleteButton);
        deliveryPanel.add(deliveryButtonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> {
            // 배달원 추가를 위한 입력 필드 생성
            JTextField nameField = new JTextField(20);
            JTextField phoneField = new JTextField(20);
            JTextField ratingField = new JTextField(20);

            // 입력 필드가 포함된 패널 생성
            JPanel panel = new JPanel();
            panel.add(new JLabel("배달원 이름:"));
            panel.add(nameField);
            panel.add(new JLabel("전화번호:"));
            panel.add(phoneField);
            panel.add(new JLabel("평점:"));
            panel.add(ratingField);

            // 팝업 창을 사용하여 배달원 정보를 입력받음
            int option = JOptionPane.showConfirmDialog(this, panel, "배달원 추가", JOptionPane.OK_CANCEL_OPTION);

            // OK 버튼을 클릭한 경우
            if (option == JOptionPane.OK_OPTION) {
                // 입력된 값 가져오기
                String name = nameField.getText();
                String phone = phoneField.getText();
                String ratingText = ratingField.getText();

                // 유효성 검사
                if (name.isEmpty() || phone.isEmpty() || ratingText.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "모든 필드를 입력해주세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 평점은 숫자여야 하므로 숫자 유효성 검사
                try {
                    float rating = Float.parseFloat(ratingText);

                    // 평점 유효성 검사 (예시: 1~5 범위로 설정)
                    if (rating < 1 || rating > 5) {
                        JOptionPane.showMessageDialog(this, "평점은 1부터 5까지 입력해주세요.", "평점 오류", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // 배달원 추가 메서드 호출
                    int newDeliveryPersonId = dbConn.addDeliveryPersonToDatabase(name, phone, rating);

                    if (newDeliveryPersonId != -1) {
                        // 배달원 추가가 성공적으로 이루어진 경우
                        JOptionPane.showMessageDialog(this, "배달원이 추가되었습니다. 배달원 ID: " + newDeliveryPersonId);

                        // 배달원 목록 테이블 갱신 (테이블 데이터 갱신)
                        dbConn.loadDeliveryData(deliveryModel); // 배달원 데이터 새로 로드

                    } else {
                        JOptionPane.showMessageDialog(this, "배달원 추가에 실패했습니다.");
                    }

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "평점은 숫자로 입력해야 합니다.", "입력 오류", JOptionPane.ERROR_MESSAGE);
                }
            }
        });



        // 배달원 수정 버튼 이벤트
        editButton.addActionListener(e -> {
            int selectedRow = deliveryTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "수정할 배달원을 선택하세요.", "선택 오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 선택된 배달원 정보 가져오기
            String deliveryPersonId = deliveryTable.getValueAt(selectedRow, 0).toString();
            String name = deliveryTable.getValueAt(selectedRow, 1).toString();
            String phoneNumber = deliveryTable.getValueAt(selectedRow, 2).toString();
            String rating = deliveryTable.getValueAt(selectedRow, 3).toString();

            // 입력 필드 준비
            JTextField nameField = new JTextField(name, 20);
            JTextField phoneField = new JTextField(phoneNumber, 20);
            JTextField ratingField = new JTextField(rating, 20);

            JPanel panel = new JPanel();
            panel.add(new JLabel("이름:"));
            panel.add(nameField);
            panel.add(new JLabel("전화번호:"));
            panel.add(phoneField);
            panel.add(new JLabel("평점:"));
            panel.add(ratingField);

            // 수정 다이얼로그 띄우기
            int option = JOptionPane.showConfirmDialog(this, panel, "배달원 정보 수정", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (option == JOptionPane.OK_OPTION) {
                String newName = nameField.getText();
                String newPhone = phoneField.getText();
                String newRating = ratingField.getText();

                // 데이터베이스 업데이트 호출
                dbConn.updateDeliveryPersonInDatabase(deliveryPersonId, newName, newPhone, newRating);

                // 테이블 데이터 갱신
                deliveryTable.setValueAt(newName, selectedRow, 1);
                deliveryTable.setValueAt(newPhone, selectedRow, 2);
                deliveryTable.setValueAt(newRating, selectedRow, 3);

                JOptionPane.showMessageDialog(this, "배달원 정보가 성공적으로 수정되었습니다.");
            }
        });

// 배달원 삭제 버튼 이벤트
        deleteButton.addActionListener(e -> {
            int selectedRow = deliveryTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "삭제할 배달원을 선택하세요.", "선택 오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String deliveryPersonId = deliveryTable.getValueAt(selectedRow, 0).toString();

            // 삭제 확인 다이얼로그
            int confirm = JOptionPane.showConfirmDialog(this, "정말 삭제하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // 데이터베이스 삭제 호출
                dbConn.deleteDeliveryPersonInDatabase(deliveryPersonId);

                // 테이블에서 행 제거
                deliveryModel.removeRow(selectedRow);
                JOptionPane.showMessageDialog(this, "배달원이 삭제되었습니다.");
            }
        });



    }





    // 패널 하단에 버튼을 추가하기 위한 메서드
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

            // 주문 버튼 클릭 이벤트 처리
            orderButton.addActionListener(e -> {
                // restaurantTable이 제대로 초기화된 경우에만 진행
                if (restaurantTable != null) {
                    int selectedRestaurantRow = restaurantTable.getSelectedRow();  // 선택된 음식점 행
                    if (selectedRestaurantRow == -1) {
                        JOptionPane.showMessageDialog(this, "음식점을 선택하세요.");
                        return;
                    }

                    int restaurantId = (int) restaurantModel.getValueAt(selectedRestaurantRow, 0); // 음식점 ID
                    String restaurantName = (String) restaurantModel.getValueAt(selectedRestaurantRow, 1); // 음식점 이름

                    // 선택된 메뉴 가져오기
                    java.util.List<Object[]> selectedMenus = new ArrayList<>();
                    for (int i = 0; i < menuModel.getRowCount(); i++) {
                        Boolean isSelected = (Boolean) menuModel.getValueAt(i, 0); // 체크박스 열 확인
                        if (isSelected != null && isSelected) {
                            String menuName = (String) menuModel.getValueAt(i, 1); // 메뉴 이름
                            Float price = (Float) menuModel.getValueAt(i, 2); // 메뉴 가격
                            selectedMenus.add(new Object[]{menuName, price});
                        }
                    }

                    if (selectedMenus.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "메뉴를 선택하세요.");
                        return;
                    }

                    // 선택된 메뉴 확인
                    StringBuilder orderDetails = new StringBuilder("음식점: " + restaurantName + "\n");
                    orderDetails.append("주문 메뉴:\n");
                    float totalPrice = 0;
                    for (Object[] menu : selectedMenus) {
                        orderDetails.append("- ").append(menu[0]).append(": ").append(menu[1]).append("원\n");
                        totalPrice += (Float) menu[1];
                    }
                    orderDetails.append("총 가격: ").append(totalPrice).append("원\n");

                    // 확인 창
                    int confirm = JOptionPane.showConfirmDialog(this, orderDetails.toString(), "주문 확인", JOptionPane.OK_CANCEL_OPTION);
                    if (confirm == JOptionPane.OK_OPTION) {
                        // 주문 데이터 추가
                        try {
                            dbConn.addOrder(restaurantId, totalPrice); // DB에 주문 추가
                            JOptionPane.showMessageDialog(this, "주문이 성공적으로 접수되었습니다.");

                            // 주문 테이블 새로고침
                            int rowCount = orderModel.getRowCount();
                            for (int i = rowCount - 1; i >= 0; i--) {
                                orderModel.removeRow(i);
                            }
                            dbConn.loadOrderData(orderModel); // 주문 데이터 로드
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(this, "주문 처리 중 오류가 발생했습니다: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "음식점 테이블이 초기화되지 않았습니다.");
                }
            });


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
            // 할인 적용 버튼 클릭 시 이벤트 처리
            applyDiscountButton.addActionListener(a -> {
                try {
                    // 할인율 가져오기
                    String discountText = discountField.getText().trim(); // 할인율 입력
                    int discount = Integer.parseInt(discountText);

                    // 할인율 유효성 검사
                    if (discount < 0 || discount > 100) {
                        JOptionPane.showMessageDialog(null, "할인율은 0~100% 사이로 입력해주세요.");
                        return;
                    }

                    // 메뉴 모델 데이터에 할인율 적용
                    for (int i = 0; i < menuModel.getRowCount(); i++) {
                        Boolean isSelected = (Boolean) menuModel.getValueAt(i, 0); // 체크박스 열 확인
                        if (isSelected != null && isSelected) { // 선택된 메뉴만 처리
                            String menuName = (String) menuModel.getValueAt(i, 1); // 메뉴 이름
                            float originalPrice = (float) menuModel.getValueAt(i, 2); // 원래 가격

                            // 할인된 가격 계산
                            float discountedPrice = originalPrice * (1 - (discount / 100.0f));

                            // 테이블에 할인된 가격 설정
                            menuModel.setValueAt(discountedPrice, i, 2);

                            // 데이터베이스에 할인된 가격 업데이트
                            boolean isUpdated = dbConn.updateMenuPriceByName(menuName, discountedPrice);

                            if (isUpdated) {
                                System.out.println(menuName + "의 가격이 " + discountedPrice + "로 업데이트되었습니다.");
                            } else {
                                System.out.println(menuName + "의 가격 업데이트에 실패했습니다.");
                            }
                        }
                    }

                    JOptionPane.showMessageDialog(null, "할인율이 적용되었습니다.");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "유효한 숫자를 입력해주세요.");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "메뉴 가격 업데이트 중 오류가 발생했습니다.");
                }
            });

















            // 음식점 탭 버튼 클릭 이벤트 (구현 필요)
            orderButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "주문 버튼 클릭"));

            topCustomerButton.addActionListener(e -> {
                // 음식점 ID를 입력받고 단골 고객 조회
                String restaurantIdStr = JOptionPane.showInputDialog(this, "음식점 ID를 입력하세요");
                if (restaurantIdStr != null && !restaurantIdStr.isEmpty()) {
                    try {
                        int restaurantId = Integer.parseInt(restaurantIdStr);

                        // DBHelper에서 단골 고객 조회
                        DB_Conn_Query dbHelper = new DB_Conn_Query();
                        String loyalCustomers = dbHelper.findLoyalCustomers(restaurantId, 2);  // 2번 이상 주문한 고객 조회

                        // 단골 고객 결과를 메시지로 출력
                        JOptionPane.showMessageDialog(this, loyalCustomers);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "유효한 음식점 ID를 입력하세요.");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "음식점 ID를 입력하세요.");
                }
            });

            topRatingButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "최고 평점 조회 버튼 클릭"));
            applyDiscountButton.addActionListener(e -> {
                try {
                    int discount = Integer.parseInt(discountField.getText());
                    JOptionPane.showMessageDialog(this, "할인율 " + discount + "% 적용!");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "유효한 숫자를 입력하세요.");
                }
            });
//        }else if ("고객".equals(tabName)) {
//            JButton updateMembershipButton = new JButton("회원 등급 업데이트");
//
//            // 고객 탭 전용 버튼 추가
//            buttonPanel.add(updateMembershipButton);

//            // 고객 탭 버튼 클릭 이벤트
//            updateMembershipButton.addActionListener(e -> {
//                // 선택된 행의 인덱스를 가져옴
//                int selectedRow = customerTable.getSelectedRow(); // JTable에서 선택된 행 인덱스 가져오기
//
//                if (selectedRow != -1) { // 선택된 행이 있을 경우
//                    // 고객 ID 가져오기 (고객 ID는 첫 번째 컬럼에 있다고 가정)
//                    int customerId = (Integer) customerModel.getValueAt(selectedRow, 0); // 첫 번째 컬럼에서 고객 ID 가져오기
//
//                    // 회원 등급 업데이트 프로시저 호출
//                    CustomerService customerService = new CustomerService();
//                    customerService.updateMembershipGrade(customerId);
//
//                    // 사용자에게 성공 메시지 표시
//                    showAlert("Success", "Membership grade updated successfully.");
//                } else {
//                    // 선택된 고객이 없을 경우 오류 메시지 표시
//                    showAlert("Error", "Please select a customer.");
//                }
//            });

        }


        // 공통 버튼 추가 (주문/리뷰 탭에서는 추가 버튼이 안 보임)
        if (!"리뷰".equals(tabName)) {
            buttonPanel.add(addButton);
        }

        // 공통 버튼 추가
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
