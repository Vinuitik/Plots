package plot;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.bson.Document;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SelfDevelopmentTracker extends JFrame {

    private MongoDBConnection dbConnection;
    private XYSeries obsidianSeries;
    private XYSeries leetcodeSeries;
    
    public SelfDevelopmentTracker() {
        dbConnection = new MongoDBConnection();
        
        obsidianSeries = new XYSeries("Obsidian Notes");
        leetcodeSeries = new XYSeries("Leetcode Rank");

        // Fetch data from the database and populate series
        loadDataFromDatabase();
        
        // UI Setup
        setTitle("Self Development Tracker");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(1, 2));  // Use GridLayout to place charts side by side

        // Create datasets
        XYSeriesCollection obsidianDataset = new XYSeriesCollection();
        obsidianDataset.addSeries(obsidianSeries);

        XYSeriesCollection leetcodeDataset = new XYSeriesCollection();
        leetcodeDataset.addSeries(leetcodeSeries);

        // Create charts
        JFreeChart obsidianChart = ChartFactory.createXYLineChart(
                "Obsidian Progress",
                "Date",
                "Notes Count",
                obsidianDataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        JFreeChart leetcodeChart = ChartFactory.createXYLineChart(
                "Leetcode Progress",
                "Date",
                "Rank",
                leetcodeDataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        // Configure date axis for both charts
        DateAxis dateAxis1 = new DateAxis("Date");
        dateAxis1.setDateFormatOverride(new SimpleDateFormat("yyyy-MM-dd"));
        XYPlot plot1 = obsidianChart.getXYPlot();
        plot1.setDomainAxis(dateAxis1);

        DateAxis dateAxis2 = new DateAxis("Date");
        dateAxis2.setDateFormatOverride(new SimpleDateFormat("yyyy-MM-dd"));
        XYPlot plot2 = leetcodeChart.getXYPlot();
        plot2.setDomainAxis(dateAxis2);

        // Create chart panels and add them to the main frame
        ChartPanel obsidianChartPanel = new ChartPanel(obsidianChart);
        ChartPanel leetcodeChartPanel = new ChartPanel(leetcodeChart);
        add(obsidianChartPanel);
        add(leetcodeChartPanel);
        
        // Create input panel for buttons
        JPanel inputPanel = new JPanel();
        JTextField valueFieldObsidian = new JTextField(5);  // Value for Obsidian Notes
        JTextField valueFieldLeetcode = new JTextField(5);  // Value for Leetcode Rank
        JButton addButtonObsidian = new JButton("Add Obsidian Data");
        JButton addButtonLeetcode = new JButton("Add Leetcode Data");

        inputPanel.add(new JLabel("Obsidian Value:"));
        inputPanel.add(valueFieldObsidian);
        inputPanel.add(addButtonObsidian);

        inputPanel.add(new JLabel("Leetcode Value:"));
        inputPanel.add(valueFieldLeetcode);
        inputPanel.add(addButtonLeetcode);

        add(inputPanel, BorderLayout.SOUTH);
        
        // Obsidian Button Action Listener
        addButtonObsidian.addActionListener(e -> {
            int value = Integer.parseInt(valueFieldObsidian.getText());
            Date currentDate = new Date();  // Use current date

            // Save to MongoDB and update graph for Obsidian Notes
            dbConnection.insertData("obsidian_notes", currentDate, value);
            obsidianSeries.add(currentDate.getTime(), value);
        });

        // Leetcode Button Action Listener
        addButtonLeetcode.addActionListener(e -> {
            int value = Integer.parseInt(valueFieldLeetcode.getText());
            Date currentDate = new Date();  // Use current date

            // Save to MongoDB and update graph for Leetcode Rank
            dbConnection.insertData("leetcode_rank", currentDate, value);
            leetcodeSeries.add(currentDate.getTime(), value);
        });
    }

    private void loadDataFromDatabase() {
        // Load Obsidian Notes data
        List<Document> obsidianData = dbConnection.getData("obsidian_notes");
        for (Document doc : obsidianData) {
            Date date = doc.getDate("date");
            int value = doc.getInteger("value");
            obsidianSeries.add(date.getTime(), value);
        }

        // Load Leetcode Rank data
        List<Document> leetcodeData = dbConnection.getData("leetcode_rank");
        for (Document doc : leetcodeData) {
            Date date = doc.getDate("date");
            int value = doc.getInteger("value");
            leetcodeSeries.add(date.getTime(), value);
        }
    }
}
