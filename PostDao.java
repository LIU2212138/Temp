package com.projectgroup.project.Dao;

import com.projectgroup.project.Model.Comment;
import com.projectgroup.project.Model.Post;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostDao {
    Post selectPost(int id);
    int getNewestId();
    List<Post> getNewestPosts(int count, String searchParameter);
    List<Comment> getComments(int count, int id);
    Comment getCommentByIdOfComment(int idOfComment);

    boolean insertPost(Post post);
    boolean insertComment(Comment comment);

    boolean updateLikeCount(int id, int like_cnt);
    boolean updateStarCount(int id, int star_cnt);
    boolean updateCommentCount(int id, int comment_cnt);
    boolean updateLikeCountForComment(int idOfComment, int like_cnt);

    Boolean checkWhetherLike(int user_id, int target_id, int type);
    Boolean insertLike(int user_id, int target_id, int type);
}
