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
        JButton button_c = new JButton("삭제");

        buttonPanel_review.add(button_a);
        buttonPanel_review.add(button_b);
        buttonPanel_review.add(button_c);


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

                    // 고객 데이터 테이블 새로고침
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
            //할인율 버튼 클릭 이벤트
            applyDiscountButton.addActionListener(a -> {
                try {
                            // 할인율 가져오기
                            String discountText = discountField.getText();
                            int discount = Integer.parseInt(discountText);

                            // 할인율 유효성 검사
                            if (discount < 0 || discount > 100) {
                                JOptionPane.showMessageDialog(null, "할인율은 0~100% 사이로 입력해주세요.");
                                return;
                            }

                            // 메뉴 모델 데이터에 할인율 적용
                            for (int i = 0; i < menuModel.getRowCount(); i++) {
                                // 원래 가격 가져오기
                                float originalPrice = (float) menuModel.getValueAt(i, 2); // 가격 열 (3번째 열)

                                // 할인된 가격 계산
                                float discountedPrice = originalPrice * (1 - (discount / 100.0f));

                                // 테이블에 할인된 가격 설정
                                menuModel.setValueAt(discountedPrice, i, 2);
                            }

                            JOptionPane.showMessageDialog(null, "할인율이 적용되었습니다.");
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(null, "유효한 숫자를 입력해주세요.");
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