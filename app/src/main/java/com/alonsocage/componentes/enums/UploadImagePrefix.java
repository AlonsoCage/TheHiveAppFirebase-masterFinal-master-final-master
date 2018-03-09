
package com.alonsocage.componentes.enums;

/**
 * Created by Alonso on 15.02.18.
 */

public enum UploadImagePrefix {

    PROFILE("profile_"), POST("post_");

    String prefix;

    UploadImagePrefix(String prefix) {
        this.prefix = prefix;
    }

    private String getPrefix() {
        return prefix;
    }

    @Override
    public String toString() {
        return getPrefix();
    }
}
