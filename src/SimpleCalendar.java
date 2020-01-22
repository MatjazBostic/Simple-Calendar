import javafx.util.Pair;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleCalendar extends JFrame {

    private JComboBox<String> monthComboBox;
    private JTextField yearTextField;
    private JTextField customDateTextField;
    private JLabel notificationLabel;
    private DefaultTableModel calendarTableModel;

    public static void main(String[] args){
        new SimpleCalendar();
    }

    SimpleCalendar(){

        super("Simple calendar");

        String[] months = {"January", "February", "March", "April", "May", "June", "July",
                "August", "September", "October", "November", "December"};
        monthComboBox = new JComboBox<>(months);
        // On month selected listener
        monthComboBox.addActionListener (e -> setupCalendar());

        yearTextField = new JTextField("2020", 4);
        yearTextField.setFont(new Font("SansSerif", Font.BOLD, 17));
        yearTextField.setHorizontalAlignment(SwingConstants.RIGHT);

        // Allow only numbers to be entered into text field
        ((AbstractDocument) yearTextField.getDocument()).setDocumentFilter(new DocumentFilter() {
            Pattern regEx = Pattern.compile("\\d+");

            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length,
                                String text, AttributeSet attrs) throws BadLocationException {
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
                SwingUtilities.invokeLater(() -> yearTextField.selectAll());
            }
        });

        customDateTextField = new JTextField("1.1.2020", 6);
        customDateTextField.setFont(new Font("SansSerif", Font.BOLD, 17));
        customDateTextField.setHorizontalAlignment(SwingConstants.RIGHT);
        // Select all text on click
        customDateTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                SwingUtilities.invokeLater(() -> customDateTextField.selectAll());
            }
        });

        JButton confirmYearButton = new JButton("Confirm");
        confirmYearButton.setPreferredSize(new Dimension(80,25));

        confirmYearButton.addActionListener(actionEvent -> setupCalendar());

        JButton confirmCustomDateButton = new JButton("Confirm");
        confirmCustomDateButton.setPreferredSize(new Dimension(80,25));

        confirmCustomDateButton.addActionListener(actionEvent -> {

            String dateStr = customDateTextField.getText();

            Pair<int[], String> result = HelperFunctions.parseDate(dateStr);

            int[] results;

            if (result.getValue() != null) {
                notificationLabel.setText(result.getValue());
                return;
            } else {
                results = result.getKey();
            }

            if(!notificationLabel.getText().isEmpty()){
                notificationLabel.setText("");
            }
            yearTextField.setText(String.valueOf(results[2]));
            // Don't have to call setupCalendar(), because this selection calls it:
            monthComboBox.setSelectedIndex(results[1] - 1);
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

        readHolidays();

        setupCalendar();
    }

    private void setupCalendar(){

        String yearStr = yearTextField.getText();
        if(yearStr.isEmpty()){
            notificationLabel.setText("Error: Year input field is empty");
            return;
        }

        int year = Integer.parseInt(yearStr);
        if(year < 0){
            notificationLabel.setText("Error: Year can't be negative.");
            return;
        }

        if(!notificationLabel.getText().isEmpty()) {
            notificationLabel.setText("");
        }

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

    private class CustomCellRenderer extends DefaultTableCellRenderer {

        private Color sundayColor = new Color(217, 152, 136);
        private Color holidayColor = new Color(144, 238, 144);

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                       int row, int col) {

            JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            try {
                if (!l.getText().isEmpty() && !yearTextField.getText().isEmpty()) {
                    for (Holiday holiday : holidays) {
                        if ((holiday.repeating || holiday.year == Integer.parseInt(yearTextField.getText())) &&
                                holiday.month == monthComboBox.getSelectedIndex() + 1 &&
                                holiday.day == Integer.parseInt(l.getText())) {
                            l.setBackground(holidayColor);
                            return l;
                        }
                    }
                }
            } catch (NumberFormatException ex){
                ex.printStackTrace();
            }
            if(col == 6) {
                l.setBackground(sundayColor);
            } else
                l.setBackground(Color.WHITE);
            return l;
        }
    }

    private List<Holiday> holidays = new ArrayList<>();

    private static class Holiday{

        int day;
        int month;
        int year;
        boolean repeating;

        Holiday(int day, int month, int year, boolean repeating){
            this.day = day;
            this.month = month;
            this.year = year;
            this.repeating = repeating;
        }
    }

    private void readHolidays(){
        Path filePath = Paths.get("./holidays.txt");
        String content;
        try {
            content = Files.readString(filePath, StandardCharsets.US_ASCII);
        } catch (IOException e) {
            e.printStackTrace();
            notificationLabel.setText("Error: File 'holidays.txt' not found.");
            return;
        }

        String[] dateStrs = content.split(",");
        for(String dateStr : dateStrs){
            String[] dateAndRepeating = dateStr.split(":");
            boolean repeating;
            try {
                if (Integer.parseInt(dateAndRepeating[1]) == 1) {
                    repeating = true;
                } else if (Integer.parseInt(dateAndRepeating[1]) == 0) {
                    repeating = false;
                } else {
                    notificationLabel.setText("Error: Incorrect repeating format in 'holidays.txt'.");
                    return;
                }
            } catch (NumberFormatException ex){
                ex.printStackTrace();
                notificationLabel.setText("Error: Incorrect repeating format in 'holidays.txt'.");
                return;
            }

            Pair<int[], String> result = HelperFunctions.parseDate(dateAndRepeating[0]);
            int[] results;

            if (result.getValue() != null) {
                notificationLabel.setText(result.getValue());
                return;
            } else {
                results = result.getKey();
            }

            if(!notificationLabel.getText().isEmpty()){
                notificationLabel.setText("");
            }

            holidays.add(new Holiday(results[0], results[1], results[2], repeating));
        }
    }

}
