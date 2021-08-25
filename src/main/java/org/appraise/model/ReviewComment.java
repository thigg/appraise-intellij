package org.appraise.model;

public class ReviewComment {
    public ReviewComment(final String filename, final int linenumber, final long timestamp, final String author, final String comment) {
        this.filename = filename;
        this.linenumber = linenumber;
        this.timestamp = timestamp;
        this.author = author;
        this.comment = comment;
    }

    String filename;

    public String getFilename() {
        return filename;
    }

    public int getLinenumber() {
        return linenumber;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getAuthor() {
        return author;
    }

    public String getComment() {
        return comment;
    }

    int linenumber;
    long timestamp;
    String author;
    String comment;

    @Override
    public String toString() {
        return "ReviewComment{" +
                "filename='" + filename + '\'' +
                ", linenumber=" + linenumber +
                ", timestamp=" + timestamp +
                ", author='" + author + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }

    public String gutterString() {
        return "Author: " + author + "\n"
                + comment;
    }
}
