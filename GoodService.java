package com.projectgroup.project.Service;

import com.projectgroup.project.Dao.GoodDao;
import com.projectgroup.project.Dao.PostDao;
import com.projectgroup.project.Model.Comment;
import com.projectgroup.project.Model.Good;
import com.projectgroup.project.Model.Post;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class GoodService {
    @Resource
    GoodDao goodDao;
    public Good selectGood (int id) {
        return goodDao.selectGood(id);
    }
    public Good[] getNewestGoods(int count, String searchParameter) {
        List<Good> goodList = goodDao.getNewestGoods(count, searchParameter);
        int length = goodList.size();
        Good[] goods = new Good[length];
        for (int i = 0; i < length; i++) {
            goods[i] = goodList.get(i);
        }
        return goods;
    }
    public boolean insertGood(Good good) {
        return goodDao.insertGood(good);
    }
    public boolean updateStarCount(int id, int starCount){
        return goodDao.updateStarCount(id, starCount);
    }

    public boolean checkWhetherLike(int user_id, int target_id, int type) {
        return Objects.requireNonNullElse(goodDao.checkWhetherLike(user_id, target_id, type), false);
    }
    public List<Comment> getComments(int id, int count) {
        return goodDao.getComments(count, id);

    }
    public boolean insertComment(Comment comment) {
        return goodDao.insertComment(comment);
    }
    public boolean updateCommentCount(int id, int commentCount) {
        return goodDao.updateCommentCount(id, commentCount);
    }
    public boolean updateLikeCount(int id, int likeCount) {
        return goodDao.updateLikeCount(id, likeCount);
    }
    public boolean insertLike(int user_id, int target_id, int type) {
        return goodDao.insertLike(user_id, target_id, type);
    }

}
