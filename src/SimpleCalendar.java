import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleCalendar extends JFrame {

    JComboBox<String> monthComboBox;
    JTextField yearTextField;
    JLabel notificationLabel;
    JTable calendarTable;

    SimpleCalendar(){

        super("Simple calendar");

        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        monthComboBox = new JComboBox<>(months);

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

        JButton confirmYearButton = new JButton("Confirm");
        confirmYearButton.setPreferredSize(new Dimension(80,25));

        confirmYearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                setupCalendar();
            }
        });

        // Setup bottom notification label
        notificationLabel = new JLabel("", JLabel.CENTER);
        notificationLabel.setVerticalAlignment(JLabel.BOTTOM);
        notificationLabel.setForeground(Color.RED);
        EmptyBorder border = new EmptyBorder(5, 15, 5, 15);
        notificationLabel.setBorder(border);

        // Setup calendar table

        Object[] columnNames = new Object[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        DefaultTableModel calendarTableModel = new DefaultTableModel(columnNames, 0);
        calendarTable = new JTable(calendarTableModel);
        calendarTableModel.addRow(new Object[]{1, 2, 3, 4, 5, 6, 7});


        JScrollPane calendarScrollPane = new JScrollPane(calendarTable);
        calendarScrollPane.setBorder(border);

        JPanel topPanel = new JPanel();
        topPanel.add(monthComboBox);
        topPanel.add(yearTextField);
        topPanel.add(confirmYearButton);
        topPanel.setBorder(border);


        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(notificationLabel, BorderLayout.SOUTH);

        add(calendarScrollPane, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(550, 400);
        setVisible(true);

    }

    private boolean yearStringEmpty;

    private void setupCalendar(){

        String yearStr = yearTextField.getText();
        if(yearStr.isEmpty()){
            notificationLabel.setText("Error: Year input field is empty");
            yearStringEmpty = true;
            return;
        } else if(yearStringEmpty) {
            yearStringEmpty = false;
            notificationLabel.setText("");
        }

        int year = Integer.parseInt(yearStr);
        int month = monthComboBox.getSelectedIndex() + 1;
        System.out.println("Setting up table. Year = " + year + ", month = " + month);

    }

    public static void main(String[] args){
        new SimpleCalendar();
    }

}
