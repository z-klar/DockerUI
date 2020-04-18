package TabModels;

import Structures.TabRowDataContainersBrief;
import Structures.TabRowDataRegistryRepo;

import javax.swing.table.AbstractTableModel;
import java.util.Vector;

public class TabModRegistryRepos  extends AbstractTableModel {

    private String[] columnNames = { "Repo", "Image" };
    private Object[][] data;

    public TabModRegistryRepos(Vector<TabRowDataRegistryRepo> rowData) {
        data = new Object[rowData.size()][columnNames.length];

        for (int i = 0; i < rowData.size(); i++) {
            data[i][0] = rowData.get(i).Repo;
            data[i][1] = rowData.get(i).Image;
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
