
package com.alonsocage.componentes.model;


import java.util.Calendar;

public class Like {

    private String id;
    private String authorId;
    private long createdDate;


    public Like() {

    }

    public Like(String authorId) {
        this.authorId = authorId;
        this.createdDate = Calendar.getInstance().getTimeInMillis();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getCreatedDate() {
        return createdDate;
    }

}
