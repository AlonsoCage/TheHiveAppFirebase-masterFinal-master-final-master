
package com.alonsocage.componentes.enums;

/**
 * Created by Alonso on 15.02.18.
 */

public enum ItemType {LOAD(10), ITEM(11);
    private final int typeCode;

    ItemType(int typeCode) {
        this.typeCode = typeCode;
    }

    public int getTypeCode() {
        return this.typeCode;
    }
}
