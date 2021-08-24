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
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

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
import org.jetbrains.annotations.Nullable;

public class ReviewCommentForm extends JPanel {

    private static final Logger LOGGER = Logger.getInstance(ReviewCommentForm.class);
    private static final int ourBalloonWidth = 350;
    private static final int ourBalloonHeight = 200;

    private final EditorTextField myReviewTextField;

    private JBPopup myBalloon;

    private Editor myEditor;
    @NotNull
    private final Project myProject;
    private final String _commentFilePath;
    private final int _commentLineNumberOffsetZero;
    private final String _workspacePath;


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
                postComment();
            }
        });
        myReviewTextField.setEnabled(true);
    }

    @Nullable
    public void postComment() {
        try {
            final String[] cmd = {
                    "git", "appraise", "comment",
                    "-m", myReviewTextField.getText(),
                    "-f", _commentFilePath,
                    "-l", "" + _commentLineNumberOffsetZero};
            LOGGER.info("cmd: " + Arrays.toString(cmd) + " in " + _workspacePath);
            final Process exec = Runtime.getRuntime().exec(cmd, null, new File(_workspacePath));
            exec.waitFor();
            LOGGER.info("appraise output" + new BufferedReader(
                    new InputStreamReader(exec.getInputStream())).lines().collect(
                    Collectors.joining()));
        } catch (IOException | InterruptedException e) {
            LOGGER.error(e);
        }
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
