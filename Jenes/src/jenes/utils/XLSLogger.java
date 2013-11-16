/*
 * JENES
 * A time and memory efficient Java library for genetic algorithms and more 
 * Copyright (C) 2011 Intelligentia srl
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 */
package jenes.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import jxl.Range;
import jxl.read.biff.BiffException;
import jxl.write.Number;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jenes.chromosome.Chromosome;
import jenes.population.Individual;
import jenes.population.Population.Statistics.Group;
import jxl.Cell;
import jxl.Workbook;
import jxl.write.DateTime;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableWorkbook;
import jxl.write.WritableSheet;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 * This class implements a logger based on Excel's XLS files.
 * 
 * Data are stored in the first column having the data label as header.
 * It does not matter in which sheet. Recording can start from any row.
 * From which row to start recording can be decided at istantiation or execution time.
 * 
 * The class can work with an empty file or with a template provided at instantiation time.
 * Using a template is a helpful with macros and plots, in order to make ready-to-use reports.
 *
 * Requires the libray JExcelApi.
 *
 * @version 2.0
 * @since 1.3
 */
public class XLSLogger extends AbstractLogger {

    private static String SCORES_LABEL = "Scores";
    private static String CHROMOSOME_LABEL = "Chromosome";
    private String filename;
    private WritableWorkbook workbook;
    private int line;
    private Map<String, WritableSheet> sheets = new HashMap<String, WritableSheet>();
    private Map<WritableSheet, Map<String, Integer>> colsOfSheet = new HashMap<WritableSheet, Map<String, Integer>>();
    private List<WritableCell> newCols = new ArrayList<WritableCell>();
    private Map<String, Boolean> alterableSchema = new HashMap<String, Boolean>();

    /**
     * Istantiates a new logger with a given schema.
     * Data are saved by defualt in the workbook jenes.log.xls.
     * Recording starts from row 1.
     *
     * @param schema - the field labels
     * @throws IOException
     */
    public XLSLogger(String[] schema) throws IOException {
        this(schema, "jenes.log.xls", 1);
    }

    /**
     * Istantiates a new logger with a given schema.
     * Recording starts from row 1.
     *
     * @param schema - the field labels
     * @param filename - the workbook filename
     * @throws IOException
     */
    public XLSLogger(String[] schema, String filename) throws IOException {
        this(schema, filename, 1);
    }

    /**
     * Istantiates a new logger with a given schema.
     *
     * @param schema - the field labels
     * @param filename - the workbook filename
     * @param from - the recording initial row
     * @throws IOException
     */
    public XLSLogger(String[] schema, String filename, int from) throws IOException {
        super(schema);

        this.filename = filename;

        File f = new File(filename);
        File ftmp = new File(filename + ".tmp");
        if (f.exists()) {
            try {
                workbook = Workbook.createWorkbook(ftmp, Workbook.getWorkbook(f));
                map(schema);
                line = from;
                return;
            } catch (BiffException ex) {
                Logger.getLogger(XLSLogger.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        workbook = Workbook.createWorkbook(ftmp);
        create(schema);
        line = 1;
    }

    /**
     * Istantiates a new logger with a given schema.
     * Recording starts from row 1.
     *
     * @param schema - the field labels
     * @param filename - the workbook name
     * @param template - the workbook template
     * @throws IOException
     */
    public XLSLogger(String[] schema, String filename, String template) throws IOException {
        super(schema);
        this.filename = filename;
        File ftmp = new File(filename + ".tmp");
        try {
            workbook = Workbook.createWorkbook(ftmp, Workbook.getWorkbook(new File(template)));
            map(schema);
            line = 1;
            return;
        } catch (BiffException ex) {
            Logger.getLogger(XLSLogger.class.getName()).log(Level.SEVERE, null, ex);
        }
        workbook = Workbook.createWorkbook(ftmp);
        create(schema);
        line = 1;
    }

    private void map(String[] schema) {
        for (String key : schema) {
            for (WritableSheet sh : workbook.getSheets()) {
                Cell cell = sh.findCell(key, 0, 0, sh.getColumns() - 1, 0, false);
                if (cell != null) {
                    int col = cell.getColumn();
                    this.sheets.put(key, sh);
                    this.putInColumns(sh, key, col);
                    this.alterableSchema.put(key, true);
                    break;
                } else if (key.equalsIgnoreCase(sh.getName())) {
                    /*if the cell does not exist, it is checked if key matches the name of the sheet */
                    this.sheets.put(key, sh);
                    //create new columns
                    HashMap<String, Integer> cols = new HashMap<String, Integer>();
                    cols.put(key, 0);
                    this.colsOfSheet.put(sh, cols);
                    this.alterableSchema.put(key, true);
                }
            }
        }
    }

    private void create(String[] schema) {
        WritableSheet sh = workbook.createSheet("Statistics", 0);
        int col = 0;
        for (String key : schema) {
            try {
                this.sheets.put(key, sh);
                this.putInColumns(sh, key, col);
                this.alterableSchema.put(key, true);
                WritableCell head = new Label(col, 0, key);
                sh.addCell(head);
                col++;
            } catch (WriteException ex) {
                Logger.getLogger(XLSLogger.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Provides the current recording row
     * 
     * @return - recording row
     */
    public int getLine() {
        return this.line;
    }

    /**
     * Sets the current recording row at a specified value.
     *
     * @param line - the new recording row
     */
    public void setLine(int line) {
        this.line = line;
    }

    /**
     * Rovides the workbook used for writing the statistics.
     * See JExcelApi documentation for futher information.
     *
     * @return the workbook
     */
    public WritableWorkbook getWorkbook() {
        //return this.getWorkbook();//XXX e' corretto?
        return this.workbook;
    }

    @Override
    protected void store() {
        for (String key : record.keySet()) {

            WritableSheet sh = sheets.get(key);
            if (sh == null) {
                continue;
            }

            Map<String, Integer> cols = this.colsOfSheet.get(sh);
            Integer c = cols.get(key);

            Object value = record.get(key);

            this.newCols.clear();
            try {
                //new cols will be re-initialized in fillCell
                WritableCell cell = this.fillCell(value, key, line, c, sh, false);
                WritableSheet sheet = this.sheets.get(key);

                if (cell != null) {
                    //... the cell is for a single value
                    sheet.addCell(cell);
                } else if (!this.newCols.isEmpty()) {
                    //... we have to store multiple values enlarging the schema
                    for (WritableCell wc : this.newCols) {
                        sheet.addCell(wc);
                    }
                }

            } catch (RowsExceededException ex) {
                Logger.getLogger(XLSLogger.class.getName()).log(Level.SEVERE, null, ex);
            } catch (WriteException ex) {
                Logger.getLogger(XLSLogger.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //go to next line
        line++;
    }

    @Override
    protected void doSave() {
        try {
            this.workbook.write();
        } catch (IOException ex) {
            Logger.getLogger(XLSLogger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void doClose() {
        try {

            this.workbook.write();
            this.workbook.close();

            File f = new File(filename);
            f.delete();

            File ft = new File(filename + ".tmp");
            ft.renameTo(f);

        } catch (IOException ex) {
            Logger.getLogger(XLSLogger.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        } catch (WriteException ex) {
            Logger.getLogger(XLSLogger.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    /**
     * Valorize a cell in the sheet
     * @param value
     * @param key column name
     * @param currentLine
     * @param c
     * @param sh
     * @param valueAsArray in the case we are storing a multi-level array 
     * @return
     * @throws WriteException 
     */
    private WritableCell fillCell(Object value, String key, int currentLine, int c, WritableSheet sh, boolean valueAsArray) throws WriteException {
        WritableCell cell = null;

        if (value instanceof java.lang.Double) {
            double d = (Double) value;
            cell = new Number(c, currentLine, d);
        } else if (value instanceof java.lang.Float) {
            float f = (Float) value;
            cell = new Number(c, currentLine, f);
        } else if (value instanceof java.lang.Integer) {
            int i = (Integer) value;
            cell = new Number(c, currentLine, i);
        } else if (value instanceof java.lang.Long) {
            long l = (Long) value;
            cell = new Number(c, currentLine, l);
        } else if (value instanceof java.util.Date) {
            Date d = (Date) value;
            cell = new DateTime(c, currentLine, d);
        } else if (value.getClass().isArray()) {
            //if we are storing an array
            Object[] arr = this.castArray(value);
            //alterableSchema tells if a column could be altered (true only the first time)
            if (this.alterableSchema.get(key) && arr.length > 1) {
                this.alterSchema(sh, c + 1, arr.length);
                this.alterableSchema.put(key, false); //reset alterable flag
            }

            if (valueAsArray) {
                //second or greater level of nesting in array
                cell = new Label(c, currentLine, value.toString());
            } else {
                //first level of nesting for array
                for (Object obj : arr) {
                    //recursive call -> we store a single cell of the array (the alter is already performed)
                    this.newCols.add(this.fillCell(obj, key, currentLine, c, sh, true));
                    //...change column...
                    c++;
                }
            }
        } else if (value instanceof Chromosome) {
            Chromosome chr = (Chromosome) value;
            for (Object obj : chr.toArray()) { //XXX check in the case of bit array
                //recursive call -> store each allele
                this.newCols.add(this.fillCell(obj, key, currentLine, c, sh, true));
                //change column
                c++;
            }

        } else if (value instanceof Individual) {
            //in the case of Individual we alter the Sheet
            Individual ind = (Individual) value;

            if (this.alterableSchema.get(key)) {
                this.alterSchema(sh, c + 1, ind.getChromosomeLength() + ind.getNumOfObjectives());
                this.alterableSchema.put(key, false);
            }

            for (Object obj : ind.getChromosome().toArray()) { //XXX check in the case of bit array
                //recursive call -> store each allele
                this.newCols.add(this.fillCell(obj, key, currentLine, c, sh, true));
                //change column
                c++;
            }

            for (Object obj : ind.getAllScores()) {
                //recursive call -> store scores
                this.newCols.add(this.fillCell(obj, key, currentLine, c, sh, true));
                //change column
                c++;
            }


        } else if (value instanceof Group) {

            //in the case of Group we create a new dedicated Sheet
            Group<Chromosome> grp = (Group<Chromosome>) value;
            int groupSaparator = 1;
            if (this.alterableSchema.get(key)) {
                Individual<Chromosome> individual = grp.get(0);
                if (individual != null) {
                    WritableCell[] cells = new WritableCell[2];

                    cells[0] = new Label(0, 0, CHROMOSOME_LABEL);
                    cells[1] = new Label(1, 0, SCORES_LABEL);

                    int len = individual.getChromosomeLength();

                    WritableSheet otherSheet = this.editOtherSheet(key, individual.getChromosomeLength(), cells);
                    otherSheet.mergeCells(len, 0, len + individual.getAllScores().length - 1, 0);
                    groupSaparator = 0;
                    //remove column from first sheet
                    if (!otherSheet.equals(sh)) {
                        //adjust schema in main sheet removing the column indicating group name
                        this.removeColumnFromFirstSheet(key, sh);
                    }
                }
                this.alterableSchema.put(key, false); //reset flag
            }

            //access new sheet just created
            WritableSheet ws = this.sheets.get(key);
            int curLine = ws.getRows() + groupSaparator;
            for (Object ind : grp) {
                //... and fill it appending rows
                this.fillCell((Individual) ind, key, curLine, 0, ws, true);

                curLine++;
            }
        } else {
            //... unknown type to store in logger
            String s = value.toString();
            cell = new Label(c, currentLine, s);
        }
        return cell;
    }

    /**
     * Alter schema
     * @param sh the sheet to change
     * @param startIndex index where to start in adding columns
     * @param length 
     */
    private void alterSchema(WritableSheet sh, int startIndex, int length) {
        Range[] mergedCells = sh.getMergedCells();

        int cellMerged = 0;
        for (Range range : mergedCells) {
            //... first cell at left
            Cell topLeft = range.getTopLeft();

            //... check merged only if we are in the first row
            if (topLeft.getRow() == 0 && topLeft.getColumn() == startIndex - 1) {
                cellMerged = range.getBottomRight().getColumn() - topLeft.getColumn();
                length = length - cellMerged;

                break;
            }
        }

        if (length > 1) { //only in the case of aumented dimension
            for (int c = 1; c < length; c++) {
                sh.insertColumn(startIndex);
            }

            try {
                sh.mergeCells(startIndex - 1, 0, startIndex - 1 + length - 1, 0);
            } catch (WriteException ex) {
                Logger.getLogger(XLSLogger.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        //... update references in sheets
        Map<String, Integer> cols = this.colsOfSheet.get(sh);
        Map<String, Integer> temp = new HashMap<String, Integer>(cols);
        for (String key : temp.keySet()) {
            int i = temp.get(key);
            if (i >= startIndex) {
                cols.put(key, i + length - 1);
            }
        }
    }

    /**
     * Called at the beginning to collect information on the schema
     * @param sh
     * @param key
     * @param col 
     */
    private void putInColumns(WritableSheet sh, String key, int col) {
        Map<String, Integer> cols = this.colsOfSheet.get(sh);
        if (cols == null) {
            cols = new HashMap<String, Integer>();
            cols.put(key, col);
            this.colsOfSheet.put(sh, cols);
        } else {
            cols.put(key, col);
        }
    }

    /**
     * Create a new sheet to log a group
     * @param key
     * @param columns
     * @return
     * @throws WriteException 
     */
    private WritableSheet editOtherSheet(String key, int columns, WritableCell... cells) throws WriteException {
        //create a new sheet
        WritableSheet sheet = this.workbook.getSheet(key);
        if (sheet == null) {
            sheet = this.workbook.createSheet(key, this.workbook.getNumberOfSheets() + 1);
            for (WritableCell cell : cells) {
                sheet.addCell(cell);
            }
            this.sheets.put(key, sheet);
            //create new columns
            HashMap<String, Integer> cols = new HashMap<String, Integer>();
            cols.put(key, 0);
            this.colsOfSheet.put(sheet, cols);
        }
        //edit new sheet schema
        this.alterSchema(sheet, 1, columns);
        return sheet;
    }

    /**
     * In case of group storing, remove column with the given label... a sheet will be used to log this kind of informations
     * @param key
     * @param sh 
     */
    private void removeColumnFromFirstSheet(String key, WritableSheet sh) {
        Map<String, Integer> cols = this.colsOfSheet.get(sh);

        int index = cols.get(key);
        cols.remove(key);
        Map<String, Integer> temp = new HashMap<String, Integer>(cols);

        //update column index and reference before remove from the sheet
        for (String colName : temp.keySet()) {
            int colNumber = temp.get(colName);
            if (colNumber > index) {
                cols.put(colName, --colNumber);
            }
        }
        sh.removeColumn(index);
    }

    /**
     * 
     * @param value
     * @return 
     */
    private Object[] castArray(Object value) {
        if (value instanceof double[]) {
            double[] arr = (double[]) value;
            Object[] obj = new Object[arr.length];
            for (int i = 0; i < arr.length; i++) {
                obj[i] = arr[i];
            }
            return obj;
        } else if (value instanceof int[]) {
            int[] arr = (int[]) value;
            Object[] obj = new Object[arr.length];
            for (int i = 0; i < arr.length; i++) {
                obj[i] = arr[i];
            }
            return obj;
        } else if (value instanceof float[]) {
            float[] arr = (float[]) value;
            Object[] obj = new Object[arr.length];
            for (int i = 0; i < arr.length; i++) {
                obj[i] = arr[i];
            }
            return obj;
        } else if (value instanceof long[]) {
            long[] arr = (long[]) value;
            Object[] obj = new Object[arr.length];
            for (int i = 0; i < arr.length; i++) {
                obj[i] = arr[i];
            }
            return obj;
        } else {
            Object[] arr = (Object[]) value;
            return arr;
        }
    }
}
