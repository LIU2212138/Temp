package com.projectgroup.project.Service;

import com.projectgroup.project.Dao.PostDao;
import com.projectgroup.project.Model.Comment;
import com.projectgroup.project.Model.Post;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class PostService {
    @Resource
    PostDao postDao;
    public Post selectPost (int id) {
        return postDao.selectPost(id);
    }
    public int getNewestId() {
        return postDao.getNewestId();
    }
    public Post[] getNewestPosts(int count, String searchParameter) {
        List<Post> postList = postDao.getNewestPosts(count, searchParameter);
        int length = postList.size();
        Post[] posts = new Post[length];
        for (int i = 0; i < length; i++) {
            posts[i] = postList.get(i);
        }
        return posts;
    }
    public List<Comment> getComments(int id, int count) {
        return postDao.getComments(count, id);

    }
    public Comment getCommentByIdOfComment(int idOfComment) {
        return postDao.getCommentByIdOfComment(idOfComment);
    }
    public boolean insertPost(Post post) {
        return postDao.insertPost(post);
    }
    public boolean insertComment(Comment comment) {
        return postDao.insertComment(comment);
    }
    public boolean updateLikeCount(int id, int likeCount) {
        return postDao.updateLikeCount(id, likeCount);
    }
    public boolean updateStarCount(int id, int starCount){
        return postDao.updateStarCount(id, starCount);
    }
    public boolean updateCommentCount(int id, int commentCount) {
        return postDao.updateCommentCount(id, commentCount);
    }
    public boolean updateLikeCountForComment(int idOfComment, int likeCount) {
        return postDao.updateLikeCountForComment(idOfComment, likeCount);
    }
    public boolean checkWhetherLike(int user_id, int target_id, int type) {
        return Objects.requireNonNullElse(postDao.checkWhetherLike(user_id, target_id, type), false);
    }

    public boolean insertLike(int user_id, int target_id, int type) {
        return postDao.insertLike(user_id, target_id, type);
    }
}
