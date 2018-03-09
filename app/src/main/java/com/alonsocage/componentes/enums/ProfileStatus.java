
package com.alonsocage.componentes.enums;

/**
 * Created by Alonso on 15.02.18.
 */

public enum ProfileStatus {
    PROFILE_CREATED(0), NOT_AUTHORIZED(1), NO_PROFILE(2);

    int status;

    ProfileStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
