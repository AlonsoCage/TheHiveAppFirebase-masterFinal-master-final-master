
package com.alonsocage.componentes.managers.listeners;

import com.alonsocage.componentes.model.Post;

public interface OnPostChangedListener {
    public void onObjectChanged(Post obj);

    public void onError(String errorText);
}
