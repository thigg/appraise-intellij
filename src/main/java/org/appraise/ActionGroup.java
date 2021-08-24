package org.appraise;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ActionGroup extends com.intellij.openapi.actionSystem.ActionGroup {
    @Override
    public AnAction @NotNull [] getChildren(@Nullable final AnActionEvent e) {
        return new AnAction[]{new AddReviewCommentAction()};
    }
}
