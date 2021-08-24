package org.appraise;

import java.util.Collections;
import java.util.List;

import com.intellij.diff.DiffContext;
import com.intellij.diff.DiffTool;
import com.intellij.diff.FrameDiffTool;
import com.intellij.diff.SuppressiveDiffTool;
import com.intellij.diff.requests.DiffRequest;
import com.intellij.diff.tools.simple.SimpleDiffTool;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class ReviewDiffTool implements FrameDiffTool, SuppressiveDiffTool {

    private static final Logger LOGGER = Logger.getInstance(ReviewDiffTool.class);


    public static final String DIMENSION_SERVICE_KEY = "Review.Comment.Balloon";

    @Override
    public @NotNull @Nls(capitalization = Nls.Capitalization.Sentence) String getName() {
        return SimpleDiffTool.INSTANCE.getName();
    }

    @Override
    public boolean canShow(@NotNull final DiffContext context, @NotNull final DiffRequest request) {
        return ReviewDiffViewer.canShowRequest(context, request);
    }

    @Override
    public @NotNull DiffViewer createComponent(@NotNull final DiffContext context, @NotNull final DiffRequest request) {
        LOGGER.info("yeah,creating diff tool");
        return new ReviewDiffViewer(context, request);
    }

    @Override
    public List<Class<? extends DiffTool>> getSuppressedTools() {

        return Collections.<Class<? extends DiffTool>>singletonList(SimpleDiffTool.class);
    }
}
