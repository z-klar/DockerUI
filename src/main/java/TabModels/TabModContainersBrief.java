package TabModels;

import Structures.TabRowDataContainersBrief;
import Structures.TabRowDataImages;

import javax.swing.table.AbstractTableModel;
import java.util.Vector;

public class TabModContainersBrief  extends AbstractTableModel {

    private String[] columnNames = { "Name", "Id", "Image", "Status", "IP Address" };
    private Object[][] data;

    public TabModContainersBrief(Vector<TabRowDataContainersBrief> rowData) {
        data = new Object[rowData.size()][columnNames.length];

        for (int i = 0; i < rowData.size(); i++) {
            data[i][0] = rowData.get(i).Name;
            data[i][1] = rowData.get(i).Id;
            data[i][2] = rowData.get(i).Image;
            data[i][3] = rowData.get(i).Status;
            data[i][4] = rowData.get(i).IPAddress;
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
