package org.appraise;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.ui.EditorTextField;
import org.appraise.model.ReviewComment;

public class GitAppraiseUtil {

    private static final Logger LOGGER = Logger.getInstance(GitAppraiseUtil.class);

    public static void postComment(final String commentFilePath, final int commentLineNumberOffsetZero, final String workspacePath, final EditorTextField myReviewTextField) {
        try {
            final String[] cmd = {
                    "git", "appraise", "comment",
                    "-m", myReviewTextField.getText(),
                    "-f", commentFilePath,
                    "-l", "" + commentLineNumberOffsetZero};
            LOGGER.info("cmd: " + Arrays.toString(cmd) + " in " + workspacePath);
            final Process exec = runCommand(workspacePath, cmd);
            exec.waitFor();
            LOGGER.info("appraise output" + new BufferedReader(
                    new InputStreamReader(exec.getInputStream())).lines().collect(
                    Collectors.joining()));
        } catch (IOException | InterruptedException e) {
            LOGGER.error(e);
        }
    }

    private static Process runCommand(final String workspacePath, final String[] cmd) throws IOException {
        return Runtime.getRuntime().exec(cmd, null, new File(workspacePath));
    }

    public static Stream<ReviewComment> getComments(final String workspacePath) {
        final String[] getRevsCmd = {
                "git", "notes", "--ref", "refs/notes/devtools/discuss"};
        LOGGER.info("cmd: " + Arrays.toString(getRevsCmd) + " in " + workspacePath);
        try {
            final Process getReviewsExec = runCommand(workspacePath, getRevsCmd);
            String[] reviews =
                    new BufferedReader(new InputStreamReader(getReviewsExec.getInputStream())).lines().collect(
                            Collectors.joining()).split(" ");
            String review = reviews[reviews.length - 1];
            final String[] getCommentsCmd = {
                    "git", "notes", "--ref", "refs/notes/devtools/discuss", "show", review};
            final Process getCommentsExec = runCommand(workspacePath, getCommentsCmd);
            ObjectMapper objectMapper = new ObjectMapper();
            return new BufferedReader(new InputStreamReader(getCommentsExec.getInputStream()))
                    .lines()
                    .map(s -> {
                        try {
                            return objectMapper.readTree(s);
                        } catch (JsonProcessingException e) {
                            LOGGER.warn("Could not deserialize: " + s);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .filter(o -> o.has("location") && o.get("location").has("path"))
                    .map(node -> new ReviewComment(
                            node.get("location").get("path").asText(),
                            node.get("location").get("range").get("startLine").asInt(),
                            node.get("timestamp").asLong(),
                            node.get("author").asText(),
                            node.get("description").asText()
                    ));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Stream.empty();
    }
}
