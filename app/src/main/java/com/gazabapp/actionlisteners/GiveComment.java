package com.gazabapp.actionlisteners;

import com.gazabapp.pojo.PostCommentListData;

public interface GiveComment {
    public void clickOnReply(PostCommentListData data,int Position,boolean IsParent,int ChildPosition,String ParentCommentID);
}
