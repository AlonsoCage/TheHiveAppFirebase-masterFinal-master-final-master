
package com.alonsocage.componentes.managers.listeners;

import com.alonsocage.componentes.model.PostListResult;

public interface OnPostListChangedListener<Post> {

    public void onListChanged(PostListResult result);

    void onCanceled(String message);
}
