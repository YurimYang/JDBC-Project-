import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.text.DateFormatter;

public class DBManage extends JFrame {

    JComboBox<String> selAttributes;
    JComboBox<String> selAttributeForUpdate;
    JComboBox<String> selGender;
    JComboBox<String> selGenderForUpdate;
    JComboBox<String> selDept;
    JComboBox<String> selDeptForUpdate;
    JTextField userIn;
    JTextField newDname;//삽입에서 신규 부서 이름
    JTextField newDno;//삽입에서 신규 부서 번호
    JTextField userInForUpdate;
    String attributes[] = {"전체", "부서", "이름", "주민번호", "성별", "생년월일", "주소", "임금", "상사"};
    String transAttribute[] = {"*", "Dname", "Name", "Ssn", "Sex", "Bdate", "Address", "Salary",
        "Supervisor"};
    String gender[] = {"남성", "여성"};
    String dept[] = {"Research", "Administration", "Headquarters"};
    String title[] = {"선택", "소속부서", "이름", "주민번호(Ssn)", "성별", "생년월일", "주소", "임금", "상사(Supervisor)"};
    //결과에서 사용할 Table의 열이름
    String resCnt = "0";

    //DB 연결용
    public Connection conn;
    public PreparedStatement ps;
    public ResultSet r;

    //Insert구문
    String managerSsn[] = {"null", "333445555", "987654321", "888665555"};
    String month[] = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
    String day[] = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
        "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25",
        "26", "27",
        "28", "29", "30", "31"};


    public DBManage() {
        //창 이름 설정
        super("Information Retrival System");

        setLayout(new BorderLayout());

        //DB연결
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String user = "root";
            String pwd = "root";
            String dbname = "company";
            String url = "jdbc:mysql://localhost:601/" + dbname + "?serverTimezone=UTC";

            conn = DriverManager.getConnection(url, user, pwd);
            System.out.println("정상적으로 연결됐습니다. ");

        } catch (SQLException e1) {
            System.out.println("연결할 수 없습니다.");
            e1.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("드라이버를 로드할 수 없습니다.");
            e.printStackTrace();
        }

        //검색범위
        Box scopeBox = Box.createHorizontalBox();
        JLabel scopeTxt = new JLabel("범위 설정");
        selAttributes = new JComboBox<String>(attributes);
        JLabel txt = new JLabel("항목을 선택하세요");
        scopeBox.add(scopeTxt);
        scopeBox.add(selAttributes);
        scopeBox.add(txt);

        JPanel scopePanel = new JPanel(new BorderLayout());
        scopePanel.setBorder(new TitledBorder(new EtchedBorder(), "검색 범위"));
        scopePanel.add(scopeBox, BorderLayout.CENTER);

        //검색항목(Checkbox)
        Box chkBox = Box.createHorizontalBox();
        JCheckBox checkName = new JCheckBox("이름(Name)");
        JCheckBox checkSsn = new JCheckBox("주민번호(Ssn)");
        JCheckBox checkBdate = new JCheckBox("생년월일(Bdate)");
        JCheckBox checkAddress = new JCheckBox("주소(Address)");
        JCheckBox checkSex = new JCheckBox("성별(Sex)");
        JCheckBox checkSalary = new JCheckBox("임금(Salary)");
        JCheckBox checkSupervisor = new JCheckBox("상사(Supervisor)");
        JCheckBox checkDepartment = new JCheckBox("부서(Department)");
        JButton searchBtn = new JButton("검색");
        //Box에 CheckBox들 추가
        chkBox.add(checkName);
        chkBox.add(checkSsn);
        chkBox.add(checkBdate);
        chkBox.add(checkAddress);
        chkBox.add(checkSex);
        chkBox.add(checkSalary);
        chkBox.add(checkSupervisor);
        chkBox.add(checkDepartment);
        chkBox.add(searchBtn);
        //Box를 Pannel에 추가 & Box 이름 설정
        JPanel chkBoxPanel = new JPanel(new BorderLayout());
        chkBoxPanel.setBorder(new TitledBorder(new EtchedBorder(), "검색 항목"));
        chkBoxPanel.add(chkBox, BorderLayout.CENTER);

        //검색결과
        Box resultBox = Box.createHorizontalBox();
        //initData - 초기 Loading시에만 없다는 것을 보여주기 위해
        String initData[][] = {
            {"None", "None", "None", "None", "None", "None", "None", "None", "None"}};
        //테이블 생성
        JTable resTable = new JTable(initData, title);
        //스크롤 기능 추가
        JScrollPane scrollResTable = new JScrollPane(resTable);
        resultBox.add(scrollResTable);
        JPanel resTablePanel = new JPanel(new BorderLayout());
        resTablePanel.setBorder(new TitledBorder(new EtchedBorder(), "검색 결과"));
        resTablePanel.add(resultBox, BorderLayout.CENTER);

        //검색 결과 수 & 선택한 직원(이름, 수)
        Box resCountBox = Box.createVerticalBox();
        JLabel cntTxt = new JLabel("검색 결과 : 0");
        JLabel resTxt = new JLabel("선택한 직원 : ");
        JButton delBtn = new JButton("선택한 직원 삭제");
        JPanel resCountPanel = new JPanel(new BorderLayout());
        resCountBox.add(cntTxt);
        resCountBox.add(resTxt);
        resCountBox.add(delBtn);
        resCountPanel.setBorder(new TitledBorder(new EtchedBorder(), "검색 결과 & 선택 결과"));
        resCountPanel.add(resCountBox, BorderLayout.WEST);

        //새로운 Tuple 삽입하기 - fName, minit, lName, ssn, bDate, address,sex,salary,superSsn, dno 변수 이용
        Box insertBox = Box.createVerticalBox();

        Box fnameBox = Box.createHorizontalBox();
        JTextField fName = new JTextField(20);
        fnameBox.add(new JLabel("First name : "));
        fnameBox.add(fName);
        insertBox.add(fnameBox);

        Box minitBox = Box.createHorizontalBox();
        JTextField mInit = new JTextField(20);
        minitBox.add(new JLabel("Middle Init : "));
        minitBox.add(mInit);
        insertBox.add(minitBox);

        Box lnameBox = Box.createHorizontalBox();
        JTextField lName = new JTextField(20);
        lnameBox.add(new JLabel("Last name : "));
        lnameBox.add(lName);
        insertBox.add(lnameBox);

        Box ssnBox = Box.createHorizontalBox();
        JTextField ssn = new JTextField(20);
        ssnBox.add(new JLabel("Ssn(주민 번호) <필수 입력> : "));
        ssnBox.add(ssn);
        insertBox.add(ssnBox);

//        Box bdateBox = Box.createHorizontalBox();
//        JTextField bDate = new JTextField(20);
//        bdateBox.add(new JLabel("생년월일 <YYYY-MM-DD> : "));
//        bdateBox.add(bDate);
//        insertBox.add(bdateBox);
        Box bdateBox = Box.createHorizontalBox();
        JTextField bYear = new JTextField(20);
        JComboBox<String> bMonth = new JComboBox<>(month);
        JComboBox<String> bDay = new JComboBox<>(day);
        bdateBox.add(new JLabel("생년월일 : "));
        bdateBox.add(bYear);
        bdateBox.add(bMonth);
        bdateBox.add(bDay);
        insertBox.add(bdateBox);

        Box addressBox = Box.createHorizontalBox();
        JTextField address = new JTextField(20);
        addressBox.add(new JLabel("주소 : "));
        addressBox.add(address);
        insertBox.add(addressBox);

        Box sexBox = Box.createHorizontalBox();
        JComboBox<String> sex = new JComboBox<String>(gender);
        sexBox.add(new JLabel("성별 : "));
        sexBox.add(sex);
        insertBox.add(sexBox);

        Box salaryBox = Box.createHorizontalBox();
        JTextField salary = new JTextField(20);
        salaryBox.add(new JLabel("임금 : "));
        salaryBox.add(salary);
        insertBox.add(salaryBox);

//        Box superSsnBox = Box.createHorizontalBox();
//        JTextField superSsn = new JTextField(20);
//        superSsnBox.add(new JLabel("상사 Ssn : "));
//        superSsnBox.add(superSsn);
//        insertBox.add(superSsnBox);
        Box superSsnBox = Box.createHorizontalBox();
        JComboBox<String> superSsn = new JComboBox<String>(managerSsn);
        superSsnBox.add(new JLabel("상사 Ssn : "));
        superSsnBox.add(superSsn);
        insertBox.add(superSsnBox);

        Box dNameBox = Box.createHorizontalBox();
        JComboBox<String> dName = new JComboBox<String>(dept);
        dName.insertItemAt("신규부서", 0);
        dNameBox.add(new JLabel("부서 : "));
        dNameBox.add(dName);
        insertBox.add(dNameBox);

        JButton insertBtn = new JButton("신규 정보 추가");
        insertBox.add(insertBtn);
        JPanel insertPanel = new JPanel(new BorderLayout());
        insertPanel.setBorder(new TitledBorder(new EtchedBorder(), "새로운 직원 정보 추가"));
        insertPanel.add(insertBox, BorderLayout.WEST);

        //Update문
        Box updateBox = Box.createHorizontalBox();
        JLabel updateTxt = new JLabel("데이터 수정 : ");
        JLabel updateTmpTxt = new JLabel("속성을 선택하고 수정하세요");
        selAttributeForUpdate = new JComboBox<String>(title);
        JButton updateBtn = new JButton("UPDATE");
        updateBox.add(updateTxt);
        updateBox.add(selAttributeForUpdate);
        updateBox.add(updateTmpTxt);
        updateBox.add(updateBtn);
        JPanel updatePanel = new JPanel(new BorderLayout());
        updatePanel.setBorder(new TitledBorder(new EtchedBorder(), "선택 항목 Update"));
        updatePanel.add(updateBox, BorderLayout.CENTER);

        //메인프레임에 패널들 붙이기
        Box center = Box.createVerticalBox();
        center.add(scopePanel);
        center.add(chkBoxPanel);
        center.add(resTablePanel);
        center.add(resCountPanel);
        center.add(updatePanel);
        center.add(insertPanel);
        add(center, BorderLayout.CENTER);

        //속성에 따라 입력 받는 형식 결정 - 검색 부분
        selAttributes.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String attribute = selAttributes.getSelectedItem().toString();
                scopeBox.remove(2);
                //ComboBox로 추가 입력을 받아야 하는 속성 : 부서, 성별
                if (attribute.equals("부서")) {
                    selDept = new JComboBox<String>(dept);
                    scopeBox.add(selDept);
                } else if (attribute.equals("성별")) {
                    selGender = new JComboBox<String>(gender);
                    scopeBox.add(selGender);
                }
                //"전체를 할 경우 - Text만 출력
                else if (attribute.equals("전체")) {
                    JLabel msg = new JLabel("전체 속성을 선택하셨습니다");
                    scopeBox.add(msg);
                } else {
                    userIn = new JTextField(20);
                    scopeBox.add(userIn);
                }
                //Update Scopebox
                scopeBox.revalidate();
                scopeBox.repaint();
            }
        });
        //속성에 따라 입력받는 형식 결정 - Update
        selAttributeForUpdate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String attribute = selAttributeForUpdate.getSelectedItem().toString();
                updateBox.remove(2);
                //ComboBox로 추가 입력을 받아야 하는 속성 : 부서, 성별
                if (attribute.equals("소속부서")) {
                    selDeptForUpdate = new JComboBox<String>(dept);
                    updateBox.add(selDeptForUpdate, 2);
                } else if (attribute.equals("성별")) {
                    selGenderForUpdate = new JComboBox<String>(gender);
                    updateBox.add(selGenderForUpdate, 2);
                }
                //"전체를 할 경우 - Text만 출력
                else if (attribute.equals("선택")) {
                    JLabel msgUpdate = new JLabel("속성을 선택하세요");
                    updateBox.add(msgUpdate, 2);
                } else {
                    userInForUpdate = new JTextField(20);
                    updateBox.add(userInForUpdate, 2);
                }
                //Update updateBox
                updateBox.revalidate();
                updateBox.repaint();
            }
        });

        //신규부서를 입력하는 경우 - 신규부서이면 부서명과 부서번호를 받음
        dName.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String deptName = dName.getSelectedItem().toString();
                if (deptName.equals("신규부서")) {
                    newDname = new JTextField(20);
                    newDno = new JTextField(20);
                    dNameBox.add(newDname);
                    dNameBox.add(newDno);
                    dNameBox.add(new JTextField("신규 부서명, 신규 부서 번호로 입력하세요"));
                } else {
                    int componentCnt = dNameBox.getComponentCount();
                    // 체크 및 마지막 2개 컴포넌트 제거
                    if (componentCnt == 5) {
                        dNameBox.remove(4);
                        dNameBox.remove(3);
                        dNameBox.remove(2);
                    }
                }
                //Update dNameBox
                dNameBox.revalidate();
                dNameBox.repaint();
            }
        });
        //검색버튼을 누르는 경우 - search(조건에 맞게 검색을 하고 모든 열에 대한 정보를 가져옴)
        //검색시 마다 체크박스 초기화 및 ssn정보 초기화
        searchBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //사용자 입력
                String userSelScope; //검색 기준 속성
                String userInConditon; //입력된 검색 조건

                List<String> userSelect = new ArrayList<>(); //사용자가 체크한 속성들

                String attribute = selAttributes.getSelectedItem().toString();
                //검색조건으로 설정한 속성 찾음
                for (int i = 0; i < attributes.length; i++) {
                    if (attribute.equals(attributes[i])) {
                        userSelScope = transAttribute[i];
                        break;
                    }
                }
                //chkBox결과 가져오기
                Component[] components = chkBox.getComponents(); // chkBox에 포함된 모든 컴포넌트 가져오기
                String chkBoxEntity[] = {"부서(Department)", "이름(Name)", "주민번호(Ssn)", "생년월일(Bdate)",
                    "주소(Address)", "성별(Sex)", "임금(Salary)", "상사(Supervisor)"};
                //chkbox 체크 여부를 확인하고, 체크된 것을 리스트에 저장
                for (Component component : components) {
                    if (component instanceof JCheckBox) { // JCheckBox 인지 확인
                        JCheckBox checkBox = (JCheckBox) component; // 형변환
                        String checkBoxText = checkBox.getText(); // 체크박스의 텍스트 가져오기
                        boolean isSelected = checkBox.isSelected(); // 체크 여부 확인
                        if (isSelected) {
                            for (int i = 1; i < attributes.length; i++) {
                                if (checkBoxText.equals(chkBoxEntity[i])) {
                                    String currentEntity = transAttribute[i];
                                    if (currentEntity.equals("Name")) {
                                        //이름일 경우 : Fname, Minit, Lname으로 수정
                                        userSelect.add("Fname");
                                        userSelect.add("Minit");
                                        userSelect.add("Lname");
                                    } else {
                                        userSelect.add(currentEntity);
                                    }
                                }
                            }
                        }
                    }
                }
                if (attribute.equals("전체")) {
                    //사용자가 "전체"를 선택한 경우
                } else if (attribute.equals("성별")) {
                    //사용자가 "성별"을 선택한 경우
                    userInConditon = selGender.getSelectedItem().toString();
                } else if (attribute.equals("부서")) {
                    //사용자가 "부서"를 설정한 경우
                    userInConditon = selDept.getSelectedItem().toString();
                } else {
                    //이외의 속성은 - 사용자가 직접 입력
                    userInConditon = userIn.getText();
                }
                //추가 코드 작성

                // 테이블 모델 생성
                //기존테이블 삭제
                resultBox.remove(0);
                //예시 테이블 - 제일 앞에는 false로 하고 열에 맞추어 데이터 삽입
                DefaultTableModel model = new DefaultTableModel(new Object[][]{
                    {false, "Item 1"},
                    {true, "Item 2"},
                    {false, "Item 3"},
                    {true, "Item 4"}
                }, new Object[]{"Select", "Item"});

                JTable table = new JTable(model);
                table.getColumnModel().getColumn(0).setCellRenderer(new CheckBoxRenderer());
                table.getColumnModel().getColumn(0).setCellEditor(new CheckBoxEditor());

                JScrollPane scrollPane = new JScrollPane(table);
                resultBox.add(scrollPane);
                //Update dNameBox
                resultBox.revalidate();
                resultBox.repaint();

            }
        });
        //테이블에 체크박스를 누르는 경우 - 누른 튜플의 Ssn을 저장(Primary Key)

        //삭제 버튼을 누르는 경우
        delBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

            }
        });
        //수정 버튼을 누르는 경우
        updateBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

            }
        });
        //신규정보추가
        insertBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //새로운 부서가 추가되는 경우 : dept배열 수정
                //Ssn이 입력이 안된 경우, 추가하지 않고 팝업으로 알림
                try {
                    String insertStmt = "INSERT INTO EMPLOYEE(Fname, Minit, Lname, Ssn, Bdate, Address, Sex, Salary, Super_ssn, Dno, created, modified) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
                    ps = conn.prepareStatement(insertStmt);
                    ps.setString(1, fName.getText());
                    ps.setString(2, mInit.getText());
                    ps.setString(3, lName.getText());
                    ps.setString(4, ssn.getText());
                    String bDate = bYear.getText() + (String) bMonth.getSelectedItem()
                        + (String) bDay.getSelectedItem();
                    ps.setString(5, bDate);
                    ps.setString(6, address.getText());
                    if ((String) sex.getSelectedItem() == "여성") {
                        ps.setString(7, "F");
                    } else {
                        ps.setString(7, "M");
                    }
                    ps.setString(8, salary.getText());
                    ps.setString(9, (String) superSsn.getSelectedItem());
                    if ((String) dName.getSelectedItem() == "Research") {
                        ps.setInt(10, 5);
                    } else if ((String) dName.getSelectedItem() == "Administration") {
                        ps.setInt(10, 4);
                    } else if ((String) dName.getSelectedItem() == "Headquarters") {
                        ps.setInt(10, 1);
                    }
                    ps.setTimestamp(11, Timestamp.valueOf(java.time.LocalDateTime.now()));
                    ps.setTimestamp(12, Timestamp.valueOf(java.time.LocalDateTime.now()));

                    /** 예외처리 **/
                    if (ssn.getText().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Ssn은 필수로 작성해주세요!", "ERROR_MESSAGE",
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    } else if (ssn.getText().length() != 9) {
                        JOptionPane.showMessageDialog(null, "Ssn은 9자리 입니다. 다시 작성해주세요!",
                            "ERROR_MESSAGE", JOptionPane.ERROR_MESSAGE);
                        return;
                    } else if (fName.getText().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Fname은 필수로 작성해주세요!", "ERROR_MESSAGE",
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    } else if (lName.getText().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Lname은 필수로 작성해주세요!", "ERROR_MESSAGE",
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    } else if (bYear.getText().length() != 4
                        || Integer.parseInt(bYear.getText()) > 2023) {
                        JOptionPane.showMessageDialog(null,
                            bYear.getText() + "는 잘못된 Year 형태입니다. 다시 작성해주세요!", "ERROR_MESSAGE",
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    } else if (salary.getText().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Salary는 필수로 작성해주세요!", "ERROR_MESSAGE",
                            JOptionPane.ERROR_MESSAGE);
                        return;

                    } else {
                        ps.executeUpdate();
                        JOptionPane.showMessageDialog(null, "직원 추가 완료!");
                    }
                } catch (SQLException e1) {
                    if (e1.getMessage().contains("PRIMARY")) {
                        JOptionPane.showMessageDialog(null,
                            ssn.getText() + "는 중복된 Ssn입니다. 다시 작성해주세요!", "ERROR_MESSAGE",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }

            }
        });

        //창 크기 설정
        setSize(1200, 900);
        //창을 보이게 함
        setVisible(true);
        //창 위의 X표로 프로그램 종료 설정
        setDefaultCloseOperation(EXIT_ON_CLOSE);

    }
}

class CheckBoxRenderer extends JCheckBox implements TableCellRenderer {

    public CheckBoxRenderer() {
        setHorizontalAlignment(JLabel.CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
        boolean hasFocus, int row, int column) {
        setSelected((value != null && (Boolean) value));
        return this;
    }
}

class CheckBoxEditor extends DefaultCellEditor {

    public CheckBoxEditor() {
        super(new JCheckBox());
        JCheckBox checkBox = (JCheckBox) getComponent();
        checkBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
            }
        });
    }
}
