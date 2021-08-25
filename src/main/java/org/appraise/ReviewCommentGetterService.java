package org.appraise;

import java.util.List;
import java.util.stream.Collectors;

import com.intellij.openapi.diagnostic.Logger;
import org.appraise.model.ReviewComment;

public class ReviewCommentGetterService {

    private static final Logger LOGGER = Logger.getInstance(ReviewCommentGetterService.class);

    public List<ReviewComment> get(String filename, String workspacepath) {
        return GitAppraiseUtil.getComments(workspacepath)
                .filter(m -> m.getFilename().equals(filename))
                .collect(Collectors.toList());

    }
}
