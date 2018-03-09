

package com.alonsocage.componentes.managers.listeners;

import java.util.List;

/**
 * Created by Alonso on 15.02.18.
 */

public interface OnDataChangedListener<T> {

    public void onListChanged(List<T> list);
}
