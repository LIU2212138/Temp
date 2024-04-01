package com.projectgroup.project.Dao;

import com.projectgroup.project.Model.Comment;
import com.projectgroup.project.Model.Good;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoodDao {
    Good selectGood(int id);
    List<Good> getNewestGoods(int count, String searchParameter);
    boolean insertGood(Good good);
    boolean updateStarCount(int id, int star_cnt);

    Boolean checkWhetherLike(int user_id, int target_id, int type);
    List<Comment> getComments(int count, int id);
    boolean insertComment(Comment comment);
    boolean updateCommentCount(int id, int comment_cnt);
    boolean updateLikeCount(int id, int like_cnt);
    Boolean insertLike(int user_id, int target_id, int type);
}
