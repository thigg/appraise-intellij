package org.appraise;

import java.net.URI;
import java.net.URISyntaxException;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
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
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

public class AddReviewCommentAction extends AnAction implements DumbAware {

    private static final Logger LOGGER = Logger.getInstance(AddReviewCommentAction.class);

    public AddReviewCommentAction() {
        super("Do Comment", "Do comment",
                IconLoader.getIcon("/images/comment_add.png"));
    }

    @Override
    public void actionPerformed(@NotNull final AnActionEvent e) {
        //AbstractVcs vcs = ProjectLevelVcsManager.getInstance(myProject).getVcsFor(file);
        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        final int lineNumberOffsetZero = getCommentLine(editor);
        final Project project = e.getData(PlatformDataKeys.PROJECT);
        String filepath = getReviewedFilePath(e.getData(PlatformDataKeys.VIRTUAL_FILE), project);

        LOGGER.info("Comment @ " + filepath + ":" + lineNumberOffsetZero);
        showReviewCommentBalloon(editor, lineNumberOffsetZero, project, filepath);
    }

    private int getCommentLine(final Editor editor) {
        return editor.getCaretModel().getLogicalPosition().line + 1;
    }

    public static String getReviewedFilePath(final @NotNull VirtualFile virtualFile, final Project project) {
        String filepath = virtualFile.getPath();
        try {
            filepath = "./" + new URI(project.getBasePath()).relativize(new URI(filepath)).getPath();
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        }
        return filepath;
    }

    public static void showReviewCommentBalloon(final @NotNull Editor dataContext,
                                          final int lineNumberOffsetZero,
                                          final Project project,
                                          final String filepath) {
        final ReviewCommentForm commentForm = new ReviewCommentForm(project, filepath, lineNumberOffsetZero,
                project.getBasePath());

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
                    GitAppraiseUtil.postComment(commentForm._commentFilePath,
                            commentForm._commentLineNumberOffsetZero, commentForm._workspacePath,
                            commentForm.myReviewTextField);
                } else {
                    LOGGER.error("comment was empty");
                }
            }
        });
        balloon.showInBestPositionFor(dataContext);
        commentForm.requestFocus();
    }
}
