package org.appraise;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import com.intellij.openapi.diagnostic.Logger;
import org.appraise.model.ReviewComment;

public class ReviewCommentGetterService {

    private static final Logger LOGGER = Logger.getInstance(ReviewCommentGetterService.class);

    public List<ReviewComment> get(String filename, String workspacepath) {
        return GitAppraiseUtil.getComments(workspacepath)
                .filter(m -> {
                    return m.getFilename().equals(filename);
                })
                .collect(Collectors.toList());

    }
}
