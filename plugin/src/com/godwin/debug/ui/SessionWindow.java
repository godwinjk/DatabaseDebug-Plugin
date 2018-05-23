package com.godwin.debug.ui;

import com.godwin.debug.common.Logger;
import com.godwin.debug.model.DDatabase;
import com.godwin.debug.model.DTable;
import com.godwin.debug.model.IBaseModel;
import com.godwin.debug.network.ClientSocket;
import com.godwin.debug.network.communication.DataCommunicationListener;
import com.godwin.debug.network.communication.DataObserver;
import com.godwin.debug.ui.autocomplete.QuerySuggestionService;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.ui.TextFieldWithAutoCompletion;
import com.intellij.ui.table.JBTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

/**
 * Created by Godwin on 5/7/2018 12:26 PM for plugin.
 *
 * @author : Godwin Joseph Kurinjikattu
 */
public class SessionWindow {
    private JFrame mFrame;
    private JPanel container;
    private JTree tableTree;
    private JTable tableDetails;
    private JSplitPane jSplitPane;
    private JScrollPane jScrollTree;

    private JScrollPane jScrollTable;
    private JPanel jRightContainer;
    private Editor mInputEditor;
    private TextFieldWithAutoCompletion mTextFieldWithAutoCompletion;

    private Project mProject;
    private Disposable parent;

    private ClientSocket socket;
    private IBaseModel mBaseModel;

    private DDatabase mDatabase;
    private DTable mTable;

    private DataCommunicationListener listener = new DataCommunicationListener() {
        @Override
        public void onGetDbData(List<DDatabase> databaseList) {
            DefaultMutableTreeNode root = new DefaultMutableTreeNode(socket.getApplication());

            for (int i = 0; i < databaseList.size(); i++) {
                DDatabase database = databaseList.get(i);
                DefaultMutableTreeNode dNode = new DefaultMutableTreeNode(database);
                if (database.getTables() != null && database.getTables().size() > 0) {
                    for (int j = 0; j < database.getTables().size(); j++) {
                        DTable table = database.getTables().get(j);
                        DefaultMutableTreeNode tNode = new DefaultMutableTreeNode(table);
                        dNode.add(tNode);
                    }
                }
                root.add(dNode);
            }

            tableTree.setModel(new DefaultTreeModel(root));
        }

        @Override
        public void onGetTableDetails(List<List<String>> table, List<String> header) {
            try {
                String[][] array = new String[table.size()][];
                for (int i = 0; i < table.size(); i++) {
                    List<String> row = table.get(i);
                    try {
                        array[i] = row.toArray(new String[row.size()]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                DefaultTableModel model = new DefaultTableModel(array, header.toArray());

                tableDetails.setModel(model);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onGetQueryResult(List<List<String>> table, List<String> header) {
            QuerySnapshotWindow window = new QuerySnapshotWindow(table, header);
        }

        @Override
        public void onGetQueryFail(int errorCode, String errorMessage) {
            ErrorAlertDialog dialog = new ErrorAlertDialog(errorMessage);
        }

        @Override
        public void onCloseClient(ClientSocket socket) {

        }
    };

    public SessionWindow(Project mProject, Disposable parent, ClientSocket socket) {
        super();
        this.mProject = mProject;
        this.parent = parent;
        this.socket = socket;

        mFrame = new JFrame();

        mFrame.setSize(new Dimension(1024, 500));
        mFrame.setResizable(true);
        mFrame.setTitle("Debug window");
        mFrame.setLayout(new BorderLayout());
        mFrame.add(container, BorderLayout.CENTER);
        mFrame.pack();
        mFrame.setVisible(true);

        jSplitPane.setResizeWeight(.4f);

        populateDatabase();

        tableTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tableTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                    tableTree.getLastSelectedPathComponent();

            /* if nothing is selected */
            if (node == null) return;

            mBaseModel = (IBaseModel) node.getUserObject();
            if (mBaseModel instanceof DTable) {
                showTable();
                mTable = (DTable) mBaseModel;
                populateTable(mTable);
            } else if (mBaseModel instanceof DDatabase) {
                mDatabase = (DDatabase) mBaseModel;
                showEditorPane();
            }
        });
    }

    private Editor createEditor() {
        PsiFile myFile = null;
        EditorFactory editorFactory = EditorFactory.getInstance();
        Document doc = myFile == null
                ? editorFactory.createDocument("")
                : PsiDocumentManager.getInstance(mProject).getDocument(myFile);
        Editor editor = editorFactory.createEditor(doc, mProject);
        EditorSettings editorSettings = editor.getSettings();
        editorSettings.setVirtualSpace(false);
        editorSettings.setLineMarkerAreaShown(false);
        editorSettings.setIndentGuidesShown(false);
        editorSettings.setFoldingOutlineShown(true);
        editorSettings.setAdditionalColumnsCount(3);
        editorSettings.setAdditionalLinesCount(3);
        editorSettings.setLineNumbersShown(true);
        editorSettings.setCaretRowShown(true);

        editor.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void beforeDocumentChange(DocumentEvent event) {

            }

            @Override
            public void documentChanged(DocumentEvent event) {

            }
        });

//        ((EditorEx) editor).setHighlighter(createHighlighter(FileTypes.PLAIN_TEXT));
        return editor;
    }

    private TextFieldWithAutoCompletion getTextFieldWithAutoCompletion() {

        TextFieldWithAutoCompletion<String> completion = TextFieldWithAutoCompletion.create(mProject, QuerySuggestionService.getDbTokensAsList(mDatabase), true, "");
        completion.setVisible(true);
        TextFieldWithAutoCompletion.StringsCompletionProvider provider = new TextFieldWithAutoCompletion.StringsCompletionProvider(QuerySuggestionService.getDbTokensAsList(mDatabase), null);

        completion.installProvider(provider);

        completion.addDocumentListener(new DocumentListener() {
            @Override
            public void beforeDocumentChange(DocumentEvent event) {
//                String text = event.getDocument().getText();
//                if (text.endsWith(">")) {
//                    event.getDocument().setText(text);
//                }
            }

            @Override
            public void documentChanged(DocumentEvent event) {
                String text = event.getDocument().getText();
                text = text.replaceAll(mDatabase.getName() + "> ", "");
                if (text.endsWith(";")) {
                    executeQuery(mDatabase, text);
//                    event.getDocument().setText(mDatabase.getName() + "> ");
                }
            }
        });
        completion.setText(mDatabase.getName() + "> ");
        return completion;
    }
class MyDocumentListener implements DocumentListener {

}
    private void populateDatabase() {
        socket.requestDbDetails();
        DataObserver.getInstance().subscribe(listener);
    }

    private void showEditorPane() {
        /*if (null == mInputEditor) {
            mInputEditor = createEditor();
            mInputEditor.getComponent().setVisible(true);
        } */
        if (null == mTextFieldWithAutoCompletion) {
            mTextFieldWithAutoCompletion = getTextFieldWithAutoCompletion();
        }
        jRightContainer.removeAll();

        jRightContainer.add(mTextFieldWithAutoCompletion, BorderLayout.CENTER);
        jRightContainer.repaint();
        jRightContainer.revalidate();
    }

    private void showTable() {
        if (null == tableDetails) {
            DefaultTableModel model = new DefaultTableModel();
            tableDetails = new JBTable(model);
            tableDetails.setVisible(true);
        }


        jRightContainer.removeAll();

        jRightContainer.add(new JScrollPane(tableDetails), BorderLayout.CENTER);
        jRightContainer.repaint();
        jRightContainer.revalidate();
    }

    public void addWindowListener(WindowAdapter adapter) {
        mFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                DataObserver.getInstance().unSubcribe(listener);
                super.windowClosing(e);
                adapter.windowClosing(e);
            }
        });
    }

    private void populateTable(DTable table) {
        socket.requestTableDetails(table);
        DataObserver.getInstance().subscribe(listener);
    }

    private void executeQuery(DDatabase database, String query) {
        socket.executeQuery(database, query);
    }

    public void close() {
        mFrame.dispatchEvent(new WindowEvent(mFrame, WindowEvent.WINDOW_CLOSING));
    }
}
