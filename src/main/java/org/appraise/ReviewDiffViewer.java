package org.appraise;

import com.intellij.diff.DiffContext;
import com.intellij.diff.requests.DiffRequest;
import com.intellij.diff.tools.simple.SimpleDiffViewer;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.ui.PopupHandler;
import org.jetbrains.annotations.NotNull;

class ReviewDiffViewer extends SimpleDiffViewer {
    public ReviewDiffViewer(@NotNull DiffContext context, @NotNull DiffRequest request) {
        super(context, request);
    }

    @Override
    protected void onInit() {
        super.onInit();
        final AddReviewCommentAction addCommentAction = new AddReviewCommentAction();
         DefaultActionGroup group = new DefaultActionGroup(addCommentAction);

        PopupHandler.installUnknownPopupHandler(getEditor2().getContentComponent(), group,
                ActionManager.getInstance());
    }
}
