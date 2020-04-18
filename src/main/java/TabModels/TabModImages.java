package TabModels;

import Structures.TabRowDataImages;

import javax.swing.table.AbstractTableModel;
import java.util.Vector;

public class TabModImages  extends AbstractTableModel {


    private String[] columnNames = { "Id", "Name", "Size", "Created" };
    private Object[][] data;

    public TabModImages(Vector<TabRowDataImages> rowData) {
        data = new Object[rowData.size()][columnNames.length];

        for (int i = 0; i < rowData.size(); i++) {
            data[i][0] = rowData.get(i).Id;
            data[i][1] = rowData.get(i).Name;
            data[i][2] = rowData.get(i).Size;
            data[i][3] = rowData.get(i).CreationDate;
        }
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return data.length;
    }
    public String getColumnName(int col) {
        return columnNames[col];
    }
    public Object getValueAt(int row, int col) {
        return data[row][col];
    }
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

}
