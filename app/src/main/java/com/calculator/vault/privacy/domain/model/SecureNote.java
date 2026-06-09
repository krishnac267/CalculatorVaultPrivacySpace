package com.calculator.vault.privacy.domain.model;



public final class SecureNote {

    private final long id;

    private final String title;

    private final String content;

    private final boolean favorite;

    private final boolean locked;

    private final boolean contentHidden;

    private final long createdAt;

    private final long updatedAt;



    public SecureNote(

            long id,

            String title,

            String content,

            boolean favorite,

            boolean locked,

            boolean contentHidden,

            long createdAt,

            long updatedAt

    ) {

        this.id = id;

        this.title = title;

        this.content = content;

        this.favorite = favorite;

        this.locked = locked;

        this.contentHidden = contentHidden;

        this.createdAt = createdAt;

        this.updatedAt = updatedAt;

    }



    public long getId() { return id; }

    public String getTitle() { return title; }

    public String getContent() { return content; }

    public boolean isFavorite() { return favorite; }

    public boolean isLocked() { return locked; }

    public boolean isContentHidden() { return contentHidden; }

    public long getCreatedAt() { return createdAt; }

    public long getUpdatedAt() { return updatedAt; }

}

