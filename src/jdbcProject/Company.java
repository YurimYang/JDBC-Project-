package jdbcProject;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 * 관련 레퍼런스
 * 1. dialog : https://blog.naver.com/PostView.nhn?blogId=battledocho&logNo=220035916932
 * 2. 추가,삭제 : https://kim22036.tistory.com/entry/JAVA-Swing%EC%9C%BC%EB%A1%9C-%ED%9A%8C%EC%9B%90%EC%A1%B0%ED%9A%8C-%ED%94%84%EB%A0%88%EC%9E%84-%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0
 */


public class Company extends JFrame implements ActionListener {
    //기본 Frame
    JPanel panel;

    //DB Frame
    JScrollPane ScPane;
    private JTable table;
    private DefaultTableModel model;

    //라벨 모음
    private JLabel searchCond = new JLabel("검색 조건");
    private JLabel searchChk = new JLabel("검색항목 ");
    private JLabel totalEmp = new JLabel("인원수 : ");
    final JLabel totalCount = new JLabel();
    private JLabel Emplabel = new JLabel("선택한 직원: ");
    private JLabel ShowSelectedEmp = new JLabel();
    private JLabel Setlabel = new JLabel("새로운 Salary: ");

    //체크박스 모음
    private JCheckBox c1 = new JCheckBox("Name", true);
    private JCheckBox c2 = new JCheckBox("Ssn", true);
    private JCheckBox c3 = new JCheckBox("Bdate", true);
    private JCheckBox c4 = new JCheckBox("Address", true);
    private JCheckBox c5 = new JCheckBox("Sex", true);
    private JCheckBox c6 = new JCheckBox("Salary", true);
    private JCheckBox c7 = new JCheckBox("Supervisor", true);
    private JCheckBox c8 = new JCheckBox("Department", true);
    private Vector<String> Head = new Vector<String>();


    private static final int BOOLEAN_COLUMN = 0;
    private int NAME_COLUMN = 0;
    private int SALARY_COLUMN = 0;
    private String dShow;


    Container me = this;
    private JTextField setSalary = new JTextField(10);

    //버튼 모음
    private JButton Search_Button = new JButton("직원 검색");
    private JButton Delete_Button = new JButton("직원 삭제");
    private JButton Insert_Button = new JButton("직원 추가");

    //정상 실행 = 1, 비정상 실행 = 0;
    int process = 0;

    //검색(1)

    //검색(2)

    //삭제(3)
    private JDialog deleteDialog;
    private JButton Delete_Inner_Button = new JButton("해당 직원 삭제");

    //추가(4)
    private JDialog insertDialog;
    private JButton Insert_Inner_Button = new JButton("새로운 직원 추가");


    public Company(){


        /** 상단 Panel **/
        //검색 조건(1,2)
        JPanel comboBoxPanel = new JPanel();
        String[] category = {"전체", "부서별"}; //스크롤 되는 콤보박스
        String[] dept = {"Research", "Administration", "Headquarters"}; //검색조건은 맡은 사람이 수정
        comboBoxPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        comboBoxPanel.add(searchCond);
        comboBoxPanel.add(new JComboBox(category));
        comboBoxPanel.add(new JComboBox(dept));

        //검색 항목
        JPanel checkBoxPanel = new JPanel();
        checkBoxPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        checkBoxPanel.add(searchChk);
        checkBoxPanel.add(c1);
        checkBoxPanel.add(c2);
        checkBoxPanel.add(c3);
        checkBoxPanel.add(c4);
        checkBoxPanel.add(c5);
        checkBoxPanel.add(c6);
        checkBoxPanel.add(c7);
        checkBoxPanel.add(c8);
        checkBoxPanel.add(Search_Button);
        Search_Button.addActionListener(this);

        //검색 조건 + 검색 항목을 묶어서 상단에 Panel화
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel,BoxLayout.Y_AXIS)); //상->하로 배치
        topPanel.add(comboBoxPanel); //위쪽부분
        topPanel.add(checkBoxPanel); //아래쪽부분

        /** 하단 Panel **/

        //검색된 인원 수
        JPanel totalPanel = new JPanel();
        totalPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        totalPanel.add(totalEmp);
        topPanel.add(totalCount);

        //직원삭제(3)
        JPanel deletePanel = new JPanel();
        deletePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        deletePanel.add(Delete_Button);
        Delete_Button.addActionListener(this);

        //직원추가(4)
        JPanel insertPanel = new JPanel();
        insertPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        insertPanel.add(Insert_Button);
        Insert_Button.addActionListener(this);

        //검색된 인원수 + 직원삭제 + 직원추가를 묶어서 하단에 Panel화
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel,BoxLayout.X_AXIS)); //좌->우로 배치
        bottomPanel.add(totalPanel);
        bottomPanel.add(deletePanel);
        bottomPanel.add(insertPanel);

        /** 상단, 하단 Panel 묶어서 하나의 Panel화 **/

        panel = new JPanel();
//        ScPane = new JScrollPane(new JTable(model));
//        panel.add(ScPane);
//        add(panel,BorderLayout.CENTER);
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        panel.add(topPanel);
        panel.add(bottomPanel);
//        panel.add(topPanel,BorderLayout.NORTH);
//        panel.add(bottomPanel,BorderLayout.SOUTH);
        revalidate();


        /** delete Dialog **/
        deleteDialog  = new JDialog(this,"Delete Employee", true);
        deleteDialog.setSize(400,150);
        deleteDialog.setLayout(new FlowLayout());
        Delete_Inner_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteDialog.setVisible(true);
            }
        });


        /** insert Dialog **/
        insertDialog  = new JDialog(this,"Insert New Employee", true);
        insertDialog.setSize(400,150);
        insertDialog.setLayout(new FlowLayout());
        Insert_Inner_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertDialog.setVisible(true);
            }
        });





    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //Search_Button
        if(e.getSource() == Search_Button){

        }
        //Delete_Button
        if(e.getSource() == Delete_Button){
            //새로운 창을 띄워 삭제할 회원번호 작성하기
            deleteDialog.setVisible(true);

        }
        //Insert_Button
        if(e.getSource() == Insert_Button){
            //새로운 창을 띄울 예정
            insertDialog.setVisible(true);


        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("2023 JDBC JAVA Project");
        frame.setContentPane(new Company().panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1500,800);
        frame.setVisible(true);
    }

}
