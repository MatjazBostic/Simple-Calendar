import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleCalendar extends JFrame {

    private JComboBox<String> monthComboBox;
    private JTextField yearTextField;
    private JTextField customDateTextField;
    private JLabel notificationLabel;
    private DefaultTableModel calendarTableModel;

    SimpleCalendar(){

        super("Simple calendar");

        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        monthComboBox = new JComboBox<>(months);
        // On month selected listener
        monthComboBox.addActionListener (new ActionListener () {
            public void actionPerformed(ActionEvent e) {
                setupCalendar();
            }
        });

        yearTextField = new JTextField("2020", 4);
        yearTextField.setFont(new Font("SansSerif", Font.BOLD, 17));
        yearTextField.setHorizontalAlignment(SwingConstants.RIGHT);

        // Allow only numbers to be entered into text field
        ((AbstractDocument) yearTextField.getDocument()).setDocumentFilter(new DocumentFilter() {
            Pattern regEx = Pattern.compile("\\d+");

            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                Matcher matcher = regEx.matcher(text);
                if (!matcher.matches()) {
                    return;
                }
                super.replace(fb, offset, length, text, attrs);
            }
        });
        // Select all text on click
        yearTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        yearTextField.selectAll();
                    }
                });
            }
        });

        customDateTextField = new JTextField("1.1.2020", 6);
        customDateTextField.setFont(new Font("SansSerif", Font.BOLD, 17));
        customDateTextField.setHorizontalAlignment(SwingConstants.RIGHT);
        // Select all text on click
        customDateTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        customDateTextField.selectAll();
                    }
                });
            }
        });

        JButton confirmYearButton = new JButton("Confirm");
        confirmYearButton.setPreferredSize(new Dimension(80,25));

        confirmYearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                setupCalendar();
            }
        });

        JButton confirmCustomDateButton = new JButton("Confirm");
        confirmCustomDateButton.setPreferredSize(new Dimension(80,25));

        confirmCustomDateButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                String dateStr = customDateTextField.getText();

                String[] parts = dateStr.split("\\.");
                if(parts.length != 3){
                    notificationLabel.setText("Error: Incorrect custom date format.");
                    return;
                }
                int day;
                int month;
                int year;
                try {
                    day = Integer.parseInt(parts[0]);
                    month = Integer.parseInt(parts[1]);
                    year = Integer.parseInt(parts[2]);
                } catch (NumberFormatException ex){
                    ex.printStackTrace();
                    notificationLabel.setText("Error: Incorrect custom date format.");
                    return;
                }
                if(month < 1 || month > 12){
                    notificationLabel.setText("Error: Incorrect custom date. Month out of range.");
                    return;
                }
                int numOfDays = HelperFunctions.getDaysInMonth(month, year);
                if(day < 1 || day > numOfDays){
                    notificationLabel.setText("Error: Incorrect custom date. Day out of range.");
                    return;
                }

                if(!notificationLabel.getText().isEmpty()){
                    notificationLabel.setText("");
                }
                yearTextField.setText(parts[2]);
                // Don't have to call setupCalendar(), because this selection calls it:
                monthComboBox.setSelectedIndex(month-1);
            }
        });

        // Setup bottom notification label
        notificationLabel = new JLabel("", JLabel.CENTER);
        notificationLabel.setVerticalAlignment(JLabel.BOTTOM);
        notificationLabel.setForeground(Color.RED);
        notificationLabel.setBorder(new EmptyBorder(0, 0, 5, 0));

        // Setup calendar table
        Object[] columnNames = new Object[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

        calendarTableModel = new DefaultTableModel(columnNames, 0);

        JTable calendarTable = new JTable(calendarTableModel);
        calendarTable.setDefaultEditor(Object.class, null);
        calendarTable.setDefaultRenderer(Object.class, new CustomCellRenderer());

        JScrollPane calendarScrollPane = new JScrollPane(calendarTable);
        calendarScrollPane.setBorder(new EmptyBorder(0, 15, 0, 15));

        // Setup view
        JPanel topPanel = new JPanel();
        topPanel.add(monthComboBox);
        topPanel.add(yearTextField);
        topPanel.add(confirmYearButton);
        topPanel.setBorder(new EmptyBorder(5, 0, 5, 0));

        JPanel customDatePanel = new JPanel();
        JLabel customDateLabel = new JLabel("Custom date:");
        customDatePanel.add(customDateLabel);
        customDatePanel.add(customDateTextField);
        customDatePanel.add(confirmCustomDateButton);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(customDatePanel, BorderLayout.NORTH);
        bottomPanel.add(notificationLabel, BorderLayout.CENTER);
        bottomPanel.setPreferredSize(new Dimension(500, 57));

        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.SOUTH);
        add(calendarScrollPane, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(550, 270);
        setMinimumSize(new Dimension(300, 260));
        setVisible(true);

        setupCalendar();
    }

    private void setupCalendar(){

        String yearStr = yearTextField.getText();
        if(yearStr.isEmpty()){
            notificationLabel.setText("Error: Year input field is empty");
            return;
        }
        if(!notificationLabel.getText().isEmpty()) {
            notificationLabel.setText("");
        }

        int year = Integer.parseInt(yearStr);
        int month = monthComboBox.getSelectedIndex() + 1;
        System.out.println("Setting up table. Year = " + year + ", month = " + month);
        calendarTableModel.setRowCount(0);

        int counter = 1;
        int startDayOfWeek = HelperFunctions.getDayOfWeek(counter, month, year);
        int daysInMonth = HelperFunctions.getDaysInMonth(month, year);

        for(int i = 0; counter <= daysInMonth ; i++) {
            Object[] firstRow = new Object[7];
            for (; (startDayOfWeek + counter <= 7 * (i + 1)) && (counter <= daysInMonth); counter++) {
                firstRow[startDayOfWeek + counter - 1 - i * 7] = counter;
            }
            calendarTableModel.addRow(firstRow);
        }
    }

    private static class CustomCellRenderer extends DefaultTableCellRenderer {
        private static Color sundayColor = new Color(217, 152, 136);
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {

            JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            l.setOpaque(true);
            if(col == 6) {
                l.setBackground(sundayColor);
            } else
                l.setBackground(Color.WHITE);
            return l;

        }
    }

    public static void main(String[] args){
        new SimpleCalendar();
    }
}
