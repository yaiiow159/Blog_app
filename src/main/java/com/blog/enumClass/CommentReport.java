package com.blog.enumClass;

public enum CommentReport {
    IS_REPORTED(true),
    NOT_REPORTED(false);
    private boolean status;

    CommentReport(boolean status) {
        this.status = status;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

}
