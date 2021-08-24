package org.appraise;

import java.net.URI;
import java.net.URISyntaxException;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.JBPopupListener;
import com.intellij.openapi.ui.popup.LightweightWindowEvent;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NotNull;

public class AddReviewCommentAction extends AnAction implements DumbAware {

    private static final Logger LOGGER = Logger.getInstance(AddReviewCommentAction.class);

    public AddReviewCommentAction() {
        super("Do Comment", "Do comment",
                IconLoader.getIcon("/images/comment_add.png"));
    }

    @Override
    public void actionPerformed(@NotNull final AnActionEvent e) {
        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        final int lineNumberOffsetZero = getCommentLine(editor);
        final Project project = e.getData(PlatformDataKeys.PROJECT);
        String filepath = getReviewedFilePath(e, project);

        LOGGER.info("Comment @ " + filepath + ":" + lineNumberOffsetZero);
        showReviewCommentBalloon(e, lineNumberOffsetZero, project, filepath);
    }

    private int getCommentLine(final Editor editor) {
        return editor.getCaretModel().getLogicalPosition().line + 1;
    }

    private String getReviewedFilePath(final @NotNull AnActionEvent e, final Project project) {
        String filepath = e.getData(PlatformDataKeys.VIRTUAL_FILE).getPath();
        try {
            filepath = "./"+new URI(project.getBasePath()).relativize(new URI(filepath)).getPath();
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        }
        return filepath;
    }

    private void showReviewCommentBalloon(final @NotNull AnActionEvent e, final int lineNumberOffsetZero, final Project project, final String filepath) {
        final ReviewCommentForm commentForm = new ReviewCommentForm(project, filepath, lineNumberOffsetZero,
                project.getBasePath());
        DataContext dataContext = e.getDataContext();

        final JBPopup balloon =
                JBPopupFactory.getInstance().createComponentPopupBuilder(commentForm, commentForm)
                        .setAdText("Hit Ctrl+Enter or Alt+Enter to save comment.")
                        .setTitle("Comment Title")
                        .setResizable(true)
                        .setMovable(true)
                        .setRequestFocus(true)
                        .setCancelOnWindowDeactivation(false)
                        .setDimensionServiceKey(null, ReviewDiffTool.DIMENSION_SERVICE_KEY, false)
                        .createPopup();
        commentForm.setMyBalloon(balloon);
        balloon.addListener(new JBPopupListener() {
            @Override
            public void onClosed(LightweightWindowEvent event) {
                if (!commentForm.getText().isEmpty()) { // do not try to save draft if text is empty
                    commentForm.postComment();
                } else {
                    LOGGER.error("comment was empty");
                }
            }
        });
        balloon.showInBestPositionFor(dataContext);
        commentForm.requestFocus();
    }
}
