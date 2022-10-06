package com.gazabapp.actionlisteners;

import com.gazabapp.pojo.PostCommentListData;
import com.gazabapp.pojo.PostListData;

public interface PostDataChanged {
    public void onPostDataChanged(PostListData data, int Position);
}
