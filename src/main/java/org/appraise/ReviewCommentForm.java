package org.appraise;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Set;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SpellCheckingEditorCustomizationProvider;
import com.intellij.openapi.fileTypes.PlainTextLanguage;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.ui.EditorCustomization;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.EditorTextFieldProvider;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.SoftWrapsEditorCustomization;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

public class ReviewCommentForm extends JPanel {

    private static final Logger LOGGER = Logger.getInstance(ReviewCommentForm.class);
    private static final int ourBalloonWidth = 350;
    private static final int ourBalloonHeight = 200;

    public final EditorTextField myReviewTextField;

    public JBPopup myBalloon;

    private Editor myEditor;
    @NotNull
    public final Project myProject;
    public final String _commentFilePath;
    public final int _commentLineNumberOffsetZero;
    public final String _workspacePath;


    public ReviewCommentForm(@NotNull Project project, final String commentFilePath,
                             final int commentLineNumberOffsetZero, final String workspacePath) {
        super(new BorderLayout());
        myProject = project;
        _commentFilePath = commentFilePath;
        _commentLineNumberOffsetZero = commentLineNumberOffsetZero;
        _workspacePath = workspacePath;


        final EditorTextFieldProvider service = ServiceManager.getService(project, EditorTextFieldProvider.class);
        final Set<EditorCustomization> editorFeatures =
                ContainerUtil.newHashSet(SoftWrapsEditorCustomization.ENABLED,
                        SpellCheckingEditorCustomizationProvider.getInstance().getEnabledCustomization());
        myReviewTextField = service.getEditorField(PlainTextLanguage.INSTANCE, project, editorFeatures);

        final JScrollPane pane = ScrollPaneFactory.createScrollPane(myReviewTextField);
        pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(pane);

        myReviewTextField.setPreferredSize(new Dimension(ourBalloonWidth, ourBalloonHeight));

        myReviewTextField.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).
                put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK), "postComment");
        myReviewTextField.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).
                put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.ALT_DOWN_MASK), "postComment");
        //todo mmake configurable

        myReviewTextField.getActionMap().put("postComment", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myBalloon.dispose();
                GitAppraiseUtil.postComment(_commentFilePath, _commentLineNumberOffsetZero, _workspacePath, myReviewTextField);
            }
        });
        myReviewTextField.setEnabled(true);
    }

    @Override
    public void requestFocus() {
        IdeFocusManager.findInstanceByComponent(myReviewTextField).requestFocus(myReviewTextField, true);
    }

    @NotNull
    public String getText() {
        return myReviewTextField.getText();
    }

    public void setMyBalloon(final JBPopup myBalloon) {
        this.myBalloon = myBalloon;
    }
}
