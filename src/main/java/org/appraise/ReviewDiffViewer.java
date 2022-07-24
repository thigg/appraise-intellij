package org.appraise;

import java.awt.Color;
import java.awt.Cursor;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.intellij.diff.DiffContext;
import com.intellij.diff.requests.DiffRequest;
import com.intellij.diff.tools.simple.SimpleDiffViewer;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorGutterAction;
import com.intellij.openapi.editor.TextAnnotationGutterProvider;
import com.intellij.openapi.editor.colors.ColorKey;
import com.intellij.openapi.editor.colors.EditorFontType;
import com.intellij.ui.JBColor;
import com.intellij.ui.PopupHandler;
import org.appraise.model.ReviewComment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class ReviewDiffViewer extends SimpleDiffViewer {
    public ReviewDiffViewer(@NotNull DiffContext context, @NotNull DiffRequest request) {
        super(context, request);
    }

    @Override
    protected void onInit() {
        super.onInit();
        final AddReviewCommentAction addCommentAction = new AddReviewCommentAction();
        String filename =
                AddReviewCommentAction.getReviewedFilePath(getCurrentContent().getHighlightFile(), this.myProject);
        DefaultActionGroup group = new DefaultActionGroup(addCommentAction);

        final List<ReviewComment> reviewComments =
                new ReviewCommentGetterService().get(filename.replace("./",""),
                        getProject().getBasePath());

        getEditor2().getGutter().registerTextAnnotation(
                new TextAnnotationGutterProvider() {

                    @Override
                    public @Nullable String getLineText(final int line, final Editor editor) {
                        return (reviewComments.stream().anyMatch(matchesLineNumber(line))) ? "OOOO" : "";
                    }

                    @Override
                    public @Nullable String getToolTip(final int line, final Editor editor) {
                        return reviewComments.stream()
                                .filter(matchesLineNumber(line))
                                .map(ReviewComment::gutterString)
                                .collect(Collectors.joining(","));
                    }

                    @Override
                    public EditorFontType getStyle(final int line, final Editor editor) {
                        return EditorFontType.PLAIN;
                    }

                    @Override
                    public @Nullable ColorKey getColor(final int line, final Editor editor) {
                        return ColorKey.createColorKey("appraise.gutter.color", JBColor.ORANGE);
                    }

                    @Override
                    public @Nullable Color getBgColor(final int line, final Editor editor) {
                        return  (reviewComments.stream().anyMatch(matchesLineNumber(line))) ? JBColor.WHITE : null;
                    }

                    @Override
                    public List<AnAction> getPopupActions(final int line, final Editor editor) {
                        return new ArrayList<>();
                    }

                    @Override
                    public void gutterClosed() {

                    }
                }
        );


        getEditor2().getGutter().registerTextAnnotation(new TextAnnotationGutterProvider() {

            @Override
            public @Nullable String getLineText(final int line, final Editor editor) {
                return "+";
            }

            @Override
            public @Nullable String getToolTip(final int line, final Editor editor) {
                return "Click to add review comment";
            }

            @Override
            public EditorFontType getStyle(final int line, final Editor editor) {
                return EditorFontType.PLAIN;
            }

            @Override
            public @Nullable ColorKey getColor(final int line, final Editor editor) {
                return ColorKey.createColorKey("appraise.gutter.color", JBColor.GREEN);
            }

            @Override
            public @Nullable Color getBgColor(final int line, final Editor editor) {
                return null;
            }

            @Override
            public List<AnAction> getPopupActions(final int line, final Editor editor) {
                return new ArrayList<>();
            }

            @Override
            public void gutterClosed() {

            }
        }, new EditorGutterAction() {
            @Override
            public void doAction(final int lineNum) {
                AddReviewCommentAction.showReviewCommentBalloon(getEditor2(), lineNum, getProject(), filename);
            }

            @Override
            public Cursor getCursor(final int lineNum) {
                return null;
            }
        });

        PopupHandler.installUnknownPopupHandler(getEditor2().getContentComponent(), group,
                ActionManager.getInstance());
    }


    public Predicate<ReviewComment> matchesLineNumber(int targetLineOffsetZero) {
        return c->c.getLinenumber() == targetLineOffsetZero + 1;
    }
}
