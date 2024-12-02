import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Todo extends JFrame {

    private JTextField inputField;
    private JTextArea taskArea;
    private JButton addButton;
    private JButton deleteButton;

    // db connection
    private static final String DB = "jdbc:mysql://localhost:3306/todo_db";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    // constructor
    public Todo() {
        setTitle("Todo list app");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // layout
        setLayout(new BorderLayout(10, 10));

        // init func
        setupGUI();
        createTable();
        getTasks();
    }

    // gui
    public void setupGUI() {
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));

        inputField = new JTextField();
        addButton = new JButton("Add");

        topPanel.add(inputField, BorderLayout.CENTER);
        topPanel.add(addButton, BorderLayout.EAST);

        taskArea = new JTextArea();
        taskArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(taskArea);

        deleteButton = new JButton("Delete");

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(deleteButton, BorderLayout.SOUTH);

        // actions
        addButton.addActionListener(e -> addTask());
        deleteButton.addActionListener(e -> deleteTask());

    }

    // create table
    public void createTable() {

        String sql = "CREATE TABLE IF NOT EXISTS tasks " +
                    "(id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "task_name VARCHAR(100) NOT NULL)";

        try (Connection conn = DriverManager.getConnection(DB, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Datubāzes error: " + e.getMessage());
        }
    }

    // get all tasks
    private void getTasks() {

        // clear list
        taskArea.setText("");

        // select query
        String sql = "SELECT task_name FROM tasks";

        try (Connection conn = DriverManager.getConnection(DB, USER, PASSWORD);
             Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {

            while(rs.next()) {
                taskArea.append(rs.getString("task_name") + "\n");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Ielādes kļūda: " + e.getMessage());
        }

    }

    // add task
    private void addTask() {
        String task = inputField.getText().trim();

        if (task.isEmpty()) {
            return;
        }

        String sql = "INSERT INTO tasks (task_name) VALUES (?)";

        try (Connection conn = DriverManager.getConnection(DB, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, task);
            pstmt.executeUpdate();

            getTasks();
            inputField.setText("");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Pievienošanas ķļūda: " + e.getMessage());
        }

    }

    // delete
    private void deleteTask() {

        String sql = "DELETE FROM tasks ORDER BY id DESC LIMIT 1";

        try (Connection conn = DriverManager.getConnection(DB, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);

            getTasks();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Dzēšanas ķļūda: " + e.getMessage());
        }

    }



}
